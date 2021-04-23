package com.example.waste_disposal_classification.classifier;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;

import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.support.metadata.MetadataExtractor;
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions;
import org.tensorflow.lite.task.vision.classifier.Classifications;
import org.tensorflow.lite.task.vision.classifier.ImageClassifier;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.min;


/**
 *  This class is in charge of loading our trained model and performs our image recognition process
 */

public class Classifier {
    private static final int MAX_RESULTS = 1;
    public static final String FILE_PATH = "model.tflite";
    private final ImageClassifier imageClassifier;
    private final int imageSizeY;
    private final int imageSizeX;

    public Classifier(Context context) throws IOException {
        ImageClassifier.ImageClassifierOptions options =
                ImageClassifier.ImageClassifierOptions.builder()
                .setMaxResults(MAX_RESULTS)
                .build();
        imageClassifier = ImageClassifier.createFromFileAndOptions(context,FILE_PATH, options);
        MappedByteBuffer tfliteModel = FileUtil.loadMappedFile(context, FILE_PATH);
        MetadataExtractor metadataExtractor = new MetadataExtractor(tfliteModel);
        int[] imageShape = metadataExtractor.getInputTensorShape(0);
        imageSizeY = imageShape[1];
        imageSizeX = imageShape[2];
    }

    public List<Recognition> recognizeImage(final Bitmap bitmap){
        TensorImage inputImage = TensorImage.fromBitmap(bitmap);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int cropSize = min(width,height);

        ImageProcessingOptions imageOptions =
                ImageProcessingOptions.builder()
                        .setRoi(
                                new Rect(
                                        (width - cropSize)/2,
                                        (height - cropSize)/2,
                                        (width + cropSize)/2,
                                        (height + cropSize)/2
                                )
                        )
                        .build();
        List<Classifications> results = imageClassifier.classify(inputImage, imageOptions);
        return getRecognitions(results);
    }

    private List<Recognition> getRecognitions(List<Classifications> classifications) {
        final ArrayList<Recognition> recognitions = new ArrayList<>();
        for (Category category : classifications.get(0).getCategories()){
            recognitions.add(
                    new Recognition("" + category.getLabel(), category.getLabel(),category.getScore())
            );
        }
        return recognitions;
    }
}
