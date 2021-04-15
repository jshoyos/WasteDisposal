package com.example.waste_disposal_classification.ui.notifications;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.waste_disposal_classification.News.Articles;
import com.example.waste_disposal_classification.News.News;
import com.example.waste_disposal_classification.R;

import java.io.IOException;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private int PERMISSION_ALL = 1;
    String[] PERMISSIONS = { Manifest.permission.INTERNET};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        final TextView textView = root.findViewById(R.id.text_notifications);
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        if (!hasPermission(getContext(), PERMISSIONS)){
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
        }
        try {
            Articles articles = News.getNews();
            System.out.println(articles.getArticles().get(0).getAuthor());
        } catch (IOException e) {
            e.printStackTrace();
            textView.setText("An error has occurred");
        }
        return root;
    }

    private boolean hasPermission(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context!=null && permissions != null){
           for (String permission : permissions){
               if(ActivityCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED){
                   return false;
               }
           }
        }
        return true;
    }
}