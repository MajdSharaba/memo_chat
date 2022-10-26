package com.yawar.memo.constant;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

import com.google.firebase.auth.PhoneAuthProvider;

public interface AllConstants {
    String VERIFICATION_CODE = "code";
    String IMAGE_PATH = "Media/Prof ile_Image/profile";
    int STORAGE_REQUEST_CODE = 4000;
    int USERNAME_CODE = 100;
    int CONTACTS_REQUEST_CODE = 2000;
    int RECORDING_REQUEST_CODE = 3000;
    int Read_Write_Storage_CODE = 5000;
    int READ_STORAGE_PERMISSION_REJECT = 1999;
    int RECORD_AUDIO_PERMISSION_REJECT = 1888;
    int STORAGE_PERMISSION_REJECT = 1777;
    int CAMERA_PERMISSION_REJECT = 1666;
    int LOCATION_PERMISSION_REJECT = 1555;
    int OPEN_MAP_PERMISSION_REJECT = 1448;
    int READ_CONTACT_PERMISSION_REJECT = 2020;
    int LOCATION_PERMISSION = 44;
    int OPEN_MAP_PERMISSION = 448;
    int OPEN_CAMERA_PERMISSION = 9921;
//    String ip="109";
////    public String base_url = "http://192.168.0."+ip+":8080/yawar_chat/views/";
//        public String base_url_final = "http://137.184.155.225/";
//
////    public String base_url = "http://192.168.0."+ip+":8080/yawar_chat/views/";
//
//    //        String base_url = "https://memoback1.herokuapp.com/";
////    public  String base_node_url =  "http://192.168.0."+ip+":3000/";
////        String base_node_url =  "https://memoback1.herokuapp.comm/";
//    String delet_from_archived_url = base_url_final+"deletearchive";
//    String delete_conversation = base_url_final+"deleteconversation";
//
//    String upload_firebase_URL = " https://storage.googleapis.com/memoback-ea9c4.appspot.com";
//    String upload_Voice_URL = base_url_final+"uploadAudio";
//    String upload_file_URL = base_url_final+"uploadFile";
//    String upload_video_URL = base_url_final+"uploadVedio";
//    String upload_image_URL = base_url_final+"uploadImgChat";
////    String get_media = base_node_url+"getmedia";
//    String download_url = "https://storage.googleapis.com/memoback-ea9c4.appspot.com/";
//    String socket_url = base_url_final;
//    String delete_message =base_url_final+"deletemessage2";
//    String add_to_archived_url = base_url_final+"archivechat";
//    String add_token = base_url_final+"addtoken";
//
//    String api_key_fcm_token_header_value = "Key=AAAA4428f68:APA91bGmEQZqZESoHenOQGETIbiOWS9N3r7e_BkHs1KFXi6ThD81FPkIHWp4dCsksTALD9IxaeHySy8ORraWmpjwvGh7Zls7Sc75NZQ0qTEwqIkWQZlMFdDC6OEGRNI22VfnkTu8LjP6";
//    String imageUrl="https://storage.googleapis.com/memoback-ea9c4.appspot.com/";
//
//    String imageUrlInConversation="https://storage.googleapis.com/memoback-ea9c4.appspot.com/";
//    String fcm_send_notification_url = "https://fcm.googleapis.com/fcm/send";



//    String CHANNEL_ID = "1000";
//    int onGoingCallChannelId = 0;



    String ip="109";
        public String base_url = "http://192.168.0."+ip+":8080/yawar_chat/views/";
//    String base_url = "https://memoback1.herokuapp.com/";
        public  String base_node_url =  "http://192.168.0."+ip+":3000/";
//    String base_node_url =  "https://memoback1.herokuapp.comm/";
    String delet_from_archived_url = base_node_url+"deletearchive";
    String delete_conversation = base_node_url+"deleteconversation";

    String upload_firebase_URL = " https://storage.googleapis.com/memoback-ea9c4.appspot.com";
    String upload_Voice_URL = base_node_url+"uploadAudio";
    String upload_file_URL = base_node_url+"uploadFile";
    String upload_video_URL = base_node_url+"uploadVedio";
    String upload_image_URL = base_node_url+"uploadImgChat";
    //    String get_media = base_node_url+"getmedia";
//    String download_url = "https://storage.googleapis.com/memoback-ea9c4.appspot.com/";
    String download_url = " https://storage.googleapis.com/memo-d0344.appspot.com/";


    String socket_url = base_node_url;
    String delete_message =base_node_url+"deletemessage2";
    String add_to_archived_url = base_node_url+"archivechat";
    String add_token = base_node_url+"addtoken";

    String api_key_fcm_token_header_value = "Key=AAAA4428f68:APA91bGmEQZqZESoHenOQGETIbiOWS9N3r7e_BkHs1KFXi6ThD81FPkIHWp4dCsksTALD9IxaeHySy8ORraWmpjwvGh7Zls7Sc75NZQ0qTEwqIkWQZlMFdDC6OEGRNI22VfnkTu8LjP6";
    String imageUrl="https://storage.googleapis.com/memo-d0344.appspot.com/profile_images/";

    String imageUrlInConversation="https://storage.googleapis.com/memoback-ea9c4.appspot.com/";
    String fcm_send_notification_url = "https://fcm.googleapis.com/fcm/send";



    String CHANNEL_ID = "1000";
    int onGoingCallChannelId = 0;








}

