package com.yawar.memo.ui.chatPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class ConversationViewModelFactory(private val anotherUserId: String, private val blockedFor: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

           if (modelClass.isAssignableFrom(ConversationModelView::class.java)) {
            return ConversationModelView(anotherUserId,blockedFor) as  T
        }
                throw IllegalArgumentException("Unknown ViewModel class")

    }
}