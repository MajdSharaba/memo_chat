package com.yawar.memo.ui.registerPage;//package com.yawar.memo.views;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.request.DownloadRequest;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yawar.memo.utils.CallProperty;
import com.yawar.memo.databinding.ActivityRegisterBinding;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.R;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.repositry.AuthRepo;
import com.yawar.memo.utils.BaseApp;
import com.yawar.memo.ui.introPage.IntroActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    List<String> spennerItems = new ArrayList<String>();
    String spennerItemChooser;
    ActivityRegisterBinding binding;
    byte[] inputData = new byte[]{};
    byte[] imageBytes = new byte[]{};
    ClassSharedPreferences classSharedPreferences;
    private static final int PICK_IMAGE = 100;
    Uri imageUri = Uri.parse("n");
    Bitmap bitmap;
    RegisterViewModel registerViewModel;
    ProgressDialog progressDialog;
    String fName = "user";
    String lName = "";
    String email = "";
    String userId ;
    String imageString ="";
//    ServerApi serverApi;
    private RequestQueue rQueue;
    BaseApp myBase;
    String displayNamee = "";
    AuthRepo authRepo;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CallProperty.setStatusBarOrScreenStatus(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        if(android.os.Build.MANUFACTURER.equals("Xiaomi")){
          showXhaomiDialog();
        }
        initView();
        initAction();
    }

    private void initView() {
        spennerItemChooser=getResources().getString(R.string.choose_special_number);
        myBase = BaseApp.getInstance();
        authRepo = myBase.getAuthRepo();
        classSharedPreferences = BaseApp.getInstance().getClassSharedPreferences();
        if(classSharedPreferences.getUser()!=null) {
            binding.etFName.setText(classSharedPreferences.getUser().getUserName());
            Glide.with(binding.imageProfile).load(classSharedPreferences.getUser().getImage()).apply(RequestOptions.placeholderOf(R.drawable.th).error(R.drawable.th)).into(binding.imageProfile);

        }

//        serverApi = new  ServerApi(RegisterActivity.this);
        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.prograss_message));

    }
    private void initAction() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spennerItems);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,  android.R.layout.simple_spinner_dropdown_item, spennerItems){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position == 0) {
                    ((CheckedTextView)v.findViewById(android.R.id.text1)).setText("");
                    ((CheckedTextView)v.findViewById(android.R.id.text1)).setHint(getResources().getString(R.string.choose_special_number)); //"Hint to be displayed"
                }
                return v;
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                CheckedTextView tv = (CheckedTextView) view;
                if(position == 0){
                    tv.setTextColor(Color.GRAY);
                }
                else {
                }
                return view;
            }
        };
        spennerItems.add(getResources().getString(R.string.choose_special_number));
        authRepo.getJsonObjectMutableLiveData().observe(this ,new androidx.lifecycle.Observer<JSONObject>() {
            @Override
            public void onChanged(JSONObject jsonObject) {
                if(jsonObject!=null) {
                    try {
                        JSONArray jsonArray = jsonObject.getJSONArray("numbers");
                        JSONObject userObject  = jsonObject.getJSONObject("user");
                        userId = userObject.getString("id");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String item = jsonArray.getString(i);
                            spennerItems.add(getSpecialNumber(item));
                        }
                        adapter.notifyDataSetChanged();
                        adapter1.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }});
        binding.spinner1.setAdapter(adapter1);
        binding.spinner1.setOnItemSelectedListener(this);

        binding.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });


        registerViewModel.getUserModelRespone().observe(this, new Observer<UserModel>() {
            @Override
            public void onChanged(UserModel userModel) {
                if(userModel!=null){
                    System.out.println(userModel.getImage()+"userModel.getImage()");
                        classSharedPreferences.setUser(userModel);
                        Intent intent = new Intent(RegisterActivity.this, IntroActivity.class);
                        startActivity(intent);
                        finish();
                }

            }
        });

        registerViewModel.getShowErrorMessage().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {


                    Toast.makeText(RegisterActivity.this, R.string.internet_message,Toast.LENGTH_LONG).show();

                    registerViewModel.setErrorMessage (false);
                }
            }
        });

        registerViewModel.getLoadingMutableLiveData().observe(this, new Observer<Boolean>() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {

                    if(!progressDialog.isShowing()){

                    progressDialog.show();}

                }
                else{
                    if(progressDialog!=null)
                    progressDialog.dismiss();

                }
            }
        });
        ///// register Button
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fName = binding.etFName.getText().toString();
                lName = binding.etLName.getText().toString();
                if(CheckAllFields()){
                    if(imageUri.toString().equals("n")){
                        if(classSharedPreferences.getUser()!=null) {
                            File d = RegisterActivity.this.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/profile");
                            download(d, classSharedPreferences.getUser().getImage(), classSharedPreferences.getVerficationNumber());
                        }
                        else {
                            alertAddImageDialog(email, "",fName,lName,spennerItemChooser,"",classSharedPreferences.getVerficationNumber());
                        }
                    }
                    else {
                        System.out.println(imageUri+"imaeUri");

//                        uploadImage(displayNamee, imageUri);
                        uploadImage(displayNamee, imageUri);
                    }
                }
            }
        });

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
                        imageUri = data.getData();
            File myFileImage = new File(imageUri.toString());

            if (imageUri.toString().startsWith("content://")) {
                Cursor cursor = null;
                try {
                    cursor = this.getContentResolver().query(imageUri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        displayNamee = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        Log.d("nameeeee>>>>  ", displayNamee);

                    }
                } finally {
                    cursor.close();
                }
            } else if (imageUri.toString().startsWith("file://")) {
                displayNamee = myFileImage.getName();


                Log.d("nameeeee>>>>  ", displayNamee);
            }
            binding.imageProfile.setImageURI(imageUri);

        }
    }
    private boolean CheckAllFields() {

        if (spennerItemChooser.equals(getResources().getString(R.string.choose_special_number))){
            Toast.makeText(this,R.string.choose_special_number,Toast.LENGTH_SHORT).show();
            return false;

        }
        spennerItemChooser = spennerItemChooser.replace("-","");
        System.out.println("spennerItemChooser"+spennerItemChooser);

        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        spennerItemChooser= adapterView.getItemAtPosition(i).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

private void uploadImage(final String imageName, Uri pdfFile) {
    String message_id = System.currentTimeMillis() + "_" +userId;
    System.out.println("firstName" + fName + "lasrName" + lName);
    registerViewModel.setLoading(true);
    InputStream iStream = null;
    if (!pdfFile.toString().equals("n")) {

        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), pdfFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
            imageBytes = baos.toByteArray();
            String path = "profile_images/" + message_id + ".png";
            StorageReference storageRef = storage.getReference(path);
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpg")
                    .build();

            UploadTask uploadTask = storageRef.putBytes(imageBytes,metadata);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d("uploadTask", "onFailure ${}: "+exception.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("uploadTask", "onSuccess: "+taskSnapshot.getUploadSessionUri());
                    registerViewModel.register(email, message_id + ".png",fName,lName,spennerItemChooser,classSharedPreferences.getNumber(),classSharedPreferences.getVerficationNumber());
                }
            });

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return storageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Log.d("uploadTask", "onComplete: "+downloadUri);
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });


        }

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

    public void showXhaomiDialog(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(false);
        alertBuilder.setTitle(getResources().getString(R.string.alert));
        alertBuilder.setMessage(getResources().getString(R.string.xhaomi_message));
        alertBuilder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                openAppPermission();

            }
        });
        alertBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }
    void openAppPermission(){
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, this.getPackageName());
        this.startActivity(intent);

    }

        void download(File d, String downloadUrl, String fileName) {
            registerViewModel.setLoading(true);

            DownloadRequest downloadID = PRDownloader.download(downloadUrl, d.getPath(), fileName)
                    .build().setOnStartOrResumeListener(new OnStartOrResumeListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onStartOrResume() {
                            System.out.println("startttttttttt download");
//                        conversationModelView.setMessageDownload(chatMessage.getId(),true);

//                        Toast.makeText(ConversationActivity.this, "Downloading started", Toast.LENGTH_SHORT).show();

                        }
                    });

            int id = downloadID.start(new OnDownloadListener() {
                @Override
                public void onDownloadComplete() {

//                uploadImage(downloadID.getFileName(),Uri.fromFile(new File(downloadID.getDirPath()+"/"+downloadID.getFileName())));
                    uploadImage(downloadID.getFileName(), Uri.fromFile(new File(downloadID.getDirPath() + "/" + downloadID.getFileName())));

                }


                @Override
                public void onError(Error error) {

                    System.out.println("error" + error);


                }
            });
        }

    private void alertAddImageDialog(String email , String img ,  String firstName ,
                                   String lastName , String sn ,  String phone ,
                                   String uuid) {


        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.alert);
        dialog.setMessage(R.string.choose_image_alert);
        dialog.setPositiveButton(R.string.register,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {

                     registerViewModel.register(email, "",fName,lName,spennerItemChooser,classSharedPreferences.getNumber(),classSharedPreferences.getVerficationNumber());

                    }
                });
        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }
   private  String getSpecialNumber(String number){
       String firstString =number.substring(0, 1);
       String secondString = number.substring(1, 4);
       String thirtyString = number.substring(4, 7);
       String lastString = number.substring(7);
        return firstString+"-"+secondString+"-"+thirtyString+"-"+lastString;
   }


}



