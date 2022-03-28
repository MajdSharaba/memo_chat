package com.yawar.memo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.yawar.memo.Api.ClassSharedPreferences;
import com.yawar.memo.R;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.utils.TimeProperties;
import com.yawar.memo.views.ConversationActivity;
import com.yawar.memo.views.UserDetailsActivity;
import com.yawar.memo.views.UserInformationActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ArchivedAdapter extends RecyclerView.Adapter<ArchivedAdapter.View_Holder> {
//    final private ListItemClickListener mOnClickListener;
    float textSize = 14.0F ;
    SharedPreferences sharedPreferences ;


    List<ChatRoomModel> list = Collections.emptyList();
    List<ChatRoomModel> listsearch = new ArrayList<ChatRoomModel>();
    List<ChatRoomModel> listsearch2= new ArrayList<ChatRoomModel>();
    public ArchivedAdapter.CallbackInterfac mCallback;
    TimeProperties timeProperties=new TimeProperties();
    Activity context;
    ClassSharedPreferences classSharedPreferences;

    public interface CallbackInterfac {

        /**
         * Callback invoked when clicked
         *
         * @param position             - the position
         * @param chatRoomModel - the text to pass back
         */
        void onHandleSelection(int position, ChatRoomModel chatRoomModel);

    }


    //public ChatRoomAdapter(List<ChatRoomModel> data, Activity context,  ListItemClickListener mOnClickListener) {
    public ArchivedAdapter(List<ChatRoomModel> data, Activity context) {
        this.list = data;
        this.context = context;
        try {
            mCallback = (ArchivedAdapter.CallbackInterfac) context;
        } catch (ClassCastException ex) {
            //.. should log the error or throw and exception
        }
        this.listsearch2.addAll(list);
        notifyDataSetChanged();


    }

    @NonNull
    @Override
    public ArchivedAdapter.View_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_room_row, parent, false);
        //ChatRoomAdapter.View_Holder holder = new View_Holder(v,mOnClickListener);

        sharedPreferences = context.getSharedPreferences("txtFontSize", Context.MODE_PRIVATE);

        ArchivedAdapter.View_Holder holder = new ArchivedAdapter.View_Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ArchivedAdapter.View_Holder holder, int position) {
        String lastMessage = "";

        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.name.setText(list.get(position).name);
        holder.textTime.setText(timeProperties.getFormattedDate(context,Long.parseLong(list.get(position).lastMessageTime)));

        switch (list.get(position).lastMessageType){
            case "imageWeb":
                lastMessage = context.getResources().getString(R.string.photo);
                holder.imageType.setVisibility(View.VISIBLE);

                holder.imageType.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_select_image));

                break;
            case "voice":
                lastMessage = context.getResources().getString(R.string.voice);
                holder.imageType.setVisibility(View.VISIBLE);

                holder.imageType.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_voice));

                break;
            case "video":
                lastMessage = context.getResources().getString(R.string.video);
                holder.imageType.setVisibility(View.VISIBLE);

                holder.imageType.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_video));
                break;
            case "file":
                lastMessage = context.getResources().getString(R.string.file);
                holder.imageType.setVisibility(View.VISIBLE);

                holder.imageType.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_file));

                break;
            case "contact":
                lastMessage = context.getResources().getString(R.string.contact);
                holder.imageType.setVisibility(View.VISIBLE);

                holder.imageType.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_person));

                break;
            case "location":
                lastMessage = context.getResources().getString(R.string.location);
                holder.imageType.setVisibility(View.VISIBLE);

                holder.imageType.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_location));

                break;
            default:
                holder.imageType.setVisibility(View.GONE);

                lastMessage = list.get(position).lastMessage ;

        }
        holder.lastMessage.setText(lastMessage);
        if(list.get(position).numberUnRMessage.equals("0"))
            holder.numUMessage.setVisibility(View.GONE);
        else {
            holder.numUMessage.setVisibility(View.VISIBLE);
            holder.numUMessage.setText(list.get(position).numberUnRMessage);}
        if(!list.get(position).getImage().isEmpty()){
            System.out.println("not freeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
            Glide.with(holder.imageView.getContext()).load(AllConstants.imageUrl +list.get(position).getImage()).error(context.getResources().getDrawable(R.drawable.th)).into(holder.imageView);
        }
        else {
            holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.th));
        }
        //Glide.with(holder.imageView.getContext()).load(list.get(position).getImage()).into(holder.imageView);
        // holder.imageView.setImageResource(list.get(position).imageId);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCallback != null) {
                    mCallback.onHandleSelection(position, list.get(position));

                }}
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getContext());
                View mView = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_user_image_layout, null);
                PhotoView photoView = mView.findViewById(R.id.imageView);
                if(!list.get(position).getImage().isEmpty()){
                    Glide.with(photoView.getContext()).load(AllConstants.imageUrl+list.get(position).getImage()).error(context.getResources().getDrawable(R.drawable.th)).into(photoView);}
                mBuilder.setView(mView);
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
                ImageButton imgBtnInfo = mView.findViewById(R.id.btimg_info);
                imgBtnInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), UserInformationActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("user_id", list.get(position).getUserId());
                        bundle.putString("name", list.get(position).getName());
                        bundle.putString("image", list.get(position).getImage());
                        bundle.putString("fcm_token", list.get(position).fcmToken);
                        bundle.putString("special", list.get(position).getSpecialNumber());
                        bundle.putString("chat_id",list.get(position).getChatId());
                        intent.putExtras(bundle);
                        view.getContext().startActivity(intent);
                        mDialog.dismiss();
                        Toast.makeText(view.getContext(), "Movie Name clicked", Toast.LENGTH_SHORT).show();

                    }
                });
                ImageButton imgBtnChat = mView.findViewById(R.id.btimg_chat);
                imgBtnChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        classSharedPreferences = new ClassSharedPreferences(context);
                        String my_id = classSharedPreferences.getUser().getUserId();
                        Bundle bundle = new Bundle();


                        bundle.putString("reciver_id",list.get(position).getUserId());

                        bundle.putString("sender_id", my_id);
                        bundle.putString("fcm_token",list.get(position).fcmToken );

                        bundle.putString("name",list.get(position).getName());
                        bundle.putString("image",list.get(position).getImage());
                        bundle.putString("chat_id",list.get(position).getChatId());


                        Intent intent = new Intent(context, ConversationActivity.class);
                        intent.putExtras(bundle);

                        context.startActivity(intent);
                        mDialog.dismiss();


                    }
                });



            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void filter(String charText) {
        System.out.println(charText+listsearch2.size());
        charText = charText.toLowerCase(Locale.getDefault());
        listsearch.clear();
        if (charText.length() == 0) {

            listsearch.addAll(listsearch2);
        } else {
            for (ChatRoomModel wp : listsearch2) {
                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    listsearch.add(wp);
                }
            }
        }
        list.clear();
        list.addAll(listsearch);
        notifyDataSetChanged();
    }


    //class View_Holder extends RecyclerView.ViewHolder implements View.OnClickListener{
    class View_Holder extends RecyclerView.ViewHolder {
        TextView name;
        TextView lastMessage;
        ImageView imageView;
        LinearLayout linearLayout;
        TextView numUMessage;
        ImageView imageType;
        TextView textTime;
        View_Holder(View itemView ) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
//            name.setTextSize(textSize);
//            name.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

            lastMessage = (TextView) itemView.findViewById(R.id.lastMessage);
//            lastMessage.setTextSize(textSize);
//            lastMessage.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));


            imageView = (ImageView) itemView.findViewById(R.id.image);
            linearLayout = itemView.findViewById(R.id.liner_chat_room_row);

            numUMessage = itemView.findViewById(R.id.num_message);
            imageType = itemView.findViewById(R.id.img_type);
            textTime = itemView.findViewById(R.id.time);

//            numUMessage.setTextSize(textSize);
//            numUMessage.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

//            textTime.setTextSize(textSize);
//            textTime.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));



        }

//     @Override
//     public void onClick(View view) {
//         mListener.onClick(view, list.get(getAdapterPosition()));
//
//     }
    }}