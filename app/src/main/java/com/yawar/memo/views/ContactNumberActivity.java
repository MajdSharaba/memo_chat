package com.yawar.memo.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.Api.ServerApi;
import com.yawar.memo.R;
import com.yawar.memo.adapter.ContactNumberAdapter;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.model.ContactModel;
import com.yawar.memo.model.SendContactNumberResponse;
import com.yawar.memo.utils.BaseApp;
import com.yawar.memo.utils.Globale;
import com.yawar.memo.permissions.Permissions;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class ContactNumberActivity extends AppCompatActivity implements ContactNumberAdapter.CallbackInterface,Observer {
    RecyclerView recyclerView;
    SearchView searchView;
    Toolbar toolbar;
    String myId;
    BaseApp myBase;
    ServerApi serverApi;



    ClassSharedPreferences classSharedPreferences;
    ArrayList<ContactModel> arrayList = new ArrayList<ContactModel>();
    ArrayList<SendContactNumberResponse> sendContactNumberResponses = new ArrayList<SendContactNumberResponse>();
    ContactNumberAdapter mainAdapter;
    private Permissions permissions;
    Globale globale;

    TextView contact_number ;

    float textSize = 25F ;
    SharedPreferences sharedPreferences ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_number);
        serverApi = new ServerApi(this);
        permissions = new Permissions();
        checkContactpermission();





        sharedPreferences = getSharedPreferences("txtFontSize", Context.MODE_PRIVATE);
//        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
//        bottomNavigationView.setSelectedItemId(R.id.calls);
        recyclerView = findViewById(R.id.recycler_view);
        contact_number = findViewById(R.id.contact_number);
//        contact_number.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));


//        toolbar = findViewById(R.id.toolbar);
//        toolbar.setTitle("Memo");
//        setSupportActionBar(toolbar);
        globale = new Globale();
        classSharedPreferences= new ClassSharedPreferences(this);
        myId = classSharedPreferences.getUser().getUserId();
        myBase = BaseApp.getInstance();
//        checkContactpermission();

        myBase.getContactNumberObserve().addObserver(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(ContactNumberActivity.this));
        sendContactNumberResponses=myBase.getContactNumberObserve().getContactNumberResponseList();
        mainAdapter = new ContactNumberAdapter(ContactNumberActivity.this,sendContactNumberResponses);
        recyclerView.setAdapter(mainAdapter);
        System.out.println(myBase.getContactNumberObserve().getContactNumberResponseList().size()+"lengtttttt");

        permissions = new Permissions();
        searchView = findViewById(R.id.search_by_secret_number);

        CharSequence charSequence = searchView.getQuery();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mainAdapter.filter(newText);
                return false;
            }
        });

//        checkpermission();

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
                Intent intent = new Intent(ContactNumberActivity.this, GroupSelectorActivity.class);
                startActivity(intent);

                return true;
            case R.id.item2:
                Toast.makeText(getApplicationContext(),"Item 2 Selected",Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void checkContactpermission() {

        if (permissions.isContactOk(this)) {
            getContactList();

        } else {
            permissions.requestContact(this);
        }
    }
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {


//        if (requestCode == STORAGE_PERMISSION_CODE) {
//            System.out.println("Permission Granted");
//
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(IntroActivity.this, "Permission Granted", Toast.LENGTH_SHORT) .show();
//            }
//            else {
//                Toast.makeText(IntroActivity.this, "Permission Denied", Toast.LENGTH_SHORT) .show();
//            }
//        }
        switch (requestCode) {

            case AllConstants.CONTACTS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContactList();

                } else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                        showPermissionDialog(getResources().getString(R.string.contact_permission),1000);

                        // Comment 2.
//                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
//                        alertBuilder.setCancelable(true);
//                        alertBuilder.setTitle(getResources().getString(R.string.permission_necessary));
//                        alertBuilder.setMessage(getResources().getString(R.string.contact_permission));
//                        alertBuilder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
//
//                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//                            public void onClick(DialogInterface dialog, int which) {
//                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                Uri uri = Uri.fromParts("package", getPackageName(), null);
//                                intent.setData(uri);
//                                startActivityForResult(intent, 1000);                                }
//                        });
//
//                        AlertDialog alert = alertBuilder.create();
//                        alert.show();
                    }


                }
                break;


        }

        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == 1000 ) {
        switch (requestCode){
            case 1000:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
                    showPermissionDialog(getResources().getString(R.string.contact_permission),1000);
//
                }
                else{
                    getContactList();
                }
                break;


        }
    }
    public void showPermissionDialog(String message,int RequestCode){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle(getResources().getString(R.string.permission_necessary));
        alertBuilder.setMessage(getResources().getString(R.string.contact_permission));
        alertBuilder.setMessage(message);

        alertBuilder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, RequestCode);                                     }
        });

        AlertDialog alert = alertBuilder.create();
        alert.show();


    }


//    private void checkpermission() {
//        ///check condition
////        if(ContextCompat.checkSelfPermission(ContactNumberActivity.this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
////            ActivityCompat.requestPermissions(ContactNumberActivity.this,new String[]{Manifest.permission.READ_CONTACTS},100);
////        }
////        else {
////            getContactList();
////        }
//        if (permissions.isContactOk(this)) {
//            getContactList();
//        }
//        else permissions.requestContact(this);
//    }
//
//    private void getContactList() {
//         final String DISPLAY_NAME = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
//                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY : ContactsContract.Contacts.DISPLAY_NAME;
//        Uri uri = ContactsContract.Contacts.CONTENT_URI;
//        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
//        Cursor cursor = getContentResolver().query(uri,null,null,null,sort);
//        if(cursor.getCount()>0){
//            while (cursor.moveToNext()){
//                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
//                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
//                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
//                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" =?";
//                Cursor phoneCursor = getContentResolver().query(uriPhone, null ,selection , new String[]{id},null);
//                if(phoneCursor.moveToNext()){
//                    String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
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
//
//    private void sendContactNumber(ArrayList<ContactModel> arrayList) {
//        String url =AllConstants.base_url+ "APIS/mycontact.php";
//        ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Uploading, please wait...");
//        progressDialog.show();
//        RequestQueue queue = Volley.newRequestQueue(this);
//
//        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                progressDialog.dismiss();
//                System.out.println(response.toString());
//
//                // on below line we are passing our response
//                // to json object to extract data from it.
//                JSONObject respObj = null;
//                try {
//                     respObj = new JSONObject(response);
//                    System.out.println(respObj);
//                    JSONArray jsonArray = (JSONArray) respObj.get("data");
////                    JSONArray jsonArray = new JSONArray(respObj.getJSONArray("data"));
//                    System.out.println(jsonArray);
//
//                    for (int i = 0; i <= jsonArray.length()-1; i++) {
//                     JSONObject jsonObject = jsonArray.getJSONObject(i);
//                     System.out.println(jsonObject.getString("name"));
//                    String id = jsonObject.getString("id");
//                    String name = jsonObject.getString("name");
//                    String number = jsonObject.getString("number");
//                    String image = jsonObject.getString("image");
////                    String imageUrl="";
////                    if(!image.isEmpty()){
////                        imageUrl = "http://192.168.1.10:8080/yawar_chat/uploads/profile/"+image;
////                    }
////                    else{
////                        imageUrl = "https://v5p7y9k6.stackpathcdn.com/wp-content/uploads/2018/03/11.jpg";
////                    }
//                    String state = jsonObject.getString("state");
//                        sendContactNumberResponses.add(new SendContactNumberResponse(id,name,number,image,state));
////                        recyclerView.setLayoutManager(new LinearLayoutManager(ContactNumberActivity.this));
////                        mainAdapter = new ContactNumberAdapter(ContactNumberActivity.this,sendContactNumberResponses);
////                        recyclerView.setAdapter(mainAdapter);
//
//
//                    }
//                    recyclerView.setLayoutManager(new LinearLayoutManager(ContactNumberActivity.this));
//                    mainAdapter = new ContactNumberAdapter(ContactNumberActivity.this,sendContactNumberResponses);
//                    recyclerView.setAdapter(mainAdapter);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//        }, new com.android.volley.Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                // method to handle errors.
////                Toast.makeText(this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                // below line we are creating a map for
//                // storing our values in key and value pair.
//                Map<String, String> params = new HashMap<String, String>();
//
//                // on below line we are passing our key
//                // and value pair to our parameters.
//                String data = new Gson().toJson(arrayList);
//                params.put("data", data);
//                params.put("id",myId);
////                params.put("email", email);
////                params.put("first_name", firstName);
////                params.put("last_name", lastName);
////                params.put("picture", imageString);
//
//                // at last we are
//                // returning our params.
//                return params;
//            }
//        };
//        // below line is to make
//        // a json object request.
//        queue.add(request);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
////        if(requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
////            getContactList();
////        }
////        else {
////            Toast.makeText(ContactNumberActivity.this, "permission Denied",Toast.LENGTH_LONG);
////            checkpermission();
////        }
//        switch (requestCode) {
//            case AllConstants.CONTACTS_REQUEST_CODE:
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    getContactList();
//                } else
//                    Toast.makeText(this, "Contact Permission denied", Toast.LENGTH_SHORT).show();
//        }
//    }



    @Override
    public void onHandleSelection(int position, SendContactNumberResponse sendContactNumberResponse) {
        Bundle bundle = new Bundle();
        System.out.println(sendContactNumberResponse.getChat_id()+sendContactNumberResponse.getFcmToken().toString()+"memooooooooo");


        bundle.putString("reciver_id",sendContactNumberResponse.getId());

        bundle.putString("sender_id", myId);
        bundle.putString("fcm_token",sendContactNumberResponse.getFcmToken() );


//        bundle.putString("reciver_id",chatRoomModel.reciverId);
        bundle.putString("name",sendContactNumberResponse.getName());
        bundle.putString("image",sendContactNumberResponse.getImage());
        bundle.putString("chat_id","");


        Intent intent = new Intent(this, ConversationActivity.class);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    @Override
    public void update(Observable observable, Object o) {
        sendContactNumberResponses.clear();
        sendContactNumberResponses=myBase.getContactNumberObserve().getContactNumberResponseList();
        System.out.println("updateeeeeeeeee"+sendContactNumberResponses.size());
        mainAdapter = new ContactNumberAdapter(ContactNumberActivity.this,sendContactNumberResponses);
        recyclerView.setAdapter(mainAdapter);



        mainAdapter.notifyDataSetChanged();

    }
//    private void search(String query) {
//        String url = "http://192.168.1.11:8080/yawar_chat/APIS/search_for_user.php";
//        ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Uploading, please wait...");
//        progressDialog.show();
//
//
//        // creating a new variable for our request queue
//        RequestQueue queue = Volley.newRequestQueue(this);
//
//        // on below line we are calling a string
//        // request method to post the data to our API
//        // in this we are calling a post method.
//        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                progressDialog.dismiss();
//
//
////            Toast.makeText(LoginOtpInformation.this, "Data added to API+"+response, Toast.LENGTH_SHORT).show();
//                System.out.println("Data added to API+"+response);
//                System.out.println("Data added to API+"+response);
//                // on below line we are passing our response
//                // to json object to extract data from it.
//                JSONObject respObj = null;
//                try {
//                    respObj = new JSONObject(response);
//                    System.out.println(respObj);
//                    JSONArray jsonArray = (JSONArray) respObj.get("data");
//                    if(jsonArray.length()>0){
//                        sendContactNumberResponses.clear();
////                    JSONArray jsonArray = new JSONArray(respObj.getJSONArray("data"));
//                    System.out.println(jsonArray);}
//
//                    for (int i = 0; i <= jsonArray.length()-1; i++) {
//                        JSONObject jsonObject = jsonArray.getJSONObject(i);
//                        System.out.println(jsonObject.getString("id"));
//
//                        String name = jsonObject.getString("first_name");
//                        String number = jsonObject.getString("sn");
//                        String image = jsonObject.getString("image");
//                        String imageUrl="";
//                        if(!image.isEmpty()){
//                            imageUrl = "http://192.168.1.11:8080/yawar_chat/uploads/profile/"+image;
//                        }
//                        else{
//                            imageUrl = "https://v5p7y9k6.stackpathcdn.com/wp-content/uploads/2018/03/11.jpg";
//                        }
//                        sendContactNumberResponses.add(new SendContactNumberResponse(name,number,imageUrl,"false"));
////                        recyclerView.setLayoutManager(new LinearLayoutManager(ContactNumberActivity.this));
////                        mainAdapter = new ContactNumberAdapter(ContactNumberActivity.this,sendContactNumberResponses);
////                        recyclerView.setAdapter(mainAdapter);
//
//
//                    }
////                    recyclerView.setLayoutManager(new LinearLayoutManager(ContactNumberActivity.this));
//                    mainAdapter = new ContactNumberAdapter(ContactNumberActivity.this,sendContactNumberResponses);
//                    recyclerView.setAdapter(mainAdapter);
////                    mainAdapter.notifyDataSetChanged();
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
////                    System.out.println(data.getString("first_name"));
////                    String user_id = data.getString("id");
//
////                    String last_name = data.getString("last_name");
////                    String email = data.getString("email");
////                    String profile_image = data.getString("profile_image");
////                    UserModel userModel = new UserModel(user_id,first_name,last_name,email,"+964 935013485");
////                    classSharedPreferences.setUser(userModel);
////                    UserModel userModel1 = classSharedPreferences.getUser();
////
////                    Intent intent = new Intent(context, BasicActivity.class);
////                    context.startActivity(intent);
////                    System.out.println(userModel1.getUserName()+userModel1.getLastName()+userModel1.getEmail());
//
//            }
//        }, new com.android.volley.Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                // method to handle errors.
////                Toast.makeText(this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                // below line we are creating a map for
//                // storing our values in key and value pair.
//                Map<String, String> params = new HashMap<String, String>();
//
//                // on below line we are passing our key
//                // and value pair to our parameters.
//                params.put("sn",query );
////                params.put("email", email);
////                params.put("first_name", firstName);
////                params.put("last_name", lastName);
////                params.put("picture", imageString);
//
//                // at last we are
//                // returning our params.
//                return params;
//            }
//        };
//        // below line is to make
//        // a json object request.
//        queue.add(request);
//    }
public void getContactList() {
    ArrayList<ContactModel> arrayList = new ArrayList<ContactModel>();
    final String DISPLAY_NAME = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY : ContactsContract.Contacts.DISPLAY_NAME;
    Uri uri = ContactsContract.Contacts.CONTENT_URI;
    String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
    Cursor cursor = getContentResolver().query(uri,null,null,null,sort);
    if(cursor.getCount()>0){
        while (cursor.moveToNext()){
            @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
            Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" =?";
            Cursor phoneCursor = getContentResolver().query(uriPhone, null ,selection , new String[]{id},null);
            if(phoneCursor.moveToNext()){
                @SuppressLint("Range") String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                System.out.println(number+"this is the number");
                ContactModel model = new ContactModel();
                model.setName(name);
                model.setNumber(number);
                System.out.println(cursor.getCount()+"this is "+cursor.getPosition());
                if(cursor.getPosition()>50){
                    break;
                }
                arrayList.add(model);
            }
        }
        cursor.close();
    }
    System.out.println(arrayList.size()+"length");
    serverApi.sendContactNumber(arrayList);

}
}
