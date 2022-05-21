package com.yawar.memo.constant;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

import com.google.firebase.auth.PhoneAuthProvider;
import com.yawar.memo.Api.ClassSharedPreferences;

public interface AllConstants {
    String VERIFICATION_CODE = "code";
    String IMAGE_PATH = "Media/Prof ile_Image/profile";
    int STORAGE_REQUEST_CODE = 4000;
    int USERNAME_CODE = 100;
    int CONTACTS_REQUEST_CODE = 2000;
    int RECORDING_REQUEST_CODE = 3000;
    int Read_Write_Storage_CODE = 5000;
    String ip="107";
//    public String base_url = "http://192.168.0."+ip+":8080/yawar_chat/views/";
     public String base_url = "https://memoback.herokuapp.com/";
//    String delet_from_archived_url = "http://192.168.0."+ip+":3000/deletearchive";
//    String delete_conversation = "http://192.168.0."+ip+":3000/deleteconversation";
        String delete_conversation = base_url+"deleteconversation";
String delet_from_archived_url = base_url+"deletearchive";



    public String upload_firebase_URL = " https://storage.googleapis.com/memoback-ea9c4.appspot.com";

//    public String upload_Voice_URL = "http://192.168.0."+ip+":3000/uploadAudio";
//    public String upload_file_URL = "http://192.168.0."+ip+":3000/uploadFile";
//    public String upload_video_URL = "http://192.168.0."+ip+":3000/uploadVedio";
//    public String upload_image_URL = "http://192.168.0."+ip+":3000/uploadImgChat";
//    public String get_media = "http://192.168.0."+ip+":3000/getmedia";
//  String delet_from_archived_url = "https://memoback.herokuapp.com/deletearchive";
//    String delete_conversation = "https://memoback.herokuapp.com/deleteconversation";
    public String upload_Voice_URL = "https://memoback.herokuapp.com/uploadAudio";
    public String upload_file_URL = "https://memoback.herokuapp.com/uploadFile";
    public String upload_video_URL = "https://memoback.herokuapp.com/uploadVedio";
    public String upload_image_URL = "https://memoback.herokuapp.com/uploadImgChat";
    public String get_media = "https://memoback.herokuapp.com/getmedia";

//    public String upload = "http://192.168.1."+ip+":3000/createStore";

//    public String download_url = "http://192.168.1."+ip+":8080/yawar_chat/uploads/";
        public String download_url = "https://storage.googleapis.com/memoback-ea9c4.appspot.com/";


//        public  String base_node_url =  "http://192.168.0."+ip+":3000/";
            public  String base_node_url =  "https://memoback.herokuapp.com/";



//    public String load_chat_message =  "http://192.168.0."+ip+":3000/messagesbyusers";
        public String load_chat_message =  "https://memoback.herokuapp.com/messagesbyusers";


//   public  String socket_url = "http://192.168.0."+ip+":3000";

    public  String socket_url = "https://memoback.herokuapp.com";


//    public  String delete_message ="http://192.168.0."+ip+":3000/deletemessage2";
//   public    String add_to_archived_url = "http://192.168.0."+ip+":3000/archivechat";
public  String delete_message =base_url+"deletemessage2";
    public    String add_to_archived_url = base_url+"archivechat";
   public  String get_my_story_url = "http://192.168.0."+ip+":3000/getMyStore";
    public  String delet_my_story_url = "http://192.168.0."+ip+":3000/deleteMyStore";
//    public String add_token = "http://192.168.0."+ip+":3000/addtoken";
    public String add_token = base_url+"addtoken";

    public String api_key_fcm_token_header_value = "Key=AAAA4428f68:APA91bGmEQZqZESoHenOQGETIbiOWS9N3r7e_BkHs1KFXi6ThD81FPkIHWp4dCsksTALD9IxaeHySy8ORraWmpjwvGh7Zls7Sc75NZQ0qTEwqIkWQZlMFdDC6OEGRNI22VfnkTu8LjP6";
//    public  String imageUrl="http://192.168.1."+ip+":8080/yawar_chat/uploads/profile/";
public  String imageUrl="https://storage.googleapis.com/memoback-ea9c4.appspot.com/";

//        public  String imageUrlInConversation="http://192.168.1."+ip+":8080/yawar_chat/uploads/chatimage/";
public  String imageUrlInConversation="https://storage.googleapis.com/memoback-ea9c4.appspot.com/";
    public  String fcm_send_notification_url = "https://fcm.googleapis.com/fcm/send";



    String CHANNEL_ID = "1000";


}
