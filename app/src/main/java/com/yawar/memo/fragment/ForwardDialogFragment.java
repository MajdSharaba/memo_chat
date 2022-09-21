package com.yawar.memo.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.R;
import com.yawar.memo.adapter.GroupSelectorAdapter;
import com.yawar.memo.model.ChatMessage;
import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.model.SendContactNumberResponse;
import com.yawar.memo.modelView.ForwardDialogViewModel;
import com.yawar.memo.service.SocketIOService;
import com.yawar.memo.utils.BaseApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForwardDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForwardDialogFragment extends DialogFragment implements Observer,GroupSelectorAdapter.CallbackInterface {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public static ArrayList<ChatMessage> chatMessageArrayList = new ArrayList<>();

    public ForwardDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ForwardDialogFragment newInstance(ArrayList<ChatMessage> param1, String param2) {
        ForwardDialogFragment fragment = new ForwardDialogFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
        chatMessageArrayList=param1;
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    BaseApp myBase;
    ArrayList<String> forwordList = new ArrayList<String>();
    ArrayList<SendContactNumberResponse> sendContactNumberResponses = new ArrayList<SendContactNumberResponse>();

    GroupSelectorAdapter mainAdapter;
    RecyclerView recyclerView;
    Button send,cancel;
    ArrayList<String> chatMessageListId= new ArrayList<>();
    ArrayList<String> chatMessageListId2= new ArrayList<String>();
    String id="";
    ClassSharedPreferences classSharedPreferences;
    String my_id;
    ForwardDialogViewModel forwardDialogViewModel;

    TextView select_title2 ;
    float textSize = 14.0F ;
    SharedPreferences sharedPreferences ;



    private void forwardMessage() {
        Intent service = new Intent(getContext(), SocketIOService.class);
        JSONObject object = new JSONObject();

        try {
            System.out.println("my_id"+"immmmmmmmmmmm"+my_id);

            object.put("id",forwordList.toString() );
            object.put("message_id", chatMessageListId.toString());
            object.put("sender_id", "\""+ my_id+"\"");
//            System.out.println("my_id"+"immmmmmmmmmmm"+my_id);


//            socket.emit("check connect", object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        forwardDialogViewModel.clearSelectedMessage();

        service.putExtra(SocketIOService.EXTRA_FORWARD_MESSAGE_PARAMTERS, object.toString());
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_Forward);
        getActivity().startService(service);
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
        View view = inflater.inflate(R.layout.fragment_dialog_forward, container, false);

        sharedPreferences = getActivity().getSharedPreferences("txtFontSize", Context.MODE_PRIVATE);

        select_title2 = view.findViewById(R.id.select_title2);
        select_title2.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));



//        view.setBackground(getActivity().getResources().getDrawable(R.drawable.dialog_bg));
        classSharedPreferences = BaseApp.getInstance().getClassSharedPreferences();
        my_id= classSharedPreferences.getUser().getUserId();
        myBase = BaseApp.getInstance();
        forwardDialogViewModel =  new ViewModelProvider(this).get(ForwardDialogViewModel.class);

//        myBase.getContactNumberObserve().addObserver(this);
//        myBase.getObserver().addObserver(this);

        System.out.println(chatMessageArrayList.size()+"chatMessageArrayList.size()");
        for(ChatMessage chatMessage :chatMessageArrayList){
            chatMessageListId.add("\""+ chatMessage.getId()+"\"");


            System.out.println(chatMessage.getId()+"chatMesssageeeee");
            id=id+chatMessage.getId()+",";

        }



        recyclerView = view.findViewById(R.id.recycler_view);

        mainAdapter = new GroupSelectorAdapter(this,sendContactNumberResponses);

        forwardDialogViewModel.loadData().observe(getActivity(), new androidx.lifecycle.Observer<ArrayList<ChatRoomModel>>() {
            @Override
            public void onChanged(ArrayList<ChatRoomModel> chatRoomModels) {
                if(chatRoomModels!=null){
                    sendContactNumberResponses.clear();
                    for(ChatRoomModel chatRoomModel:chatRoomModels) {
                     sendContactNumberResponses.add(new SendContactNumberResponse(chatRoomModel.getOther_id(),chatRoomModel.getUsername(),chatRoomModel.getSn(),chatRoomModel.getImage(),"true",chatRoomModel.getId(),chatRoomModel.getUser_token(),chatRoomModel.getBlocked_for(),"ll"));

                    }
                    mainAdapter.updateList(sendContactNumberResponses);

                }
                //adapter.notifyDataSetChanged();

            }
        });


//        sendContactNumberResponses = myBase.getContactNumberObserve().getContactNumberResponseList();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mainAdapter);

        send = view.findViewById(R.id.send);
        cancel = view.findViewById(R.id.cancel);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                System.out.println(chatMessageListId.toString()+"chatMessageListId.toString(");
                forwardMessage();
                dismiss();

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();

            }
        });


        return  view;
    }

    @Override
    public void update(Observable observable, Object o) {

    }

    @Override
    public void onHandleSelection(int position, SendContactNumberResponse sendContactNumberResponse, boolean isChecked) {
        if(isChecked){
            System.out.println(sendContactNumberResponse.getId());
//            "\""+ chatMessage.getId()+"\""
            forwordList.add(sendContactNumberResponse.getId());}
        else {
            forwordList.remove(sendContactNumberResponse.getId());
        }
    }
}