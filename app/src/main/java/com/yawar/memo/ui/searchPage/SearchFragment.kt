package com.yawar.memo.ui.searchPage

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.paging.CombinedLoadStates
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.yawar.memo.BaseApp
import com.yawar.memo.R
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.databinding.FragmentSearchBinding
import com.yawar.memo.domain.model.AnthorUserInChatRoomId
import com.yawar.memo.domain.model.SearchModel
import com.yawar.memo.permissions.Permissions
import com.yawar.memo.sessionManager.ClassSharedPreferences
import com.yawar.memo.ui.chatPage.ConversationActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class SearchFragment : Fragment(), SearchAdapter.CallbackInterface {
    var list = ArrayList<SearchModel?>()
    lateinit var binding: FragmentSearchBinding
    lateinit var searchAdapter: SearchAdapter
    var res = ArrayList<SearchModel>()
    private lateinit var permissions: Permissions
    lateinit var classSharedPreferences: ClassSharedPreferences
    lateinit var my_id: String
    var searchParamters = ""
    lateinit var linearLayoutManager: LinearLayoutManager
    private var timer: Timer? = Timer()
    private val DELAY: Long = 1000
    private lateinit var loadingPB: ProgressBar
    private lateinit var nestedSV: NestedScrollView
    val handler = Handler()
    val anthorUserInChatRoomId = AnthorUserInChatRoomId.getInstance("","","","","","","")
    val searchModelView by viewModels<SearchModelView>()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        val view = binding.root
        timer = Timer()
        permissions = Permissions()
        classSharedPreferences = BaseApp.instance?.classSharedPreferences!!
        my_id = classSharedPreferences.user.userId.toString()
        checkpermission()
        binding.searchBySecretNumber.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (timer != null) {
                    timer!!.cancel()
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                try {
                    if (newText.length >= 0) {
                        if (timer != null) {
                            timer!!.cancel()
                        }
                        timer = Timer()
                        val t: TimerTask = object : TimerTask() {
                            override fun run() {
                                searchParamters = newText
                                checkpermission()
                            }
                        }
                        timer!!.schedule(t, 300)
                    }
                } catch (e: Exception) {
                    println("EROOOR")
                }

                return false
            }
        })
        searchAdapter = SearchAdapter(this, requireActivity())
        binding.bindAdapter(articleAdapter = searchAdapter)
//            searchAdapter.submitData(viewLifecycleOwner.lifecycle,it)
        val items = searchModelView.items
        lifecycleScope.launch {
            // We repeat on the STARTED lifecycle because an Activity may be PAUSED
            // but still visible on the screen, for example in a multi window app
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                items.collectLatest {
                    Log.d("lifecycleScope", "")
                    searchAdapter.submitData(it)



                }
            }
        }

        val listener: (CombinedLoadStates) -> Unit = { combinedLoadStates ->
            if (searchAdapter.itemCount == 0) {
                binding.linerNoSearchResult.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.linerNoSearchResult.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }
        }
        searchAdapter.addLoadStateListener(listener)
        return view
    }

    override fun onDestroy() {
        if (timer != null) {
            timer!!.cancel()
        }
        super.onDestroy()
    }

    private fun checkpermission() {
        if (permissions.isContactOk(context)) {
            searchModelView.searchQuery(searchParamters)
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                AllConstants.CONTACTS_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        when (requestCode) {
            AllConstants.CONTACTS_REQUEST_CODE -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                println("no permission")
                if (!shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_CONTACTS
                    )
                ) {
                    println("show permissionDialog")
                    showPermissionDialog(resources.getString(R.string.contact_permission), 1000)
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onHandleSelection(position: Int, searchRespone: SearchModel?) {
        val contactIntent = Intent(ContactsContract.Intents.Insert.ACTION)
        contactIntent.type = ContactsContract.RawContacts.CONTENT_TYPE
        contactIntent.putExtra(ContactsContract.Intents.Insert.NAME, searchRespone!!.first_name)
            .putExtra(ContactsContract.Intents.Insert.PHONE, searchRespone.phone)
        startActivityForResult(contactIntent, 1)
    }

    override fun onClickItem(position: Int, searchRespone: SearchModel?) {
        val bundle = Bundle()
        Log.d("searchFragment", "onClickItem: ")
        bundle.putString("sender_id", my_id)
        bundle.putString("reciver_id", searchRespone!!.id)
        bundle.putString("name", searchRespone.first_name)
        bundle.putString("image", searchRespone.image)
        bundle.putString("chatId", "")
        bundle.putString("fcm_token", searchRespone.token)
        bundle.putString("special", searchRespone.sn)
        bundle.putString("blockedFor", searchRespone.blocked_for)
        anthorUserInChatRoomId.id = searchRespone!!.id!!
        anthorUserInChatRoomId.fcmToken = searchRespone?.token!!
        anthorUserInChatRoomId.blockedFor = searchRespone.blocked_for
        anthorUserInChatRoomId.specialNumber = searchRespone?.sn!!
        anthorUserInChatRoomId.userName = searchRespone.first_name + ""+searchRespone.last_name
        anthorUserInChatRoomId.chatId = ""
        anthorUserInChatRoomId.imageUrl = searchRespone?.image!!
        val intent = Intent(context, ConversationActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        when (requestCode) {
            1000 -> {
                println("case 1000")
                if (requireActivity().checkSelfPermission(
                        Manifest.permission.READ_CONTACTS
                    ) == PackageManager.PERMISSION_DENIED
                ) {
                    showPermissionDialog(resources.getString(R.string.contact_permission), 1000)
                } else {

               searchModelView.searchQuery(searchParamters)

                }
            }
            1 -> {
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(context, "Added Contact", Toast.LENGTH_SHORT).show()
                    searchAdapter.notifyDataSetChanged()
                    return
                }
                if (resultCode == Activity.RESULT_CANCELED) {
                    searchAdapter.notifyDataSetChanged()
                    Toast.makeText(context, "Cancelled Added Contact", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun showPermissionDialog(message: String?, RequestCode: Int) {
        val alertBuilder = AlertDialog.Builder(
            context
        )
        alertBuilder.setCancelable(true)
        alertBuilder.setTitle(resources.getString(R.string.permission_necessary))
        alertBuilder.setMessage(resources.getString(R.string.contact_permission))
        alertBuilder.setMessage(message)
        alertBuilder.setPositiveButton(
            R.string.settings
        ) { dialog, which ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", requireActivity().packageName, null)
            intent.data = uri
            startActivityForResult(intent, RequestCode)
        }
        val alert = alertBuilder.create()
        alert.show()
    }
}

    private fun FragmentSearchBinding.bindAdapter(articleAdapter: SearchAdapter) {
        recyclerView.adapter = articleAdapter
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)

    }
