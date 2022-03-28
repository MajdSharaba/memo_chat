package com.yawar.memo.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.yawar.memo.R;
import com.yawar.memo.adapter.GroupSelectorAdapter;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.model.ContactModel;
import com.yawar.memo.model.GroupSelectorRespone;
import com.yawar.memo.model.SendContactNumberResponse;
import com.yawar.memo.utils.BaseApp;
import com.yawar.memo.utils.Globale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class GroupSelectorActivity extends AppCompatActivity implements GroupSelectorAdapter.CallbackInterface, Observer {

    RecyclerView recyclerView;
    SearchView searchView;
    Toolbar toolbar;
    BaseApp myBase;
    ArrayList<ContactModel> arrayList = new ArrayList<ContactModel>();
    ArrayList<SendContactNumberResponse> groupSelectorRespones = new ArrayList<SendContactNumberResponse>();
    ArrayList<SendContactNumberResponse> sendContactNumberResponses = new ArrayList<SendContactNumberResponse>();

//    ArrayList<String> arrayzOfSelectorId = new ArrayList<String>();
    GroupSelectorAdapter mainAdapter;
    Globale globale;

    TextView new_group ;
    float textSize = 14.0F ;
    SharedPreferences sharedPreferences ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_selector);

        sharedPreferences = getSharedPreferences("txtFontSize", Context.MODE_PRIVATE);

//        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
//        bottomNavigationView.setSelectedItemId(R.id.calls);
        recyclerView = findViewById(R.id.recycler_view);
        toolbar = findViewById(R.id.toolbar);

        new_group = findViewById(R.id.new_group);
        new_group.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        toolbar.setTitle("Memo");
        setSupportActionBar(toolbar);
        myBase = BaseApp.getInstance();
        myBase.getContactNumberObserve().addObserver(this);
        for(SendContactNumberResponse sendContactNumberResponse:myBase.getContactNumberObserve().getContactNumberResponseList()){
            if(!sendContactNumberResponse.getState().equals("false")){
                sendContactNumberResponses.add(sendContactNumberResponse);
            }
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(GroupSelectorActivity.this));
         mainAdapter = new GroupSelectorAdapter(GroupSelectorActivity.this,sendContactNumberResponses);
        recyclerView.setAdapter(mainAdapter);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  globale.setSendContactNumberResponses(sendContactNumberResponses);
//                for (String id:
//                        arrayzOfSelectorId) {
//                    System.out.println(id);
//
//                }
               Bundle bundle = new Bundle();


                Intent intent = new Intent(GroupSelectorActivity.this, GroupPropertiesActivity.class);
                bundle.putSerializable("newPlaylist", sendContactNumberResponses);
                intent.putExtras(bundle);
                startActivity(intent);

            }

        });
//        searchView = findViewById(R.id.search_by_secret_number);
//
//        CharSequence charSequence = searchView.getQuery();
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//
//                return false;
//
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                ///  itemAdapter.filter(newText);
//                return false;
//            }
//        });






//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//
//                    case R.id.chat:
////                        openFragment(new ChatRoomFragment());
//                        Intent inten = new Intent(ContactNumberActivity.this, BasicActivity.class);
//                        inten.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                        startActivity(inten);
//
//                    case R.id.addchat:
//                        Intent intent = new Intent(ContactNumberActivity.this, ProfileActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                        startActivity(intent);
//
//
//                    case R.id.calls:
////                        Intent inten = new Intent(ContactNumberActivity.this, ContactNumberActivity.class);
////                        inten.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
////                        startActivity(inten);
//
//
//                }
//
//                return false;
//            }
//        });
        ///////////////////////////////////
        ///checkpermission();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.basic_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.group:
                Toast.makeText(getApplicationContext(),"Item 1 Selected",Toast.LENGTH_LONG).show();
                return true;
            case R.id.item2:
                Toast.makeText(getApplicationContext(),"Item 2 Selected",Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




    private void checkpermission() {
        ///check condition
        if(ContextCompat.checkSelfPermission(GroupSelectorActivity.this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(GroupSelectorActivity.this,new String[]{Manifest.permission.READ_CONTACTS},100);
        }
        else {
            getContactList();
        }
    }

    private void getContactList() {
        final String DISPLAY_NAME = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY : ContactsContract.Contacts.DISPLAY_NAME;
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
        Cursor cursor = getContentResolver().query(uri,null,null,null,sort);
        if(cursor.getCount()>0){
            while (cursor.moveToNext()){
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" =?";
                Cursor phoneCursor = getContentResolver().query(uriPhone, null ,selection , new String[]{id},null);
                if(phoneCursor.moveToNext()){
                    String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    ContactModel model = new ContactModel();
                    model.setName(name);
                    model.setNumber(number);
                    arrayList.add(model);
                }
            }
            cursor.close();
        }
        System.out.println(arrayList.size());
        sendContactNumber(arrayList);

    }

    private void sendContactNumber(ArrayList<ContactModel> arrayList) {
        String url = AllConstants.base_url+ "APIS/mycontact.php";
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading, please wait...");
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();

                // on below line we are passing our response
                // to json object to extract data from it.
                JSONObject respObj = null;
                try {
                    respObj = new JSONObject(response);
                    System.out.println(respObj);
                    JSONArray jsonArray = (JSONArray) respObj.get("data");
//                    JSONArray jsonArray = new JSONArray(respObj.getJSONArray("data"));
                    System.out.println(jsonArray);

                    for (int i = 0; i <= jsonArray.length()-1; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        System.out.println(jsonObject.getString("name"));
                        String id = jsonObject.getString("id");
                        String name = jsonObject.getString("name");
                        String number = jsonObject.getString("number");
                        String image = jsonObject.getString("image");
                        String chat_id = jsonObject.getString("chat_id");
                        String fcm_token = jsonObject.getString("user_token");
                        String state = jsonObject.getString("state");
                        if(!state.equals( "false")){
                        groupSelectorRespones.add(new SendContactNumberResponse(id,name,number,image,state,chat_id,fcm_token));}



                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(GroupSelectorActivity.this));
                   // mainAdapter = new GroupSelectorAdapter(GroupSelectorActivity.this,groupSelectorRespones);
                    recyclerView.setAdapter(mainAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
//                Toast.makeText(this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // below line we are creating a map for
                // storing our values in key and value pair.
                Map<String, String> params = new HashMap<String, String>();

                // on below line we are passing our key
                // and value pair to our parameters.
                String data = new Gson().toJson(arrayList);
                params.put("data", data);
//                params.put("email", email);
//                params.put("first_name", firstName);
//                params.put("last_name", lastName);
//                params.put("picture", imageString);

                // at last we are
                // returning our params.
                return params;
            }
        };
        // below line is to make
        // a json object request.
        queue.add(request);
//        myBase.addToRequestQueue(request);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getContactList();
        }
        else {
            Toast.makeText(GroupSelectorActivity.this, "permission Denied",Toast.LENGTH_LONG);
            checkpermission();
        }
    }

//    @Override
//    public void onHandleSelection(int position, GroupSelectorRespone groupSelectorRespone,boolean ischecked) {
//        SendContactNumberResponse  sendContactNumberResponse = new SendContactNumberResponse(groupSelectorRespone.getId(),groupSelectorRespone.getName(),groupSelectorRespone.getNumber(),groupSelectorRespone.getImage(),groupSelectorRespone.getState());
//
//
//        if(ischecked){
//
//            sendContactNumberResponses.add(sendContactNumberResponse);
//        System.out.println(groupSelectorRespone.getId());}
//        else {
//            sendContactNumberResponses.remove(sendContactNumberResponse);
//        }
//        System.out.println(sendContactNumberResponses.size());
////        for (String id:
////                arrayzOfSelectorId) {
////            System.out.println(id);
////
////        }
//    }

    @Override
    public void onHandleSelection(int position, SendContactNumberResponse sendContactNumberResponse, boolean isChecked) {
//        SendContactNumberResponse  sendContactNumber = new SendContactNumberResponse();


        if(isChecked){

            groupSelectorRespones.add(sendContactNumberResponse);}
//            System.out.println(groupSelectorRespone.getId());}
        else {
            groupSelectorRespones.remove(sendContactNumberResponse);
        }
        System.out.println(groupSelectorRespones.size());
//        for (String id:
//                arrayzOfSelectorId) {
//            System.out.println(id);
//
//        }
    }

    @Override
    public void update(Observable observable, Object o) {

    }
}
