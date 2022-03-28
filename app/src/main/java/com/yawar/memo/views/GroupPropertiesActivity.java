package com.yawar.memo.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yawar.memo.Api.ServerApi;
import com.yawar.memo.R;
import com.yawar.memo.adapter.ContactNumberAdapter;
import com.yawar.memo.model.SendContactNumberResponse;
import com.yawar.memo.utils.Globale;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class GroupPropertiesActivity extends AppCompatActivity {
     Globale globale;
    ArrayList<SendContactNumberResponse> sendContactNumberResponses = new ArrayList<SendContactNumberResponse>();
    RecyclerView recyclerView;
    ContactNumberAdapter mainAdapter;
    Bitmap bitmap;
    EditText edName;
    String imageString = "";
    Uri imageUri ;
    private ImageView image;
    TextView group_name;
    TextView memebers;

    float textSize = 14.0F ;
    SharedPreferences sharedPreferences ;

    ServerApi serverApi ;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_properties);


        sharedPreferences =  getSharedPreferences("txtFontSize", Context.MODE_PRIVATE);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        serverApi = new ServerApi(this);
        image = findViewById(R.id.imageProfile);
        edName = findViewById(R.id.et_gName);
        group_name = findViewById(R.id.group_name);

        group_name.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        memebers = findViewById(R.id.memebers);
        memebers.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));


        sendContactNumberResponses=
                (ArrayList<SendContactNumberResponse>)bundle.getSerializable("newPlaylist");
        recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(GroupPropertiesActivity.this));
        mainAdapter = new ContactNumberAdapter(GroupPropertiesActivity.this,sendContactNumberResponses);
        recyclerView.setAdapter(mainAdapter);

        //   sendContactNumberResponses= (ArrayList<SendContactNumberResponse>) b.get("sendContactNumberResponses");


       System.out.println( sendContactNumberResponses.size()+",,,,,,");
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> id = new ArrayList<String>();
                for (SendContactNumberResponse object:
                        sendContactNumberResponses ) {
                    id.add(object.getId());



                }
                String name = String.valueOf(edName.getText());

                serverApi.createGroup(name,imageString,id);
                System.out.println(name+id.size()+imageString);


            }

        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode== Activity.RESULT_OK){
            imageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
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
}