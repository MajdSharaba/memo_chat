package com.yawar.memo.views

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.yawar.memo.R
import com.yawar.memo.adapter.GroupSelectorAdapter
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.model.ContactModel
import com.yawar.memo.model.SendContactNumberResponse
import com.yawar.memo.utils.BaseApp
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class GroupSelectorActivity : AppCompatActivity(), GroupSelectorAdapter.CallbackInterface{
    lateinit var recyclerView: RecyclerView
    lateinit var searchView: SearchView
    lateinit var toolbar: Toolbar
    lateinit var myBase: BaseApp
    var arrayList = ArrayList<ContactModel>()
    var groupSelectorRespones = ArrayList<SendContactNumberResponse?>()
    var sendContactNumberResponses = ArrayList<SendContactNumberResponse>()
    var mainAdapter: GroupSelectorAdapter? = null
    var new_group: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_selector)
        recyclerView = findViewById(R.id.recycler_view)
        toolbar = findViewById(R.id.toolbar)
        new_group = findViewById(R.id.new_group)
        toolbar.title = "Memo"
        setSupportActionBar(toolbar)
        myBase = BaseApp.getInstance()
//        myBase.contactNumberObserve.addObserver(this)
        for (sendContactNumberResponse in myBase.contactNumberObserve.contactNumberResponseList) {
            if (sendContactNumberResponse.state != "false") {
                sendContactNumberResponses.add(sendContactNumberResponse)
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        mainAdapter = GroupSelectorAdapter(this, sendContactNumberResponses)
        recyclerView.adapter = mainAdapter
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val bundle = Bundle()
            val intent = Intent(this, GroupPropertiesActivity::class.java)
            bundle.putSerializable("newPlaylist", sendContactNumberResponses)
            intent.putExtras(bundle)
            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.basic_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return when (id) {
            R.id.group -> {
                Toast.makeText(applicationContext, "Item 1 Selected", Toast.LENGTH_LONG).show()
                true
            }
            R.id.item2 -> {
                Toast.makeText(applicationContext, "Item 2 Selected", Toast.LENGTH_LONG).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkpermission() {
        ///check condition
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                100
            )
        } else {
            contactList
        }
    }

    private val contactList: Unit
        private get() {
            val DISPLAY_NAME =
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
            val uri = ContactsContract.Contacts.CONTENT_URI
            val sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
            val cursor = contentResolver.query(uri, null, null, null, sort)
            if (cursor!!.count > 0) {
                while (cursor.moveToNext()) {
                    @SuppressLint("Range") val id =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                    @SuppressLint("Range") val name =
                        cursor.getString(cursor.getColumnIndex(DISPLAY_NAME))
                    val uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                    val selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?"
                    val phoneCursor =
                        contentResolver.query(uriPhone, null, selection, arrayOf(id), null)
                    if (phoneCursor!!.moveToNext()) {
                        @SuppressLint("Range") val number = phoneCursor.getString(
                            phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        )
                        val model = ContactModel()
                        model.name = name
                        model.number = number
                        arrayList.add(model)
                    }
                }
                cursor.close()
            }
            println(arrayList.size)
            sendContactNumber(arrayList)
        }

    private fun sendContactNumber(arrayList: ArrayList<ContactModel>) {
        val url = AllConstants.base_url_final + "APIS/mycontact.php"
//        val url = AllConstants.base_url + "APIS/mycontact.php"

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading, please wait...")
        progressDialog.show()
        val queue = Volley.newRequestQueue(this)
        val request: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                progressDialog.dismiss()

                var respObj: JSONObject? = null
                try {
                    respObj = JSONObject(response)
                    println(respObj)
                    val jsonArray = respObj["data"] as JSONArray
                    println(jsonArray)
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        println(jsonObject.getString("name"))
                        val id = jsonObject.getString("id")
                        val name = jsonObject.getString("name")
                        val number = jsonObject.getString("number")
                        val image = jsonObject.getString("image")
                        val chat_id = jsonObject.getString("chat_id")
                        val fcm_token = jsonObject.getString("user_token")
                        val state = jsonObject.getString("state")
                        if (state != "false") {
                        }
                    }
                    recyclerView.layoutManager = LinearLayoutManager(this)
                    recyclerView.adapter = mainAdapter
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {

            }) {
            override fun getParams(): Map<String, String>? {

                val params: MutableMap<String, String> = HashMap()

                val data = Gson().toJson(arrayList)
                params["data"] = data

                return params
            }
        }

        queue.add(request)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            contactList
        } else {
            Toast.makeText(this@GroupSelectorActivity, "permission Denied", Toast.LENGTH_LONG)
            checkpermission()
        }
    }


    override fun onHandleSelection(
        position: Int,
        sendContactNumberResponse: SendContactNumberResponse?,
        isChecked: Boolean
    ) {
        if (isChecked) {
            groupSelectorRespones.add(sendContactNumberResponse)
        } else {
            groupSelectorRespones.remove(sendContactNumberResponse)
        }

    }

}