package com.yawar.memo.utils;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FileDownloader {

    private static final String TAG = "FileDownloader";

    private static final int MEGABYTE = 1024 * 1024;

    public static void downloadFile(String fileUrl, File directory) {
        try {
            Log.v(TAG, "downloadFile() invoked ");
            Log.v(TAG, "downloadFile() fileUrl " + fileUrl);
            Log.v(TAG, "downloadFile() directory " + directory);

            URL url = new URL(fileUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(directory);
            int totalSize = urlConnection.getContentLength();
            System.out.println(totalSize+"totalSize");

            byte[] buffer = new byte[MEGABYTE];
            int bufferLength = 0;
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, bufferLength);
            }
            System.out.println(totalSize+"totalSize"+directory.length());
            if(totalSize>directory.length()){
                System.out.println("fill deleteddddddddddd");
                directory.delete();
            }

            fileOutputStream.close();
            Log.v(TAG, "downloadFile() completed ");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            directory.delete();
            Log.e(TAG, "downloadFile() error" + e.getMessage());
            Log.e(TAG, "downloadFile() error" + e.getStackTrace());
        } catch (MalformedURLException e) {
            directory.delete();
            e.printStackTrace();
            Log.e(TAG, "downloadFile() error" + e.getMessage());
            Log.e(TAG, "downloadFile() error" + e.getStackTrace());
        } catch (IOException e) {
            directory.delete();

            e.printStackTrace();
            Log.e(TAG, "downloadFile() error" + e.getMessage());
            Log.e(TAG, "downloadFile() error" + e.getStackTrace());
        }
    }
}