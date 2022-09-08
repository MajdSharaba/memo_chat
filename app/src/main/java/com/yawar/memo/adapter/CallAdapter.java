package com.yawar.memo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yawar.memo.R;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.model.CallModel;
import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.utils.TimeProperties;

import java.util.ArrayList;
import java.util.List;



public class CallAdapter extends ListAdapter<CallModel,CallAdapter.View_Holder> implements Filterable {

    List<CallModel> listsearch = new ArrayList<CallModel>() ;
    ClassSharedPreferences classSharedPreferences;
    Context context;
    TimeProperties timeProperties=new TimeProperties();





    //public ChatRoomAdapter(List<ChatRoomModel> data, Activity context,  ListItemClickListener mOnClickListener) {
    public CallAdapter( Context context ) {
        super(new MyDiffUtilCall());
        this.context = context;
        listsearch.addAll(getCurrentList());
        classSharedPreferences = new ClassSharedPreferences(context);
    }

    @NonNull
    @Override
    public CallAdapter.View_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calls, parent, false);
        //ChatRoomAdapter.View_Holder holder = new View_Holder(v,mOnClickListener);
        CallAdapter.View_Holder holder = new CallAdapter.View_Holder(v);
        return holder;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull CallAdapter.View_Holder holder, int position) {

        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        CallModel callModel = getItem(position);
        holder.name.setText(callModel.getUsername());
        holder.time.setText(TimeProperties.getDate(Long.parseLong(callModel.getCreatedAt()), "MMMM dd,h:mm aa"));
        // Glide.with(holder.imageView.getContext()).load(model.getImage()).into(holder.imageView);
        if (!callModel.getImage().isEmpty()) {
            Glide.with(holder.imageView.getContext()).load(AllConstants.imageUrl + callModel.getImage()).apply(RequestOptions.placeholderOf(R.drawable.th).error(R.drawable.th)).into(holder.imageView);

        }
        if(callModel.getCall_type().equals("video")){
            holder.imageType.setImageDrawable(context.getDrawable(R.drawable.ic_video_call));
        }
        else {
            holder.imageType.setImageDrawable(context.getDrawable(R.drawable.ic_call_blue));

        }

        if(callModel.getCaller_id().equals(classSharedPreferences.getUser().getUserId())){
            holder.imageStatuse.setImageDrawable(context.getDrawable(R.drawable.ic_out_going_call));
        }
        else {
            holder.imageStatuse.setImageDrawable(context.getDrawable(R.drawable.ic_incoming_call));

        }

        if (callModel.getCall_status().equals("missed call")){
            holder.imageStatuse.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.red)));

        }
        else if (callModel.getCall_status().equals("answer")){
            holder.imageStatuse.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.memo_background_color_new)));

        }
        else {
            holder.imageStatuse.setImageDrawable(context.getDrawable(R.drawable.ic_close));
            holder.imageStatuse.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.red)));

        }



    }




    @Override
    public Filter getFilter() {
        return exampleFilter;
    }
    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<CallModel> filteredList = new ArrayList<>();
            System.out.println(listsearch.size()+"listsearch.size()");

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(listsearch);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (CallModel item : listsearch) {
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

            submitList((List)results.values);
        }
    };

    public void setData(ArrayList<CallModel> newData) {
        listsearch = newData;

        submitList(newData);
    }



    //class View_Holder extends RecyclerView.ViewHolder implements View.OnClickListener{
    class View_Holder extends RecyclerView.ViewHolder {
        TextView name;
        TextView time;
        ImageView imageView;
        ImageView imageType;
        ImageView imageStatuse;


        View_Holder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_name);
            time = (TextView) itemView.findViewById(R.id.tv_time);
            imageView = (ImageView) itemView.findViewById(R.id.iv_image);
            imageType = (ImageView) itemView.findViewById(R.id.image_call_type);
            imageStatuse = (ImageView) itemView.findViewById(R.id.image_call_status);





        }
    }




public static class MyDiffUtilCall extends DiffUtil.ItemCallback<CallModel> {


    @Override
    public boolean areItemsTheSame(@NonNull CallModel oldItem, @NonNull CallModel newItem) {
        return oldItem.getId().equals(newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull CallModel oldItem, @NonNull CallModel newItem) {
        return 0 == oldItem.compareTo(newItem);
    }

}
}
