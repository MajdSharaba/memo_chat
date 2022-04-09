package com.yawar.memo.adapter;

import android.content.Intent;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.yawar.memo.Api.ClassSharedPreferences;
import com.yawar.memo.R;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.fragment.ChatRoomFragment;
import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.utils.TimeProperties;
import com.yawar.memo.views.ConversationActivity;
import com.yawar.memo.views.UserDetailsActivity;
import com.yawar.memo.views.UserInformationActivity;

import java.util.ArrayList;
import java.util.Collections;
        import java.util.List;
import java.util.Locale;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.View_Holder> implements Filterable {
//    final private ListItemClickListener mOnClickListener;

    List<ChatRoomModel> list = Collections.emptyList();
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
    public ChatRoomAdapter(List<ChatRoomModel> data, ChatRoomFragment context) {
        this.list = data;
        this.context = context;
        try {
            mCallback = (ChatRoomAdapter.CallbackInterfac) context;
        } catch (ClassCastException ex) {
            //.. should log the error or throw and exception
        }
        listsearch.addAll(list);


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
            String lastMessage = "";
            holder.name.setText(list.get(position).name);

//            holder.textTime.setText(timeProperties.getFormattedDate(context.getActivity(),Long.parseLong(list.get(position).lastMessageTime)));
                    holder.textTime.setText(timeProperties.getFormattedDate(context.getActivity(),Long.parseLong(list.get(position).lastMessageTime)));
        if(!list.get(position).isTyping()) {
            holder.lastMessage.setTextColor(context.getResources().getColor(R.color.gray));
            System.out.println(list.get(position).lastMessageType+"list.get(position).lastMessageType");
            switch (list.get(position).lastMessageType) {
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

                    lastMessage = list.get(position).lastMessage;

            }
            holder.lastMessage.setText(lastMessage);
            if (list.get(position).numberUnRMessage.equals("0"))
                holder.numUMessage.setVisibility(View.GONE);
            else {
                holder.numUMessage.setVisibility(View.VISIBLE);
                holder.numUMessage.setText(list.get(position).numberUnRMessage);
                System.out.println(list.get(position).numberUnRMessage+list.get(position).numberUnRMessage);
            }
        }
        else{
            holder.lastMessage.setTextColor(context.getResources().getColor(R.color.green));

            holder.lastMessage.setText(context.getResources().getString(R.string.writing_now));
            holder.numUMessage.setVisibility(View.GONE);

        }
            if(!list.get(position).getImage().isEmpty()){
             Glide.with(holder.imageView.getContext()).load(AllConstants.imageUrl+list.get(position).getImage()).error(context.getResources().getDrawable(R.drawable.th)).into(holder.imageView);
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

                        }
                    });
                    ImageButton imgBtnChat = mView.findViewById(R.id.btimg_chat);
                    imgBtnChat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            classSharedPreferences = new ClassSharedPreferences(context.getActivity());
                            String my_id = classSharedPreferences.getUser().getUserId();
                            Bundle bundle = new Bundle();


                            bundle.putString("reciver_id",list.get(position).getUserId());

                            bundle.putString("sender_id", my_id);
                            bundle.putString("fcm_token",list.get(position).fcmToken );

                            bundle.putString("name",list.get(position).getName());
                            bundle.putString("image",list.get(position).getImage());
                            bundle.putString("chat_id",list.get(position).getChatId());


                            Intent intent = new Intent(context.getActivity(), ConversationActivity.class);
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
    @Override
    public Filter getFilter() {
        return exampleFilter;
    }
    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ChatRoomModel> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(listsearch);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ChatRoomModel item : listsearch) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
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
            list.clear();
            list.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

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
     TextView textTime;
     LinearLayout linearLayout;


     View_Holder(View itemView) {
         super(itemView);
         name = (TextView) itemView.findViewById(R.id.name);
         lastMessage = (TextView) itemView.findViewById(R.id.lastMessage);
         imageView = (ImageView) itemView.findViewById(R.id.image);
         linearLayout = itemView.findViewById(R.id.liner_chat_room_row);
         numUMessage = itemView.findViewById(R.id.num_message);
         imageType = itemView.findViewById(R.id.img_type);
         textTime = itemView.findViewById(R.id.time);


     }
 }}




