package com.yawar.memo.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tsuryo.swipeablerv.SwipeLeftRightCallback;
import com.tsuryo.swipeablerv.SwipeableRecyclerView;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.R;
import com.yawar.memo.adapter.ArchivedAdapter;
import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.modelView.ArchivedActViewModel;

import java.util.ArrayList;
import java.util.List;

import com.yawar.memo.utils.BaseApp;

public class ArchivedActivity extends AppCompatActivity implements ArchivedAdapter.CallbackInterfac{

    SwipeableRecyclerView recyclerView;
    LinearLayout linear_no_archived;
    List<ChatRoomModel> archived = new ArrayList<>();
    ArchivedAdapter itemAdapter;
    SearchView searchView;
    Toolbar toolbar;
    ArchivedActViewModel archivedActViewModel;
//    ChatRoomRepo chatRoomRepo;
    ClassSharedPreferences classSharedPreferences;
    String myId;
    BaseApp myBase;
    TextView archive ;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archived);
        toolbar = findViewById(R.id.toolbar);

        recyclerView =  findViewById(R.id.recycler_view);
        linear_no_archived = findViewById(R.id.liner_no_chat_history);

        archive =  findViewById(R.id.archived);
        classSharedPreferences= BaseApp.getInstance().getClassSharedPreferences();
        myId = classSharedPreferences.getUser().getUserId();
        archivedActViewModel = new ViewModelProvider(this).get(ArchivedActViewModel.class);
        myBase = (BaseApp) getApplication();
//        chatRoomRepo = myBase.getChatRoomRepo();
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);


        archivedActViewModel.loadData().observe(this, new androidx.lifecycle.Observer<ArrayList<ChatRoomModel>>() {
            @Override
            public void onChanged(ArrayList<ChatRoomModel> chatRoomModels) {
                if(chatRoomModels!=null){
                    ArrayList<ChatRoomModel> list = new ArrayList<>();

                    archived.clear();
                    for(ChatRoomModel chatRoomModel:chatRoomModels) {
                        if(!(chatRoomModel.getState() ==null)) {
                            if (chatRoomModel.getState().equals("0") || chatRoomModel.getState().equals(myId)) {
                                list.add(chatRoomModel.clone());
                                archived.add(chatRoomModel);
                            }
                        }
                    }
                    if(archived.isEmpty()){
                        linear_no_archived.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        archivedActViewModel.setArchived(false);

                    }
                    else {
                        linear_no_archived.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);

                        itemAdapter.setData((ArrayList<ChatRoomModel>) list);
                    }

                }

            }
        });
        itemAdapter = new ArchivedAdapter(this);
        recyclerView.setAdapter(itemAdapter);
        recyclerView.setListener(new SwipeLeftRightCallback.Listener() {
            @Override
            public void onSwipedLeft(int position) {
             System.out.println(position);
            }

            @Override
            public void onSwipedRight(int position) {
                archivedActViewModel.removeFromArchived(myId,archived.get(position).getOther_id());
            }
        });

        searchView = findViewById(R.id.search);
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


    }


    @Override
    public void onHandleSelection(int position, ChatRoomModel chatRoomModel) {
        Toast.makeText(this, "Position " + chatRoomModel.getLast_message(), Toast.LENGTH_SHORT).show();
        System.out.println(chatRoomModel.getUsername());
        Bundle bundle = new Bundle();
        bundle.putString("reciver_id",chatRoomModel.getOther_id());
        bundle.putString("sender_id", myId);
        bundle.putString("fcm_token",chatRoomModel.getUser_token() );
        bundle.putString("name",chatRoomModel.getUsername());
        bundle.putString("image",chatRoomModel.getImage());
        bundle.putString("chat_id",chatRoomModel.getId());
        bundle.putString("special", chatRoomModel.getSn());
        bundle.putString("blockedFor",chatRoomModel.getBlocked_for());
        Intent intent = new Intent(this, ConversationActivity.class);
        intent.putExtras(bundle);

        startActivity(intent);

    }

}