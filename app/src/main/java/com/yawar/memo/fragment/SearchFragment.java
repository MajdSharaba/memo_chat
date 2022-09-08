package com.yawar.memo.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.modelView.ChatRoomViewModel;
import com.yawar.memo.modelView.SearchModelView;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.R;
import com.yawar.memo.adapter.SearchAdapter;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.model.SearchRespone;
import com.yawar.memo.views.ConversationActivity;
import com.yawar.memo.permissions.Permissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class SearchFragment extends Fragment implements SearchAdapter.CallbackInterface {

    RecyclerView recyclerView;
    SearchView searchView;
    Toolbar toolbar;
   ArrayList<SearchRespone> list = new ArrayList<>();

    //    List<SearchRespone> searchResponeArrayList = new ArrayList<>();
    SearchAdapter searchAdapter;
    BottomNavigationView bottomNavigationView;
    ArrayList<SearchRespone> res = new ArrayList<>();
    LinearLayoutManager linearLayoutManager ;
    private Permissions permissions;
    ClassSharedPreferences classSharedPreferences;
    String my_id ;
    String searchParamters = "";
    private Timer timer = new Timer();
    private final long DELAY = 1000;
    private ProgressBar loadingPB;
    private NestedScrollView nestedSV;
    final Handler handler = new Handler();
    private int FIRST_PAGE = 1 ;
    int limit = 2 ;
    boolean end = false ;
    TextView search ;
    SharedPreferences sharedPreferences;
    SearchModelView searchModelView;
    LinearLayout linerNOSearchResult;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        sharedPreferences = getActivity().getSharedPreferences("txtFontSize", Context.MODE_PRIVATE);
        timer     = new Timer();
//        loadingPB = view.findViewById(R.id.idPBLoading);
//        nestedSV  = view.findViewById(R.id.idNestedSV);

        permissions = new Permissions();
        classSharedPreferences = new ClassSharedPreferences(getContext());
        my_id = classSharedPreferences.getUser().getUserId();
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);




        toolbar = view.findViewById(R.id.toolbar);
        search = view.findViewById(R.id.search);
        searchView = view.findViewById(R.id.search_by_secret_number);
        linerNOSearchResult = view.findViewById(R.id.liner_no_search_result);
        searchModelView = new ViewModelProvider(this).get(SearchModelView.class);
        checkpermission();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (timer != null) {
                    timer.cancel();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    if (newText.length() >= 0) {
                        if( timer != null){
                            timer.cancel();
                        }

                        timer = new Timer();
                        TimerTask t = new TimerTask() {
                            @Override
                            public void run() {

                                FIRST_PAGE = 1;

                                end = false;

                                searchParamters = newText;

                                checkpermission();
                            }
                        };
                        timer.schedule(t,300);
                    }
                } catch (Exception e)
                {
                    System.out.println("EROOOR");
                }

//                if (newText.length() >= 0) {
//                    if(timer!=null){
//                        timer.cancel();
//                    }
//                     timer = new Timer();
//                    TimerTask t = new TimerTask() {
//                        @Override
//                        public void run() {
//                            page=1;
//                            end = false;
//                            searchResponeArrayList.clear();
//                            searchParamters = newText;
//                            checkpermission();
//
//                        }
//                    };
//                  timer.schedule(t,300);
//                    timer.schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            TODO: do what you need here (refresh list)
//                            searchResponeArrayList.clear();
//                            searchParamters = newText;
//                            checkpermission();
//                            }
//                    } ,DELAY);
//                }


//                searchResponeArrayList.clear();
//                res.clear();
//                recyclerView.getRecycledViewPool().clear();



                return false;
            }
        });



//        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
//            @Override
//            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
//                FIRST_PAGE++;
//                searchModelView.search(searchParamters,String.valueOf(FIRST_PAGE) , my_id);
//            }
//        });

        recyclerView.setLayoutManager(linearLayoutManager);
        searchAdapter = new SearchAdapter(SearchFragment.this,getActivity());

        searchModelView.liveSearchResponeList.observe(getActivity(), new androidx.lifecycle.Observer<ArrayList<SearchRespone>>() {
            @Override
            public void onChanged(ArrayList<SearchRespone> searchResponeArrayList) {
//                list.clear();
                list = new ArrayList<>();
                if(searchResponeArrayList!=null) {
                    if (searchResponeArrayList.isEmpty()) {
                        linerNOSearchResult.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        linerNOSearchResult.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        for (SearchRespone searchRespone : searchResponeArrayList) {
                            list.add(searchRespone.clone());
                        }
                        System.out.println("list" + list);
                        searchAdapter.setData((ArrayList<SearchRespone>) list);
//                 searchAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        recyclerView.setAdapter(searchAdapter);
        return  view;

    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }
        super.onDestroy();
    }

    private void checkpermission() {
        System.out.println("checkpermission");

        if (permissions.isContactOk(getContext())) {

            System.out.println("permission grannnnt");

//            searchResponeArrayList.clear();
//            res.clear();
//            recyclerView.getRecycledViewPool().clear();

            searchModelView.search(searchParamters,String.valueOf(FIRST_PAGE) , my_id);

        } else {

            System.out.println("permission not   grannnnt");
            requestPermissions( new String[]{Manifest.permission.READ_CONTACTS}, AllConstants.CONTACTS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

//        if(requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//            getContactList();
//        }
//        else {
//            Toast.makeText(ContactNumberActivity.this, "permission Denied",Toast.LENGTH_LONG);
//            checkpermission();
//        }

        System.out.println( AllConstants.CONTACTS_REQUEST_CODE+"xxxxxxxxxxxxxxxxx");
        switch (requestCode) {
            case AllConstants.CONTACTS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

//                    searchResponeArrayList.clear();
//                    res.clear();
//                    recyclerView.getRecycledViewPool().clear();

                    searchModelView.search(searchParamters,String.valueOf(FIRST_PAGE) , my_id);

                } else {
                    System.out.println("no permission");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                        System.out.println("show permissionDialog");
                        showPermissionDialog(getResources().getString(R.string.contact_permission), 1000);
                    }
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
//    private void search(int page , int size) {
//
//        String url = AllConstants.base_url + "APIS/search_for_user.php";
//
////        ProgressDialog progressDialog = new ProgressDialog(getContext());
////        progressDialog.setMessage("Uploading, please wait...");
////        progressDialog.show();
////        creating a new variable for our request queue
//
//        RequestQueue queue = Volley.newRequestQueue(getContext());
//
//        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
////             progressDialog.dismiss();
//               /*
//               searchResponeArrayList.clear();
//               res.clear();
//               recyclerView.getRecycledViewPool().clear();
//               */
//                System.out.println("Data added to API+" + response);
//                System.out.println("Data added to API+" + response);
//
//                // on below line we are passing our response
//                // to json object to extract data from it.
//
//                JSONObject respObj = null;
//                try {
//                    respObj = new JSONObject(response);
//                    JSONArray jsonArray = (JSONArray) respObj.get("data");
//
////                    System.out.println(jsonArray.length()+"falseeeeeeeeeeeeeeeee"+(page+jsonArray.length()<limit));
////                    if(jsonArray.length()<limit){
////                        loadingPB.setVisibility(View.GONE);
////                        System.out.println("trueeeeeeeeeeeeeeeeeeee"+page);
////                        end = true ;
////                    }
////                    else {
////                        end = false ;
////                    }
//
//                    System.out.println("searchParamters"+searchParamters+""+page);
////               ArrayList<SearchRespone> res = new ArrayList<SearchRespone>();
//
//                    for (int i = 0; i <= jsonArray.length() - 1; i++) {
//                        JSONObject jsonObject = jsonArray.getJSONObject(i);
//                        System.out.println(jsonObject.getString("id"));
//                        String id = jsonObject.getString("id");
//                        String phone = jsonObject.getString("phone");
//                        System.out.println(phone);
//                        String name = jsonObject.getString("first_name")+" "+jsonObject.getString("last_name");
//                        String secretNumber = jsonObject.getString("sn");
//                        String image = jsonObject.getString("image");
//                        String token = jsonObject.getString("token");
//                        String blockedFor = jsonObject.getString("blocked_for");
//
//
//
////                      String imageUrl="";
////                      if(!image.isEmpty()) {
////                         imageUrl = "http://192.168.1.10:8080/yawar_chat/uploads/profile/"+image;
////                                          }
////                    else{
////                            imageUrl = "https://v5p7y9k6.stackpathcdn.com/wp-content/uploads/2018/03/11.jpg";
////                        }
////                        List res = new ArrayList();
//
//                        res.add(new SearchRespone(id, name, secretNumber, image, phone,token,blockedFor));
//                        searchAdapter = new SearchAdapter(SearchFragment.this,getActivity() , res);
//                        recyclerView.setAdapter(searchAdapter);
//
//                        searchResponeArrayList = res ;
//                        searchAdapter.notifyItemRangeInserted(searchAdapter.getItemCount(),res.size() - size);
//
////                      searchResponeArrayList.add(new SearchRespone( id , name , secretNumber, image , phone , token));
////                      recyclerView.setLayoutManager(new LinearLayoutManager(ContactNumberActivity.this));
////                      mainAdapter = new ContactNumberAdapter(ContactNumberActivity.this,sendContactNumberResponses);
////                      recyclerView.setAdapter(mainAdapter);
////                      searchResponeArrayList.add(jsonArray.length());
//
//                    }
//
//                    System.out.println(searchResponeArrayList.size()+"the string");
//
////                    searchAdapte
////                    recyclerView.getRecycledViewPool().clear();
////                    searchAdapter.notifyItemRangeInserted(searchAdapter.getItemCount(),res.size()-size);
////                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
////                    searchAdapter.notifyDataSetChanged();
////                    mainAdapter.notifyDataSetChanged();
//
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
////                    System.out.println(data.getString("first_name"));
////                    String user_id = data.getString("id");
////                    String last_name = data.getString("last_name");
////                    String email = data.getString("email");
////                    String profile_image = data.getString("profile_image");
////                    UserModel userModel = new UserModel(user_id,first_name,last_name,email,"+964 935013485");
////                    classSharedPreferences.setUser(userModel);
////                    UserModel userModel1 = classSharedPreferences.getUser();
////                    Intent intent = new Intent(context, BasicActivity.class);
////                    context.startActivity(intent);
////                    System.out.println(userModel1.getUserName()+userModel1.getLastName()+userModel1.getEmail());
//
//            }
//        }, new com.android.volley.Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                // method to handle errors.
//                // Toast.makeText(this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                // below line we are creating a map for
//                // storing our values in key and value pair.
//                Map<String, String> params = new HashMap<String, String>();
//                // on below line we are passing our key
//                // and value pair to our parameters.
//
//                params.put("sn", searchParamters);
//                params.put("page", String.valueOf(page));
//                params.put("my_id", classSharedPreferences.getUser().getUserId());
//
//
////              params.put("email", email);
////              params.put("first_name", firstName);
////              params.put("last_name", lastName);
////              params.put("picture", imageString);
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


    @Override
    public void onHandleSelection(int position, SearchRespone searchRespone) {
        Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
        contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

        contactIntent.putExtra(ContactsContract.Intents.Insert.NAME , searchRespone.getName())
                .putExtra(ContactsContract.Intents.Insert.PHONE, searchRespone.getPhone());
        startActivityForResult(contactIntent, 1);
    }

    @Override
    public void onClickItem(int position, SearchRespone searchRespone) {
        Bundle bundle = new Bundle();
        bundle.putString("sender_id", my_id);
        bundle.putString("reciver_id", searchRespone.getId());
        bundle.putString("name", searchRespone.getName());
        bundle.putString("image", searchRespone.getImage());
        bundle.putString("chatId", "");
        bundle.putString("fcm_token",searchRespone.getToken() );
        bundle.putString("special",searchRespone.getSecretNumber() );
        bundle.putString("blockedFor",searchRespone.getBlockedFor());

        Intent intent = new Intent(getContext(), ConversationActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode){
            case 1000:
                System.out.println("case 1000");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
                    showPermissionDialog(getResources().getString(R.string.contact_permission),1000);
                }
                else{

//                    searchResponeArrayList.clear();
//                    res.clear();
//                    recyclerView.getRecycledViewPool().clear();

                    searchModelView.search(searchParamters,String.valueOf(FIRST_PAGE) , my_id);
                }
                break;

            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(getContext(), "Added Contact", Toast.LENGTH_SHORT).show();
                    searchAdapter.notifyDataSetChanged();
                    return;
                }
                if (resultCode == Activity.RESULT_CANCELED) {
                    searchAdapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Cancelled Added Contact", Toast.LENGTH_SHORT).show();
                }
        }
    }

    public void showPermissionDialog(String message,int RequestCode){

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle(getResources().getString(R.string.permission_necessary));
        alertBuilder.setMessage(getResources().getString(R.string.contact_permission));
        alertBuilder.setMessage(message);
        alertBuilder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, RequestCode);

            }
        });

        AlertDialog alert = alertBuilder.create();
        alert.show();


    }

}