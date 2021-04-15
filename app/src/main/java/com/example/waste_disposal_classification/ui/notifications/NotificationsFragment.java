package com.example.waste_disposal_classification.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.waste_disposal_classification.R;
import com.example.waste_disposal_classification.classifier.ImageClassification;

import java.io.IOException;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
       // initUIElements(root);

        return root;
    }

//    private void initUIElements(View root) {
//        captureBtn = root.findViewById(R.id.btn_capture_image);
//        uploadBtn = root.findViewById(R.id.btn_upload_image);
//        imageView = root.findViewById(R.id.camera_capture);
//        listViewPrediction = root.findViewById(R.id.listview_prediction);
//        try {
//            imageClassifier = new ImageClassification(getActivity());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        captureBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (hasPermission()){
//                    openCamera();
//                }
//                else{
//                    requestPermission();
//                }
//            }
//        });
//        uploadBtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                if(hasGalleryPermission()){
//                    openGallery();
//                }
//                else{
//                    requestGalleryPermission();
//                }
//            }
//        });
//    }
}