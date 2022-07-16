package com.yawar.memo.adapter;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
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
import com.yawar.memo.fragment.SearchFragment;
import com.yawar.memo.model.DeviceLinkModel;
import com.yawar.memo.model.SearchRespone;

import java.util.ArrayList;


public class DeviceLinkAdapter extends RecyclerView.Adapter<com.yawar.memo.adapter.DeviceLinkAdapter.ViewHolders> {
    ///Initialize variable
    Activity activity;
    float textSize = 14.0F ;
    SharedPreferences sharedPreferences ;

    ArrayList<DeviceLinkModel> arrayList;
    private CallbackInterface mCallback;
    public interface CallbackInterface{

        /**
         * Callback invoked when clicked
         * @param position - the position
         * @param deviceLinkModel - the text to pass back
         */
        void onHandleSelection(int position, DeviceLinkModel deviceLinkModel);
        void onClickItem(int position, DeviceLinkModel deviceLinkModel);

    }


    ///Create constructor
    public DeviceLinkAdapter(Activity activity, ArrayList<DeviceLinkModel> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
        try{
            mCallback = (CallbackInterface) activity;
        }catch(ClassCastException ex){
            //.. should log the error or throw and exception
        }
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public com.yawar.memo.adapter.DeviceLinkAdapter.ViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ///Initialize view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device_link, parent, false);


        DeviceLinkAdapter.ViewHolders holder = new DeviceLinkAdapter.ViewHolders(view);

        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull com.yawar.memo.adapter.DeviceLinkAdapter.ViewHolders holder, int position) {

        DeviceLinkModel model = arrayList.get(position);
        holder.tvName.setText(model.getName());
        holder.tvNumber.setText(model.getTime());
        System.out.println(model.getImage());

        // Glide.with(holder.imageView.getContext()).load(model.getImage()).into(holder.imageView);
        if(!model.getImage().isEmpty()){
//            Glide.with(holder.imageView.getContext()).load(AllConstants.imageUrl+model.getImage()).error(activity.getDrawable(R.drawable.th)).into(holder.imageView);
            Glide.with(holder.imageView.getContext()).load(AllConstants.imageUrl+model.getImage()).apply(RequestOptions.placeholderOf(R.drawable.th).error(R.drawable.th)).into(holder.imageView);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCallback != null){
                    mCallback.onClickItem(position, arrayList.get(position));
                }
            }
        });







    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }




    public class ViewHolders extends RecyclerView.ViewHolder {
        ///Initialize variable
        TextView tvName;
        ImageView imageView;
        TextView tvNumber;

        Button button;
        public ViewHolders(@NonNull View itemView) {
            super(itemView);
            ////Assign variable
            tvName = itemView.findViewById(R.id.tv_name);
                tvName.setTextSize(textSize);
                tvName.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

            tvNumber = itemView.findViewById(R.id.tv_number);
                tvNumber.setTextSize(textSize);
                tvNumber.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));


            imageView = itemView.findViewById(R.id.iv_image);
            button = itemView.findViewById(R.id.btn_add);
        }
    }
}