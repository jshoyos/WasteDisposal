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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.waste_disposal_classification.R;
import com.example.waste_disposal_classification.classifier.Classifier;
import com.example.waste_disposal_classification.classifier.Recognition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *  Camera fragment that is used for uploading, capturing and classifying images
 */
public class CameraFragment extends Fragment {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1000;
    private static final int CAMERA_REQUEST_CODE = 1001;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 4000;
    private static final int STORAGE_REQUEST_CODE = 4001;
    private CameraViewModel cameraViewModel;
    private Classifier imageClassifier;
    private Button captureBtn;
    private Button uploadBtn;
    private Button redirectButton;
    private ImageView imageView;
    private ImageView imageView2;
    private TextView textViewPrediction;
    private TextView textView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        cameraViewModel =
                new ViewModelProvider(this).get(CameraViewModel.class);
        View root = inflater.inflate(R.layout.fragment_camera, container, false);
        initUIElements(root);
        return root;
    }

    /**
     * initializes variables and sets onclick events
     * @param root
     */
    private void initUIElements(View root) {
        captureBtn = root.findViewById(R.id.btn_capture_image);
        uploadBtn = root.findViewById(R.id.btn_upload_image);
        imageView = root.findViewById(R.id.camera_capture);
        imageView2 = (ImageView) root.findViewById(R.id.result);
        textViewPrediction = (TextView) root.findViewById(R.id.listview_prediction);
        redirectButton = (Button) root.findViewById(R.id.Redirect_button);
        textView = (TextView) root.findViewById(R.id.plastic_text_view);
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

    /**
     * Gets the gallery permissions
     */
    private void requestGalleryPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                Toast.makeText(getContext(),"Camera Permission Required", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * This method opens the android device's image gallery
     */
    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"), STORAGE_REQUEST_CODE);;
    }

    /**
     * Checks the gallery permissions
     * @return boolean
     */
    private boolean hasGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    /**
     * Gets the camera permissions
     */
    private void requestPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                Toast.makeText(getContext(),"Camera Permission Required", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * This method opens the android device's image gallery
     */
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent,CAMERA_REQUEST_CODE);
    }
    /**
     * Checks the camera permissions
     * @return boolean
     */
    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    @Override
    /**
     * This method opens the respective internal device resource depending on the request code and correct permissions
     */
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

    /**
     * This method takes the image from the device and calls the predict function
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE){
            try{
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(photo);
                display(imageClassifier.recognizeImage(photo));
            }
            catch(Exception e){
                Toast.makeText(getActivity(), "Camera Closed", Toast.LENGTH_SHORT).show();
            }

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

    }

    /**
     * Displays the resulting classification
     * @param results
     */
    private void display(List<Recognition> results) {
        final List<String> predictionsList = new ArrayList<>();

        for (Recognition recog : results) {
            String category;
            textView.setVisibility(View.INVISIBLE);
            redirectButton.setVisibility(View.INVISIBLE);
            imageView2.setImageResource(0);
            switch (recog.getTitle()){
                case "0":
                    category = "plastic";
                    textView.setVisibility(View.VISIBLE);
                    redirectButton.setVisibility(View.VISIBLE);
                    redirectButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                            navController.navigate(R.id.navigation_plastic_code);
                        }
                    });
                    break;
                case "1":
                    imageView2.setImageResource(R.drawable.recyclingsymbol);
                    category = "paper";
                    break;
                case "2":
                    imageView2.setImageResource(R.drawable.norecycling);
                    category = "metal";
                    break;
                case "3":
                    imageView2.setImageResource(R.drawable.recyclingsymbol);
                    category = "cardboard";
                    break;
                case "4":
                    imageView2.setImageResource(R.drawable.recyclingsymbol);
                    category = "glass";
                    break;
                default:
                    imageView2.setImageResource(R.drawable.norecycling);
                    category = "trash";
                    break;
            }

            predictionsList.add("Detected " +category + " with " + String.format("%.2f",recog.getConfidence()*100) + "% accuracy.");

        }
        textViewPrediction.setText(predictionsList.get(0));
    }

    /**
     * Checks the overall permissions
     * @return boolean
     */
    private boolean hasAllPermissions(int[] grantResults) {
        for(int result : grantResults){
            if(result == PackageManager.PERMISSION_DENIED){
                return false;
            }
        }
        return true;
    }
}