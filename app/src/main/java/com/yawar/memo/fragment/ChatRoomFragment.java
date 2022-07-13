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
import androidx.lifecycle.ViewModelProvider;
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
import com.yawar.memo.Api.ServerApi;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.modelView.ChatRoomViewModel;
import com.yawar.memo.modelView.IntroActModelView;
import com.yawar.memo.repositry.ChatRoomRepo;
import com.yawar.memo.service.SocketIOService;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
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
import com.yawar.memo.views.DashBord;
import com.yawar.memo.views.GroupSelectorActivity;
import com.yawar.memo.views.IntroActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatRoomFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatRoomFragment extends Fragment implements ChatRoomAdapter.CallbackInterfac {

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
    ChatRoomViewModel chatRoomViewModel;
    ChatRoomAdapter itemAdapter;
    SearchView searchView;
    Toolbar toolbar;
    ClassSharedPreferences classSharedPreferences;
    ServerApi serverApi;
    UserModel userModel;
    Globale globale;
    ImageButton iBAddArchived;
    ChatRoomRepo chatRoomRepo;
    LinearLayout linerArchived;
    boolean isArchived;

    TextView chat ;
    float textSize = 14.0F ;
    SharedPreferences sharedPreferences ;








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

        myBase = BaseApp.getInstance();

        classSharedPreferences = new ClassSharedPreferences(getContext());

        myId = classSharedPreferences.getUser().getUserId();



        ////////////for toolbar
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("");
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
//        isArchived = myBase.getObserver().isArchived();
        chatRoomViewModel = new ViewModelProvider(this).get(ChatRoomViewModel.class);

        chatRoomRepo = myBase.getChatRoomRepo();
        chatRoomRepo.isArchivedMutableLiveData.observe(getActivity(), new androidx.lifecycle.Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    linerArchived.setVisibility(View.VISIBLE);
                }
                else{
                    linerArchived.setVisibility(View.GONE);
                }
            }
        });



        chatRoomViewModel.loadData().observe(getActivity(), new androidx.lifecycle.Observer<ArrayList<ChatRoomModel>>() {
            @Override
            public void onChanged(ArrayList<ChatRoomModel> chatRoomModels) {
                if(chatRoomModels!=null){
                    ArrayList<ChatRoomModel> list = new ArrayList<>();
                    postList.clear();
                    for(ChatRoomModel chatRoomModel:chatRoomModels) {
                        if (!chatRoomModel.getState().equals("0")&&!chatRoomModel.getState().equals(myId)) {
//                            System.out.println(chatRoomModel.getState() + "statttttttttttttttttttttte");
                            list.add(chatRoomModel.clone());
                            postList.add(chatRoomModel);
                            System.out.println(postList.size()+"this is size");
                        }
                    }

                                        itemAdapter.setData((ArrayList<ChatRoomModel>) list);


                }

            }
        });
        itemAdapter = new ChatRoomAdapter( this);
        recyclerView.setAdapter(itemAdapter);
        recyclerView.setListener(new SwipeLeftRightCallback.Listener() {
            @Override
            public void onSwipedLeft(int position) {

//                delete(postList.get(position));
                chatRoomViewModel.deleteChatRoom(myId,postList.get(position).userId);
            }

            @Override
            public void onSwipedRight(int position) {
//                addToArchived(postList.get(position));
                chatRoomViewModel.addToArchived(myId,postList.get(position).userId);

//                chatRoomRepo.setArchived(true);


            }
        });




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
        chat.setTextSize(textSize);
        chat.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));



        return view;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();




    }



    @Override
    public void onResume() {



        super.onResume();



    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {

        super.onPause();
    }



    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.basic_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }


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
        bundle.putString("blockedFor",chatRoomModel.blockedFor);


        ///////////////////////



        Intent intent = new Intent(getContext(), ConversationActivity.class);
        intent.putExtras(bundle);

        startActivity(intent);

    }


//    private void addToArchived(ChatRoomModel chatRoomModel) {
//        System.out.println(chatRoomModel.lastMessage);
//        final ProgressDialog progressDialo = new ProgressDialog(getContext());
//        // url to post our data
////        String url = "http://192.168.1.8:8000/archivechat";
//        progressDialo.setMessage(getResources().getString(R.string.prograss_message));
//        progressDialo.show();
//        // creating a new variable for our request queue
//        RequestQueue queue = Volley.newRequestQueue(getContext());
//        // on below line we are calling a string
//        // request method to post the data to our API
//        // in this we are calling a post method.
//        StringRequest request = new StringRequest(Request.Method.POST, AllConstants.add_to_archived_url, new com.android.volley.Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                progressDialo.dismiss();
//                System.out.println("Data added to API+"+response);
//                chatRoomRepo.setState(chatRoomModel.chatId,myId);
////                myBase.getObserver().setState(chatRoomModel.chatId,myId);
////                itemAdapter.notifyDataSetChanged();
//
//
//
//            }
//        }, new com.android.volley.Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                // method to handle errors.
////                Toast.makeText(getContext(), "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
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
//                params.put("my_id",myId );
//                params.put("your_id", chatRoomModel.userId);
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
//    private void delete(ChatRoomModel chatRoomModel) {
//        System.out.println(chatRoomModel.userId+"hatRoomModel.reciverId"+myId);
//        final ProgressDialog progressDialo = new ProgressDialog(getContext());
//        // url to post our data
//        progressDialo.setMessage(getResources().getString(R.string.prograss_message));
//        progressDialo.show();
//        // creating a new variable for our request queue
//        RequestQueue queue = Volley.newRequestQueue(getContext());
//        // on below line we are calling a string
//        // request method to post the data to our API
//        // in this we are calling a post method.
//        StringRequest request = new StringRequest(Request.Method.POST, AllConstants.delete_conversation, new com.android.volley.Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                progressDialo.dismiss();
//                System.out.println("Data added to API+"+response);
////                myBase.getObserver().deleteChatRoom(chatRoomModel.chatId);
////                itemAdapter.notifyDataSetChanged();
////                chatRoomRepo.deleteChatRoom(chatRoomModel.chatId);
//
//
//            }
//        }, new com.android.volley.Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                // method to handle errors.
////                Toast.makeText(getContext(), "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
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
//                params.put("my_id",myId );
//                params.put("your_id", chatRoomModel.userId);
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




}
