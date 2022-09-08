package com.yawar.memo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.yawar.memo.call.RequestCallActivity;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.R;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.utils.MyDiffUtilCallBack;
import com.yawar.memo.utils.TimeProperties;
import com.yawar.memo.views.ConversationActivity;
import com.yawar.memo.views.UserInformationActivity;

import java.util.ArrayList;
import java.util.List;

public class ArchivedAdapter extends ListAdapter<ChatRoomModel,ArchivedAdapter.View_Holder>implements Filterable {
//    final private ListItemClickListener mOnClickListener;
    float textSize = 14.0F ;
    SharedPreferences sharedPreferences ;


    List<ChatRoomModel> listsearch = new ArrayList<ChatRoomModel>();
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
    public ArchivedAdapter( Activity context) {
        super(new MyDiffUtilCallBack());
        this.context = context;
        try {
            mCallback = (ArchivedAdapter.CallbackInterfac) context;
        } catch (ClassCastException ex) {
            //.. should log the error or throw and exception
        }
        this.listsearch.addAll(getCurrentList());
        classSharedPreferences = new ClassSharedPreferences(context);

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
        ChatRoomModel chatRoomModel = getItem(position);


        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.name.setText(chatRoomModel.getUsername());
        holder.textTime.setText(timeProperties.getFormattedDate(context,Long.parseLong(chatRoomModel.getCreated_at())));
        if(!chatRoomModel.isTyping()) {
            holder.lastMessage.setTextColor(context.getResources().getColor(R.color.gray));
            System.out.println(chatRoomModel.message_type +"list.get(position).lastMessageType");
            switch (chatRoomModel.message_type) {
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

                    lastMessage = chatRoomModel.last_message;

            }
            holder.lastMessage.setText(lastMessage);
            if (chatRoomModel.num_msg.equals("0"))
                holder.numUMessage.setVisibility(View.GONE);
            else {
                holder.numUMessage.setVisibility(View.VISIBLE);
                holder.numUMessage.setText(chatRoomModel.num_msg);
                System.out.println(chatRoomModel.num_msg +chatRoomModel.num_msg);
            }

            if(chatRoomModel.msg_sender.equals(classSharedPreferences.getUser().getUserId())) {
                holder.imageLasrMessageType.setVisibility(View.VISIBLE);

                switch (chatRoomModel.getMstate()) {


                    case "1":

                        holder.imageLasrMessageType.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_send_done));
                        holder.imageLasrMessageType.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.gray)));


                        break;
                    case "2":

                        holder.imageLasrMessageType.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_recive_done));
                        holder.imageLasrMessageType.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.gray)));

                        break;
                    case "3":

                        holder.imageLasrMessageType.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_recive_done_green));
                        holder.imageLasrMessageType.setImageTintList(null);


                        break;

                    default:
                        holder.imageLasrMessageType.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_not_send));
                        holder.imageLasrMessageType.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.gray)));


                }
            }
            else{
                holder.imageLasrMessageType.setVisibility(View.GONE);

            }

        }
        else{
            holder.lastMessage.setTextColor(context.getResources().getColor(R.color.green));
            holder.imageType.setVisibility(View.GONE);
            holder.imageLasrMessageType.setVisibility(View.GONE);

            holder.lastMessage.setText(context.getResources().getString(R.string.writing_now));
            holder.numUMessage.setVisibility(View.GONE);

        }

//        switch (chatRoomModel.getLast_message()){
//            case "imageWeb":
//                lastMessage = context.getResources().getString(R.string.photo);
//                holder.imageType.setVisibility(View.VISIBLE);
//
//                holder.imageType.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_select_image));
//
//                break;
//            case "voice":
//                lastMessage = context.getResources().getString(R.string.voice);
//                holder.imageType.setVisibility(View.VISIBLE);
//
//                holder.imageType.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_voice));
//
//                break;
//            case "video":
//                lastMessage = context.getResources().getString(R.string.video);
//                holder.imageType.setVisibility(View.VISIBLE);
//
//                holder.imageType.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_video));
//                break;
//            case "file":
//                lastMessage = context.getResources().getString(R.string.file);
//                holder.imageType.setVisibility(View.VISIBLE);
//
//                holder.imageType.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_file));
//
//                break;
//            case "contact":
//                lastMessage = context.getResources().getString(R.string.contact);
//                holder.imageType.setVisibility(View.VISIBLE);
//
//                holder.imageType.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_person));
//
//                break;
//            case "location":
//                lastMessage = context.getResources().getString(R.string.location);
//                holder.imageType.setVisibility(View.VISIBLE);
//
//                holder.imageType.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_location));
//
//                break;
//            default:
//                holder.imageType.setVisibility(View.GONE);
//
//                lastMessage = chatRoomModel.getLast_message() ;
//
//        }
//        holder.lastMessage.setText(lastMessage);
//        if(chatRoomModel.getNum_msg().equals("0"))
//            holder.numUMessage.setVisibility(View.GONE);
//        else {
//            holder.numUMessage.setVisibility(View.VISIBLE);
//            holder.numUMessage.setText(chatRoomModel.getNum_msg());}
        if(!chatRoomModel.getImage().isEmpty()){
            System.out.println("not freeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
//            Glide.with(holder.imageView.getContext()).load(AllConstants.imageUrl +chatRoomModel.getImage()).error(context.getResources().getDrawable(R.drawable.th)).into(holder.imageView);
            Glide.with(holder.imageView.getContext()).load(AllConstants.imageUrl+chatRoomModel.getImage()).apply(RequestOptions.placeholderOf(R.drawable.th).error(R.drawable.th)).into(holder.imageView);

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
                    mCallback.onHandleSelection(position, chatRoomModel);

                }}
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getContext());
                View mView = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_user_image_layout, null);
                PhotoView photoView = mView.findViewById(R.id.imageView);
                if(!chatRoomModel.getImage().isEmpty()){
                    Glide.with(photoView.getContext()).load(AllConstants.imageUrl+chatRoomModel.getImage()).apply(RequestOptions.placeholderOf(R.drawable.th).error(R.drawable.th)).into(photoView);
                }
                mBuilder.setView(mView);
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
                ImageButton imgBtnInfo = mView.findViewById(R.id.btimg_info);
                imgBtnInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), UserInformationActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("user_id", chatRoomModel.getOther_id());
                        bundle.putString("name", chatRoomModel.getUsername());
                        bundle.putString("image", chatRoomModel.getImage());
                        bundle.putString("fcm_token", chatRoomModel.getUser_token());
                        bundle.putString("special", chatRoomModel.getSn());
                        bundle.putString("chat_id",chatRoomModel.getId());
                        bundle.putString("blockedFor",chatRoomModel.blocked_for);

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


                        bundle.putString("reciver_id",chatRoomModel.getOther_id());
                        bundle.putString("sender_id", my_id);
                        bundle.putString("fcm_token",chatRoomModel.getUser_token() );
                        bundle.putString("name",chatRoomModel.getUsername());
                        bundle.putString("image",chatRoomModel.getImage());
                        bundle.putString("chat_id",chatRoomModel.getId());
                        bundle.putString("blockedFor",chatRoomModel.blocked_for);



                        Intent intent = new Intent(context, ConversationActivity.class);
                        intent.putExtras(bundle);

                        context.startActivity(intent);
                        mDialog.dismiss();


                    }
                });
                ImageButton imgBtnCall = mView.findViewById(R.id.btimg_call);
                imgBtnCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, RequestCallActivity.class);
                        intent.putExtra("anthor_user_id", chatRoomModel.getOther_id());
                        intent.putExtra("user_name", chatRoomModel.getUsername());
                        intent.putExtra("isVideo", false);
                        intent.putExtra("fcm_token", chatRoomModel.user_token);
                        intent.putExtra("image_profile", chatRoomModel.getImage());



                        view.getContext().startActivity(intent);
                        mDialog.dismiss();

                    }
                });
                if(chatRoomModel.blocked_for!=null) {
                    if (chatRoomModel.blocked_for != "null") {
                        imgBtnCall.setEnabled(false);
                    } else {
                        imgBtnCall.setEnabled(true);

                    }
                }
                else {
                    imgBtnCall.setEnabled(true);
                }



            }
        });

    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }
    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ChatRoomModel> filteredList = new ArrayList<>();
//            listsearch = getCurrentList();
            System.out.println(listsearch.size()+"listsearch.size()");

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(listsearch);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ChatRoomModel item : listsearch) {
                    if (item.getUsername().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
//            list.clear();
//            list.addAll((List) results.values);
            submitList((List)results.values);
//            notifyDataSetChanged();
        }
    };
    public void updateList(ArrayList<ChatRoomModel> updateList){
//        list = updateList;
//        listsearch.clear();
//        listsearch.addAll(list);

//        notifyDataSetChanged();
    }
    public void setData(ArrayList<ChatRoomModel> newData) {
        listsearch = newData;

        submitList(newData);
    }


    //class View_Holder extends RecyclerView.ViewHolder implements View.OnClickListener{
    class View_Holder extends RecyclerView.ViewHolder {
        TextView name;
        TextView lastMessage;
        ImageView imageView;
        LinearLayout linearLayout;
        ImageView imageLasrMessageType;

        TextView numUMessage;
        ImageView imageType;
        TextView textTime;
        View_Holder(View itemView ) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            name.setTextSize(textSize);
            name.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

            lastMessage = itemView.findViewById(R.id.lastMessage);
            lastMessage.setTextSize(textSize);
            lastMessage.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));


            imageView = itemView.findViewById(R.id.image);
            linearLayout = itemView.findViewById(R.id.liner_chat_room_row);
            imageLasrMessageType = (ImageView) itemView.findViewById(R.id.img_state);


            numUMessage = itemView.findViewById(R.id.num_message);
            imageType = itemView.findViewById(R.id.img_type);
            textTime = itemView.findViewById(R.id.time);

            numUMessage.setTextSize(textSize);
            numUMessage.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

            textTime.setTextSize(textSize);
            textTime.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));



        }

//     @Override
//     public void onClick(View view) {
//         mListener.onClick(view, list.get(getAdapterPosition()));
//
//     }
    }}