package com.yawar.memo.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.yawar.memo.Api.ClassSharedPreferences;
import com.yawar.memo.R;
import com.yawar.memo.adapter.MediaAdapter;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.model.ContactModel;
import com.yawar.memo.model.MediaModel;
import com.yawar.memo.model.SendContactNumberResponse;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.utils.BaseApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInformationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<MediaModel> recyclerDataArrayList = new ArrayList<>();
    CircleImageView circleImageView;
    TextView txtUserName;
    TextView txtSpecialNumber;
    TextView call;
    TextView video;
    TextView message;
    TextView mute;
    TextView more;
    TextView special_number;
    TextView media;
    String userName;
    String specialNumber;
    String chatId;
    String another_user_id;
    String my_id;
    String fcm_token;
    String imageUrl;
    ClassSharedPreferences classSharedPreferences;
    BaseApp myBase;
    MediaAdapter adapter;
    ImageView imgBtnMessage;

    float textSize = 14.0F ;
    SharedPreferences sharedPreferences ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_information);
        
        recyclerView = findViewById(R.id.idCourseRV);

        sharedPreferences =  getSharedPreferences("txtFontSize", Context.MODE_PRIVATE);

         adapter = new MediaAdapter(recyclerDataArrayList, this);
//        recyclerDataArrayList.add(new MediaModel(R.drawable.th));
//        recyclerDataArrayList.add(new MediaModel(R.drawable.th));
//        recyclerDataArrayList.add(new MediaModel(R.drawable.th));
//        recyclerDataArrayList.add(new MediaModel(R.drawable.th));
//        recyclerDataArrayList.add(new MediaModel(R.drawable.th));

        // setting grid layout manager to implement grid view.
        // in this method '2' represents number of columns to be displayed in grid view.
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,  LinearLayoutManager.HORIZONTAL,false);

        // at last set adapter to recycler view.
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        initViews();
        initAction();
    }



    private void initViews() {
        classSharedPreferences = new ClassSharedPreferences(this);
        myBase = BaseApp.getInstance();
        Bundle bundle = getIntent().getExtras();
       userName = bundle.getString("name", "Default");
       specialNumber = bundle.getString("special", "Default");
       chatId = bundle.getString("chat_id", "Default");
       another_user_id = bundle.getString("user_id", "Default");
        fcm_token = bundle.getString("fcm_token", "Default");
        imageUrl = bundle.getString("image", "Default");
        my_id = classSharedPreferences.getUser().getUserId();

        
        circleImageView = findViewById(R.id.imageView);
        txtUserName = findViewById(R.id.txt_user_name);
        txtUserName.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        txtSpecialNumber = findViewById(R.id.txt_special_number);
        txtSpecialNumber.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        call = findViewById(R.id.call);
        call.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        video = findViewById(R.id.video);
        video.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        message = findViewById(R.id.message);
        message.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        mute = findViewById(R.id.mute);
        mute.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));


        more = findViewById(R.id.more);
        more.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        special_number = findViewById(R.id.special_number);
        special_number.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        media = findViewById(R.id.media);
        media.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));



        if(!imageUrl.isEmpty()){
            Glide.with(circleImageView.getContext()).load(AllConstants.imageUrl+imageUrl).into(circleImageView);}
       txtUserName.setText(userName);
        txtSpecialNumber.setText(specialNumber);
        imgBtnMessage =  findViewById(R.id.ib_message);
        getMedia();



    }
    private void initAction() {
        imgBtnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();


                bundle.putString("reciver_id",another_user_id);

                bundle.putString("sender_id", my_id);
                bundle.putString("fcm_token",fcm_token );

//        bundle.putString("reciver_id",chatRoomModel.reciverId);
                bundle.putString("name",userName);
                bundle.putString("image",imageUrl);
                bundle.putString("chat_id",chatId);


                Intent intent = new Intent(UserInformationActivity.this, ConversationActivity.class);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });
    }

    public void getMedia() {

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait...");
        progressDialog.show();



        StringRequest request = new StringRequest(Request.Method.POST, AllConstants.get_media, new Response.Listener<String>() {



            @Override
            public void onResponse(String response) {


                progressDialog.dismiss();

                try {
                    System.out.println(response.toString());
                    JSONArray jsonArray = new JSONArray(response);
//                    System.out.println(respObj + "");
//                    JSONArray jsonArray = (JSONArray) respObj.get();
//                    JSONArray jsonArray = new JSONArray(respObj.getJSONArray("data"));
                    System.out.println(jsonArray);

                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        //System.out.println(jsonObject.getString("last_message"));
                        String image = jsonObject.getString("message");
                        recyclerDataArrayList.add(new MediaModel(image));

//                        System.out.println(AllConstants.base_url + "uploads/profile/" + jsonObject.getString("image"));
                    }
                    adapter.notifyDataSetChanged();


//                    Intent intent = new Intent(IntroActivity.this, DashBord.class);
//
//                    startActivity(intent);
//                    IntroActivity.this.finish();


//                    else {
//                        linerArchived.setVisibility(View.GONE);
//
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }

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

                // on below line we are passing our key
                // and value pair to our parameters.
                params.put("sender_id", my_id);
                params.put("reciver_id",another_user_id);
//                params.put("email", email);
//                params.put("first_name", firstName);
//                params.put("last_name", lastName);
//                params.put("picture", imageString);

                // at last we are
                // returning our params.
                return params;
            }

        };
        myBase.addToRequestQueue(request);
    }


}
