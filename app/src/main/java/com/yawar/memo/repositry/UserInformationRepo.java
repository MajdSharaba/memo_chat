package com.yawar.memo.repositry;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yawar.memo.utils.BaseApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserInformationRepo {
    public MutableLiveData<String> blockedFor;

    ChatRoomRepo chatRoomRepo = BaseApp.getInstance().getChatRoomRepo();


    public MutableLiveData<Boolean> blocked;
    public MutableLiveData<Boolean> unBlocked;
    BaseApp myBase = BaseApp.getInstance();



    public UserInformationRepo(Application application) { //application is subclass of context


        blockedFor = new MutableLiveData<>(null);
        blocked = new MutableLiveData<>(null);
        unBlocked = new MutableLiveData<>(null);






    }
    public MutableLiveData<String> getBlockedFor() {
        return blockedFor;
    }

    public void setBlockedFor(String blockedFor) {
        this.blockedFor.setValue(blockedFor);
    }

    public MutableLiveData<Boolean> getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked.setValue(blocked);
    }

    public MutableLiveData<Boolean> getUnBlocked() {
        return unBlocked;
    }

    public void setUnBlocked(Boolean blocked) {
        this.unBlocked.setValue(blocked);
    }


    public void sendBlockRequest(String my_id, String anthor_user_id) {
        StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.0.109:3000/addtoblock", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String blokedForRespone = "";
                boolean blockedRespone = false;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    blokedForRespone = jsonObject.getString("blocked_for");
                    blockedRespone= jsonObject.getBoolean("blocked");


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                blockedFor.setValue(blokedForRespone);
                blocked.setValue(blockedRespone);
                chatRoomRepo.setBlockedState(anthor_user_id,blokedForRespone);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // progressDialog.dismiss();
//                Toast.makeText(UserInformationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                // below line we are creating a map for
                // storing our values in key and value pair.
                Map<String, String> params = new HashMap<String, String>();

                params.put("my_id", my_id);
                params.put("user_id",anthor_user_id);

                return params;
            }

        };
        myBase.addToRequestQueue(request);
    }
    public void sendUnbBlockUser(String my_id,String anthor_user_id) {


        StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.0.109:3000/deleteblock", new Response.Listener<String>() {



            @Override
            public void onResponse(String response) {


                String blokedForRespone = "";
                Boolean unBlockedRespone = false;

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    blokedForRespone = jsonObject.getString("blocked_for");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                blockedFor.setValue(blokedForRespone);
                unBlocked.setValue(unBlockedRespone);
                chatRoomRepo.setBlockedState(anthor_user_id,blokedForRespone);










            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("errorrrrrrrr"+error);

                // progressDialog.dismiss();
//                Toast.makeText(UserInformationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            protected Map<String, String> getParams() {
                // below line we are creating a map for
                // storing our values in key and value pair.
                Map<String, String> params = new HashMap<String, String>();

                // on below line we are passing our key
                // and value pair to our parameters.
                params.put("my_id", my_id);
                params.put("user_id",anthor_user_id);

                return params;
            }

        };
        myBase.addToRequestQueue(request);
    }

}
