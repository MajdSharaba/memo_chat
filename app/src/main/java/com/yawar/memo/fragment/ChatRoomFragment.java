package com.yawar.memo.fragment;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.service.notification.StatusBarNotification;
import android.view.LayoutInflater;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;
import com.tsuryo.swipeablerv.SwipeableRecyclerView;
import com.yawar.memo.Api.ServerApi;
//import com.yawar.memo.call.CompleteActivity;
import com.yawar.memo.call.CallProperty;
import com.yawar.memo.constant.AllConstants;
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


public class ChatRoomFragment extends Fragment implements ChatRoomAdapter.CallbackInterfac {




    public static final String ON_CHANGE_DATA_RECEIVER = "android.zeroprojects.mafia.activity.ON_CHANGE_DATA_RECEIVER";
    public static final String ON_SOCKET_CONNECTION = "android.zeroprojects.mafia.activity.ON_SOCKET_CONNECTION";
    public static final String ON_MESSAGE_RECEIVED = "ConversationActivity.ON_MESSAGE_RECEIVED";
    public static final String TYPING = "ConversationActivity.ON_TYPING";




    SwipeableRecyclerView recyclerView;
    List<ChatRoomModel> postList = new ArrayList<>();
    String myId;
    BaseApp myBase;
    ChatRoomViewModel chatRoomViewModel;
    ChatRoomAdapter itemAdapter;
    Button startNewChat;
    SearchView searchView;
    Toolbar toolbar;
    ClassSharedPreferences classSharedPreferences;
    ServerApi serverApi;
    UserModel userModel;
    ImageButton iBAddArchived;
    ChatRoomRepo chatRoomRepo;
    LinearLayout linerArchived;
    LinearLayout lineerNoMessage;
    boolean isArchived;
    FloatingActionButton fab;

    TextView chat ;
//    SharedPreferences sharedPreferences ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_chat_room, container, false);


        myBase = BaseApp.getInstance();

        classSharedPreferences = BaseApp.getInstance().getClassSharedPreferences();

        myId = classSharedPreferences.getUser().getUserId();


        linerArchived = view.findViewById(R.id.liner_archived);
        lineerNoMessage = view.findViewById(R.id.liner_no_chat);
        startNewChat =  view.findViewById(R.id.btn_start_chat);
        fab = view.findViewById(R.id.fab);
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
                if(chatRoomModels!=null) {
                    if (chatRoomModels.isEmpty()) {
                        lineerNoMessage.setVisibility(View.VISIBLE);
                        fab.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        lineerNoMessage.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        fab.setVisibility(View.VISIBLE);
                        ArrayList<ChatRoomModel> list = new ArrayList<>();
                        postList.clear();

                        for (ChatRoomModel chatRoomModel : chatRoomModels) {
                            if (chatRoomModel.getState() == null) {
                                System.out.println(chatRoomModel.username+"username"+myId);
                                list.add(chatRoomModel.clone());
                                postList.add(chatRoomModel);
                            } else if (!chatRoomModel.getState().equals("0") && !chatRoomModel.getState().equals(myId)) {
                                list.add(chatRoomModel.clone());
                                postList.add(chatRoomModel);

                            } else {
                                chatRoomRepo.isArchivedMutableLiveData.setValue(true);
                            }
                        }

                        itemAdapter.setData((ArrayList<ChatRoomModel>) list);


                    }
                }
            }
        });
        recyclerView.setAdapter(itemAdapter);
        recyclerView.setListener(new SwipeLeftRightCallback.Listener() {
            @Override
            public void onSwipedLeft(int position) {

                chatRoomViewModel.deleteChatRoom(myId,postList.get(position).other_id);
            }

            @Override
            public void onSwipedRight(int position) {
                chatRoomViewModel.addToArchived(myId,postList.get(position).other_id);

            }
        });




        ////////////////FloatingActionButton




        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ContactNumberActivity.class);
                startActivity(intent);

            }

        });

          ///////new chat
        startNewChat.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ContactNumberActivity.class);
                startActivity(intent);

            }

        });


////////////// for search
        searchView = view.findViewById(R.id.search);
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


//        sharedPreferences = getActivity().getSharedPreferences("txtFontSize", Context.MODE_PRIVATE);

        chat = view.findViewById(R.id.chat);




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

        Bundle bundle = new Bundle();


        bundle.putString("reciver_id",chatRoomModel.other_id);

        bundle.putString("sender_id", myId);
        bundle.putString("fcm_token",chatRoomModel.user_token);

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
