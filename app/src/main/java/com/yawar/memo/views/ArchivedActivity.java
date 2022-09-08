package com.yawar.memo.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;
import com.tsuryo.swipeablerv.SwipeableRecyclerView;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.Api.ServerApi;
import com.yawar.memo.R;
import com.yawar.memo.adapter.ArchivedAdapter;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.modelView.ArchivedActViewModel;
import com.yawar.memo.repositry.ChatRoomRepo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yawar.memo.utils.BaseApp;

public class ArchivedActivity extends AppCompatActivity implements ArchivedAdapter.CallbackInterfac{

    SwipeableRecyclerView recyclerView;
    List<ChatRoomModel> data;
    LinearLayout linear_no_archived;
    List<ChatRoomModel> archived = new ArrayList<>();

    ArchivedAdapter itemAdapter;
    SearchView searchView;
    Toolbar toolbar;
    ArchivedActViewModel archivedActViewModel;
    ChatRoomRepo chatRoomRepo;
    ClassSharedPreferences classSharedPreferences;
    ServerApi serverApi;
    UserModel userModel;
    ImageButton iBAddArchived;
    String myId;
    BaseApp myBase;

    SharedPreferences sharedPreferences ;
    TextView archive ;
//    float textSize = 16.0F ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archived);
        toolbar = findViewById(R.id.toolbar);

        recyclerView =  findViewById(R.id.recycler_view);
        linear_no_archived = findViewById(R.id.liner_no_chat_history);

        archive =  findViewById(R.id.archived);
//        archive.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

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
                    ArrayList<ChatRoomModel> list = new ArrayList<>();

                    archived.clear();
                    for(ChatRoomModel chatRoomModel:chatRoomModels) {
                        if(!(chatRoomModel.getState() ==null)) {
                            if (chatRoomModel.getState().equals("0") || chatRoomModel.getState().equals(myId)) {
                                list.add(chatRoomModel.clone());
                                archived.add(chatRoomModel);
                            }
                        }
                    }
                    if(archived.isEmpty()){
                        linear_no_archived.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        archivedActViewModel.setArchived(false);

                    }
                    else {
                        linear_no_archived.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);

                        itemAdapter.setData((ArrayList<ChatRoomModel>) list);
                    }

                }
                //adapter.notifyDataSetChanged();

            }
        });
        itemAdapter = new ArchivedAdapter(this);
        recyclerView.setAdapter(itemAdapter);
        recyclerView.setListener(new SwipeLeftRightCallback.Listener() {
            @Override
            public void onSwipedLeft(int position) {
             System.out.println(position);
            }

            @Override
            public void onSwipedRight(int position) {
                archivedActViewModel.removeFromArchived(myId,archived.get(position).getOther_id());
//                removeFromArchived(archived.get(position));
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
                itemAdapter.getFilter().filter(newText);
                return false;
            }
        });


    }


    @Override
    public void onHandleSelection(int position, ChatRoomModel chatRoomModel) {
        Toast.makeText(this, "Position " + chatRoomModel.getLast_message(), Toast.LENGTH_SHORT).show();
        System.out.println(chatRoomModel.getUsername());
        Bundle bundle = new Bundle();
        bundle.putString("reciver_id",chatRoomModel.getOther_id());

        bundle.putString("sender_id", myId);
        bundle.putString("fcm_token",chatRoomModel.getUser_token() );

//        bundle.putString("reciver_id",chatRoomModel.reciverId);
        bundle.putString("name",chatRoomModel.getUsername());
        bundle.putString("image",chatRoomModel.getImage());
        bundle.putString("chat_id",chatRoomModel.getId());
        bundle.putString("special", chatRoomModel.getSn());
        bundle.putString("blockedFor",chatRoomModel.blocked_for);



//        bundle.putString("sender_id", myId);
//        bundle.putString("reciver_id",chatRoomModel.userId);
//        bundle.putString("name",chatRoomModel.name);
//        bundle.putString("image",chatRoomModel.getImage());


        Intent intent = new Intent(this, ConversationActivity.class);
        intent.putExtras(bundle);

        startActivity(intent);

    }
    private void removeFromArchived(ChatRoomModel chatRoomModel) {
        System.out.println(chatRoomModel.getLast_message());
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
              chatRoomRepo.setState(chatRoomModel.getId(),"null");
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
                params.put("your_id", chatRoomModel.getOther_id());

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