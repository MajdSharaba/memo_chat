package com.yawar.memo.views;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;

import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.Api.ServerApi;
import com.yawar.memo.R;
import com.yawar.memo.adapter.BlockUserAdapter;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.modelView.BlockedActViewModel;
import com.yawar.memo.repositry.ChatRoomRepo;
import com.yawar.memo.utils.BaseApp;

import java.util.ArrayList;

public class BlockedUsersActivity extends AppCompatActivity implements BlockUserAdapter.CallbackInterface {
    ClassSharedPreferences classSharedPreferences ;
    BaseApp myBase;
    UserModel userModel;
    RecyclerView recyclerView;
    BlockedActViewModel blockedActViewModel;
    BlockUserAdapter blockUserAdapter;
    ChatRoomRepo chatRoomRepo;
    ServerApi serverApi;
    ArrayList<UserModel> userBlockeds = new ArrayList<UserModel>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_users);
        getSupportActionBar().setTitle(R.string.contact_number_blocked);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null)
            supportActionBar.setDisplayHomeAsUpEnabled(true);

        classSharedPreferences = new ClassSharedPreferences(this);
        myBase=BaseApp.getInstance();
        blockedActViewModel = new ViewModelProvider(this).get(BlockedActViewModel.class);

        chatRoomRepo = myBase.getChatRoomRepo();
        userModel = classSharedPreferences.getUser();
        serverApi = new ServerApi(this);
        blockedActViewModel.loadData().observe(this, new androidx.lifecycle.Observer<ArrayList<UserModel>>() {
            @Override
            public void onChanged(ArrayList<UserModel> userModels) {
                if(userModels!=null){
                    userBlockeds.clear();
                    for(UserModel user:userModels) {
                        System.out.println("this"+userModel.getUserId()+"+");
                            if(user.getStatus().equals(userModel.getUserId())||user.getStatus().equals("0")){

                                userBlockeds.add(user);}

                    }
                    blockUserAdapter.updateList((ArrayList<UserModel>) userBlockeds);

                }
                //adapter.notifyDataSetChanged();

            }
        });
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(BlockedUsersActivity.this));
        blockUserAdapter = new BlockUserAdapter(BlockedUsersActivity.this,userBlockeds);
        recyclerView.setAdapter(blockUserAdapter);

//        getUsersBlocked();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }











//    public void getUsersBlocked() {
//
//
//        ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage(getResources().getString(R.string.prograss_message));
//        progressDialog.show();
//
//
//
//        StringRequest request = new StringRequest(Request.Method.POST,  "http://192.168.1.7:3000/myblocklist" , new Response.Listener<String>() {
//
//
//            @Override
//            public void onResponse(String response) {
//
//
//                progressDialog.dismiss();
//
//                try {
//                    System.out.println(response+"responeeeeeee");
////                    JSONObject respObj = new JSONObject(response);
////                    System.out.println(respObj + "");
//                    JSONArray jsonArray = new JSONArray(response);
//////                    JSONArray jsonArray = new JSONArray(respObj.getJSONArray("data"));
////                    System.out.println(jsonArray);
////                    postList.clear();
////
//                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
//                        JSONObject jsonObject = jsonArray.getJSONObject(i);
//                        //System.out.println(jsonObject.getString("last_message"));
//                        String image = jsonObject.getString("profile_image");
//
//                        String special_number = jsonObject.getString("sn");
//                       String fName = jsonObject.getString("first_name");
//                        String lName = jsonObject.getString("last_name");
//                        String phone = jsonObject.getString("phone");
//                        String userId = jsonObject.getString("id");
//                        String email = jsonObject.getString("email");
//
//                        userBlockeds.add(new UserModel(userId,fName,lName,email,phone,special_number,image,"2"));
//                    }
//
//
//                    blockUserAdapter.notifyDataSetChanged();
//
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    progressDialog.dismiss();
//                }
////                if(isArchived){
////                    linerArchived.setVisibility(View.VISIBLE);
////                }
//                ///itemAdapter = new ChatRoomAdapter(postList, getApplicationContext(), listener);
////                itemAdapter = new ChatRoomAdapter(postList, BasicActivity.this);
////////                itemAdapter=new ChatRoomAdapter(getApplicationContext(),postList);
////                recyclerView.setAdapter(itemAdapter);
////                itemAdapter.notifyDataSetChanged();
////                Toast.makeText(BasicActivity.this, "Success", Toast.LENGTH_SHORT).show();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                progressDialog.dismiss();
////                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                // below line we are creating a map for
//                // storing our values in key and value pair.
//                Map<String, String> params = new HashMap<String, String>();
//                // on below line we are passing our key
//                // and value pair to our parameters.
//                params.put("my_id",userModel.getUserId());
//                System.out.println(params);
//                // at last we are
//                // returning our params.
//                return params;
//            }
//
//        };
//        myBase.addToRequestQueue(request);
//    }

    @Override
    public void onHandleSelection(int position, UserModel blockUser) {
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
//        dialog.setMessage(getString(R.string.alert_delete_message));
        dialog.setTitle(R.string.alert_unblock_user);
        dialog.setPositiveButton(R.string.Unblock,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {

                        serverApi.unbBlockUser(userModel.getUserId(),blockUser);
                    }
                });
        dialog.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog=dialog.create();
        alertDialog.show();
    }



}
