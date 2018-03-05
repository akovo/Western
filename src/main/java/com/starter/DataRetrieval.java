package com.starter;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DataRetrieval {

    public static final String API_KEY = "17_cTdFVNIoNNrMxpfHCWg";

    public static final String BASE_URL = "http://oec-2018.herokuapp.com/";

    private static final HttpClient HTTP_CLIENT = new DefaultHttpClient();

    public synchronized static String executeRequest(String url) {
        HttpGet request = new HttpGet(url);
        HttpResponse response = null;
        try {
            response = HTTP_CLIENT.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader rd = null;
        String data = null;
        try {
            rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            StringBuffer result = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            data = result.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(data);

        return data;
    }
}
