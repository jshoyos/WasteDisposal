package com.example.waste_disposal_classification.ui.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.waste_disposal_classification.R;
import com.example.waste_disposal_classification.classifier.Classifier;
import com.example.waste_disposal_classification.classifier.Recognition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class CameraFragment extends Fragment {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1000;
    private static final int CAMERA_REQUEST_CODE = 1001;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 4000;
    private static final int STORAGE_REQUEST_CODE = 4001;
    private CameraViewModel cameraViewModel;
    private Classifier imageClassifier;
    private Button captureBtn;
    private Button uploadBtn;
    private ImageView imageView;
    private ListView listViewPrediction;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        cameraViewModel =
                new ViewModelProvider(this).get(CameraViewModel.class);
        View root = inflater.inflate(R.layout.fragment_camera, container, false);
        initUIElements(root);
//        cameraViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                //textView.setText(s);
//            }
//        });
        return root;
    }

    private void initUIElements(View root) {
        captureBtn = root.findViewById(R.id.btn_capture_image);
        uploadBtn = root.findViewById(R.id.btn_upload_image);
        imageView = root.findViewById(R.id.camera_capture);
        listViewPrediction = root.findViewById(R.id.listview_prediction);
        try {
            imageClassifier = new Classifier(getContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasPermission()){
                    openCamera();
                }
                else{
                    requestPermission();
                }
            }
        });
        uploadBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(hasGalleryPermission()){
                    openGallery();
                }
                else{
                    requestGalleryPermission();
                }
            }
        });
    }

    private void requestGalleryPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(getContext(),"Camera Permission Required", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"), STORAGE_REQUEST_CODE);;
    }

    private boolean hasGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void requestPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                Toast.makeText(getContext(),"Camera Permission Required", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent,CAMERA_REQUEST_CODE);
    }

    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_PERMISSION_REQUEST_CODE){
            if(hasAllPermissions(grantResults)){
                openCamera();
            }
            else{
                requestPermission();;
            }
        }
        else if(requestCode == STORAGE_PERMISSION_REQUEST_CODE){
            if(hasAllPermissions(grantResults)){
                openGallery();
            }
            else{
                requestGalleryPermission();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE){
            Bitmap photo = (Bitmap) Objects.requireNonNull((data).getExtras()).get("data");
            imageView.setImageBitmap(photo);
            display(imageClassifier.recognizeImage(photo));
        }
        else if(requestCode == STORAGE_REQUEST_CODE){
            if (data != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                    imageView.setImageBitmap(bitmap);
                    display(imageClassifier.recognizeImage(bitmap));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else if(requestCode == Activity.RESULT_CANCELED){
            Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void display(List<Recognition> results) {
        final List<String> predictionsList = new ArrayList<>();
        for (Recognition recog : results) {
            String category;
            switch (recog.getTitle()){
                case "0":
                    category = "plastic";
                    break;
                case "1":
                    category = "paper";
                    break;
                case "2":
                    category = "metal";
                    break;
                case "3":
                    category = "cardboard";
                    break;
                case "4":
                    category = "glass";
                    break;
                default:
                    category = "trash";
                    break;
            }
            predictionsList.add(category + " :::::: " + recog.getConfidence());
        }
        ArrayAdapter<String> predictionsAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, predictionsList);
        listViewPrediction.setAdapter(predictionsAdapter);
    }

    private boolean hasAllPermissions(int[] grantResults) {
        for(int result : grantResults){
            if(result == PackageManager.PERMISSION_DENIED){
                return false;
            }
        }
        return true;
    }
}