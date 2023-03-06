package com.yawar.memo.utils;

import static com.yawar.memo.SocketFunctionKt.newMeesage;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.yawar.memo.BaseApp;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.domain.model.ChatMessage;
import com.yawar.memo.repositry.ChatMessageRepoo;
import com.yawar.memo.ui.chatPage.ConversationActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class FileUtil {

    private static final String LOG_TAG = "FileUtils";

    private static Uri contentUri = null;



    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.<br>
     * <br>
     * Callers should check whether the path is local before assuming it
     * represents a local file.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     */
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {
        // Check here to KITKAT or new version
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        String selection = null;
        String[] selectionArgs = null;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                String fullPath = getPathFromExtSD(split);
                if (fullPath != "") {
                    return fullPath;
                } else {
                    return null;
                }
            }

            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    final String id;
                    Cursor cursor = null;
                    try {
                        cursor = context.getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            String fileName = cursor.getString(0);
                            String path = Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName;
                            if (!TextUtils.isEmpty(path)) {
                                return path;
                            }
                        }
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                    id = DocumentsContract.getDocumentId(uri);
                    if (!TextUtils.isEmpty(id)) {
                        if (id.startsWith("raw:")) {
                            return id.replaceFirst("raw:", "");
                        }
                        String[] contentUriPrefixesToTry = new String[] {
                                "content://downloads/public_downloads",
                                "content://downloads/my_downloads"
                        };
                        for (String contentUriPrefix : contentUriPrefixesToTry) {
                            try {
                                final Uri contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), Long.valueOf(id));

                                // final Uri contentUri = ContentUris.withAppendedId(
                                //        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                                return getDataColumn(context, contentUri, null, null);
                            } catch (NumberFormatException e) {
                                // In Android 8 and Android P the id is not a number
                                return uri.getPath().replaceFirst("^/document/raw:", "").replaceFirst("^raw:", "");
                            }
                        }
                    }
                } else {
                    final String id = DocumentsContract.getDocumentId(uri);
                    final boolean isOreo = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
                    if (id.startsWith("raw:")) {
                        return id.replaceFirst("raw:", "");
                    }
                    try {
                        contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    if (contentUri != null) {
                        return getDataColumn(context, contentUri, null, null);
                    }
                }
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;

                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            } else if (isGoogleDriveUri(uri)) {
                return getDriveFilePath(uri, context);
            }
        }


        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }

            if (isGoogleDriveUri(uri)) {
                return getDriveFilePath(uri, context);
            }
            if( Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
                // return getFilePathFromURI(context,uri);
                return getMediaFilePathForN(uri, context);
                // return getRealPathFromURI(context,uri);
            } else {
                return getDataColumn(context, uri, null, null);
            }
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Check if a file exists on device
     *
     * @param filePath The absolute file path
     */
    private static boolean fileExists(String filePath) {
        File file = new File(filePath);

        return file.exists();
    }


    /**
     * Get full file path from external storage
     *
     * @param pathData The storage type and the relative path
     */
    private static String getPathFromExtSD(String[] pathData) {
        final String type = pathData[0];
        final String relativePath = "/" + pathData[1];
        String fullPath = "";

        // on my Sony devices (4.4.4 & 5.1.1), `type` is a dynamic string
        // something like "71F8-2C0A", some kind of unique id per storage
        // don't know any API that can get the root path of that storage based on its id.
        //
        // so no "primary" type, but let the check here for other devices
        if ("primary".equalsIgnoreCase(type)) {
            fullPath = Environment.getExternalStorageDirectory() + relativePath;
            if (fileExists(fullPath)) {
                return fullPath;
            }
        }

        // Environment.isExternalStorageRemovable() is `true` for external and internal storage
        // so we cannot relay on it.
        //
        // instead, for each possible path, check if file exists
        // we'll start with secondary storage as this could be our (physically) removable sd card
        fullPath = System.getenv("SECONDARY_STORAGE") + relativePath;
        if (fileExists(fullPath)) {
            return fullPath;
        }

        fullPath = System.getenv("EXTERNAL_STORAGE") + relativePath;
        if (fileExists(fullPath)) {
            return fullPath;
        }

        return fullPath;
    }

    private static String getDriveFilePath(Uri uri, Context context) {
        Uri returnUri = uri;
        ContentResolver contentResolver = context.getContentResolver();
        Cursor returnCursor = contentResolver.query(returnUri, null, null, null, null);

        // Get the column indexes of the data in the Cursor,
        // move to the first row in the Cursor, get the data,
        // and display it.

        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        File file = new File(context.getCacheDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();

            // int bufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            Log.e("File Size", "Size " + file.length());
            inputStream.close();
            outputStream.close();
            Log.e("File Path", "Path " + file.getPath());
            Log.e("File Size", "Size " + file.length());
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return file.getPath();
    }

    private static String getMediaFilePathForN(Uri uri, Context context) {
        Uri returnUri = uri;
        Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);

        // Get the column indexes of the data in the Cursor,
        // move to the first row in the Cursor, get the data,
        // and display it.

        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        File file = new File(context.getFilesDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();

            //int bufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            Log.e("File Size", "Size " + file.length());
            inputStream.close();
            outputStream.close();
            Log.e("File Path", "Path " + file.getPath());
            Log.e("File Size", "Size " + file.length());
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return file.getPath();
    }


    private static String getDataColumn(Context context, Uri uri,
                                        String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * @param uri - The Uri to check.
     * @return - Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri - The Uri to check.
     * @return - Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri - The Uri to check.
     * @return - Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri - The Uri to check.
     * @return - Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Drive.
     */
    private static boolean isGoogleDriveUri(Uri uri) {
        return "com.google.android.apps.docs.storage".equals(uri.getAuthority()) //
                || "com.google.android.apps.docs.storage.legacy".equals(uri.getAuthority());
    }

    ///////for copy file
    public static void copyFileOrDirectory(String srcDir, String dstDir, String fileName) {

        try {
            System.out.println(srcDir + dstDir + "copy Recorrrrrrd");

            File src = new File(srcDir);
//            File dst = new File(dstDir, src.getName());
            File dst = new File(dstDir, fileName);



            if (src.isDirectory()) {

                String[] files = src.list();
                int filesLength = files.length;
                for (int i = 0; i < filesLength; i++) {
                    String src1 = (new File(src, files[i]).getPath());
                    String dst1 = dst.getPath();
                    copyFileOrDirectory(src1, dst1,fileName);

                }
            } else {
                copyFile(src, dst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        System.out.println(sourceFile.getPath() + destFile.getPath() + "nameeeeeee");
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }
    ///////for copy file
    public static void copyPdfFileOrDirectory(String srcDir, String dstDir) {

        try {
            System.out.println(srcDir + dstDir + "copy Recorrrrrrd");

            File src = new File(srcDir);
            File dst = new File(dstDir, src.getName());
//            File dst = new File(dstDir, fileName);



            if (src.isDirectory()) {

                String[] files = src.list();
                int filesLength = files.length;
                for (int i = 0; i < filesLength; i++) {
                    String src1 = (new File(src, files[i]).getPath());
                    String dst1 = dst.getPath();
                    copyFileOrDirectory(src1, dst1,src.getName());

                }
            } else {
                copyFile(src, dst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        System.out.println(mimeType + "memeType");
        return mimeType != null && mimeType.indexOf("video") == 0;

    }
    ////////////


///// end copy file

    /// for Get Bytes
    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
    ////////////////////
    public static ChatMessage uploadImage(String imageName, Uri pdfFile, ConversationActivity activity, String user_id, String anthor_user_id
    , String blockrdFor, String token) {
        String dataTime =  String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis());
        ChatMessageRepoo chatMessageRepo = BaseApp.Companion.getInstance().chatMessageRepoo;
        BaseApp myBase = BaseApp.Companion.getInstance();
        Bitmap bitmap = null;
        byte[] imageBytes = new byte[]{};



        String message_id = System.currentTimeMillis() + "_" + user_id;


        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessageId(message_id);//dummy
        chatMessage.setImage(pdfFile.toString());
        chatMessage.setFileName(imageName);

        chatMessage.setDateTime(dataTime);
        chatMessage.setMe(true);
        chatMessage.setType("imageWeb");
        chatMessage.setState("0");
        chatMessage.setSenderId(user_id);
        chatMessage.setRecivedId(anthor_user_id);
        chatMessage.setUpload(true);
//        messageET.setText("");
//        chatMessage.setDate(String.valueOf(cal.getTimeInMillis()));
        chatMessage.setChecked(false);
//        displayMessage(chatMessage);


        InputStream iStream = null;

//            iStream = activity.getContentResolver().openInputStream(pdfFile);
//            System.out.println(pdfFile);
//            //"file:///storage/emulated/0/memo/1640514470604.3gp"
//            final byte[] inputData = FileUtil.getBytes(iStream);
        try {
            bitmap = MediaStore.Images.Media.getBitmap(BaseApp.Companion.getInstance().getContentResolver(), pdfFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
            imageBytes = baos.toByteArray();
        }


        byte[] finalImageBytes = imageBytes;
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, AllConstants.upload_image_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        System.out.println("responeeeeeeeeeeee" + new String(response.data));

//                            rQueue.getCache().clear();
                        try {
                            JSONObject jsonObject = new JSONObject(new String(response.data));

                            JSONObject sendObject = new JSONObject();
                            JSONObject notification = new JSONObject();



                            sendObject.put("sender_id", jsonObject.getString("sender_id"));
                            sendObject.put("reciver_id", jsonObject.getString("reciver_id"));
                            sendObject.put("message", jsonObject.getString("message"));
                            sendObject.put("message_type", jsonObject.getString("message_type"));
                            sendObject.put("state", jsonObject.getString("state"));
                            sendObject.put("message_id", message_id);
                            sendObject.put("chat_id", jsonObject.getInt("chat_id"));
                            sendObject.put("id", jsonObject.getInt("id"));
                            sendObject.put("deleted_for", jsonObject.getString("deleted_for"));

                            if (jsonObject.getBoolean("newchat") == true) {
                                sendObject.put("newchat", jsonObject.getBoolean("newchat"));
                            }

                            sendObject.put("orginalName", jsonObject.getString("orginalName"));
                            sendObject.put("dateTime",dataTime);
                            notification.put("token", token);
                            notification.put("my_token",  myBase.getClassSharedPreferences().getFcmToken());

                            notification.put("image", myBase.getClassSharedPreferences().getUser().getImage());
                            notification.put("title",  myBase.getClassSharedPreferences().getUser().getUserName() +" "+  myBase.getClassSharedPreferences().getUser().getLastName() );
                            notification.put("chat_id", jsonObject.getInt("chat_id"));
                            notification.put("blockedFor",blockrdFor);

                            sendObject.put("notification",notification);

                            chatMessageRepo.setMessageUpload(message_id,false);
//                            activity.newMeesage(sendObject);
                            newMeesage(sendObject);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("sender_id", user_id);
                params.put("reciver_id", anthor_user_id);
                params.put("message_type", "imageWeb");
                params.put("state", "0");
                params.put("orginalName", imageName);
                params.put("dateTime", dataTime);
                return params;
            }

            /*
             *pass files using below method
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();

                params.put("img_chat", new DataPart(imageName, finalImageBytes, "plan/text"));

                return params;
            }
        };


        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//            rQueue = Volley.newRequestQueue(activity);
//            rQueue.add(volleyMultipartRequest);
        myBase.addToRequestQueue(volleyMultipartRequest);


        return chatMessage;
    }

    ///////////////////
    public static ChatMessage uploadVideo(String pdfname, Uri pdffile, ConversationActivity activity,
                                          String user_id, String anthor_user_id, String blockedFor, String token) {
        BaseApp myBase = BaseApp.Companion.getInstance();
        String dataTime =  String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis());

//        ChatMessageRepoo chatMessageRepo = myBase.getChatMessageRepoo();
        ChatMessageRepoo chatMessageRepo = myBase.chatMessageRepoo;



        String message_id = System.currentTimeMillis() + "_" + user_id;

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessageId(message_id);//dummy
        chatMessage.setMessage(pdffile.toString());
        chatMessage.setFileName(pdfname);
        chatMessage.setUpload(true);
        chatMessage.setSenderId(user_id);
        chatMessage.setRecivedId(anthor_user_id);
        chatMessage.setDateTime(dataTime);
        chatMessage.setMe(true);
        chatMessage.setType("video");
        chatMessage.setState("0");
        chatMessage.setChecked(false);


        InputStream iStream = null;
        try {

            iStream =BaseApp.Companion.getInstance().getContentResolver().openInputStream(pdffile);
            System.out.println(pdffile);
            //"file:///storage/emulated/0/memo/1640514470604.3gp"
            final byte[] inputData = FileUtil.getBytes(iStream);

            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, AllConstants.upload_video_URL,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {

                            try {
                                JSONObject jsonObject = new JSONObject(new String(response.data));

                                JSONObject sendObject = new JSONObject();
                                JSONObject notification = new JSONObject();

                                System.out.println("responeeeeeeeee"+response);

                                sendObject.put("sender_id", jsonObject.getString("sender_id"));
                                sendObject.put("reciver_id", jsonObject.getString("reciver_id"));
                                sendObject.put("message", jsonObject.getString("message"));
                                sendObject.put("message_type", jsonObject.getString("message_type"));
                                sendObject.put("state", jsonObject.getString("state"));
                                sendObject.put("message_id", message_id);
                                sendObject.put("chat_id", jsonObject.getInt("chat_id"));
                                sendObject.put("id", jsonObject.getInt("id"));
                                sendObject.put("deleted_for", jsonObject.getString("deleted_for"));

                                if (jsonObject.getBoolean("newchat") == true) {
                                    System.out.println("newwwwwwwwwwwww chatttttttttttt");
                                    sendObject.put("newchat", jsonObject.getBoolean("newchat"));
                                }

                                sendObject.put("orginalName", jsonObject.getString("orginalName"));
                                sendObject.put("dateTime", String.valueOf(dataTime));

                                notification.put("token",token );
                                notification.put("my_token",  myBase.getClassSharedPreferences().getFcmToken());
                                notification.put("image", myBase.getClassSharedPreferences().getUser().getImage());
                                notification.put("title",  myBase.getClassSharedPreferences().getUser().getUserName() +" "+  myBase.getClassSharedPreferences().getUser().getLastName() );
                                notification.put("chat_id", jsonObject.getInt("chat_id"));
                                notification.put("blockedFor",blockedFor);

                                sendObject.put("notification",notification);

                                chatMessageRepo.setMessageUpload(message_id,false);
//                                activity.newMeesage(sendObject);
                                newMeesage(sendObject);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }) {

                /*
                 * If you want to add more parameters with the image
                 * you can do it here
                 * here we have only one parameter with the image
                 * which is tags
                 * */
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("sender_id", user_id);
                    params.put("reciver_id", anthor_user_id);
                    params.put("message_type", "video");
                    params.put("state", "0");
                    params.put("orginalName", pdfname);
                    params.put("dateTime", dataTime);
                    return params;
                }

                /*
                 *pass files using below method
                 * */
                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();

                    params.put("vedios", new DataPart(pdfname, inputData, "plan/text"));

                    return params;
                }
            };


            volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            myBase.addToRequestQueue(volleyMultipartRequest);



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    return chatMessage;
    }
    ////////////////
    public static ChatMessage uploadVoice(String voiceName, Uri voicedPath, ConversationActivity activity,
                                          String user_id, String anthor_user_id, String blockedFor, String token) {
        BaseApp myBase = BaseApp.Companion.getInstance();
        String dataTime =  String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis());

//        ChatMessageRepoo chatMessageRepo = myBase.getChatMessageRepoo();
        ChatMessageRepoo chatMessageRepo = myBase.chatMessageRepoo;

        String message_id = System.currentTimeMillis() + "_" + user_id;
        System.out.println("the message id is" + message_id);


        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessageId(message_id);//dummy
        chatMessage.setMessage(voicedPath.toString());
        chatMessage.setFileName(voiceName);

        chatMessage.setDateTime(dataTime);
        chatMessage.setMe(true);
        chatMessage.setType("voice");
        chatMessage.setState("0");
        chatMessage.setUpload(true);
        chatMessage.setChecked(false);
        chatMessage.setSenderId(user_id);
        chatMessage.setRecivedId(anthor_user_id);



        InputStream iStream = null;
        try {

            iStream = activity.getContentResolver().openInputStream(voicedPath);
            final byte[] inputData = FileUtil.getBytes(iStream);

            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, AllConstants.upload_Voice_URL,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            try {
                                JSONObject jsonObject = new JSONObject(new String(response.data));
                                JSONObject sendObject = new JSONObject();
                                JSONObject notification = new JSONObject();


                                sendObject.put("sender_id", jsonObject.getString("sender_id"));
                                sendObject.put("reciver_id", jsonObject.getString("reciver_id"));
                                sendObject.put("message", jsonObject.getString("message"));
                                sendObject.put("message_type", jsonObject.getString("message_type"));
                                sendObject.put("state", jsonObject.getString("state"));
                                sendObject.put("message_id", message_id);
                                sendObject.put("orginalName", jsonObject.getString("orginalName"));
                                sendObject.put("chat_id", jsonObject.getInt("chat_id"));
                                sendObject.put("id", jsonObject.getInt("id"));
                                sendObject.put("deleted_for", jsonObject.getString("deleted_for"));

                                if (jsonObject.getBoolean("newchat") == true) {
                                    sendObject.put("newchat", jsonObject.getBoolean("newchat"));
                                }

                                sendObject.put("dateTime",dataTime);
                                notification.put("token", token);
                                notification.put("my_token",  myBase.getClassSharedPreferences().getFcmToken());
                                notification.put("image", myBase.getClassSharedPreferences().getUser().getImage());
                                notification.put("title",  myBase.getClassSharedPreferences().getUser().getUserName() +" "+  myBase.getClassSharedPreferences().getUser().getLastName() );
                                notification.put("chat_id", jsonObject.getInt("chat_id"));
                                notification.put("blockedFor",blockedFor);

                                sendObject.put("notification",notification);


                                chatMessageRepo.setMessageUpload(message_id,false);
                                activity.newMeesage(sendObject);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
//                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }) {

                /*
                 * If you want to add more parameters with the image
                 * you can do it here
                 * here we have only one parameter with the image
                 * which is tags
                 * */
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("sender_id", user_id);
                    params.put("reciver_id", anthor_user_id);
                    params.put("message_type", "voice");
                    params.put("state", "0");
                    params.put("orginalName", voiceName);
                    params.put("dateTime", dataTime);
                    return params;
                }

                /*
                 *pass files using below method
                 * */
                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();

//                    params.put("audios", new DataPart(voiceName, inputData));
                    params.put("audios", new DataPart(voiceName, inputData, "audio/aac"));


                    return params;
                }
            };


            volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//            rQueue.add(volleyMultipartRequest);
            myBase.addToRequestQueue(volleyMultipartRequest);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return chatMessage;

    }


    ////////////////////////

    public static ChatMessage uploadPDF(String pdfname, Uri pdffile, ConversationActivity activity, String user_id,
                                        String anthor_user_id , String blockedFor, String token) {
        String message_id = System.currentTimeMillis() + "_" + user_id;
        String dataTime =  String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis());
        BaseApp myBase = BaseApp.Companion.getInstance();
//        ChatMessageRepoo chatMessageRepo = myBase.getChatMessageRepoo();
        ChatMessageRepoo chatMessageRepo = myBase.chatMessageRepoo;

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessageId(message_id);//dummy
        chatMessage.setMessage(pdffile.toString());
        chatMessage.setFileName(pdfname);
        chatMessage.setSenderId(user_id);
        chatMessage.setRecivedId(anthor_user_id);
        chatMessage.setDateTime(dataTime);


//        chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatMessage.setMe(true);
        chatMessage.setType("file");
        chatMessage.setState("0");
        chatMessage.setUpload(true);
        chatMessage.setChecked(false);


        InputStream iStream = null;
        try {
            iStream = activity.getContentResolver().openInputStream(pdffile);
            //"file:///storage/emulated/0/memo/1640514470604.3gp"
            final byte[] inputData = FileUtil.getBytes(iStream);

            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, AllConstants.upload_file_URL,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            Log.d("ressssssoo", new String(response.data));
                            System.out.println("ressssssssponeee" + new String(response.data));
                            try {
                                JSONObject jsonObject = new JSONObject(new String(response.data));



                                System.out.println(jsonObject.getString("orginalName") + pdfname);

                                JSONObject sendObject = new JSONObject();
                                JSONObject notification = new JSONObject();


                                sendObject.put("sender_id", jsonObject.getString("sender_id"));
                                sendObject.put("reciver_id", jsonObject.getString("reciver_id"));
                                sendObject.put("message", jsonObject.getString("message"));
                                sendObject.put("message_type", jsonObject.getString("message_type"));
                                sendObject.put("state", jsonObject.getString("state"));
                                sendObject.put("message_id", message_id);
                                sendObject.put("chat_id", jsonObject.getInt("chat_id"));
                                sendObject.put("id", jsonObject.getInt("id"));
                                sendObject.put("deleted_for", jsonObject.getString("deleted_for"));

                                if (jsonObject.getBoolean("newchat") == true) {
                                    sendObject.put("newchat", jsonObject.getBoolean("newchat"));
                                }


                                sendObject.put("orginalName", jsonObject.getString("orginalName"));
                                sendObject.put("dateTime", dataTime);
                                notification.put("token", token);
                                notification.put("my_token",  myBase.getClassSharedPreferences().getFcmToken());
                                notification.put("image", myBase.getClassSharedPreferences().getUser().getImage());
                                notification.put("title",  myBase.getClassSharedPreferences().getUser().getUserName() +" "+  myBase.getClassSharedPreferences().getUser().getLastName() );
                                notification.put("chat_id", jsonObject.getInt("chat_id"));
                                notification.put("blockedFor",blockedFor);
                                sendObject.put("notification",notification);


                                chatMessageRepo.setMessageUpload(message_id,false);
                                activity.newMeesage(sendObject);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
//                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }) {

                /*
                 * If you want to add more parameters with the image
                 * you can do it here
                 * here we have only one parameter with the image
                 * which is tags
                 * */
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("sender_id", user_id);
                    params.put("reciver_id", anthor_user_id);
                    params.put("message_type", "file");
                    params.put("state", "0");
                    params.put("orginalName", pdfname);
                    params.put("dateTime", dataTime);
                    return params;
                }

                /*
                 *pass files using below method
                 * */
                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();

//                    params.put("files", new DataPart(pdfname, inputData, "plan/text"));
                    params.put("files", new DataPart(pdfname, inputData, "plan/text"));


                    return params;
                }
            };


            volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//            rQueue.add(volleyMultipartRequest);
            myBase.addToRequestQueue(volleyMultipartRequest);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

      return  chatMessage;
    }




//    public static void fileUpload(String filePath) {
//
//        api apiInterface = RetrofitClient.getInstance(AllConstants.base_node_url).getapi();
////        Logger.addLogAdapter(new AndroidLogAdapter());
//
//        File file = new File(filePath);
//        //create RequestBody instance from file
//        RequestBody requestFile = RequestBody.create(MediaType.parse("image"), file);
//
//        // MultipartBody.Part is used to send also the actual file name
//        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
//
//        Gson gson = new Gson();
////        String patientData = gson.toJson(imageSenderInfo);
//
////        RequestBody description = RequestBody.create(okhttp3.MultipartBody.FORM);
//
//        // finally, execute the request
//        Call<String> call = apiInterface.fileUpload( body);
//        call.enqueue(new Callback<String>() {
//
//
//            @Override
//            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
//              System.out.println(call+"responeeeeeeeeee"+response);
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
//                System.out.println(call+"eroorrrrrrrrrrrr");
//
//            }
//        });
//    }


}