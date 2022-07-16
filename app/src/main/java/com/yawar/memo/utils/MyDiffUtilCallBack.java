package com.yawar.memo.utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.yawar.memo.model.ChatRoomModel;

  public class MyDiffUtilCallBack extends DiffUtil.ItemCallback<ChatRoomModel> {



    @Override
    public boolean areItemsTheSame(@NonNull ChatRoomModel oldItem, @NonNull ChatRoomModel newItem) {
        return oldItem.getUserId().equals(newItem.getUserId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull ChatRoomModel oldItem, @NonNull ChatRoomModel newItem) {
        return 0==oldItem.compareTo(newItem);
    }
}
