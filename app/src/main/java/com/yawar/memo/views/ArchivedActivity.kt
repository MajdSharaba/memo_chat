package com.yawar.memo.views

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsuryo.swipeablerv.SwipeLeftRightCallback
import com.tsuryo.swipeablerv.SwipeableRecyclerView
import com.yawar.memo.R
import com.yawar.memo.adapter.ArchivedAdapter
import com.yawar.memo.model.ChatRoomModel
import com.yawar.memo.modelView.ArchivedActViewModel
import com.yawar.memo.sessionManager.ClassSharedPreferences
import com.yawar.memo.utils.BaseApp

class ArchivedActivity : AppCompatActivity(), ArchivedAdapter.CallbackInterfac {
    lateinit var recyclerView: SwipeableRecyclerView
    lateinit var linear_no_archived: LinearLayout
     var archived: MutableList<ChatRoomModel> = ArrayList()
    lateinit var itemAdapter: ArchivedAdapter
    lateinit var searchView: SearchView
    lateinit var toolbar: Toolbar
    lateinit var archivedActViewModel: ArchivedActViewModel
    lateinit var classSharedPreferences: ClassSharedPreferences
    lateinit var myId: String
    lateinit var myBase: BaseApp
    lateinit var archive: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archived)
        recyclerView = findViewById(R.id.recycler_view)
        linear_no_archived = findViewById(R.id.liner_no_chat_history)
        archive = findViewById(R.id.archived)
        classSharedPreferences = BaseApp.getInstance().classSharedPreferences
        myId = classSharedPreferences.user.userId!!
        archivedActViewModel = ViewModelProvider(this).get(
            ArchivedActViewModel::class.java
        )
        myBase = application as BaseApp
        //        chatRoomRepo = myBase.getChatRoomRepo();
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = linearLayoutManager
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
                        linear_no_archived.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                        archivedActViewModel.setArchived(false)
                    } else {
                        linear_no_archived.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        itemAdapter.setData(list)
                    }
                }
            })
        itemAdapter = ArchivedAdapter(this)
        recyclerView.adapter = itemAdapter
        recyclerView.setListener(object : SwipeLeftRightCallback.Listener {
            override fun onSwipedLeft(position: Int) {
                println(position)
            }

            override fun onSwipedRight(position: Int) {
                archivedActViewModel.removeFromArchived(myId, archived[position].other_id)
            }
        })
        searchView = findViewById(R.id.search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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