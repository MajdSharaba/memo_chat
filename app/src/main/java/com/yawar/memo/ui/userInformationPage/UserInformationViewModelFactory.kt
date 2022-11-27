package com.yawar.memo.ui.userInformationPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yawar.memo.ui.chatPage.ConversationModelView



    class UserInformationViewModelFactory (private val blockedFor: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            if (modelClass.isAssignableFrom(UserInformationViewModel::class.java)) {
                return UserInformationViewModel(blockedFor) as  T
            }
            throw IllegalArgumentException("Unknown ViewModel class")

        }

}