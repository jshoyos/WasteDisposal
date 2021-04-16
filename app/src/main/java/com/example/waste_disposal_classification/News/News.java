package com.example.waste_disposal_classification.News;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class News{
    private final static String url ="https://newsapi.org/v2/everything?q=recycling||compost&apiKey=c82b3f260d714a5bb2e11c5563ce00cb";
    private static String GetRequest() throws IOException{
        String readLine = null;
        java.net.URL urlRequest = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlRequest.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK){
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer response = new StringBuffer();
            while ((readLine = in .readLine()) != null) {
                response.append(readLine);
            } in .close();
        }
        return readLine;
    }

    public static Articles getNews() throws IOException{
       String news = GetRequest();
       Gson gson = new Gson();
       Articles results = gson.fromJson(news, Articles.class);
       return  results;
    }
}
