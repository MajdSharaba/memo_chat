package com.yawar.memo.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.yawar.memo.R;
import com.yawar.memo.adapter.UserAdapter;
import com.yawar.memo.model.UserSeen;
import com.yawar.memo.utils.BaseApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class BottomSheetDialog extends BottomSheetDialogFragment implements Observer {
    RecyclerView recyclerView;
    List<UserSeen> postList = new ArrayList<>();
    UserAdapter itemAdapter;
    BaseApp myBase;


    public BottomSheetDialog(int counter) {
        this.counter = counter;
    }

    int counter;
    TextView userseenstory ;
    float textSize = 14.0F ;
    SharedPreferences sharedPreferences ;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_layout,
                container, false);
        recyclerView = v.findViewById(R.id.recycler);
        System.out.println(counter+"counter");

        sharedPreferences = getActivity().getSharedPreferences("txtFontSize", Context.MODE_PRIVATE);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
//        postList.add(new UserSeen("sh"));
//        postList.add(new UserSeen("sh"));
//        postList.add(new UserSeen("sh"));
//        postList.add(new UserSeen("sh"));

        myBase = (BaseApp) getActivity().getApplication();
//        myBase.getObserver().addObserver(this);
        postList =  myBase.getStoriesObserve().getMyStatus().getStatusList().get(counter).getUserSeens();

        System.out.println(postList.size()+"postList");


        itemAdapter = new UserAdapter(postList, getActivity());
        recyclerView.setAdapter(itemAdapter);
        itemAdapter.notifyDataSetChanged();

        userseenstory = v.findViewById(R.id.userseenstory);
        userseenstory.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));


        return v;
    }

    @Override
    public void update(Observable observable, Object o) {

    }
}


