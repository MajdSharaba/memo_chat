package com.yawar.memo.utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.yawar.memo.domain.model.ChatRoomModel;

  public class MyDiffUtilCallBack extends DiffUtil.ItemCallback<ChatRoomModel> {



    @Override
    public boolean areItemsTheSame(@NonNull ChatRoomModel oldItem, @NonNull ChatRoomModel newItem) {
        return oldItem.getOther_id().equals(newItem.getOther_id());
    }

    @Override
    public boolean areContentsTheSame(@NonNull ChatRoomModel oldItem, @NonNull ChatRoomModel newItem) {
        return oldItem.equals(newItem);
    }
}
