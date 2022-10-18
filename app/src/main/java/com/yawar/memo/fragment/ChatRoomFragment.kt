package com.yawar.memo.fragment
//import com.yawar.memo.call.CompleteActivity;
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tsuryo.swipeablerv.SwipeLeftRightCallback
import com.tsuryo.swipeablerv.SwipeableRecyclerView
import com.yawar.memo.Api.ServerApi
import com.yawar.memo.R
import com.yawar.memo.adapter.ChatRoomAdapter
import com.yawar.memo.model.ChatRoomModel
import com.yawar.memo.model.UserModel
import com.yawar.memo.modelView.ChatRoomViewModel
import com.yawar.memo.sessionManager.ClassSharedPreferences
import com.yawar.memo.utils.BaseApp
import com.yawar.memo.views.ArchivedActivity
import com.yawar.memo.views.ContactNumberActivity
import com.yawar.memo.views.ConversationActivity
import com.yawar.memo.views.GroupSelectorActivity

class ChatRoomFragment : Fragment(), ChatRoomAdapter.CallbackInterfac {
    lateinit var recyclerView: SwipeableRecyclerView
    var postList: MutableList<ChatRoomModel> = ArrayList()
    lateinit var myId: String
    lateinit var myBase: BaseApp
    lateinit var chatRoomViewModel: ChatRoomViewModel
    lateinit var itemAdapter: ChatRoomAdapter
    lateinit var startNewChat: Button
    lateinit var searchView: SearchView
    lateinit var toolbar: Toolbar
    lateinit var classSharedPreferences: ClassSharedPreferences
    lateinit var serverApi: ServerApi
    lateinit var userModel: UserModel
    lateinit var linerArchived: LinearLayout
    lateinit var lineerNoMessage: LinearLayout
    lateinit var fab: FloatingActionButton
    lateinit var chat: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat_room, container, false)
        myBase = BaseApp.getInstance()
        classSharedPreferences = BaseApp.getInstance().classSharedPreferences
        myId = classSharedPreferences.user.userId.toString()
        linerArchived = view.findViewById(R.id.liner_archived)
        lineerNoMessage = view.findViewById(R.id.liner_no_chat)
        startNewChat = view.findViewById(R.id.btn_start_chat)
        fab = view.findViewById(R.id.fab)
        linerArchived.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, ArchivedActivity::class.java)
            startActivity(intent)
        })
        recyclerView = view.findViewById(R.id.recycler)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = linearLayoutManager
        chatRoomViewModel = ViewModelProvider(this).get(ChatRoomViewModel::class.java)
        chatRoomViewModel.getIsArchived().observe(
            requireActivity()) { aBoolean ->
            if (aBoolean) {
                linerArchived.visibility = View.VISIBLE
            } else {
                linerArchived.visibility = View.GONE
            }
        }
        itemAdapter = ChatRoomAdapter(this)
        chatRoomViewModel.loadData().observe(
            requireActivity(),
            Observer<ArrayList<ChatRoomModel?>?> { chatRoomModels ->
                if (chatRoomModels != null) {
                    if (chatRoomModels.isEmpty()) {
                        lineerNoMessage.visibility = View.VISIBLE
                        fab.visibility = View.GONE
                        recyclerView.visibility = View.GONE
                    } else {
                        lineerNoMessage.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        fab.visibility = View.VISIBLE
                        val list = ArrayList<ChatRoomModel?>()
                        postList.clear()
                        for (chatRoomModel in chatRoomModels) {
                            if (chatRoomModel != null) {
                                if (chatRoomModel.state != "0" && chatRoomModel.state != myId) {
                                    list.add(chatRoomModel.clone())
                                    postList.add(chatRoomModel)
                                } else {
                                    chatRoomViewModel.setArchived(true)
                                }
                            }
                        }
                        itemAdapter.setData(list)
                    }
                }
            })
        recyclerView.adapter = itemAdapter
        recyclerView.setListener(object : SwipeLeftRightCallback.Listener {
            override fun onSwipedLeft(position: Int) {
                chatRoomViewModel.deleteChatRoom(myId, postList[position].other_id)
            }

            override fun onSwipedRight(position: Int) {
                chatRoomViewModel.addToArchived(myId, postList[position].other_id)
            }
        })


        ////////////////FloatingActionButton
        fab.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, ContactNumberActivity::class.java)
            startActivity(intent)
        })

        ///////new chat
        startNewChat.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, ContactNumberActivity::class.java)
            startActivity(intent)
        })


////////////// for search
        searchView = view.findViewById(R.id.search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                itemAdapter.filter!!.filter(newText)
                return false
            }
        })

/////// for Bottom nav


        chat = view.findViewById(R.id.chat)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.basic_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return when (id) {
            R.id.group -> {
                val intent = Intent(activity, GroupSelectorActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.item2 -> {
                Toast.makeText(activity, "Item 2 Selected", Toast.LENGTH_LONG).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onHandleSelection(position: Int, chatRoomModel: ChatRoomModel?) {
        val bundle = Bundle()
        bundle.putString("reciver_id", chatRoomModel!!.other_id)
        bundle.putString("sender_id", myId)
        bundle.putString("fcm_token", chatRoomModel.user_token)
        bundle.putString("name", chatRoomModel.username)
        bundle.putString("image", chatRoomModel.image)
        bundle.putString("chat_id", chatRoomModel.id)
        bundle.putString("special", chatRoomModel.sn)
        bundle.putString("blockedFor", chatRoomModel.blocked_for)


        ///////////////////////
        val intent = Intent(context, ConversationActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}