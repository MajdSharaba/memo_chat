package com.yawar.memo.ui.AcrchivedPage

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsuryo.swipeablerv.SwipeLeftRightCallback
import com.yawar.memo.R
import com.yawar.memo.databinding.ActivityArchivedBinding
import com.yawar.memo.model.ChatRoomModel
import com.yawar.memo.sessionManager.ClassSharedPreferences
import com.yawar.memo.ui.chatPage.ConversationActivity
import com.yawar.memo.utils.BaseApp

class ArchivedActivity : AppCompatActivity(), ArchivedAdapter.CallbackInterfac {
     var archived: MutableList<ChatRoomModel> = ArrayList()
    lateinit var itemAdapter: ArchivedAdapter
    lateinit var archivedActViewModel: ArchivedActViewModel
    lateinit var classSharedPreferences: ClassSharedPreferences
    lateinit var myId: String
    lateinit var myBase: BaseApp
    lateinit var binding: ActivityArchivedBinding


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_archived)
        classSharedPreferences = BaseApp.getInstance().classSharedPreferences
        myId = classSharedPreferences.user.userId!!
        archivedActViewModel = ViewModelProvider(this).get(
            ArchivedActViewModel::class.java
        )
        myBase = application as BaseApp
        //        chatRoomRepo = myBase.getChatRoomRepo();
        binding.recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.recyclerView.layoutManager = linearLayoutManager
        archivedActViewModel.loadData().observe(this,
            Observer<ArrayList<ChatRoomModel?>?> { chatRoomModels ->
                if (chatRoomModels != null) {
                    val list = ArrayList<ChatRoomModel?>()
                    archived.clear()
                    for (chatRoomModel in chatRoomModels) {
                        if (chatRoomModel != null) {
                            if (chatRoomModel.state == "0" || chatRoomModel.state == myId) {
                                list.add(chatRoomModel.clone())
                                archived.add(chatRoomModel)
                            }
                        }
                    }
                    if (archived.isEmpty()) {
                        binding.linerNoChatHistory.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                        archivedActViewModel.setArchived(false)
                    } else {
                        binding.linerNoChatHistory.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                        itemAdapter.setData(list)
                    }
                }
            })
        itemAdapter = ArchivedAdapter(this)
        binding.recyclerView.adapter = itemAdapter
        binding.recyclerView.setListener(object : SwipeLeftRightCallback.Listener {
            override fun onSwipedLeft(position: Int) {
                println(position)
            }

            override fun onSwipedRight(position: Int) {
                archivedActViewModel.removeFromArchived(myId, archived[position].other_id)
            }
        })
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                itemAdapter.filter.filter(newText)
                return false
            }
        })
    }

    override fun onHandleSelection(position: Int, chatRoomModel: ChatRoomModel?) {
        Toast.makeText(this, "Position " + chatRoomModel!!.last_message, Toast.LENGTH_SHORT).show()
        println(chatRoomModel.username)
        val bundle = Bundle()
        bundle.putString("reciver_id", chatRoomModel.other_id)
        bundle.putString("sender_id", myId)
        bundle.putString("fcm_token", chatRoomModel.user_token)
        bundle.putString("name", chatRoomModel.username)
        bundle.putString("image", chatRoomModel.image)
        bundle.putString("chat_id", chatRoomModel.id)
        bundle.putString("special", chatRoomModel.sn)
        bundle.putString("blockedFor", chatRoomModel.blocked_for)
        val intent = Intent(this, ConversationActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}