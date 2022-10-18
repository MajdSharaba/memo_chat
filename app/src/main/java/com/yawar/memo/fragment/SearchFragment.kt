package com.yawar.memo.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yawar.memo.R
import com.yawar.memo.adapter.SearchAdapter
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.model.SearchRespone
import com.yawar.memo.modelView.SearchModelView
import com.yawar.memo.permissions.Permissions
import com.yawar.memo.sessionManager.ClassSharedPreferences
import com.yawar.memo.utils.BaseApp
import com.yawar.memo.views.ConversationActivity
import java.util.*

class SearchFragment : Fragment(), SearchAdapter.CallbackInterface {
    lateinit var recyclerView: RecyclerView
    lateinit var searchView: SearchView
//    lateinit var toolbar: Toolbar
    var list = ArrayList<SearchRespone?>()
    lateinit var progressBar: ProgressBar

    lateinit var searchAdapter: SearchAdapter
    lateinit var bottomNavigationView: BottomNavigationView
    var res = ArrayList<SearchRespone>()
    lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var permissions: Permissions
    lateinit var classSharedPreferences: ClassSharedPreferences
    lateinit var my_id: String
    var searchParamters = ""
    private var timer: Timer? = Timer()
    private val DELAY: Long = 1000
    private lateinit var loadingPB: ProgressBar
    private lateinit var nestedSV: NestedScrollView
    val handler = Handler()
    private var FIRST_PAGE = 1
    var limit = 2
    var end = false
    lateinit var search: TextView
    lateinit var searchModelView: SearchModelView
    lateinit var linerNOSearchResult: LinearLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        timer = Timer()
        permissions = Permissions()
        classSharedPreferences = BaseApp.getInstance().classSharedPreferences
        my_id = classSharedPreferences.user.userId.toString()
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        progressBar = view.findViewById(R.id.progress_circular)
//        toolbar = view.findViewById(R.id.toolbar)
        search = view.findViewById(R.id.search)
        searchView = view.findViewById(R.id.search_by_secret_number)
        linerNOSearchResult = view.findViewById(R.id.liner_no_search_result)
        searchModelView = ViewModelProvider(this)[SearchModelView::class.java]
        checkpermission()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
                                FIRST_PAGE = 1
                                end = false
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


        recyclerView.layoutManager = linearLayoutManager
        searchAdapter = SearchAdapter(this, requireActivity())
        searchModelView.searchResponeArrayList.observe(
            requireActivity(),
            Observer<ArrayList<SearchRespone?>?> { searchResponeArrayList ->
                //                list.clear();
                list = ArrayList()
                if (searchResponeArrayList != null) {
                    if (searchResponeArrayList.isEmpty()) {
                        linerNOSearchResult.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    } else {
                        linerNOSearchResult.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        for (searchRespone in searchResponeArrayList) {
                            if (searchRespone != null) {
                                list.add(searchRespone.clone())
                            }
                        }
                        println("list$list")
                        searchAdapter.setData(list)
                        //                 searchAdapter.notifyDataSetChanged();
                    }
                }
            })
        recyclerView.adapter = searchAdapter
        searchModelView.loadingMutableLiveData.observe(
            requireActivity()
        ) { aBoolean ->
            if (aBoolean != null) {
                if (aBoolean) {
                    recyclerView.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                    linerNOSearchResult.visibility = View.GONE
                } else {
                    recyclerView.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    linerNOSearchResult.visibility = View.VISIBLE
                }
            }
        }
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

            searchModelView.search(searchParamters, FIRST_PAGE.toString(), my_id)
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

                searchModelView.search(searchParamters, FIRST_PAGE.toString(), my_id)
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

    override fun onHandleSelection(position: Int, searchRespone: SearchRespone?) {
        val contactIntent = Intent(ContactsContract.Intents.Insert.ACTION)
        contactIntent.type = ContactsContract.RawContacts.CONTENT_TYPE
        contactIntent.putExtra(ContactsContract.Intents.Insert.NAME, searchRespone!!.name)
            .putExtra(ContactsContract.Intents.Insert.PHONE, searchRespone.phone)
        startActivityForResult(contactIntent, 1)
    }

    override fun onClickItem(position: Int, searchRespone: SearchRespone?) {
        val bundle = Bundle()
        bundle.putString("sender_id", my_id)
        bundle.putString("reciver_id", searchRespone!!.id)
        bundle.putString("name", searchRespone.name)
        bundle.putString("image", searchRespone.image)
        bundle.putString("chatId", "")
        bundle.putString("fcm_token", searchRespone.token)
        bundle.putString("special", searchRespone.SecretNumber)
        bundle.putString("blockedFor", searchRespone.blockedFor)
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

//                    searchResponeArrayList.clear();
//                    res.clear();
//                    recyclerView.getRecycledViewPool().clear();
                    searchModelView.search(searchParamters, FIRST_PAGE.toString(), my_id)
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