package com.yawar.memo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yawar.memo.R;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.model.SendContactNumberResponse;

import java.util.ArrayList;

public class GroupSelectorAdapter extends RecyclerView.Adapter<com.yawar.memo.adapter.GroupSelectorAdapter.ViewHolders> {
    Fragment fragment;
    Activity activity;
    ArrayList<SendContactNumberResponse> arrayList;
    private GroupSelectorAdapter.CallbackInterface mCallback;
    float textSize = 14.0F ;
    SharedPreferences sharedPreferences ;


    public interface CallbackInterface {

        /**
         * Callback invoked when clicked
         *
         * @param position             - the position
         * @param sendContactNumberResponse - the text to pass back
         * @param isChecked - the boolean to pass back
         */
        void onHandleSelection(int position, SendContactNumberResponse sendContactNumberResponse,boolean isChecked);
    }


    ///Create constructor
    public GroupSelectorAdapter(Fragment fragment, ArrayList<SendContactNumberResponse> arrayList) {
        this.fragment = fragment;
        this.arrayList = arrayList;
        try {
            mCallback = (GroupSelectorAdapter.CallbackInterface) fragment;
        } catch (ClassCastException ex) {
            //.. should log the error or throw and exception
        }
        notifyDataSetChanged();
    }
    public GroupSelectorAdapter(Activity activity, ArrayList<SendContactNumberResponse> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
        try {
            mCallback = (GroupSelectorAdapter.CallbackInterface) activity;
        } catch (ClassCastException ex) {
            //.. should log the error or throw and exception
        }
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public com.yawar.memo.adapter.GroupSelectorAdapter.ViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ///Initialize view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_selector, parent, false);


//        sharedPreferences = activity.getSharedPreferences("txtFontSize", Context.MODE_PRIVATE);

        GroupSelectorAdapter.ViewHolders holder = new GroupSelectorAdapter.ViewHolders(view);

        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull com.yawar.memo.adapter.GroupSelectorAdapter.ViewHolders holder, int position) {

        SendContactNumberResponse model = arrayList.get(position);
        holder.tvName.setText(model.getName());
        holder.tvNumber.setText(model.getNumber());
        System.out.println(model.getImage());
         if(!model.getImage().isEmpty()){
        Glide.with(holder.imageView.getContext()).load(AllConstants.imageUrl+model.getImage()).into(holder.imageView);}

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("pre clicked");
                if (mCallback != null) {
                    System.out.println("post clicked");

                    mCallback.onHandleSelection(position, arrayList.get(position), holder.checkBox.isChecked());
                }
            }
        });



    }
    public void updateList(ArrayList<SendContactNumberResponse> updateList){
        arrayList = updateList;


        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class ViewHolders extends RecyclerView.ViewHolder {
        ///Initialize variable
        TextView tvName;
        TextView tvNumber;
        ImageView imageView;

        CheckBox checkBox;

        public ViewHolders(@NonNull View itemView) {
            super(itemView);
            ////Assign variable
            tvName = itemView.findViewById(R.id.tv_name);
//            tvName.setTextSize(textSize);
//            tvName.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

            tvNumber = itemView.findViewById(R.id.tv_status);
//            tvNumber.setTextSize(textSize);
//            tvNumber.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));


            imageView = itemView.findViewById(R.id.image);
            checkBox = itemView.findViewById(R.id.imb_check);
        }
    }
}
