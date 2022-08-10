package com.yawar.memo.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;
import com.tsuryo.swipeablerv.SwipeableRecyclerView;
import com.yawar.memo.Api.ServerApi;
import com.yawar.memo.call.CompleteActivity;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.modelView.ChatRoomViewModel;
import com.yawar.memo.repositry.ChatRoomRepo;
import com.yawar.memo.service.SocketIOService;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.views.ArchivedActivity;
import com.yawar.memo.views.ContactNumberActivity;
import com.yawar.memo.views.ConversationActivity;
import com.yawar.memo.R;
import com.yawar.memo.adapter.ChatRoomAdapter;
import com.yawar.memo.model.ChatRoomModel;

import java.util.ArrayList;
import java.util.List;

import com.yawar.memo.utils.BaseApp;
import com.yawar.memo.views.GroupSelectorActivity;

import org.json.JSONException;
import org.json.JSONObject;

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

//    private void sendMesssage() {
//        Intent service = new Intent(getContext(), SocketIOService.class);
//        JSONObject object = new JSONObject();
//        try {
//            object.put("my_id", user_id);
//            object.put("your_id", anthor_user_id);
////            socket.emit("check connect", object);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        service.putExtra(SocketIOService.EXTRA_SEND_MESSAGE_FOR_CALL_PARAMTES, object.toString());
//        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_SEND_MESSAGE_FOR_CALL);
//        getContext().startService(service);
//    }

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

        itemAdapter = new ChatRoomAdapter( this);



        chatRoomViewModel.loadData().observe(getActivity(), new androidx.lifecycle.Observer<ArrayList<ChatRoomModel>>() {
            @Override
            public void onChanged(ArrayList<ChatRoomModel> chatRoomModels) {
                if(chatRoomModels!=null){
                    System.out.println("update items");
                    ArrayList<ChatRoomModel> list = new ArrayList<>();
                    postList.clear();
                    System.out.println("chatRoomModels.sizaee"+chatRoomModels);

                    for(ChatRoomModel chatRoomModel:chatRoomModels) {
                        System.out.println(chatRoomModel.blocked_for+"chatRoomModels.sizaee");
                        if(chatRoomModel.getState()==null){


                            list.add(chatRoomModel.clone());
                            postList.add(chatRoomModel);
                        }

                        else if (!chatRoomModel.getState().equals("0")&&!chatRoomModel.getState().equals(myId)) {
//                            System.out.println(chatRoomModel.getState() + "statttttttttttttttttttttte");
                            list.add(chatRoomModel.clone());
                            postList.add(chatRoomModel);

                        }
                        else {
                            System.out.println(chatRoomModel.getState()+"statttttttttttte");
                            chatRoomRepo.isArchivedMutableLiveData.setValue(true);
                        }
                    }
                    System.out.println(list.size()+"list"+postList.size());

                     itemAdapter.setData((ArrayList<ChatRoomModel>) list);


                }

            }
        });
//        itemAdapter = new ChatRoomAdapter( this);
        recyclerView.setAdapter(itemAdapter);
        recyclerView.setListener(new SwipeLeftRightCallback.Listener() {
            @Override
            public void onSwipedLeft(int position) {

//                delete(postList.get(position));
                chatRoomViewModel.deleteChatRoom(myId,postList.get(position).other_id);
            }

            @Override
            public void onSwipedRight(int position) {
//                addToArchived(postList.get(position));
                chatRoomViewModel.addToArchived(myId,postList.get(position).other_id);

//                chatRoomRepo.setArchived(true);


            }
        });




        ////////////////FloatingActionButton
        FloatingActionButton fab = view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//
//                Intent intent = new Intent(getContext(), ContactNumberActivity.class);
//                startActivity(intent);
                Intent intent = new Intent(getContext(), CompleteActivity.class);
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


        bundle.putString("reciver_id",chatRoomModel.other_id);

        bundle.putString("sender_id", myId);
        bundle.putString("fcm_token",chatRoomModel.user_token);

//        bundle.putString("reciver_id",chatRoomModel.reciverId);
        bundle.putString("name",chatRoomModel.username);
        bundle.putString("image",chatRoomModel.getImage());
        bundle.putString("chat_id",chatRoomModel.getId());
        bundle.putString("special", chatRoomModel.getSn());
        bundle.putString("blockedFor",chatRoomModel.blocked_for);


        ///////////////////////



        Intent intent = new Intent(getContext(), ConversationActivity.class);
        intent.putExtras(bundle);

        startActivity(intent);

    }







}
