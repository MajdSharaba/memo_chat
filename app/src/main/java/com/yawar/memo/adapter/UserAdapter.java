package com.yawar.memo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yawar.memo.R;
import com.yawar.memo.model.UserSeen;

import java.util.Collections;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<com.yawar.memo.adapter.UserAdapter.View_Holder> {
    List<UserSeen> list = Collections.emptyList();
    Context context;


    float textSize = 14.0F ;
    SharedPreferences sharedPreferences ;



    //public ChatRoomAdapter(List<ChatRoomModel> data, Activity context,  ListItemClickListener mOnClickListener) {
    public UserAdapter(List<UserSeen> data, Activity context) {
        this.list = data;
        this.context = context;



    }

    @NonNull
    @Override
    public com.yawar.memo.adapter.UserAdapter.View_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_seen_story_item, parent, false);
        //ChatRoomAdapter.View_Holder holder = new View_Holder(v,mOnClickListener);
        com.yawar.memo.adapter.UserAdapter.View_Holder holder = new com.yawar.memo.adapter.UserAdapter.View_Holder(v);
        sharedPreferences = context.getSharedPreferences("txtFontSize", Context.MODE_PRIVATE);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull com.yawar.memo.adapter.UserAdapter.View_Holder holder, int position) {

        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.name.setText(list.get(position).name);
//        if(!list.get(position).getImage().isEmpty()){
//            Glide.with(holder.imageView.getContext()).load("http://192.168.1.2:8080/yawar_chat/uploads/profile/"+list.get(position).getImage()).into(holder.imageView);}
        //Glide.with(holder.imageView.getContext()).load(list.get(position).getImage()).into(holder.imageView);
        // holder.imageView.setImageResource(list.get(position).imageId);


//        holder.imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }




    //class View_Holder extends RecyclerView.ViewHolder implements View.OnClickListener{
    class View_Holder extends RecyclerView.ViewHolder {
        TextView name;

        //ListItemClickListener mListener;

        //View_Holder(View itemView , ListItemClickListener listener) {
        View_Holder(View itemView ) {
            super(itemView);
            //mListener = listener;
            name = itemView.findViewById(R.id.name);
            name.setTextSize(textSize);
            name.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));


            ///  itemView.setOnClickListener(this);
        }

//     @Override
//     public void onClick(View view) {
//         mListener.onClick(view, list.get(getAdapterPosition()));
//
//     }
    }}
