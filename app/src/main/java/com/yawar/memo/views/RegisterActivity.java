package com.yawar.memo.views;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.yawar.memo.Api.ClassSharedPreferences;
import com.yawar.memo.Api.ServerApi;
import com.yawar.memo.R;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.model.ChatMessage;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.repositry.AuthRepo;
import com.yawar.memo.utils.BaseApp;
import com.yawar.memo.utils.FileUtil;
import com.yawar.memo.utils.VolleyMultipartRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;


public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
     ImageView image;
    Spinner dropdown;

    List<String> spennerItem = new ArrayList<String>();
    String spennerItemChooser;

    EditText edFname,edLname;
    byte[] inputData = new byte[]{};

    ClassSharedPreferences classSharedPreferences;

    Button btnRegister,btnSkip;
    private static final int PICK_IMAGE = 100;
    Uri imageUri = Uri.parse("n");
    Bitmap bitmap;
    ProgressDialog progressDialog;
    String fName = "user";
    String lName = "";
    String email = "";
    String userId ;
    String imageString ="";
    ServerApi serverApi;
    private RequestQueue rQueue;
    BaseApp myBase;
    String displayNamee = "";
    AuthRepo authRepo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
        initAction();
    }

    private void initView() {
        image = findViewById(R.id.imageProfile);
        spennerItemChooser=getResources().getString(R.string.choose_special_number);
        myBase = BaseApp.getInstance();
        authRepo = myBase.getAuthRepo();


//        edEmail = findViewById(R.id.et_em);
        edFname = findViewById(R.id.et_fName);
        edLname = findViewById(R.id.et_lName);
        image = findViewById(R.id.imageProfile);
        btnRegister = findViewById(R.id.btn_Register);
//        btnSkip = findViewById(R.id.btn_skip);
        classSharedPreferences = new ClassSharedPreferences(RegisterActivity.this);
        serverApi = new  ServerApi(RegisterActivity.this);
        dropdown = findViewById(R.id.spinner1);
    }
    private void initAction() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,spennerItem);

        spennerItem.add(getResources().getString(R.string.choose_special_number));
        authRepo.jsonObjectMutableLiveData.observe(this ,new androidx.lifecycle.Observer<JSONObject>() {
            @Override
            public void onChanged(JSONObject jsonObject) {
                if(jsonObject!=null) {
                    try {
                        JSONArray jsonArray = jsonObject.getJSONArray("numbers");
                        JSONObject userObject  = jsonObject.getJSONObject("user");
                        userId = userObject.getString("id");
                        System.out.println(userId+"userId");


                        for (int i = 0; i < jsonArray.length(); i++) {
                            String item = jsonArray.getString(i);
                            spennerItem.add(item);
                        }
                        adapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }});
//        JSONObject jsonObject = classSharedPreferences.getSecretNumbers();
//        try {
//            JSONArray jsonArray = jsonObject.getJSONArray("numbers");
//            JSONObject userObject  = jsonObject.getJSONObject("user");
//            userId = userObject.getString("id");
//            System.out.println(userId+"userId");
//
//
//            for (int i = 0; i < jsonArray.length(); i++) {
//                String item = jsonArray.getString(i);
//                spennerItem.add(item);
//            }

            dropdown.setAdapter(adapter);
            dropdown.setOnItemSelectedListener(this);



//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        System.out.println(jsonObject.toString()+"majjjjjjjjjjjjjjjjjd");
        ///// get image from gallery
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
        ///// register Button
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fName = edFname.getText().toString();
                lName = edLname.getText().toString();
//                email = edEmail.getText().toString();
                if(CheckAllFields()){
                    uploadImage(displayNamee, imageUri);
//                serverApi.CompleteRegister(fName,lName,email,imageString,spennerItemChooser,userId);
                }
            }
        });

//        btnSkip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               // serverApi.register(fName,lName,email,imageString);
//            }
//        });
    }

    

    private void openGallery() {
        ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }
    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==Activity.RESULT_OK){
//            String displayNamee = null;
                        imageUri = data.getData();


            File myFileImage = new File(imageUri.toString());

//                        System.out.println("FileUtil.getPath(this,selectedMediaUri)" + FileUtil.getPath(this, selectedMediaUri) + path);
//                            copyFileOrDirectory(FileUtil.getPath(this,selectedMediaUri),Environment.getExternalStoragePublicDirectory("memo/send/video").getAbsolutePath());


            if (imageUri.toString().startsWith("content://")) {
                Cursor cursor = null;
                try {
                    cursor = this.getContentResolver().query(imageUri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        displayNamee = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        Log.d("nameeeee>>>>  ", displayNamee);
//                                    System.out.println(displayNamee);

//                        uploadImage(displayNamee, imageUri);
                    }
                } finally {
                    cursor.close();
                }
            } else if (imageUri.toString().startsWith("file://")) {
                displayNamee = myFileImage.getName();
                System.out.println(displayNamee + "lkkkkkkkkkkkkkkkk");
//                uploadImage(displayNamee, imageUri);


                Log.d("nameeeee>>>>  ", displayNamee);
            }
                        image.setImageURI(imageUri);

        }
//            imageUri = data.getData();
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//            if(bitmap!=null){
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//                byte[] imageBytes = baos.toByteArray();
//                imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);}
//
//
//            image.setImageURI(imageUri);
//        }
    }
    private boolean CheckAllFields() {
//        char[] chars = fName.toCharArray();
//        for(char c : chars){
//            if(Character.isDigit(c)){
//                edFname.setError(getResources().getString(R.string.valied_name_message));
//                return false;
//            }
//
//        }
        if (spennerItemChooser.equals(getResources().getString(R.string.choose_special_number))){
            Toast.makeText(this,R.string.choose_special_number,Toast.LENGTH_SHORT).show();
            return false;

        }
//        System.out.println(spennerItemChooser+R.string.choose_special_number+",;,;l,;");

        // after all validation return true.
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        spennerItemChooser= adapterView.getItemAtPosition(i).toString();
//        System.out.println(spennerItemChooser);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

//    private void postData(String firstname, String lastname) {
//        Retrofit retrofit = apiClient.getClient();
//        apiRest retrofitAPI = retrofit.create(apiRest.class);
////        Retrofit retrofit = apiClient.getClient();
//
//
//        // passing data from our text fields to our modal class.
//        UserModel modal = new UserModel(963);
//
//        // calling a method to create a post and passing our modal class.
//        Call<String> call = retrofitAPI.createPost("963");
//        System.out.println(call.toString()+"majdddddddddddddddddddddddddddd");
//
//        // on below line we are executing our method.
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//
//                // this method is called when we get response from our api.
//                Toast.makeText(LoginOtpInformation.this, "Data added to API", Toast.LENGTH_SHORT).show();
//
//                // below line is for hiding our progress bar.
////                loadingPB.setVisibility(View.GONE);
//
//                // on below line we are setting empty text
//                // to our both edit text.
////                jobEdt.setText("");
////                nameEdt.setText("");
//
//                // we are getting response from our body
//                // and passing it to our modal class.
//                String responseFromAPI = response.body();
//
//                // on below line we are getting our data from modal class and adding it to our string.
//                String responseString = "Response Code : " + response.code() + "\nName : " + responseFromAPI+ "\n" + "Job : " ;
//                System.out.println(responseString);
//
//                // below line we are setting our
//                // string to our text view.
////                responseTV.setText(responseString);
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                System.out.println(call.toString()+"ssssssssssssssssssssss");
//
//                // setting text to our text view when
//                // we get error response from API.
////                responseTV.setText("Error found is : " + t.getMessage());
//            }
//        });
//    }
    
//private void postDataUsingVolley(String firstName, String lastName, String email,Bitmap bitmap) {
//    // url to post our data
//    String url = "http://192.168.1.10:8080/yawar_chat/APIS/signup.php";
//    progressDialog = new ProgressDialog(LoginOtpInformation.this);
//    progressDialog.setMessage("Uploading, please wait...");
//    progressDialog.show();
//    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//    if(bitmap!=null){
//    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//    byte[] imageBytes = baos.toByteArray();
//     imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);}
//
//
//    // creating a new variable for our request queue
//    RequestQueue queue = Volley.newRequestQueue(LoginOtpInformation.this);
//
//    // on below line we are calling a string
//    // request method to post the data to our API
//    // in this we are calling a post method.
//    StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
//        @Override
//        public void onResponse(String response) {
//            progressDialog.dismiss();
//
//
////            Toast.makeText(LoginOtpInformation.this, "Data added to API+"+response, Toast.LENGTH_SHORT).show();
//            System.out.println("Data added to API+"+response);
//            try {
//                // on below line we are passing our response
//                // to json object to extract data from it.
//                JSONObject respObj = new JSONObject(response);
//                JSONObject data = respObj.getJSONObject("data");
//                System.out.println(data.getString("first_name"));
//                String user_id = data.getString("id");
//                String first_name = data.getString("first_name");
//                String last_name = data.getString("last_name");
//                String email = data.getString("email");
//                String profile_image = data.getString("profile_image");
//                UserModel userModel = new UserModel(user_id,first_name,last_name,email,"+964 935013485");
//                classSharedPreferences.setUser(userModel);
//                UserModel userModel1 = classSharedPreferences.getUser();
//
//                Intent intent = new Intent(LoginOtpInformation.this,BasicActivity.class);
//                startActivity(intent);
//               System.out.println(userModel1.getUserName()+userModel1.getLastName()+userModel1.getEmail());
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }, new com.android.volley.Response.ErrorListener() {
//        @Override
//        public void onErrorResponse(VolleyError error) {
//            // method to handle errors.
//            Toast.makeText(LoginOtpInformation.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
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
//            params.put("phone", classSharedPreferences.getVerficationNumber());
//            params.put("email", email);
//            params.put("first_name", firstName);
//            params.put("last_name", lastName);
//            params.put("picture", imageString);
//
//            // at last we are
//            // returning our params.
//            return params;
//        }
//    };
//    // below line is to make
//    // a json object request.
//    queue.add(request);
//}
private void uploadImage(final String imageName, Uri pdfFile) {



        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.prograss_message));
        progressDialog.show();

    InputStream iStream = null;
    try {
        if(!pdfFile.toString().equals("n")){
        iStream = getContentResolver().openInputStream(pdfFile);
        System.out.println(pdfFile);
        //"file:///storage/emulated/0/memo/1640514470604.3gp"
          inputData = getBytes(iStream);}

//      String url = AllConstants.base_url+"uploadImgProfile";
        String url = "http://192.168.0.106:3000/uploadImgProfile";

//              "http://192.168.1.7:3000/uploadImgProfile";
//        AllConstants.base_url+"uploadImgProfile"
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        System.out.println("responeeeeeeeeeeee" + new String(response.data));
                        progressDialog.dismiss();

                        rQueue.getCache().clear();
                        try {
                            JSONArray jsonArray = new JSONArray(new String(response.data));

                            JSONObject respObj = jsonArray.getJSONObject(0);
                            System.out.println(respObj);
                            String user_id = respObj.getString("id");
                            String first_name = respObj.getString("first_name");
                            String last_name = respObj.getString("last_name");
                            String email = respObj.getString("email");
                            String profile_image = respObj.getString("profile_image");
                            String secret_number = respObj.getString("sn");
                            String number = respObj.getString("phone");
                            String status= respObj.getString("status");

                            UserModel userModel = new UserModel(user_id,first_name,last_name,email,number,secret_number,profile_image,status);
                            classSharedPreferences.setUser(userModel);
                            Intent intent = new Intent(RegisterActivity.this, IntroActivity.class);
                            startActivity(intent);
                            finish();





                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();

//                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("first_name", fName);
                params.put("last_name", lName);

                params.put("sn", spennerItemChooser);
                params.put("id", userId);
                return params;
            }

            /*
             *pass files using below method
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();

                params.put("img_profile", new DataPart(imageName, inputData,"plan/text"));

                return params;
            }
        };


        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue = Volley.newRequestQueue(RegisterActivity.this);
//            rQueue.add(volleyMultipartRequest);
        myBase.addToRequestQueue(volleyMultipartRequest);


    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }


}
    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

}
