package com.yawar.memo.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;
import com.tsuryo.swipeablerv.SwipeableRecyclerView;
import com.yawar.memo.Api.ClassSharedPreferences;
import com.yawar.memo.Api.ServerApi;
import com.yawar.memo.R;
import com.yawar.memo.adapter.ArchivedAdapter;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.modelView.ArchivedActViewModel;
import com.yawar.memo.modelView.ChatRoomViewModel;
import com.yawar.memo.repositry.ChatRoomRepo;
import com.yawar.memo.utils.Globale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import com.yawar.memo.utils.BaseApp;

public class ArchivedActivity extends AppCompatActivity implements ArchivedAdapter.CallbackInterfac{

    SwipeableRecyclerView recyclerView;
    List<ChatRoomModel> data;
    List<ChatRoomModel> archived = new ArrayList<>();

    ArchivedAdapter itemAdapter;
    SearchView searchView;
    Toolbar toolbar;
    ArchivedActViewModel archivedActViewModel;
    ChatRoomRepo chatRoomRepo;
    ClassSharedPreferences classSharedPreferences;
    ServerApi serverApi;
    UserModel userModel;
    Globale globale;
    ImageButton iBAddArchived;
    String myId;
    BaseApp myBase;

    SharedPreferences sharedPreferences ;
    TextView archive ;
//    float textSize = 16.0F ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
     
        setContentView(R.layout.activity_archived);

//        sharedPreferences =  getSharedPreferences("txtFontSize", Context.MODE_PRIVATE);


        toolbar = findViewById(R.id.toolbar);
//        toolbar.setTitle("Memo");
//        setSupportActionBar(toolbar);
        recyclerView =  findViewById(R.id.recycler_view);

        archive =  findViewById(R.id.archived);
//        archive.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        globale = new Globale();
        classSharedPreferences= new ClassSharedPreferences(this);
        myId = classSharedPreferences.getUser().getUserId();
        archivedActViewModel = new ViewModelProvider(this).get(ArchivedActViewModel.class);
        myBase = (BaseApp) getApplication();
        chatRoomRepo = myBase.getChatRoomRepo();

//        myBase.getObserver().addObserver(this);
        recyclerView.setHasFixedSize(true);
//        recyclerView.setAdapter(itemAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        archivedActViewModel.loadData().observe(this, new androidx.lifecycle.Observer<ArrayList<ChatRoomModel>>() {
            @Override
            public void onChanged(ArrayList<ChatRoomModel> chatRoomModels) {
                if(chatRoomModels!=null){
                    archived.clear();
                    for(ChatRoomModel chatRoomModel:chatRoomModels) {
                        if (chatRoomModel.getState().equals("0")||chatRoomModel.getState().equals(myId)) {
//                            System.out.println(chatRoomModel.getState() + "statttttttttttttttttttttte");
                            archived.add(chatRoomModel);
                        }
                    }
                    itemAdapter.updateList((ArrayList<ChatRoomModel>) archived);
                if(archived.size()<1){
                  chatRoomRepo.setArchived(false);
                }
                }
                //adapter.notifyDataSetChanged();

            }
        });
        itemAdapter = new ArchivedAdapter(archived,this);
        recyclerView.setAdapter(itemAdapter);
        recyclerView.setListener(new SwipeLeftRightCallback.Listener() {
            @Override
            public void onSwipedLeft(int position) {
             System.out.println(position);
            }

            @Override
            public void onSwipedRight(int position) {
                removeFromArchived(archived.get(position));
//                if(archived.size()<1){
//                    myBase.getObserver().setArchived(false);
//                }
//                itemAdapter.notifyDataSetChanged();
            }
        });
       // GetData();

//        itemAdapter.notifyDataSetChanged();
        searchView = findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                itemAdapter.filter(newText);
                return false;
            }
        });


    }

//    private void GetData() {
////        userModel = classSharedPreferences.getUser();
////        System.out.println(userModel.getUserId());
//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Loading...");
//        // progressDialog.show();
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        StringRequest request = new StringRequest(Request.Method.GET, AllConstants.base_url+"APIS/my_archive_chat.php?user_id="+myId, new Response.Listener<String>() {
//
//
//            @Override
//            public void onResponse(String response) {
////                progressDialog.dismiss();
//                try {
//                    JSONObject respObj = new JSONObject(response);
//                    System.out.println(respObj);
//                    JSONArray jsonArray = (JSONArray) respObj.get("data");
////                    JSONArray jsonArray = new JSONArray(respObj.getJSONArray("data"));
//                    System.out.println(jsonArray);
//
//                    for (int i = 0; i <= jsonArray.length()-1; i++) {
//                        JSONObject jsonObject = jsonArray.getJSONObject(i);
//                        System.out.println(jsonObject.getString("last_message"));
//                        String image =  jsonObject.getString("image");
////                        String imageUrl="";
////                        if(!image.isEmpty()){
////                            imageUrl = globale.base_url+"/uploads/profile/"+image;
////                        }
////                        else{
////                            imageUrl = "https://v5p7y9k6.stackpathcdn.com/wp-content/uploads/2018/03/11.jpg";
////                        }
//
//                        archived.add(new ChatRoomModel(
//                                jsonObject.getString("username"),
//                                jsonObject.getString("sender_id"),
//                                jsonObject.getString("reciver_id"),
//                                jsonObject.getString("last_message"),
//                                image,
//                                false,
//                                 "0",
//                                  "0",
//                                    "9",
//
//                            "0",
//                                false,
//                                jsonObject.getString("user_token")
//
//
////                                "https://th.bing.com/th/id/OIP.2s7VxdmHEoDKji3gO_i-5QHaHa?pid=ImgDet&rs=1"
//
//                        ));
//                        System.out.println(AllConstants.base_url+"uploads/profile/"+jsonObject.getString("image"));
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    progressDialog.dismiss();
//                }
//                ///itemAdapter = new ChatRoomAdapter(postList, getApplicationContext(), listener);
//                itemAdapter = new ArchivedAdapter(archived, ArchivedActivity.this);
//////                itemAdapter=new ChatRoomAdapter(getApplicationContext(),postList);
//                recyclerView.setAdapter(itemAdapter);
//                itemAdapter.notifyDataSetChanged();
//                Toast.makeText(ArchivedActivity.this, "Success", Toast.LENGTH_SHORT).show();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                progressDialog.dismiss();
//                Toast.makeText(ArchivedActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }) {
//
//        };
//        requestQueue.add(request);
//    }

    @Override
    public void onHandleSelection(int position, ChatRoomModel chatRoomModel) {
        Toast.makeText(this, "Position " + chatRoomModel.lastMessage, Toast.LENGTH_SHORT).show();
        System.out.println(chatRoomModel.name);
        Bundle bundle = new Bundle();
        bundle.putString("reciver_id",chatRoomModel.userId);

        bundle.putString("sender_id", myId);
        bundle.putString("fcm_token",chatRoomModel.fcmToken );

//        bundle.putString("reciver_id",chatRoomModel.reciverId);
        bundle.putString("name",chatRoomModel.name);
        bundle.putString("image",chatRoomModel.getImage());
        bundle.putString("chat_id",chatRoomModel.getChatId());
        bundle.putString("special", chatRoomModel.getSpecialNumber());


//        bundle.putString("sender_id", myId);
//        bundle.putString("reciver_id",chatRoomModel.userId);
//        bundle.putString("name",chatRoomModel.name);
//        bundle.putString("image",chatRoomModel.getImage());


        Intent intent = new Intent(this, ConversationActivity.class);
        intent.putExtras(bundle);

        startActivity(intent);

    }
    private void removeFromArchived(ChatRoomModel chatRoomModel) {
        System.out.println(chatRoomModel.lastMessage);
        final ProgressDialog progressDialo = new ProgressDialog(this);
        // url to post our data

        progressDialo.setMessage(getResources().getString(R.string.prograss_message));
        progressDialo.show();
        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(this);
        // on below line we are calling a string
        // request method to post the data to our API
        // in this we are calling a post method.
        StringRequest request = new StringRequest(Request.Method.POST, AllConstants.delet_from_archived_url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialo.dismiss();
                System.out.println("Data added to API+"+response);
              chatRoomRepo.setState(chatRoomModel.chatId,"null");
//                archived.remove(chatRoomModel);
//                itemAdapter.notifyDataSetChanged();
//                if(archived.size()<1){
//                    myBase.getObserver().setArchived(false);
//                }



            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialo.dismiss();

                // method to handle errors.
//                Toast.makeText(ArchivedActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // below line we are creating a map for
                // storing our values in key and value pair.
                Map<String, String> params = new HashMap<String, String>();

                // on below line we are passing our key
                // and value pair to our parameters.
                params.put("my_id",myId );
                params.put("your_id", chatRoomModel.userId);

                // at last we are
                // returning our params.
                return params;
            }
        };
        // below line is to make
        // a json object request.
//        queue.add(request);
        myBase.addToRequestQueue(request);
    }

//    @Override
//    public void update(Observable observable, Object o) {
////        itemAdapter = new ArchivedAdapter(archived,this);
////        recyclerView.setAdapter(itemAdapter);
//
////        archived.clear();
////        for(ChatRoomModel chatRoomModel:myBase.getObserver().getChatRoomModelList()){
////            if(chatRoomModel.getState().equals("1"))
////                System.out.println(chatRoomModel.lastMessage);
////                archived.add(chatRoomModel);}
////
//    }
}