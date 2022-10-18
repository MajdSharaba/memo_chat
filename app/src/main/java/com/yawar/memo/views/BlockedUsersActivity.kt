package com.yawar.memo.views

import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.yawar.memo.service.SocketIOService
import com.yawar.memo.sessionManager.ClassSharedPreferences
import com.yawar.memo.Api.ServerApi
import com.yawar.memo.R
import com.yawar.memo.adapter.BlockUserAdapter
import com.yawar.memo.model.UserModel
import com.yawar.memo.modelView.BlockedActViewModel
import com.yawar.memo.utils.BaseApp
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList
import kotlin.math.log


class BlockedUsersActivity : AppCompatActivity(), BlockUserAdapter.CallbackInterface {
    lateinit var classSharedPreferences: ClassSharedPreferences
    lateinit var myBase: BaseApp
    lateinit var userModel: UserModel
    lateinit var recyclerView: RecyclerView
    lateinit var blockedActViewModel: BlockedActViewModel
     var blockUserAdapter: BlockUserAdapter? = null
    lateinit var serverApi: ServerApi
    private lateinit var UserBlocked: UserModel
     var userBlockeds = ArrayList<UserModel>()
    private fun sendUnBlockFor(blocked: Boolean) {
        val userUnBlocked = JSONObject()
        try {
            userUnBlocked.put("my_id", userModel.userId)
            userUnBlocked.put("user_id", UserBlocked.userId)
            userUnBlocked.put("blocked_for", blockedActViewModel.blockedFor().value)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val service = Intent(this, SocketIOService::class.java)
        service.putExtra(SocketIOService.EXTRA_UN_BLOCK_PARAMTERS, userUnBlocked.toString())
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_UN_BLOCK)
        startService(service)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blocked_users)
        supportActionBar?.setTitle(R.string.contact_number_blocked)
        val supportActionBar = supportActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        classSharedPreferences = BaseApp.getInstance().classSharedPreferences
        myBase = BaseApp.getInstance()
        blockedActViewModel = ViewModelProvider(this).get(BlockedActViewModel::class.java)

//        chatRoomRepo = myBase.getChatRoomRepo();
        userModel = classSharedPreferences.user
        serverApi = ServerApi(this)
        blockedActViewModel.loadData().observe(this,
            Observer<ArrayList<UserModel?>?> { userModels ->
                if (userModels != null) {
                    Log.d("BlockedUsersActivity", "onCreate: ${userModels.size}")
                    userBlockeds.clear()
                    for (user in userModels) {
                        if (user != null) {
                            Log.d("BlockedUsersActivity", "add: ")

                            userBlockeds.add(user)
                        }
                    }
                    Log.d("BlockedUsersActivity", "updateList${userBlockeds.size} ")

                    blockUserAdapter?.updateList(userBlockeds)
                }
                //adapter.notifyDataSetChanged();
        blockedActViewModel.isBlocked().observe(
            this
        ) { s ->
            if (s != null) {
                if (s) {
                    blockedActViewModel.setBlocked(false)
                }
            }
        }
        ////////////
        blockedActViewModel.isUnBlocked().observe(
            this
        ) { s ->
            if (s != null) {
                if (s) {
                    sendUnBlockFor(s)
                    blockedActViewModel.setUnBlocked(false)
                }
            }
        }

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        blockUserAdapter = BlockUserAdapter(this, userBlockeds)
        recyclerView.adapter = blockUserAdapter

//        getUsersBlocked();
    })

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> finish()
        }
        return true
    }

    override fun onHandleSelection(position: Int, blockUser: UserModel?) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(R.string.alert_unblock_user)
        dialog.setPositiveButton(R.string.Unblock,
            DialogInterface.OnClickListener { _, _ ->
                if (blockUser != null) {
                    UserBlocked = blockUser
                }
                if (blockUser != null) {
                    blockedActViewModel.sendUnBlockRequest(userModel.userId, blockUser.userId)
                }
            })
        dialog.setNegativeButton(R.string.cancel,
            DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
        val alertDialog = dialog.create()
        alertDialog.show()
    }
}