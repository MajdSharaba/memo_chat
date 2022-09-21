package com.yawar.memo.fragment;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.tsuryo.swipeablerv.SwipeableRecyclerView;
import com.yawar.memo.R;
import com.yawar.memo.adapter.CallAdapter;
import com.yawar.memo.adapter.ChatRoomAdapter;
import com.yawar.memo.model.CallModel;
import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.model.SearchRespone;
import com.yawar.memo.modelView.CallHistoryModelView;
import com.yawar.memo.modelView.ChatRoomViewModel;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.utils.BaseApp;

import java.util.ArrayList;


public class CallHistoryFragment extends Fragment {

CallHistoryModelView callHistoryModelView;
    RecyclerView recyclerView;
    CallAdapter itemAdapter;
    SearchView searchView;
    ClassSharedPreferences classSharedPreferences;
    LinearLayout linerNoCalls;
    ProgressBar progressBar;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_call_history, container, false);
        recyclerView =  view.findViewById(R.id.recycler_view);
        linerNoCalls = view.findViewById(R.id.liner_no_call_history);
        progressBar = view.findViewById(R.id.progress_circular);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        classSharedPreferences = BaseApp.getInstance().getClassSharedPreferences();
        itemAdapter = new CallAdapter( getActivity());

        callHistoryModelView = new ViewModelProvider(this).get(CallHistoryModelView.class);
//        callHistoryModelView.loadData(classSharedPreferences.getUser().getUserId());
        callHistoryModelView.loadData(classSharedPreferences.getUser().getUserId()).observe(getActivity(), new Observer<ArrayList<CallModel>>() {
            @Override
            public void onChanged(ArrayList<CallModel> callModels) {
                ArrayList<CallModel> list = new ArrayList<>();
                if (callModels != null) {
                    System.out.println("no call");
                     if(callModels.isEmpty()){

                         linerNoCalls.setVisibility(View.VISIBLE);
                         recyclerView.setVisibility(View.GONE);
                     }
                     else {
                         linerNoCalls.setVisibility(View.GONE);
                         recyclerView.setVisibility(View.VISIBLE);

                         for (CallModel callModel : callModels) {
                             list.add(callModel.clone());
                         }
                         itemAdapter.setData((ArrayList<CallModel>) list);

                     }
                }
            }
            });


        recyclerView.setAdapter(itemAdapter);

        callHistoryModelView.loading.observe(getActivity(), new androidx.lifecycle.Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean!=null) {
                    System.out.println("loadinggggg");

                    if (aBoolean) {
                        recyclerView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                        linerNoCalls.setVisibility(View.GONE);

                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        linerNoCalls.setVisibility(View.VISIBLE);


                    }
                }
            }
        });

        searchView = view.findViewById(R.id.search_view);
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


        return view;
    }


}