package com.yawar.memo.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.yawar.memo.call.CallProperty;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.Api.ServerApi;
import com.yawar.memo.R;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.language.BottomSheetFragment;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.repositry.AuthRepo;
import com.yawar.memo.repositry.BlockUserRepo;
import com.yawar.memo.repositry.ChatRoomRepo;
import com.yawar.memo.utils.BaseApp;
import com.yawar.memo.utils.VolleyMultipartRequest;
import com.yawar.memo.views.BlockedUsersActivity;
import com.yawar.memo.views.DevicesLinkActivity;
import com.yawar.memo.views.SettingsActivity;
import com.yawar.memo.views.SplashScreen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class SettingsFragment extends Fragment {


    CircleImageView imageView;
    BaseApp myBase;
    private RequestQueue rQueue;
    String currentLanguage = "en", currentLang;
    public static final String TAG = "bottom_sheet";

    int seekValue = 2;
//  TextView name ;
    TextView userName ;
    TextView phoneNumber ;
    TextView setPhoto ;
    TextView setUserName ;
    CardView devises ;
    TextView dev ;
    Bitmap bitmap;
    CardView recentCalls ;
    TextView recentCall ;
    CardView notificationAndSounds ;
    TextView notificationAnd ;
    CardView appearance ;
    TextView Appearanc ;
    CardView language ;
    TextView languag ;
    ServerApi serverApi;
    Uri imageUri ;
    CardView fontSize ;
    TextView fontSiz ;
    CardView blockList ;
    TextView askMemoQuesti ;
    CardView deleteAccount;
    TextView preferene ;
    CardView tellafriend ;
    TextView tellafri ;
    CardView logOut;
    TextView hel ;
    int progressNew = 0 ;
//    float textSize = 14.0F ;
    UserModel userModel;
//    SharedPreferences sharedPreferences ;
    ClassSharedPreferences classSharedPreferences;
    String imageString = "";
    String firstName = "";
    String lastName = "";
    AuthRepo authRepo;
    ChatRoomRepo chatRoomRepo;
    BlockUserRepo blockUserRepo;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
//        CallProperty.setStatusBarOrScreenStatus(getActivity());
        currentLanguage = getActivity().getIntent().getStringExtra(currentLang);

//        sharedPreferences = getActivity().getSharedPreferences("txtFontSize", Context.MODE_PRIVATE);
        classSharedPreferences = BaseApp.getInstance().getClassSharedPreferences();

        userModel = classSharedPreferences.getUser();
        serverApi = new ServerApi(getActivity());
        myBase = BaseApp.getInstance();
        chatRoomRepo = myBase.getChatRoomRepo();
        blockUserRepo = myBase.getBlockUserRepo();
        authRepo = myBase.getAuthRepo();

        getFindViewById(view);

        return view;
    }

    private void getFindViewById(View view) {

        imageView = view.findViewById(R.id.imageView);
        if (!userModel.getImage().isEmpty()) {
            Glide.with(imageView.getContext()).load(AllConstants.imageUrl+userModel.getImage()).apply(RequestOptions.placeholderOf(R.drawable.th).error(R.drawable.th)).into(imageView);
        }

//
//        name =(TextView) view.findViewById(R.id.name);
//        name.setTextSize(textSize);
//        name.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));
//

        userName = view.findViewById(R.id.username);
//        userName.setTextSize(textSize);
        userName.setText(userModel.getUserName()+" "+userModel.getLastName());
//        userName.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        phoneNumber = view.findViewById(R.id.phoneNumber);
//        phoneNumber.setTextSize(textSize);
//        phoneNumber.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));
        if(userModel.getPhone()!=null) {
            String firstString = userModel.getPhone().substring(0, 4);
            String secondString = userModel.getPhone().substring(4, 7);
            String thirtyString = userModel.getPhone().substring(7, 10);
            String lastString = userModel.getPhone().substring(10);

            phoneNumber.setText(firstString + "-" + secondString + "-" + thirtyString + "-" + lastString);
        }


        setPhoto = view.findViewById(R.id.selectImage);
//        setPhoto.setTextSize(textSize);
//        setPhoto.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        setUserName = view.findViewById(R.id.setUserName);
//        setUserName.setTextSize(textSize);
//        setUserName.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        devises = view.findViewById(R.id.devices);
        dev = view.findViewById(R.id.dev);
//        dev.setTextSize(textSize);
//        dev.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

//        recentCalls = view.findViewById(R.id.recentCalls);
//        recentCall = view.findViewById(R.id.recentCall);
//        recentCall.setTextSize(textSize);
//        recentCall.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

//        notificationAndSounds = view.findViewById(R.id.notificationAndSound);
//        notificationAnd = view.findViewById(R.id.notificationAnd);
//        notificationAnd.setTextSize(textSize);
//        notificationAnd.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        appearance = view.findViewById(R.id.Appearance);
        Appearanc = view.findViewById(R.id.Appearanc);
//        Appearanc.setTextSize(textSize);
//        Appearanc.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        language = view.findViewById(R.id.language);
        languag = view.findViewById(R.id.languag);
//        languag.setTextSize(textSize);
//        languag.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

//        fontSize = view.findViewById(R.id.fontSize);
//        fontSiz = view.findViewById(R.id.fontSiz);
//        fontSiz.setTextSize(textSize);
//        fontSiz.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        blockList = view.findViewById(R.id.contact_number_blocked);
        askMemoQuesti = view.findViewById(R.id.askMemoQuesti);
//        askMemoQuesti.setTextSize(textSize);
//        askMemoQuesti.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        deleteAccount = view.findViewById(R.id.delete_accont);
        preferene = view.findViewById(R.id.preferene);
//        preferene.setTextSize(textSize);
//        preferene.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

//        tellafriend = view.findViewById(R.id.tellAFriend);
//        tellafri = view.findViewById(R.id.tellafri);
//        tellafri.setTextSize(textSize);
//        tellafri.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        logOut = view.findViewById(R.id.log_out);
        hel = view.findViewById(R.id.hel);
//        hel.setTextSize(textSize);
//        hel.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        getOnClick();
    }


    private void getOnClick() {

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "This Image View", Toast.LENGTH_SHORT).show();
            }
        });
//        name.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getActivity(), "This Name", Toast.LENGTH_SHORT).show();
//            }
//        });
        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "This User Name", Toast.LENGTH_SHORT).show();
            }
        });
        phoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "This phoneNumber", Toast.LENGTH_SHORT).show();
            }
        });
        setPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ImagePicker.with(getActivity())
//                        .crop()	    			//Crop image(Optional), Check Customization for more option
//                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
//                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
//                        .start();
////                        startActivityForResult( PICK_IMAGE);
                openGallery();

            }
        });
        setUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                View mView = getLayoutInflater().inflate(R.layout.input_name_dialog,null);
                final EditText txt_inputFirstName = mView.findViewById(R.id.ed_first_name);
                final EditText txt_inputLastName = mView.findViewById(R.id.ed_last_name);
                txt_inputFirstName.setHint(userModel.getUserName());
                txt_inputLastName.setHint(userModel.getLastName());

                Button btn_cancel = mView.findViewById(R.id.btn_cancel);
                Button btn_okay = mView.findViewById(R.id.btn_add);
                alert.setView(mView);
                final AlertDialog alertDialog = alert.create();
                alertDialog.setCanceledOnTouchOutside(false);
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                btn_okay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!txt_inputFirstName.getText().toString().isEmpty()){

                            firstName= txt_inputFirstName.getText().toString();
                        }
                        else {
                            firstName = userModel.getUserName();

                        }
                        if(!txt_inputLastName.getText().toString().isEmpty()){
                            lastName= txt_inputLastName.getText().toString();

                        }
                        else{
                            lastName = userModel.getLastName();


                        }
                        System.out.println(firstName+"firstName"+lastName+"lastName");

                        serverApi.updateProfile(firstName,lastName,"","",userModel.getUserId());

                        userName.setText(firstName+" "+lastName);
                        userModel.setUserName(firstName);
                        userModel.setLastName(lastName);
//                        classSharedPreferences.setUser(userModel);


                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
            });


        devises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DevicesLinkActivity.class);
                startActivity(intent);
            }
        });
//        notificationAndSounds.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Toast.makeText(getActivity(), "This Name", Toast.LENGTH_SHORT).show();
//
//            }
//        });
        appearance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SettingsActivity.class);

                startActivity(intent);//                Toast.makeText(getActivity(), "This Notification And Sounds", Toast.LENGTH_SHORT).show();

            }
        });

        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BottomSheetFragment fragment = new BottomSheetFragment();
                fragment.show(getActivity().getSupportFragmentManager(), TAG);

            }
        });
/*

        fontSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "This Font Size", Toast.LENGTH_SHORT).show();


                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View dialogView = inflater.inflate(R.layout.fon_size_layout, null);
                builder.setView(dialogView);
                AlertDialog dialogadd = builder.create();


                TextView textViewFont , resultFontSize ;

                Button btnChange  ;
                SharedPreferences sharedPreferences ;
                SeekBar seekBar ;


//                textViewFont   = dialogView.findViewById(R.id.textViewFont);
                resultFontSize = dialogView.findViewById(R.id.resultFontSize);

//                btnChange   = dialogView.findViewById(R.id.btnChange);
                sharedPreferences = getActivity().getSharedPreferences("txtFontSize", Context.MODE_PRIVATE);

                seekBar = dialogView.findViewById(R.id.seekbar);

//                textViewFont.setTextSize(textSize); // size 20sp
                resultFontSize.setTextSize(textSize);


                resultFontSize.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                        textSize = textSize + (progress - progressNew);
                        progressNew = progress ;
//                        fontSize.setSize(progressNew);

//                        textViewFont.setTextSize(fontSize.getSize());
//                        resultFontSize.setTextSize(fontSize.getSize());
//                        name.setTextSize(fontSize.getSize());
//                        userName.setTextSize(fontSize.getSize());
//                        phoneNumber.setTextSize(fontSize.getSize());
//                        setUserName.setTextSize(fontSize.getSize());
//                        setPhoto.setTextSize(fontSize.getSize());
//                        dev.setTextSize(fontSize.getSize());
//                        recentCall.setTextSize(fontSize.getSize());
//                        notificationAnd.setTextSize(fontSize.getSize());
//                        Appearanc.setTextSize(fontSize.getSize());
//                        languag.setTextSize(fontSize.getSize());
//                        fontSiz.setTextSize(fontSize.getSize());
//                        askMemoQuesti.setTextSize(fontSize.getSize());
//                        preferene.setTextSize(fontSize.getSize());
//                        tellafri.setTextSize(fontSize.getSize());
//                        hel.setTextSize(fontSize.getSize());


                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        //textViewSize.setText(progressNew + "/" + seekBar.getMax());


                    }
                });

//                btnChange.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
////                        String  txtFontSize = String.valueOf(textViewFont.getTextSize());
//
//                        SharedPreferences.Editor editor  = sharedPreferences.edit();
////                        editor.putString("txtFontSize", txtFontSize);
//                        editor.commit();
//                        Toast.makeText(getActivity(), "Information Saved", Toast.LENGTH_SHORT).show();
//
//
//                        resultFontSize.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));
//
//                    }
//                });


                resultFontSize.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));





                dialogadd.show();




            }
        });
*/

//        fontSize.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getActivity(), "This Font Size", Toast.LENGTH_SHORT).show();
//
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                LayoutInflater inflater = LayoutInflater.from(getActivity());
//                View dialogView = inflater.inflate(R.layout.fon_size_layout, null);
//                builder.setView(dialogView);
//                AlertDialog dialogadd = builder.create();
//
//
//                TextView textViewFont , resultFontSize ;
//
//                Button btnChange  ;
//                SharedPreferences sharedPreferences ;
//                SeekBar seekBar ;
//
//
////              textViewFont   = dialogView.findViewById(R.id.textViewFont);
//                resultFontSize = dialogView.findViewById(R.id.resultFontSize);
////              btnChange   = dialogView.findViewById(R.id.btnChange);
//                sharedPreferences = getActivity().getSharedPreferences("txtFontSize", Context.MODE_PRIVATE);
//                seekBar = dialogView.findViewById(R.id.seekbar);
//
////              textViewFont.setTextSize(textSize); // size 20sp
//                resultFontSize.setTextSize(seekValue);
//
//
//                resultFontSize.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));
//
//                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                    @Override
//                    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
//                        seekValue = progress;
//
//                        resultFontSize.setText("Memo Font Size");
//                        resultFontSize.setTextSize(seekValue);
//                        //   name.setTextSize(seekValue);
//                        userName.setTextSize(seekValue);
//                        phoneNumber.setTextSize(seekValue);
//                        setUserName.setTextSize(seekValue);
//                        setPhoto.setTextSize(seekValue);
//                        dev.setTextSize(seekValue);
//                        recentCall.setTextSize(seekValue);
//                        notificationAnd.setTextSize(seekValue);
//                        Appearanc.setTextSize(seekValue);
//                        languag.setTextSize(seekValue);
//                        fontSiz.setTextSize(seekValue);
//                        askMemoQuesti.setTextSize(seekValue);
//                        preferene.setTextSize(seekValue);
//                        tellafri.setTextSize(seekValue);
//                        hel.setTextSize(seekValue);
//                    }
//
//                    @Override
//                    public void onStartTrackingTouch(SeekBar seekBar) {
//                        String temp = "Processing...";
//                        resultFontSize.setText(temp);
//
//                    }
//
//                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
//                    @Override
//                    public void onStopTrackingTouch(SeekBar seekBar) {
//                        //textViewSize.setText(progressNew + "/" + seekBar.getMax());
//
//                        seekBar.getAccessibilityTraversalAfter();
//                        //   seekBar.get
//
//                        resultFontSize.setText("Memo Font Size");
//                        resultFontSize.setTextSize(seekValue);
//                        //    name.setTextSize(seekValue);
//                        userName.setTextSize(seekValue);
//                        phoneNumber.setTextSize(seekValue);
//                        setUserName.setTextSize(seekValue);
//                        setPhoto.setTextSize(seekValue);
//                        dev.setTextSize(seekValue);
//                        recentCall.setTextSize(seekValue);
//                        notificationAnd.setTextSize(seekValue);
//                        Appearanc.setTextSize(seekValue);
//                        languag.setTextSize(seekValue);
//                        fontSiz.setTextSize(seekValue);
//                        askMemoQuesti.setTextSize(seekValue);
//                        preferene.setTextSize(seekValue);
//                        tellafri.setTextSize(seekValue);
//                        hel.setTextSize(seekValue);
//
//
//                        SharedPreferences.Editor editor  = sharedPreferences.edit();
//                        editor.putString("txtFontSize", String.valueOf(seekValue));
//                        editor.commit();
//
//                        resultFontSize.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));
//
//                    }
//                });
//
////                btnChange.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View view) {
//////                        String  txtFontSize = String.valueOf(textViewFont.getTextSize());
////                        SharedPreferences.Editor editor  = sharedPreferences.edit();
//////                        editor.putString("txtFontSize", txtFontSize);
////                        editor.commit();
////                        Toast.makeText(getActivity(), "Information Saved", Toast.LENGTH_SHORT).show();
////                        resultFontSize.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));
////                    }
////                });
//
//
//                //    resultFontSize.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));
//
//
//
//
//
//                dialogadd.show();
//
//
//
//
//            }
//        });
        blockList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), BlockedUsersActivity.class);
                startActivity(intent);
            }
        });

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder dialog=new android.app.AlertDialog.Builder(getActivity());
                dialog.setTitle(R.string.alert_delete_account);
                dialog.setPositiveButton(R.string.delete_account,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                serverApi.deleteAccount();

                            }
                        });
                dialog.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                android.app.AlertDialog alertDialog=dialog.create();
                alertDialog.show();

            }
        });

//        tellafriend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               // Toast.makeText(getActivity(), "This Tell a Friend", Toast.LENGTH_SHORT).show();
//
//                Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.setType("text/plain");
//              //  String Body ="حمل هذا التطبيق";
//                String Sub ="https://play.google.com/store/apps/details?id=com.facebook.katana"+"\n"
//                        +"حمل التطبيق الان.";
//           //     intent.putExtra(Intent.EXTRA_TEXT,Body);
//                intent.putExtra(Intent.EXTRA_TEXT,Sub);
//                startActivity(Intent.createChooser(intent,"Share Using"));
//
//            }
//        });
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder dialog=new android.app.AlertDialog.Builder(getActivity());
                dialog.setTitle(R.string.alert_log_out);
                dialog.setPositiveButton(R.string.logout,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                classSharedPreferences.setUser(null);
                                classSharedPreferences.setVerficationNumber(null);
                                authRepo.jsonObjectMutableLiveData.setValue(null);
                                chatRoomRepo.chatRoomListMutableLiveData.setValue(null);
                                blockUserRepo.userBlockListMutableLiveData.setValue(null);
                                Intent intent = new Intent(getContext(), SplashScreen.class);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        });
                dialog.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                android.app.AlertDialog alertDialog=dialog.create();
                alertDialog.show();

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

        if (resultCode == Activity.RESULT_OK) {
            imageUri = data.getData();
           System.out.println("this is image URLllllll"+imageUri);

            File myFileImage = new File(imageUri.toString());




            if (imageUri.toString().startsWith("content://")) {
                Cursor cursor = null;
                try {
                    cursor = getActivity().getContentResolver().query(imageUri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        @SuppressLint("Range") String displayNamee = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        Log.d("nameeeee>>>>  ", displayNamee);
//                                    System.out.println(displayNamee);

                        uploadImage(displayNamee, imageUri);
                    }
                } finally {
                    cursor.close();
                }
            } else if (imageUri.toString().startsWith("file://")) {
                String displayNamee = myFileImage.getName();
                System.out.println(displayNamee + "lkkkkkkkkkkkkkkkk");
                uploadImage(displayNamee, imageUri);


                Log.d("nameeeee>>>>  ", displayNamee);
            }
            imageView.setImageURI(imageUri);

        }
    }
    private void uploadImage(final String imageName, Uri pdfFile) {

        InputStream iStream = null;
        try {
                iStream = getActivity().getContentResolver().openInputStream(pdfFile);
                System.out.println(pdfFile);
                //"file:///storage/emulated/0/memo/1640514470604.3gp"
               final byte[] inputData = getBytes(iStream);


            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, AllConstants.base_url_final+"upadteImageProfile",
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            System.out.println("responeeeeeeeeeeee" + new String(response.data));

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





                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
//                            Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
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
                    params.put("id", userModel.getUserId());


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
            rQueue = Volley.newRequestQueue(getActivity());
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

