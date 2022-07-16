package com.yawar.memo.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yawar.memo.R;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.model.UserModel;
import com.yawar.memo.model.UserModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

//public class BlockUserAdapter {
//}
public class BlockUserAdapter extends RecyclerView.Adapter<BlockUserAdapter.ViewHolder> {
    ///Initialize variable
    Activity activity;
    List<UserModel> arrayList= Collections.emptyList();

    public BlockUserAdapter.CallbackInterface mCallback;

    float textSize = 14.0F ;
    SharedPreferences sharedPreferences ;


    public interface CallbackInterface {

        /**
         * Callback invoked when clicked
         *
         * @param position             - the position
         * SendContactNumberResponse - the text to pass back
         */
        void onHandleSelection(int position, UserModel userModel);

    }


    ///Create constructor
    public BlockUserAdapter(Activity activity, List<UserModel> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
        try {
            mCallback = (BlockUserAdapter.CallbackInterface) activity;
        } catch (ClassCastException ex) {
            //.. should log the error or throw and exception
        }
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ///Initialize view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_block_user,parent,false);

        sharedPreferences = activity.getSharedPreferences("txtFontSize", Context.MODE_PRIVATE);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserModel model = arrayList.get(position);
        holder.tvName.setText(model.getUserName()+" "+model.getLastName());
        holder.tvNumber.setText(model.getSecretNumber());
        System.out.println(model.getImage());
        if(!model.getImage().isEmpty()){
//            Glide.with(holder.imageView.getContext()).load(AllConstants.imageUrl+model.getImage()).error(activity.getDrawable(R.drawable.th)).into(holder.imageView);
            Glide.with(holder.imageView.getContext()).load(AllConstants.imageUrl+model.getImage()).apply(RequestOptions.placeholderOf(R.drawable.th).error(R.drawable.th)).into(holder.imageView);
           }
        else {
            holder.imageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.th));

        }

        // Glide.with(holder.imageView.getContext()).load(model.getImage()).into(holder.imageView);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mCallback != null) {

                        mCallback.onHandleSelection(position, arrayList.get(position));

                    }

                }
            });

        }





    @Override
    public int getItemCount() {
        return arrayList.size();
    }
//    public void filter(String charText) {
//        System.out.println(charText+listsearch2.size());
//        charText = charText.toLowerCase(Locale.getDefault());
//        listsearch.clear();
//        if (charText.length() == 0) {
//
//            listsearch.addAll(listsearch2);
//        } else {
//            for (SendContactNumberResponse wp : listsearch2) {
//                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
//                    listsearch.add(wp);
//                }
//            }
//        }
//        arrayList.clear();
//        arrayList.addAll(listsearch);
//        notifyDataSetChanged();
//    }
public void updateList(ArrayList<UserModel> updateList){
    arrayList = updateList;

    notifyDataSetChanged();
}

    public class ViewHolder extends RecyclerView.ViewHolder {
        ///Initialize variable
        TextView tvName;
        TextView tvNumber;
        ImageView imageView;
        Button button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ////Assign variable
            tvName = itemView.findViewById(R.id.tv_name);
            tvName.setTextSize(textSize);
            tvName.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));


            tvNumber = itemView.findViewById(R.id.tv_number);
            tvNumber.setTextSize(textSize);
            tvNumber.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

            imageView = itemView.findViewById(R.id.iv_image);
            button = itemView.findViewById(R.id.btn_share);
        }

    }
}
