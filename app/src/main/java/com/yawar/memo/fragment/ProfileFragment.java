package com.yawar.memo.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.Api.ServerApi;
import com.yawar.memo.R;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.views.LoginActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    float textSize = 14.0F ;
    SharedPreferences sharedPreferences ;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    private ImageView image;
    TextView txName;
    EditText edFname;
    EditText edLname;
    EditText edEmail;
    TextView txnumber;
    EditText edStatus;
    TextView tvspecial;
    TextView textstatue;
    TextView textnumber;
    Button btnUpdate;
    Bitmap bitmap;
    ClassSharedPreferences classSharedPreferences;
    ProgressDialog progressDialog;
    String fName ;
    String lName ;
    String status ;
    String userId ;
    String imageString = "";
    ServerApi serverApi;

    TextView welcome ;
    TextView firstname ;
    TextView last_name ;
    TextView special_number ;
    TextView number ;



    String name;
    Button logOutBtn;
    UserModel userModel;
    private static final int PICK_IMAGE = 100;
    Uri imageUri ;
    private GoogleApiClient googleApiClient;


    private GoogleSignInOptions gso;
    BottomNavigationView bottomNavigationView;
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sharedPreferences = getActivity().getSharedPreferences("txtFontSize", Context.MODE_PRIVATE);

        initView(view);
        initAction();
        return  view;
    }
    private void initView(View view) {

        welcome = view.findViewById(R.id.welcome);
        welcome.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        firstname = view.findViewById(R.id.firstname);

        firstname.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        last_name = view.findViewById(R.id.last_name);
        last_name.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));


        special_number = view.findViewById(R.id.special_number);
        special_number.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));


        number = view.findViewById(R.id.number);
        number.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        txName = view.findViewById(R.id.username);
        txName.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        txnumber = view.findViewById(R.id.number);
        txnumber.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        edFname = view.findViewById(R.id.et_fName);
        edLname = view.findViewById(R.id.et_lName);
        edStatus = view.findViewById(R.id.status);

        tvspecial = view.findViewById(R.id.textspecial);
        tvspecial.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        textstatue = view.findViewById(R.id.textstatue);
        textstatue.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        textnumber = view.findViewById(R.id.textnumber);
        textnumber.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));


        serverApi = new ServerApi(getActivity());
        ClassSharedPreferences classSharedPreferences = new ClassSharedPreferences(getContext());
        userModel = classSharedPreferences.getUser();
        userId = userModel.getUserId();
        fName = userModel.getUserName();
        lName = userModel.getLastName();
        status = userModel.getStatus();
        image = view.findViewById(R.id.image);

        if(!userModel.getUserName().equals(""))
            txName.setHint(userModel.getLastName()+userModel.getUserName());


        txnumber.setHint(userModel.getPhone());
        if(!userModel.getLastName().equals(""))
            edStatus.setText(userModel.getStatus());
        tvspecial.setHint(userModel.getSecretNumber());
        if(!userModel.getUserName().equals(""))

            edFname.setText(userModel.getUserName());
        if(!userModel.getLastName().equals(""))
            edLname.setText(userModel.getLastName());

        btnUpdate = view.findViewById(R.id.btn_update);

//        if(!userModel.getImage().equals("")||userModel.getImage().equals(null) )
//        Glide.with(image).load(globale.base_url+"uploads/profile/"+userModel.getImage()).into(image);


        ///editText.setHint(userModel.getUserName()+userModel.getLastName());
//        logOutBtn = findViewById(R.id.btn_logout);
//        gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//
//        googleApiClient=new GoogleApiClient.Builder(this)
//                .enableAutoManage(this,this)
//                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
//                .build();

    }
    private void initAction() {
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }

        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fName  = edFname.getText().toString();


                lName = edLname.getText().toString();

                status = edStatus.getText().toString();
                System.out.println(fName+lName+status+userId);


                serverApi.updateProfile(fName,lName,status,imageString,userId);

            }
        });

    }
    private void openGallery() {
        /// Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start();
//        startActivityForResult( PICK_IMAGE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode== Activity.RESULT_OK){
            imageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            if(bitmap!=null){
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                /// System.out.println(imageString);

                image.setImageURI(imageUri);
            }}}
    private void gotoLoginActivity(){
        Intent intent=new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
    }


}