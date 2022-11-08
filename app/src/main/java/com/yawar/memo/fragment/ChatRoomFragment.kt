package com.yawar.memo.fragment
//import com.yawar.memo.call.CompleteActivity;
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
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
import com.yawar.memo.databinding.ActivitySplashScreenBinding
import com.yawar.memo.databinding.FragmentChatRoomBinding
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
    var postList: MutableList<ChatRoomModel> = ArrayList()
    lateinit var myId: String
    lateinit var myBase: BaseApp
    lateinit var chatRoomViewModel: ChatRoomViewModel
    lateinit var itemAdapter: ChatRoomAdapter
    lateinit var classSharedPreferences: ClassSharedPreferences
    lateinit var serverApi: ServerApi
    lateinit var userModel: UserModel
    lateinit var binding: FragmentChatRoomBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        val view = inflater.inflate(R.layout.fragment_chat_room, container, false)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat_room,container,false)
        val view = binding.getRoot();
        myBase = BaseApp.getInstance()
        classSharedPreferences = BaseApp.getInstance().classSharedPreferences
        myId = classSharedPreferences.user.userId.toString()

        binding.content.linerArchived.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, ArchivedActivity::class.java)
            startActivity(intent)
        })
        binding.content.recycler.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.content.recycler.layoutManager = linearLayoutManager
        chatRoomViewModel = ViewModelProvider(this).get(ChatRoomViewModel::class.java)
        chatRoomViewModel.getIsArchived().observe(
            requireActivity()) { aBoolean ->
            if (aBoolean) {
                binding.content.linerArchived.visibility = View.VISIBLE
            } else {
                binding.content.linerArchived.visibility = View.GONE
            }
        }
        itemAdapter = ChatRoomAdapter(this)
        chatRoomViewModel.loadData().observe(
            requireActivity(),
            Observer<ArrayList<ChatRoomModel?>?> { chatRoomModels ->
                if (chatRoomModels != null) {
                    if (chatRoomModels.isEmpty()) {
                        binding.content.linerNoChat.visibility = View.VISIBLE
                        binding.fab.visibility = View.GONE
                        binding.content.recycler.visibility = View.GONE
                    } else {
                        binding.content.linerNoChat.visibility = View.GONE
                        binding.content.recycler.visibility = View.VISIBLE
                        binding.fab.visibility = View.VISIBLE
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
        binding.content.recycler.adapter = itemAdapter
        binding.content.recycler.setListener(object : SwipeLeftRightCallback.Listener {
            override fun onSwipedLeft(position: Int) {
                chatRoomViewModel.deleteChatRoom(myId, postList[position].other_id)
            }

            override fun onSwipedRight(position: Int) {
                chatRoomViewModel.addToArchived(myId, postList[position].other_id)
            }
        })


        ////////////////FloatingActionButton
        binding.fab.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, ContactNumberActivity::class.java)
            startActivity(intent)
        })

        ///////new chat
        binding.content.btnStartChat.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, ContactNumberActivity::class.java)
            startActivity(intent)
        })


////////////// for search
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                itemAdapter.filter!!.filter(newText)
                return false
            }
        })

/////// for Bottom nav


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