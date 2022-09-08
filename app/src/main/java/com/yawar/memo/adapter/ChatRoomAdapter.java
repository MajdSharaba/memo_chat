package com.yawar.memo.adapter;

import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.yawar.memo.R;
import com.yawar.memo.call.RequestCallActivity;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.fragment.ChatRoomFragment;
import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.utils.MyDiffUtilCallBack;
import com.yawar.memo.utils.TimeProperties;
import com.yawar.memo.views.ConversationActivity;
import com.yawar.memo.views.UserInformationActivity;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomAdapter extends ListAdapter<ChatRoomModel,ChatRoomAdapter.View_Holder> implements Filterable {
//    final private ListItemClickListener mOnClickListener;

//    List<ChatRoomModel> list ;
    List<ChatRoomModel> listsearch = new ArrayList<ChatRoomModel>() ;

    //    List<ChatRoomModel> listsearch = new ArrayList<ChatRoomModel>();
//    List<ChatRoomModel> listsearch2= new ArrayList<ChatRoomModel>();
    public ChatRoomAdapter.CallbackInterfac mCallback;
    ChatRoomFragment context;
    TimeProperties timeProperties=new TimeProperties();
    ClassSharedPreferences classSharedPreferences;



    public interface CallbackInterfac {

        /**
         * Callback invoked when clicked
         *
         * @param position             - the position
         * RoomModel - the text to pass back
         */
        void onHandleSelection(int position, ChatRoomModel chatRoomModel);
        /// void onLongPress(int position, ChatRoomModel chatRoomModel,boolean checked);

    }


    //public ChatRoomAdapter(List<ChatRoomModel> data, Activity context,  ListItemClickListener mOnClickListener) {
    public ChatRoomAdapter( ChatRoomFragment context) {
        super(new MyDiffUtilCallBack());
//        this.list = data;
        this.context = context;
        try {
            mCallback = (ChatRoomAdapter.CallbackInterfac) context;
        } catch (ClassCastException ex) {
            //.. should log the error or throw and exception
        }
        listsearch.addAll(getCurrentList());
        classSharedPreferences = new ClassSharedPreferences(context.getActivity());
    }

    @NonNull
    @Override
    public ChatRoomAdapter.View_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_room_row, parent, false);
        //ChatRoomAdapter.View_Holder holder = new View_Holder(v,mOnClickListener);
        ChatRoomAdapter.View_Holder holder = new ChatRoomAdapter.View_Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomAdapter.View_Holder holder, int position) {

        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        ChatRoomModel chatRoomModel = getItem(position);
        String lastMessage = "";
        holder.name.setText(chatRoomModel.username);

//            holder.textTime.setText(timeProperties.getFormattedDate(context.getActivity(),Long.parseLong(list.get(position).lastMessageTime)));
        holder.textTime.setText(timeProperties.getFormattedDate(context.getActivity(),Long.parseLong(chatRoomModel.created_at)));
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
        if(!chatRoomModel.getImage().isEmpty()){
//            Glide.with(holder.imageView.getContext()).load(AllConstants.imageUrl+chatRoomModel.getImage()).error(context.getResources().getDrawable(R.drawable.th)).into(holder.imageView);
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

                }
            }
        });
//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                System.out.println("majd");
//                if (mCallback != null) {
//                    System.out.println("Llllllllllllllllll");
//                    if(!list.get(position).isChecked){
//                    holder.linearLayout.setBackgroundResource(R.color.blue);
//                        mCallback.onLongPress(position, list.get(position),true);
//                        list.get(position).setChecked(true);
//                    }
//                    else{
//                        System.out.println("holder");
//                        holder.linearLayout.setBackgroundResource(R.color.backgroundColor);
//                        mCallback.onLongPress(position, list.get(position),false);
//
//                        list.get(position).setChecked(false);
//
//
//
//                    }
//
//                }
//                return false;
//            }
//        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String my_id = classSharedPreferences.getUser().getUserId();

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getContext());
                View mView = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_user_image_layout, null);
                PhotoView photoView = mView.findViewById(R.id.imageView);
                if(!chatRoomModel.getImage().isEmpty()){
//                    Glide.with(photoView.getContext()).load(AllConstants.imageUrl+chatRoomModel.getImage()).error(context.getResources().getDrawable(R.drawable.th)).into(photoView);
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
                        bundle.putString("fcm_token", chatRoomModel.user_token);
                        bundle.putString("special", chatRoomModel.getSn());
                        bundle.putString("chat_id",chatRoomModel.getId());
                        bundle.putString("blockedFor",chatRoomModel.blocked_for);


                        intent.putExtras(bundle);
                        view.getContext().startActivity(intent);
                        mDialog.dismiss();

                    }
                });
                ImageButton imgBtnChat = mView.findViewById(R.id.btimg_chat);
                imgBtnChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Bundle bundle = new Bundle();


                        bundle.putString("reciver_id",chatRoomModel.getOther_id());

                        bundle.putString("sender_id", my_id);
                        bundle.putString("fcm_token",chatRoomModel.user_token);

                        bundle.putString("name",chatRoomModel.getUsername());
                        bundle.putString("image",chatRoomModel.getImage());
                        bundle.putString("chat_id",chatRoomModel.getId());
                        bundle.putString("blockedFor",chatRoomModel.blocked_for);



                        Intent intent = new Intent(context.getActivity(), ConversationActivity.class);
                        intent.putExtras(bundle);

                        context.startActivity(intent);
                        mDialog.dismiss();


                    }
                });
                ImageButton imgBtnCall = mView.findViewById(R.id.btimg_call);
                imgBtnCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context.getActivity(), RequestCallActivity.class);
                        intent.putExtra("anthor_user_id", chatRoomModel.getOther_id());
                        intent.putExtra("user_name", chatRoomModel.getUsername());
                        intent.putExtra("isVideo", false);
                        intent.putExtra("fcm_token", chatRoomModel.user_token);
                        intent.putExtra("image_profile", chatRoomModel.getImage());



                        view.getContext().startActivity(intent);
                        mDialog.dismiss();

                    }
                });
//                if(chatRoomModel.blocked_for.toString().equals(classSharedPreferences.getUser().getUserId())||chatRoomModel.blocked_for.toString().equals(chatRoomModel.getOther_id())||chatRoomModel.blocked_for.toString().equals("0")){
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

        notifyDataSetChanged();
    }
    public void setData(ArrayList<ChatRoomModel> newData) {
        listsearch = newData;

        submitList(newData);
    }

//    public void filter(String charText) {
//        System.out.println(charText+listsearch2.size());
//        charText = charText.toLowerCase(Locale.getDefault());
//            listsearch.clear();
//        if (charText.length() == 0) {
//
//            listsearch.addAll(listsearch2);
//        } else {
//            for (ChatRoomModel wp : listsearch2) {
//                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
//                    listsearch.add(wp);
//                }
//            }
//        }
//        list.clear();
//        list.addAll(listsearch);
//        notifyDataSetChanged();
//    }


    //class View_Holder extends RecyclerView.ViewHolder implements View.OnClickListener{
    class View_Holder extends RecyclerView.ViewHolder {
        TextView name;
        TextView lastMessage;
        TextView numUMessage;
        ImageView imageView;
        ImageView imageType;
        ImageView imageLasrMessageType;
        TextView textTime;
        LinearLayout linearLayout;



        View_Holder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            lastMessage = (TextView) itemView.findViewById(R.id.lastMessage);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            imageLasrMessageType = (ImageView) itemView.findViewById(R.id.img_state);

            linearLayout = itemView.findViewById(R.id.liner_chat_room_row);
            numUMessage = itemView.findViewById(R.id.num_message);
            imageType = itemView.findViewById(R.id.img_type);
            textTime = itemView.findViewById(R.id.time);


        }
    }



}

