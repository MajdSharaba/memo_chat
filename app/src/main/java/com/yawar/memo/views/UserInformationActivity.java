package com.yawar.memo.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yawar.memo.call.RequestCallActivity;
import com.yawar.memo.modelView.UserInformationViewModel;
import com.yawar.memo.service.SocketIOService;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.Api.ServerApi;
import com.yawar.memo.R;
import com.yawar.memo.adapter.MediaAdapter;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.model.MediaModel;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.repositry.BlockUserRepo;
import com.yawar.memo.repositry.ChatRoomRepo;
import com.yawar.memo.utils.BaseApp;
import com.yawar.memo.utils.TimeProperties;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInformationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<MediaModel> recyclerDataArrayList = new ArrayList<>();
    ServerApi serverApi;
    CircleImageView circleImageView;
    LinearLayout linerMore, linerAudioCall,linerVideoCall;
    TextView txtUserName;
    TextView txtSpecialNumber;
    TextView txtState;
    TimeProperties timeProperties;

    BlockUserRepo blockUserRepo;
    UserInformationViewModel userInformationViewModel;
    TextView call;
    TextView video;
    TextView message;
    TextView mute;
    TextView more;
    TextView special_number;
    TextView media;
    String userName;
    String sn;
    String chatId;
    String another_user_id;
    String my_id;
    String fcm_token;
    String imageUrl;
    ClassSharedPreferences classSharedPreferences;
    BaseApp myBase;
    MediaAdapter adapter;
    ImageView imgBtnMessage;
    PopupMenu p;
    ChatRoomRepo chatRoomRepo;
    String blockedFor;
    boolean isBlockForMe = false;
    ImageView imageVideoCall, imageAudioCall, imageMore, imageChat;
    public static final String CHEK = "ConversationActivity.CHECK_CONNECT";


//    float textSize = 14.0F ;
//    SharedPreferences sharedPreferences ;


    private final BroadcastReceiver check = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String check = intent.getExtras().getString("check");
            JSONObject checkObject = null;
            String checkConnect = "false";
            String user_id;

            try {

                checkObject = new JSONObject(check);
                user_id = checkObject.getString("user_id");
                if(user_id.equals(another_user_id)) {
                    checkConnect = checkObject.getString("is_connect");
                    userInformationViewModel.setLastSeen(checkObject.getString("last_seen"));
                    userInformationViewModel.set_state(checkConnect);
                }
                } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };
    private void sendBlockFor(Boolean blocked) {

        JSONObject userBlocked = new JSONObject();
        JSONObject item = new JSONObject();

        try {
            item.put("blocked_for",userInformationViewModel.blockedFor().getValue());
            item.put("Block",blocked);
            userBlocked.put("my_id", my_id);
            userBlocked.put("user_id",another_user_id );
            userBlocked.put("blocked_for",userInformationViewModel.blockedFor().getValue());
            userBlocked.put("userDoBlockName",classSharedPreferences.getUser().getUserName());
            userBlocked.put("userDoBlockSpecialNumber",classSharedPreferences.getUser().getSecretNumber());
            userBlocked.put("userDoBlockImage",classSharedPreferences.getUser().getImage());


        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent service = new Intent(this, SocketIOService.class);
        service.putExtra(SocketIOService.EXTRA_BLOCK_PARAMTERS, userBlocked.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_BLOCK);
        startService(service);


    }

    private void sendUnBlockFor(Boolean blocked) {

        JSONObject userUnBlocked = new JSONObject();

        try {
            userUnBlocked.put("my_id", my_id);
            userUnBlocked.put("user_id",another_user_id );
            userUnBlocked.put("blocked_for",userInformationViewModel.blockedFor().getValue());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent service = new Intent(this, SocketIOService.class);


        service.putExtra(SocketIOService.EXTRA_UN_BLOCK_PARAMTERS, userUnBlocked.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_UN_BLOCK);
        startService(service);


    }
    private void checkConnect() {
        Intent service = new Intent(this, SocketIOService.class);
        JSONObject object = new JSONObject();
        try {
            object.put("my_id", my_id);
            object.put("your_id", another_user_id);
//            socket.emit("check connect", object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        service.putExtra(SocketIOService.EXTRA_CHECK_CONNECT_PARAMTERS, object.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_CHECK_CONNECT);
        startService(service);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_information);
        LocalBroadcastManager.getInstance(this).registerReceiver(check, new IntentFilter(CHEK));
        recyclerView = findViewById(R.id.idCourseRV);
         adapter = new MediaAdapter(recyclerDataArrayList, this);


        // setting grid layout manager to implement grid view.
        // in this method '2' represents number of columns to be displayed in grid view.
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,  LinearLayoutManager.HORIZONTAL,false);

        // at last set adapter to recycler view.
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        initViews();
        initAction();
    }



    private void initViews() {
        classSharedPreferences = BaseApp.getInstance().getClassSharedPreferences();
        userInformationViewModel = new ViewModelProvider(this).get(UserInformationViewModel.class);
        timeProperties = new TimeProperties();


        myBase = BaseApp.getInstance();
        chatRoomRepo = myBase.getChatRoomRepo();
        blockUserRepo= myBase.getBlockUserRepo();
        Bundle bundle = getIntent().getExtras();
       userName = bundle.getString("name", "Default");
       sn = bundle.getString("special", "Default");
       chatId = bundle.getString("chat_id", "Default");
       another_user_id = bundle.getString("user_id", "Default");
        fcm_token = bundle.getString("fcm_token", "Default");
        imageUrl = bundle.getString("image", "Default");
        blockedFor = bundle.getString("blockedFor","");
        my_id = classSharedPreferences.getUser().getUserId();
        checkConnect();
        userInformationViewModel.setBlockedFor(blockedFor);
        txtState = findViewById(R.id.last_seen);
        userInformationViewModel.state.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                System.out.println("stateee"+s);
                if(s!=null){
                    if (s.equals("true")) {
//                        isCoonect = true;
                        System.out.println("dialay"+s);
                        txtState.setText(R.string.connect_now);
                    } else if (s.equals("false")) {
//                        isCoonect = false;

                        if (!userInformationViewModel.getLastSeen().equals("null")) {
                            txtState.setText(getResources().getString(R.string.last_seen) + " " + timeProperties.getDateForLastSeen(UserInformationActivity.this, Long.parseLong(userInformationViewModel.getLastSeen())));
                        }


                    }

                }
            }
        });

        userInformationViewModel.getMedia().observe(this, new androidx.lifecycle.Observer<ArrayList<MediaModel>>() {
                    @Override
                    public void onChanged(ArrayList<MediaModel> mediaModelArrayList) {
                        if (mediaModelArrayList != null) {

                            for (MediaModel media:
                                 mediaModelArrayList) {
                                recyclerDataArrayList.add(media);
                            }


                        }
                        adapter.notifyDataSetChanged();
                    }
                });



//////////////////////////
        userInformationViewModel.isBlocked().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {
                if(s!=null){
                    sendBlockFor(s);
                    userInformationViewModel.setBlocked(null);


                }
            }
        });
        ////////////
        userInformationViewModel.isUnBlocked().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean s) {
                System.out.println("stateee"+s);
                if(s!=null){
//                    conversationModelView.
                    sendUnBlockFor(s);
                    userInformationViewModel.setUnBlocked(null);


                }
            }
        });
        userInformationViewModel.blockedFor().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                boolean isAnyOneBlock = false;
                if (s != null) {
                    if (s.equals(my_id)||s.equals("0")) {
                        isBlockForMe = true;
                        imageAudioCall.setEnabled(false);
                        imageVideoCall.setEnabled(false);
           }
                    else if (s.equals(another_user_id)) {

                        isBlockForMe = false;
                        imageAudioCall.setEnabled(false);
                        imageVideoCall.setEnabled(false);


                    }
             else {
                        isBlockForMe = false;
                        imageAudioCall.setEnabled(true);
                        imageVideoCall.setEnabled(true);


                    }
            }
                else{
                    isBlockForMe = false;
                    imageAudioCall.setEnabled(true);
                    imageVideoCall.setEnabled(true);

                }

        }});

        /////////



        serverApi = new ServerApi(this);
        linerMore = findViewById(R.id.liner_more);
        linerVideoCall=findViewById(R.id.liner_video_call);
        linerAudioCall=findViewById(R.id.liner_audio_call);
        imageVideoCall=findViewById(R.id.img_video_call);
        imageAudioCall=findViewById(R.id.img_audio_call);
        imageMore=findViewById(R.id.img_more);
        imageChat=findViewById(R.id.img_message);












        circleImageView = findViewById(R.id.imageView);
        txtUserName = findViewById(R.id.txt_user_name);
//        txtUserName.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        txtSpecialNumber = findViewById(R.id.txt_special_number);
//        txtSpecialNumber.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        call = findViewById(R.id.call);
//        call.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        video = findViewById(R.id.video);
//        video.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        message = findViewById(R.id.message);
//        message.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

//        mute = findViewById(R.id.mute);
//        mute.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));


        more = findViewById(R.id.more);
//        more.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        special_number = findViewById(R.id.special_number);
//        special_number.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

        media = findViewById(R.id.media);
//        media.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));



        if(!imageUrl.isEmpty()){
            Glide.with(circleImageView.getContext()).load(AllConstants.imageUrl+imageUrl).into(circleImageView);}
       txtUserName.setText(userName);
        System.out.println(sn+"special_number");
        if(!sn.isEmpty()) {
            String firstString = sn.substring(0, 1);
            String secondString = sn.substring(1, 4);
            String thirtyString = sn.substring(4, 7);
            String lastString = sn.substring(7);

            txtSpecialNumber.setText(firstString + "-" + secondString + "-" + thirtyString + "-" + lastString);
        }
        userInformationViewModel.mediaRequest(my_id,another_user_id);
//        getMedia();




    }
    private void initAction() {
        imageChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();


                bundle.putString("reciver_id",another_user_id);

                bundle.putString("sender_id", my_id);
                bundle.putString("fcm_token",fcm_token );

//        bundle.putString("reciver_id",chatRoomModel.reciverId);
                bundle.putString("name",userName);
                bundle.putString("image",imageUrl);
                bundle.putString("chat_id",chatId);
                bundle.putString("blockedFor", userInformationViewModel.blockedFor().getValue());



                Intent intent = new Intent(UserInformationActivity.this, ConversationActivity.class);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });
        //////
        imageMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                block();
                popupMenuExample();

            }
        });
        imageAudioCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                block();
                Intent intent = new Intent(UserInformationActivity.this, RequestCallActivity.class);
                intent.putExtra("anthor_user_id", another_user_id);
                intent.putExtra("user_name", userName);
                intent.putExtra("isVideo", false);
                intent.putExtra("fcm_token", fcm_token);
                intent.putExtra("image_profile", imageUrl);



                startActivity(intent);
            }
        });

        imageVideoCall.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//                block();
            Intent intent = new Intent(UserInformationActivity.this, RequestCallActivity.class);
//                Intent intent = new Intent(ConversationActivity.this, CompleteActivity.class);
            intent.putExtra("anthor_user_id", another_user_id);
            intent.putExtra("user_name", userName);
            intent.putExtra("isVideo", true);
            intent.putExtra("fcm_token", fcm_token);
            intent.putExtra("image_profile", imageUrl);
            startActivity(intent);

        }
    });
}


    private void popupMenuExample() {
        PopupMenu p = new PopupMenu(this, linerMore);
        p.getMenuInflater().inflate(R.menu.main_menu, p .getMenu());

        if(isBlockForMe){
            p.getMenu().findItem(R.id.block).setVisible(false);
            p.getMenu().findItem(R.id.unBlock).setVisible(true);}
        else {
            p.getMenu().findItem(R.id.block).setVisible(true);
            p.getMenu().findItem(R.id.unBlock).setVisible(false);
        }

        p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                UserModel userModel = new UserModel(another_user_id,userName,userName,"","", sn,imageUrl,"");

                switch (item.getItemId()){
                    case R.id.block:
                        AlertDialog.Builder dialog=new AlertDialog.Builder(UserInformationActivity.this);
                        dialog.setTitle(R.string.alert_block_user);
                        dialog.setPositiveButton(R.string.block,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {

//                                        serverApi.block(my_id,userModel);
                                        userInformationViewModel.sendBlockRequest(my_id,another_user_id);
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

                        break;

//
                    case R.id.unBlock:

                    AlertDialog.Builder dialogUnBlock=new AlertDialog.Builder(UserInformationActivity.this);
                        dialogUnBlock.setTitle(R.string.alert_unblock_user);
                        dialogUnBlock.setPositiveButton(R.string.Unblock,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {

//                                    serverApi.unbBlockUser(my_id,userModel);
                                    userInformationViewModel.sendUnBlockRequest(my_id,another_user_id);

                                }
                            });
                        dialogUnBlock.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertUnBlockDialog=dialogUnBlock.create();
                        alertUnBlockDialog.show();

                    break;


//                        p.getMenu().findItem(R.id.block).setVisible(false);
//                        p.getMenu().findItem(R.id.block).setVisible(true);




                }
                return true;
            }
        });
        p.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(check);

    }
}
