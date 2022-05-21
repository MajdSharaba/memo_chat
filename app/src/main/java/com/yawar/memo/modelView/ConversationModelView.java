package com.yawar.memo.modelView;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yawar.memo.model.ChatMessage;
import com.yawar.memo.model.ChatRoomModel;
import com.yawar.memo.repositry.ChatMessageRepo;
import com.yawar.memo.repositry.ChatRoomRepo;
import com.yawar.memo.utils.BaseApp;

import java.util.ArrayList;

    public class ConversationModelView extends ViewModel {
        BaseApp baseApp = BaseApp.getInstance();
        private final ChatMessageRepo repository = baseApp.getChatMessageRepo() ;

        public MutableLiveData<ArrayList<ChatMessage>> getChatMessaheHistory() {
            return repository.chatMessageistMutableLiveData;

        }
    }

