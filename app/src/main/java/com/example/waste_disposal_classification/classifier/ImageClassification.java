package com.example.waste_disposal_classification.classifier;

import android.app.Activity;
import android.graphics.Bitmap;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.image.ops.Rot90Op;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ImageClassification {

    private static final float PROBABILITY_MEAN = 0.0f;
    private static final float PROBABILITY_STD = 255.0f;
    private static final float IMAGE_STD = 1.0f;
    private static final float IMAGE_MEAN = 0.0f;
    private static final int MAX_SIZE = 5;
    private final Interpreter tensorClassifier;
    private final int imageResizeX;
    private final int imageResizeY;
    private  TensorImage inputImageBuffer;
    private final List<String> labels;
    private final TensorBuffer probabilityImage;
    private final TensorProcessor probabilityProcessor;

    public ImageClassification(Activity activity) throws IOException {
        //load the model
        MappedByteBuffer classifierModel = FileUtil.loadMappedFile(activity, "model.tflite");
        labels = FileUtil.loadLabels(activity, "labels_mobilnet_quant_v1_224.txt");
        tensorClassifier = new Interpreter(classifierModel, null);

        int imageTensorIndex = 0;
        int probabilityTensorIndex = 0;

        int[] inputImageShape = tensorClassifier.getInputTensor(imageTensorIndex).shape();
        DataType inputDataType = tensorClassifier.getInputTensor(imageTensorIndex).dataType();

        int[] outputImageShape = tensorClassifier.getOutputTensor(probabilityTensorIndex).shape();
        DataType outputDataType = tensorClassifier.getOutputTensor(probabilityTensorIndex).dataType();

        imageResizeX = inputImageShape[1];
        imageResizeY = inputImageShape[2];

        inputImageBuffer = new TensorImage(inputDataType);
        probabilityImage = TensorBuffer.createFixedSize(outputImageShape,outputDataType);
        probabilityProcessor = new TensorProcessor.Builder().add(new NormalizeOp(PROBABILITY_MEAN, PROBABILITY_STD)).build();
    }

    public  List<Recognition> recognizeImage(final Bitmap bitmap, final int sensorOrientation){
        List<Recognition> recognitions = new ArrayList<>();
        inputImageBuffer = loadImage(bitmap, sensorOrientation);
        tensorClassifier.run(inputImageBuffer.getBuffer(), probabilityImage.getBuffer().rewind());
        Map<String, Float> labelledProbability = new TensorLabel(labels,probabilityProcessor.process(probabilityImage)).getMapWithFloatValue();
        for(Map.Entry<String, Float> entry : labelledProbability.entrySet()){
            recognitions.add(new Recognition(entry.getKey(), entry.getValue()));
        }

        //sorting predictions based on confidence
        Collections.sort(recognitions);
        //return top 5
        recognitions.subList(0, MAX_SIZE > recognitions.size() ? recognitions.size() : MAX_SIZE).clear();
        return  recognitions;
    }

    private TensorImage loadImage(Bitmap bitmap, int sensorOrientation) {
        inputImageBuffer.load(bitmap);
        int noOfRotations = sensorOrientation/90;
        int cropSize = Math.min(bitmap.getWidth(),bitmap.getHeight());
        ImageProcessor imageProcessor = new ImageProcessor.Builder()
                .add(new ResizeWithCropOrPadOp(cropSize,cropSize))
                .add(new ResizeOp(imageResizeX, imageResizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                .add(new Rot90Op(sensorOrientation))
                .add(new NormalizeOp(IMAGE_MEAN, IMAGE_STD))
                .build();
        return  imageProcessor.process(inputImageBuffer);
    }
}
