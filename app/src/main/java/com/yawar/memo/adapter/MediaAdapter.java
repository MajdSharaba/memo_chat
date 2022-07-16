package com.yawar.memo.adapter;


import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.yawar.memo.R;
import com.yawar.memo.constant.AllConstants;
import com.yawar.memo.model.MediaModel;

import java.util.ArrayList;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.RecyclerViewHolder> {

    private final ArrayList<MediaModel> courseDataArrayList;
    private final Context mcontext;

    public MediaAdapter(ArrayList<MediaModel> recyclerDataArrayList, Context mcontext) {
        this.courseDataArrayList = recyclerDataArrayList;
        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_item, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        // Set the data to textview and imageview.
        MediaModel recyclerData = courseDataArrayList.get(position);
        if(!recyclerData.getImgid().isEmpty()){
//        Glide.with( holder.courseIV.getContext()).load(AllConstants.imageUrlInConversation+recyclerData.getImgid()).centerCrop().error(mcontext.getResources().getDrawable(R.drawable.th))
//                .into(holder.courseIV);
            Glide.with(holder.courseIV.getContext()).load(AllConstants.imageUrlInConversation+recyclerData.getImgid()).centerCrop().apply(RequestOptions.placeholderOf(R.drawable.th).error(R.drawable.th)).into(holder.courseIV);

            holder.courseIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                        androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder(view.getContext());
//                        View mView = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_image_cht, null);
//                        ImageView photoView =  mView.findViewById(R.id.photo_view);
////                        Glide.with(photoView.getContext()).load("https://th.bing.com/th/id/OIP.DP48uGkldTg01Wx5KTExXAHaE6?pid=ImgDet&rs=1").into(photoView);
//                        photoView.setImageResource(R.drawable.ic_send_done);
//                        mBuilder.setView(mView);
////                        androidx.appcompat.app.AlertDialog mDialog = mBuilder.create();
//                        mBuilder.show();
                    Dialog dialog = new Dialog(mcontext);
                    dialog.setContentView(R.layout.dialog_image_cht);
                    dialog.setTitle("Title...");

                    // set the custom dialog components - text, image and button
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);


                    PhotoView image = dialog.findViewById(R.id.photo_view);
//                    Glide.with(image.getContext()).load(AllConstants.imageUrlInConversation+recyclerData.getImgid()).centerCrop().into(image);
                    Glide.with(image.getContext()).load(AllConstants.imageUrlInConversation+recyclerData.getImgid()).centerCrop().apply(RequestOptions.placeholderOf(R.drawable.th).error(R.drawable.th)).into(image);

                    dialog.show();

                }
            });

        }
//        holder.courseIV.setImageResource(recyclerData.getImgid());
    }

    @Override
    public int getItemCount() {
        // this method returns the size of recyclerview
        return courseDataArrayList.size();
    }

    // View Holder Class to handle Recycler View.
    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private final ImageView courseIV;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            courseIV = itemView.findViewById(R.id.idIVcourseIV);
        }
    }
}