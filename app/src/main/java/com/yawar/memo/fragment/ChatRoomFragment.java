package com.yawar.memo.fragment;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Environment;
import android.view.LayoutInflater;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;
import com.tsuryo.swipeablerv.SwipeableRecyclerView;
import com.yawar.memo.Api.ClassSharedPreferences;
import com.yawar.memo.Api.ServerApi;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.service.SocketIOService;
import com.yawar.memo.utils.Globale;
import com.yawar.memo.views.ArchivedActivity;
import com.yawar.memo.views.ContactNumberActivity;
import com.yawar.memo.views.ConversationActivity;
import com.yawar.memo.R;
import com.yawar.memo.adapter.ChatRoomAdapter;
import com.yawar.memo.model.ChatRoomModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import com.yawar.memo.utils.BaseApp;
import com.yawar.memo.views.GroupSelectorActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatRoomFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatRoomFragment extends Fragment implements ChatRoomAdapter.CallbackInterfac, Observer {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatRoomFragment() {
        // Required empty public constructor
    }
//    RecyclerView recyclerView;
//    List<ChatRoomModel> data;
//    ChatRoomAdapter itemAdapter;

    public static final String ON_CHANGE_DATA_RECEIVER = "android.zeroprojects.mafia.activity.ON_CHANGE_DATA_RECEIVER";
    public static final String ON_SOCKET_CONNECTION = "android.zeroprojects.mafia.activity.ON_SOCKET_CONNECTION";
    public static final String ON_MESSAGE_RECEIVED = "ConversationActivity.ON_MESSAGE_RECEIVED";
    public static final String TYPING = "ConversationActivity.ON_TYPING";

    public static final String NEW_MESSAGE ="new Message" ;



//    private static final String TAG = BasicActivity.class.getSimpleName();

    SwipeableRecyclerView recyclerView;
    List<ChatRoomModel> data;
    List<ChatRoomModel> postList = new ArrayList<>();
    List<ChatRoomModel> archived = new ArrayList<>();
    String myId;
    BaseApp myBase;

    ChatRoomAdapter itemAdapter;
    SearchView searchView;
    Toolbar toolbar;
    ClassSharedPreferences classSharedPreferences;
    ServerApi serverApi;
    UserModel userModel;
    Globale globale;
    ImageButton iBAddArchived;
    LinearLayout linerArchived;
    boolean isArchived;

    TextView chat ;
    float textSize = 14.0F ;
    SharedPreferences sharedPreferences ;



    //    public static void start(Context context) {
//        Intent starter = new Intent(context, BasicActivity.class);
//        context.startActivity(starter);
//    }
    private BroadcastReceiver onSocketConnect = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConnected = intent.getExtras().getBoolean("status");

        }
    };
    private BroadcastReceiver reciveNwMessage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                 System.out.println("newMessssssssssssssssssssssssssssssssssssssssssssge");
                    String objectString = intent.getExtras().getString("message");
                    JSONObject message = null;
                    try {
                        message = new JSONObject(objectString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    String text = "";
                    String type = "";
                    String state = "";
                    String senderId = "";
                    String reciverId = "";
                    String id = "";
                    String fileName = "";
                String chatId = "";
                String dateTime = "";

            try {

                        /// JSONObject jsonObject= (JSONObject) messageJson.get("data");
                        text = message.getString("message");
                        type = message.getString("message_type");
                        state = message.getString("state");
                        senderId = message.getString("sender_id");
//                        id = message.getString("message_id");
                        reciverId = message.getString("reciver_id");
                        chatId =  message.getString("chat_id");
                       dateTime = message.getString("dateTime");
//                        fileName = message.getString("orginalName");


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            String anthor_id="";
            if(senderId.equals(myId)){
                anthor_id= reciverId;
            }
            else {
                anthor_id = senderId;
            }

                if(!state.equals("3")){
                    System.out.println("set Last Messageeeeeeeeeeeeeeeee");
            myBase.getObserver().setLastMessage(text,chatId,myId,anthor_id,type,state,dateTime);
                }
            }





    };
    private BroadcastReceiver reciveTyping = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

                    String typingString = intent.getExtras().getString("typing");
                    System.out.println("typinnnnnnnnnnnnnnnng");
                    JSONObject message = null;
                    String isTyping = "false";
                    String anthor_id = "";
                    String chat_id= "";

                    try {
                        message = new JSONObject(typingString);
                        isTyping = message.getString("typing");
                        anthor_id = message.getString("my_id");
                        chat_id = message.getString("chat_id");


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(isTyping.equals("true")){
                        myBase.getObserver().setTyping(chat_id,true);

                    }
                    else{
                        myBase.getObserver().setTyping(chat_id,false);
                    }


//                    if(anthor_user_id.equals(anthor_id)){
//
//                        if (isTyping.equals("true")) {
//                            tv_state.setText(R.string.writing_now);
//                            tv_state.setVisibility(View.VISIBLE);
//                        } else if (isCoonect) {
//                            tv_state.setText(R.string.connect_now);
//                        } else {
//                            tv_state.setText(getResources().getString(R.string.last_seen)+" "+timeProperties.getFormattedDate(context,Long.parseLong(lastSeen)));
//

//                        tv_state.setVisibility(View.GONE);
//                        }
////
//                    }}

        }
    };
//    private BroadcastReceiver reciveNewChat = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            String objectString = intent.getExtras().getString("new chat");
//            System.out.println(objectString+"new chattttttttttttttttttttttttttttttt");
//
//            JSONObject jsonObject = null;
//            try {
//                jsonObject = new JSONObject(objectString);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//
//
//            JSONObject message = null;
//            JSONObject user = null;
//            String text = "";
//            String chatId= null;
////            String senderId = "";
////            String reciverId = "";
////            String id = "";
////            String fileName = "";
////            String chatId = "";
//
//            try {
//
//                /// JSONObject jsonObject= (JSONObject) messageJson.get("data");
//                message = jsonObject.getJSONObject("message");
//                text = message.getString("message");
//                user = jsonObject.getJSONObject("user");
//                chatId = jsonObject.getString("chat_id");
//
//
//                if(!user.getString("id").equals(myId)) {
//                    myBase.getObserver().addChatRoom(new ChatRoomModel(
//                            user.getString("first_name"),
//                            user.getString("id"),
//                            text,
//
//
//                            user.getString("profile_image")
//                            ,
//                            false,
//                            "1",
//                            chatId,
//                            "null",
//                            "1",
//                            false,
//                            user.getString("user_token"),
//                            "",
//                             "type",
//                              "1",
//                                 "1646028789098"
//
//
////                                "https://th.bing.com/th/id/OIP.2s7VxdmHEoDKji3gO_i-5QHaHa?pid=ImgDet&rs=1"
//
//                    ));
//                }
//                    //              state = message.getString("state");
////                senderId = message.getString("sender_id");
////                id = message.getString("message_id");
////                reciverId = message.getString("reciver_id");
////                chatId =  message.getString("chat_id");
//////                        fileName = message.getString("orginalName");
//
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
////            String anthor_id="";
////            if(senderId.equals(myId)){
////                anthor_id= reciverId;
////            }
////            else {
////                anthor_id = senderId;
////            }
////
////            if(!state.equals("3")){
////                myBase.getObserver().setLastMessage(text,chatId,myId,anthor_id);
//            }
//
////
////
////
////
////
//    };


    public static ChatRoomFragment newInstance(String param1, String param2) {
        ChatRoomFragment fragment = new ChatRoomFragment();
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
        View view = inflater.inflate(R.layout.fragment_chat_room, container, false);
//        Intent service = new Intent(getContext(), SocketIOService.class);
//       getActivity().startService(service);
//        connectSocket();
//        LocalBroadcastManager.getInstance(getContext()).registerReceiver(reciveNwMessage, new IntentFilter(ON_MESSAGE_RECEIVED));
//        LocalBroadcastManager.getInstance(getContext()).registerReceiver(reciveTyping, new IntentFilter(TYPING));

//        LocalBroadcastManager.getInstance(getContext()).registerReceiver(reciveNewChat, new IntentFilter(NEW_MESSAGE));

//        Locale locale = new Locale(lan);
//        Locale.setDefault(locale);
//        Resources resources = this.getResources();
//        Configuration config = resources.getConfiguration();
//        config.setLocale(locale);
//        resources.updateConfiguration(config, resources.getDisplayMetrics());
        myBase = BaseApp.getInstance();

        myBase.getObserver().addObserver(this);
        classSharedPreferences = new ClassSharedPreferences(getContext());

        myId = classSharedPreferences.getUser().getUserId();


//        SharedPreferences prefs = getSharedPreferences("languag", MODE_PRIVATE);
//
//        prefs.edit().putString("lan", "en").commit();

        ////////////for toolbar
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Memo");
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        activity.setSupportActionBar(toolbar);





//        };
        linerArchived = view.findViewById(R.id.liner_archived);
        linerArchived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ArchivedActivity.class);

                startActivity(intent);

            }
        });

        recyclerView =  view.findViewById(R.id.recycler);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        isArchived = myBase.getObserver().isArchived();
        System.out.println("isArchived"+isArchived);

        if(isArchived){
            System.out.println(isArchived);
            linerArchived.setVisibility(View.VISIBLE);

        }
          for(ChatRoomModel chatRoomModel:myBase.getObserver().getChatRoomModelList()) {
              if (!chatRoomModel.getState().equals("0")&&!chatRoomModel.getState().equals(myId)) {
                  System.out.println(chatRoomModel.getState() + "statttttttttttttttttttttte");
                  postList.add(chatRoomModel);
              }
          }
        itemAdapter = new ChatRoomAdapter(postList, this);
        recyclerView.setAdapter(itemAdapter);
        recyclerView.setListener(new SwipeLeftRightCallback.Listener() {
            @Override
            public void onSwipedLeft(int position) {

                delete(postList.get(position));
            }

            @Override
            public void onSwipedRight(int position) {
                addToArchived(postList.get(position));
                myBase.getObserver().setArchived(true);

                linerArchived.setVisibility(View.VISIBLE);

            }
        });

//        postList =serverApi.getChatRoom(recyclerView,listener);
//         itemAdapter = new ChatRoomAdapter(postList,BasicActivity.this, listener);
////                itemAdapter=new ChatRoomAdapter(getApplicationContext(),postList);
//        recyclerView.setAdapter(itemAdapter);
//        itemAdapter.notifyDataSetChanged();
//        //        itemAdapter.notifyDataSetChanged(); recyclerView.setAdapter(itemAdapter);
        itemAdapter.notifyDataSetChanged();


        ////////////////FloatingActionButton
        FloatingActionButton fab = view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), ContactNumberActivity.class);
                startActivity(intent);

            }

        });


//        ChatRoomFragment chatRoomFrafment = new ChatRoomFragment();
////////////// for search
        searchView = view.findViewById(R.id.search);
        CharSequence charSequence = searchView.getQuery();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                itemAdapter.getFilter().filter(newText);
                return false;
            }
        });

/////// for Bottom nav


        sharedPreferences = getActivity().getSharedPreferences("txtFontSize", Context.MODE_PRIVATE);

        chat = view.findViewById(R.id.chat);
//        chat.setTextSize(textSize);
//        chat.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));



        return view;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("on destrooyyyyyyyyyy");
//        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(onSocketConnect);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(reciveNwMessage);
//        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(reciveNewChat);



    }



    @Override
    public void onResume() {

        itemAdapter.notifyDataSetChanged();
//        connectSocket();


        super.onResume();
//        itemAdapter.notifyDataSetChanged();

//        GetData();


    }

    @Override
    public void onStop() {
        System.out.println("oStooooop");
        super.onStop();
    }

    @Override
    public void onPause() {
        System.out.println("opause");

        super.onPause();
    }


    private void connectSocket() {
        Intent service = new Intent(getContext(), SocketIOService.class);
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_JOIN);
        getContext().startService(service);
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.basic_menu, menu);
         super.onCreateOptionsMenu(menu,inflater);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getActivity().getMenuInflater().inflate(R.menu.basic_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.group:
                Intent intent = new Intent(getActivity(), GroupSelectorActivity.class);
                startActivity(intent);

                return true;
            case R.id.item2:
                Toast.makeText(getActivity(),"Item 2 Selected",Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }







    @Override
    public void onHandleSelection(int position, ChatRoomModel chatRoomModel) {
//        System.out.println("mmmmmmmmmmmmmmmmmmmajd");
//
//        Toast.makeText(getContext(), "Position " + chatRoomModel.lastMessage, Toast.LENGTH_SHORT).show();
//        System.out.println(chatRoomModel.name);
        Bundle bundle = new Bundle();


            bundle.putString("reciver_id",chatRoomModel.userId);

        bundle.putString("sender_id", myId);
        bundle.putString("fcm_token",chatRoomModel.fcmToken );

//        bundle.putString("reciver_id",chatRoomModel.reciverId);
        bundle.putString("name",chatRoomModel.name);
        bundle.putString("image",chatRoomModel.getImage());
        bundle.putString("chat_id",chatRoomModel.getChatId());
        bundle.putString("special", chatRoomModel.getSpecialNumber());

        ///////////////////////



        Intent intent = new Intent(getContext(), ConversationActivity.class);
        intent.putExtras(bundle);

        startActivity(intent);

    }


    private void addToArchived(ChatRoomModel chatRoomModel) {
        System.out.println(chatRoomModel.lastMessage);
        final ProgressDialog progressDialo = new ProgressDialog(getContext());
        // url to post our data
//        String url = "http://192.168.1.8:8000/archivechat";
        progressDialo.setMessage(getResources().getString(R.string.prograss_message));
        progressDialo.show();
        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(getContext());
        // on below line we are calling a string
        // request method to post the data to our API
        // in this we are calling a post method.
        StringRequest request = new StringRequest(Request.Method.POST, AllConstants.add_to_archived_url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialo.dismiss();
                System.out.println("Data added to API+"+response);
                myBase.getObserver().setState(chatRoomModel.chatId,myId);
                itemAdapter.notifyDataSetChanged();



            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
//                Toast.makeText(getContext(), "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // below line we are creating a map for
                // storing our values in key and value pair.
                Map<String, String> params = new HashMap<String, String>();

                // on below line we are passing our key
                // and value pair to our parameters.
                params.put("my_id",myId );
                params.put("your_id", chatRoomModel.userId);

                // at last we are
                // returning our params.
                return params;
            }
        };
        // below line is to make
        // a json object request.
        queue.add(request);
    }
    private void delete(ChatRoomModel chatRoomModel) {
        System.out.println(chatRoomModel.userId+"hatRoomModel.reciverId"+myId);
        final ProgressDialog progressDialo = new ProgressDialog(getContext());
        // url to post our data
        progressDialo.setMessage(getResources().getString(R.string.prograss_message));
        progressDialo.show();
        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(getContext());
        // on below line we are calling a string
        // request method to post the data to our API
        // in this we are calling a post method.
        StringRequest request = new StringRequest(Request.Method.POST, AllConstants.delete_conversation, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialo.dismiss();
                System.out.println("Data added to API+"+response);
                myBase.getObserver().deleteChatRoom(chatRoomModel.chatId);
                itemAdapter.notifyDataSetChanged();


            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
//                Toast.makeText(getContext(), "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // below line we are creating a map for
                // storing our values in key and value pair.
                Map<String, String> params = new HashMap<String, String>();

                // on below line we are passing our key
                // and value pair to our parameters.
                params.put("my_id",myId );
                params.put("your_id", chatRoomModel.userId);

                // at last we are
                // returning our params.
                return params;
            }
        };
        // below line is to make
        // a json object request.
        queue.add(request);
    }


    @Override
    public void update(Observable observable, Object o) {
        System.out.println("outttttttttttttttt");
        postList.clear();
        for(ChatRoomModel chatRoomModel:myBase.getObserver().getChatRoomModelList()){
            if(!chatRoomModel.getState().equals("0")&&!chatRoomModel.getState().equals(myId))
                postList.add(chatRoomModel);}
//        postList=myBase.getObserver().getChatRoomModelList();
        isArchived=myBase.getObserver().isArchived();
        if(isArchived){
            linerArchived.setVisibility(View.VISIBLE);

        }
        else{
            linerArchived.setVisibility(View.GONE);

        }

         System.out.println("frommmmmmmmmmmmmmmmmm herrrrrrrrrrrrrrr");
//        itemAdapter.notifyDataSetChanged();
        itemAdapter = new ChatRoomAdapter(postList, this);


        recyclerView.setAdapter(itemAdapter);



    }

}