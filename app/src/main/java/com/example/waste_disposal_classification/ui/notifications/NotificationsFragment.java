package com.example.waste_disposal_classification.ui.notifications;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.waste_disposal_classification.R;
import com.kwabenaberko.newsapilib.NewsApiClient;
import com.kwabenaberko.newsapilib.models.Article;
import com.kwabenaberko.newsapilib.models.request.EverythingRequest;
import com.kwabenaberko.newsapilib.models.response.ArticleResponse;

import java.util.List;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private int PERMISSION_ALL = 1;
    String[] PERMISSIONS = { Manifest.permission.INTERNET};
    private List<Article> articles;
    private RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        recyclerView = root.findViewById(R.id.recyclerView);

        if (!hasPermission(getContext(), PERMISSIONS)){
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
        }
        try {
            NewsApiClient newsApiClient = new NewsApiClient("c82b3f260d714a5bb2e11c5563ce00cb");

            newsApiClient.getEverything(
                    new EverythingRequest.Builder()
                            .q("recycling")
                            .build(),
                    new NewsApiClient.ArticlesResponseCallback() {
                        @Override
                        public void onSuccess(ArticleResponse response) {
                            articles = response.getArticles();
                            setAdapter();
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            System.out.println(throwable.getMessage());
                        }
                    }
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
        return root;
    }

    private void setAdapter() {
        recyclerAdapter adapter = new recyclerAdapter(articles);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
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