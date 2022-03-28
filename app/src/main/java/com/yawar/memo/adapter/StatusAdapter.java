//package com.yawar.memo.adapter;
//
//import android.graphics.Color;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
////import com.devlomi.circularstatusview.CircularStatusView;
//import com.yawar.memo.R;
//import com.yawar.memo.model.Status;
//import com.yawar.memo.model.UserStatus;
//
//import java.util.List;
//
//public class StatusAdapter extends RecyclerView.Adapter<com.yawar.memo.adapter.StatusAdapter.StatusHolder> {
//
//    private List<UserStatus> userStatusList;
//
//    public StatusAdapter(List<UserStatus> statusList) {
//        this.userStatusList = statusList;
//    }
//
//    @Override
//    public StatusHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_status, parent, false);
//        return new StatusHolder(row);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull StatusHolder holder, int position) {
//
//        holder.bind(userStatusList.get(position));
//    }
//
//    @Override
//    public int getItemCount() {
//        return userStatusList.size();
//    }
//
//    class StatusHolder extends RecyclerView.ViewHolder {
//
//        private CircularStatusView circularStatusView;
//        private TextView textView;
//        private ImageView imageView;
//
//
//        public StatusHolder(View itemView) {
//            super(itemView);
//            circularStatusView = itemView.findViewById(R.id.circular_status_view);
//            textView = itemView.findViewById(R.id.textView);
//            imageView = itemView.findViewById(R.id.circle_image);
//        }
//
//
//        public void bind(UserStatus userStatus) {
//            textView.setText(userStatus.getUserName());
//            List<Status> statusList = userStatus.getStatusList();
//            circularStatusView.setPortionsCount(statusList.size());
//            int notSeenColor = itemView.getContext().getResources().getColor(R.color.blue);
//            int seenColor = Color.GRAY;
//            String lastImage;
//            boolean first =true;
//
//
//
//            if (userStatus.areAllSeen()) {
//                //set all portions color
//                System.out.println("All Seen");
//                if(!statusList.isEmpty()){
//                circularStatusView.setPortionsColor(seenColor);
//                Glide.with(imageView.getContext()).load(statusList.get(0).getUrl()).centerCrop()
//                        .into(imageView);}
//            } else {
//                for (int i = 0; i < statusList.size(); i++) {
//                    Status status = statusList.get(i);
//
//                    System.out.println(status.isSeen()+" int color = status.isSeen() ? seenColor : notSeenColor;");
//
//                    int color = status.isSeen() ? seenColor : notSeenColor;
//                    //set specific color for every portion
//                    if(!status.isSeen()&&first){
//                        Glide.with(imageView.getContext()).load(status.getUrl()).centerCrop()
//                                .into(imageView);
//                        first=false;
//                    }
//                    circularStatusView.setPortionColorForIndex(i, color);
//
//
//
//                }
//
//            }
//
//
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (onStatusClickListener != null)
//                        onStatusClickListener.onStatusClick(circularStatusView, getAdapterPosition());
//                }
//            });
//        }
//    }
//
//    public interface OnStatusClickListener {
//        void onStatusClick(CircularStatusView circularStatusView, int pos);
//    }
//
//    OnStatusClickListener onStatusClickListener;
//
//    public void setOnStatusClickListener(OnStatusClickListener onStatusClickListener) {
//        this.onStatusClickListener = onStatusClickListener;
//    }
//}
