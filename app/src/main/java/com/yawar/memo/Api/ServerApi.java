package com.yawar.memo.Api;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.yawar.memo.R;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.model.ContactModel;
import com.yawar.memo.model.SendContactNumberResponse;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.repositry.AuthRepo;
import com.yawar.memo.repositry.BlockUserRepo;
import com.yawar.memo.repositry.ChatRoomRepoo;
import com.yawar.memo.service.SocketIOService;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.utils.BaseApp;
import com.yawar.memo.views.DashBord;
import com.yawar.memo.views.IntroActivity;
import com.yawar.memo.views.RegisterActivity;
import com.yawar.memo.views.SplashScreen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerApi {
    Activity context;
    ProgressDialog progressDialog;
    BaseApp myBase =BaseApp.getInstance() ;
    ClassSharedPreferences classSharedPreferences = myBase.getClassSharedPreferences();

    boolean isArchived ;
     boolean isResponeSucess;
//    ChatRoomRepo chatRoomRepo = myBase.getChatRoomRepo();
//    BlockUserRepo blockUserRepo = myBase.getBlockUserRepo();
    AuthRepo authRepo =myBase.getAuthRepo();





    public ServerApi(Activity context) {
        this.context = context;
    }

   /// public void register(String firstName, String lastName, String email, String imageString) {
//   public void register() {
////        classSharedPreferences = new ClassSharedPreferences(context);
//        System.out.println( classSharedPreferences.getVerficationNumber()+"classSharedPreferences.getVerficationNumber()");
//        // url to post our data
//        String url = AllConstants.base_url_final+"APIS/signup.php";
//        progressDialog = new ProgressDialog(context);
//        progressDialog.setMessage(context.getResources().getString(R.string.prograss_message));
//        progressDialog.show();
//        RequestQueue queue = Volley.newRequestQueue(context);
//        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                progressDialog.dismiss();
//                System.out.println("Data added to API+"+response);
//                try {
//                    // on below line we are passing our response
//                    // to json object to extract data from it.
//                    JSONObject respObj = new JSONObject(response);
//                    JSONObject data = respObj.getJSONObject("data");
//
//                    classSharedPreferences.setSecretNumbers(data);
//                    Intent intent = new Intent(context, RegisterActivity.class);
//
//                    context.startActivity(intent);
//                    context.finish();
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new com.android.volley.Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                // method to handle errors.
//                Toast.makeText(context, "Faield to get response = " + error, Toast.LENGTH_SHORT).show();
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                // below line we are creating a map for
//                // storing our values in key and value pair.
//                Map<String, String> params = new HashMap<String, String>();
//
//
//                // on below line we are passing our key
//                // and value pair to our parameters.
//                params.put("phone", classSharedPreferences.getVerficationNumber());
//                return params;
//            }
//        };
//        // below line is to make
//        // a json object request.
//       myBase.addToRequestQueue(request);
//    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////c

    //////////////////////////////////
    public void updateProfile(String firstName, String lastName, String status, String imageString,String userId) {
//        classSharedPreferences = new ClassSharedPreferences(context);
        // url to post our data
        String url = AllConstants.base_url_final+"APIS/updateprofile.php";
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getResources().getString(R.string.prograss_message));
        progressDialog.show();


        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(context);

        // on below line we are calling a string
        // request method to post the data to our API
        // in this we are calling a post method.
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();


//            Toast.makeText(LoginOtpInformation.this, "Data added to API+"+response, Toast.LENGTH_SHORT).show();
                System.out.println("Data added to API+"+response);
                try {
                    // on below line we are passing our response
                    // to json object to extract data from it.
                    JSONObject respObj = new JSONObject(response);
                    System.out.println(respObj);
                    JSONObject data = respObj.getJSONObject("data");
                    String user_id = data.getString("id");
                    String first_name = data.getString("first_name");
                    String last_name = data.getString("last_name");
                    String email = data.getString("email");
                    String profile_image = data.getString("profile_image");
                    String secret_number = data.getString("sn");
                    String number = data.getString("phone");
                    String status= data.getString("status");

                    UserModel userModel = new UserModel(user_id,first_name,last_name,email,number,secret_number,profile_image,status);
                    classSharedPreferences.setUser(userModel);
//                    Intent intent = new Intent(context, IntroActivity.class);
//                    context.startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
                Toast.makeText(context, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // below line we are creating a map for
                // storing our values in key and value pair.
                Map<String, String> params = new HashMap<String, String>();

                // on below line we are passing our key
                // and value pair to our parameters.
                params.put("status", status);
                params.put("first_name", firstName);
                params.put("last_name", lastName);
                params.put("picture", imageString);
                params.put("id", userId);




                // at last we are
                // returning our params.
                return params;
            }
        };
        // below line is to make
        // a json object request.
        myBase.addToRequestQueue(request);
    }
    ///////////////////////////////////////
//    public void createGroup(String name,String imageString,ArrayList<String> arrayList) {
//
//        String url =AllConstants.base_url_final+ "APIS/addgroup.php";
//    ProgressDialog progressDialog = new ProgressDialog(context);
//        progressDialog.setMessage("Uploading, please wait...");
//        progressDialog.show();
//    RequestQueue queue = Volley.newRequestQueue(context);
//
//    StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
//        @Override
//        public void onResponse(String response) {
//            progressDialog.dismiss();
//
//            // on below line we are passing our response
//            // to json object to extract data from it.
//            JSONObject respObj = null;
//            try {
//                respObj = new JSONObject(response);
//                System.out.println(respObj);
//                Intent intent = new Intent(context, IntroActivity.class);
//                context.startActivity(intent);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
//    }, new com.android.volley.Response.ErrorListener() {
//        @Override
//        public void onErrorResponse(VolleyError error) {
//            // method to handle errors.
////                Toast.makeText(this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
//        }
//    }) {
//        @Override
//        protected Map<String, String> getParams() {
//            // below line we are creating a map for
//            // storing our values in key and value pair.
//            Map<String, String> params = new HashMap<String, String>();
//
//            // on below line we are passing our key
//            // and value pair to our parameters.
//            String data = new Gson().toJson(arrayList);
//            params.put("users_id", data);
//            params.put("user_id","2");
//            params.put("name",name);
////                params.put("email", email);
////                params.put("first_name", firstName);
////                params.put("last_name", lastName);
//               params.put("image", imageString);
//
//            // at last we are
//            // returning our params.
//            return params;
//        }
//    };
//    // below line is to make
//    // a json object request.
//        myBase.addToRequestQueue(request);
//}
    ////////////////////////////////////////

//    public void getContactList() {
//        ArrayList<ContactModel> arrayList = new ArrayList<ContactModel>();
//        final String DISPLAY_NAME = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
//                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY : ContactsContract.Contacts.DISPLAY_NAME;
//        Uri uri = ContactsContract.Contacts.CONTENT_URI;
//        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
//        Cursor cursor = context.getContentResolver().query(uri,null,null,null,sort);
//        if(cursor.getCount()>0){
//            while (cursor.moveToNext()){
//                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
//                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
//                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
//                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" =?";
//                Cursor phoneCursor = context.getContentResolver().query(uriPhone, null ,selection , new String[]{id},null);
//                if(phoneCursor.moveToNext()){
//                    @SuppressLint("Range") String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                    System.out.println(number+"this is the number");
//                    ContactModel model = new ContactModel();
//                    model.setName(name);
//                    model.setNumber(number);
//                    arrayList.add(model);
//                }
//            }
//            cursor.close();
//        }
//        System.out.println(arrayList.size());
//        sendContactNumber(arrayList);
//
//    }



    //block user
    ///////////////////////////////
//    public void block(String my_id,UserModel userModel) {
//        System.out.println("block User");
//
//
////        classSharedPreferences = new ClassSharedPreferences(context);
//        BlockUserRepo blockUserRepo = myBase.getBlockUserRepo();
//
//
//        ProgressDialog progressDialog = new ProgressDialog(context);
//        progressDialog.setMessage(context.getResources().getString(R.string.prograss_message));
//        progressDialog.show();
//
//
//
//        StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.0.109:3000/addtoblock", new Response.Listener<String>() {
//
//
//
//            @Override
//            public void onResponse(String response) {
//
//
//                progressDialog.dismiss();
//                System.out.println(response+"respone block");
//                String blokedFor = "";
//                boolean blocked =false;
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//                    blokedFor = jsonObject.getString("blocked_for");
//                    blocked= jsonObject.getBoolean("blocked");
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                userModel.setStatus(blokedFor);
////                blockUserRepo.addBlockUser(blokedFor);
//                blockUserRepo.addBlockUser(userModel);
//                Intent service = new Intent(context, SocketIOService.class);
//                JSONObject userBlocked = new JSONObject();
//                JSONObject item = new JSONObject();
//
//                try {
//                    item.put("blocked_for",blokedFor);
//                    item.put("Block",blocked);
//                    userBlocked.put("my_id", my_id);
//                    userBlocked.put("user_id",userModel.getUserId() );
//                    userBlocked.put("blocked_for",blokedFor);
////"item :blockedFor,Block"
//                    userBlocked.put("userDoBlockName",classSharedPreferences.getUser().getUserName());
//                    userBlocked.put("userDoBlockSpecialNumber",classSharedPreferences.getUser().getSecretNumber());
//                    userBlocked.put("userDoBlockImage",classSharedPreferences.getUser().getImage());
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                service.putExtra(SocketIOService.EXTRA_BLOCK_PARAMTERS, userBlocked.toString());
//                service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_BLOCK);
//                context.startService(service);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                // progressDialog.dismiss();
////                Toast.makeText(UserInformationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }) {
//            protected Map<String, String> getParams() {
//                // below line we are creating a map for
//                // storing our values in key and value pair.
//                Map<String, String> params = new HashMap<String, String>();
//
//                // on below line we are passing our key
//                // and value pair to our parameters.
//                params.put("my_id", my_id);
//                params.put("user_id",userModel.getUserId());
////                params.put("email", email);
////                params.put("first_name", firstName);
////                params.put("last_name", lastName);
////                params.put("picture", imageString);
//
//                // at last we are
//                // returning our params.
//                return params;
//            }
//
//        };
//        myBase.addToRequestQueue(request);
//    }
    //unBlock user
    ///////////////////////////////
    public void deleteAccount() {
        ChatRoomRepoo chatRoomRepoo = myBase.getChatRoomRepoo();
//        classSharedPreferences = new ClassSharedPreferences(context);
        BlockUserRepo blockUserRepo = myBase.getBlockUserRepo();

        final ProgressDialog progressDialo = new ProgressDialog(context);
        // url to post our data
        progressDialo.setMessage(context.getResources().getString(R.string.prograss_message));
        progressDialo.show();
        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(context);
        // on below line we are calling a string
        // request method to post the data to our API
        // in this we are calling a post method.
        StringRequest request = new StringRequest(Request.Method.POST, AllConstants.base_url_final+"APIS/delete_my_account.php", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialo.dismiss();
                System.out.println("Data respone+"+response);
                boolean data = false;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    data = jsonObject.getBoolean("data");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(data){
                    classSharedPreferences.setUser(null);
                    classSharedPreferences.setVerficationNumber(null);
                    authRepo.setjsonObjectMutableLiveData(null);
                    chatRoomRepoo.setChatRoomListMutableLiveData(null);
                    blockUserRepo.setUserBlockListMutableLiveData(null);

                Intent intent = new Intent(context, SplashScreen.class);
                context.startActivity(intent);
                    context.finish();
                }


            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
//                Toast.makeText(getContext(), "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // below line we are creating a map for
                // storing our values in key and value pair.
                Map<String, String> params = new HashMap<String, String>();

                // on below line we are passing our key
                // and value pair to our parameters.
                params.put("sn",classSharedPreferences.getUser().getSecretNumber() );
                params.put("user_id",classSharedPreferences.getUser().getUserId());

                // at last we are
                // returning our params.
                return params;
            }
        };
        // below line is to make
        // a json object request.
        queue.add(request);
    }
    public void sendNotification(String message, String type,String fcmToken, String chat_id, String blockedFor) {
//        classSharedPreferences = new ClassSharedPreferences(context);


        try {
//            RequestQueue queue = Volley.newRequestQueue(ConversationActivity.this);



            JSONObject data = new JSONObject();
            JSONObject android = new JSONObject();
            android.put("priority", "high");
            data.put("title", classSharedPreferences.getUser().getUserName()+" "+ classSharedPreferences.getUser().getLastName());
            data.put("body", message);
            data.put("image", classSharedPreferences.getUser().getImage());
            data.put("chat_id", chat_id);
            data.put("sender_id", classSharedPreferences.getUser().getUserId());
            data.put("fcm_token", classSharedPreferences.getFcmToken());
            data.put("special", classSharedPreferences.getUser().getSecretNumber());
            data.put("blockedFor", blockedFor);




            data.put("type", type);

            JSONObject notification_data = new JSONObject();
            notification_data.put("data", data);
            notification_data.put("to", fcmToken);
            notification_data.put("content_available", true);
            notification_data.put("android", android);


            JsonObjectRequest request = new JsonObjectRequest(AllConstants.fcm_send_notification_url, notification_data, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println("responeeeeeeeeeeeeeeeeeeeeeeeeee" + message + fcmToken);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", AllConstants.api_key_fcm_token_header_value);
                    return headers;
                }
            };

//      queue.add(request);
            myBase.addToRequestQueue(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }





}
