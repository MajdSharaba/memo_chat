package com.yawar.memo.views
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yawar.memo.Api.ServerApi
import com.yawar.memo.R
import com.yawar.memo.adapter.ContactNumberAdapter
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.databinding.ActivityContactNumberBinding
import com.yawar.memo.model.ContactModel
import com.yawar.memo.model.SendContactNumberResponse
import com.yawar.memo.modelView.ContactNumberViewModel
import com.yawar.memo.permissions.Permissions
import com.yawar.memo.sessionManager.ClassSharedPreferences
import com.yawar.memo.utils.BaseApp

class ContactNumberActivity : AppCompatActivity(), ContactNumberAdapter.CallbackInterface {
    lateinit var binding: ActivityContactNumberBinding
    lateinit var myId: String
    lateinit var myBase: BaseApp
    lateinit var serverApi: ServerApi
    lateinit var contactNumberViewModel: ContactNumberViewModel
    lateinit var classSharedPreferences: ClassSharedPreferences
    var arrayList = ArrayList<ContactModel>()
    var sendContactNumberResponses = ArrayList<SendContactNumberResponse?>()
    var mainAdapter: ContactNumberAdapter? = null
    lateinit var permissions: Permissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contact_number)

        serverApi = ServerApi(this)
        permissions = Permissions()

        myBase = BaseApp.getInstance()
        classSharedPreferences = myBase.classSharedPreferences
        myId = classSharedPreferences.user.userId.toString()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
//        sendContactNumberResponses = myBase.contactNumberObserve.contactNumberResponseList
        mainAdapter = ContactNumberAdapter(this, sendContactNumberResponses)
        binding.recyclerView.adapter = mainAdapter
        permissions = Permissions()
        contactNumberViewModel = ViewModelProvider(this).get(
            ContactNumberViewModel::class.java
        )
        checkContactpermission()
        binding.searchBySecretNumber.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                mainAdapter!!.filter(newText)
                return false
            }
        })
        contactNumberViewModel.contactModelListMutableLiveData.observe(
            this
        ) { sendContactNumberResponses ->
            if (sendContactNumberResponses != null) {
                if (sendContactNumberResponses.isEmpty()) {
                    binding.linerNoContactsNumber.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                } else {
                    binding.linerNoContactsNumber.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    mainAdapter =
                        ContactNumberAdapter(this, sendContactNumberResponses)
                    binding.recyclerView.adapter = mainAdapter
                    mainAdapter!!.notifyDataSetChanged()
                }
            }
        }
        contactNumberViewModel.loadingMutableLiveData.observe(
            this
        ) { aBoolean ->
            if (aBoolean != null) {
                println("loadinggggg")
                if (aBoolean) {
                    binding.recyclerView.visibility = View.GONE
                    binding.progressCircular.visibility = View.VISIBLE
                    binding.linerNoContactsNumber.visibility = View.GONE
                } else {
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.progressCircular.visibility = View.GONE
                    binding.linerNoContactsNumber.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.basic_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return when (id) {
            R.id.group -> {
                val intent = Intent(this, GroupSelectorActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.item2 -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkContactpermission() {
        if (permissions.isContactOk(this)) {
            contactList
        } else {
            permissions.requestContact(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            AllConstants.CONTACTS_REQUEST_CODE -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                contactList
            } else {
                if ( !shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_CONTACTS
                    )
                ) {
                    showPermissionDialog(resources.getString(R.string.contact_permission), 1000)
                }
            }
        }
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1000 -> if ( checkSelfPermission(
                    Manifest.permission.READ_CONTACTS
                ) == PackageManager.PERMISSION_DENIED
            ) {
                showPermissionDialog(resources.getString(R.string.contact_permission), 1000)
                //
            } else {
                contactList
            }
        }
    }

    fun showPermissionDialog(message: String?, RequestCode: Int) {
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setCancelable(true)
        alertBuilder.setTitle(resources.getString(R.string.permission_necessary))
        alertBuilder.setMessage(resources.getString(R.string.contact_permission))
        alertBuilder.setMessage(message)
        alertBuilder.setPositiveButton(
            R.string.settings
        ) { dialog, which ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivityForResult(intent, RequestCode)
        }
        val alert = alertBuilder.create()
        alert.show()
    }

    override fun onHandleSelection(
        position: Int,
        sendContactNumberResponse: SendContactNumberResponse?
    ) {
        val bundle = Bundle()
        bundle.putString("reciver_id", sendContactNumberResponse!!.id)
        bundle.putString("sender_id", myId)
        bundle.putString("fcm_token", sendContactNumberResponse.fcmToken)
        bundle.putString("name", sendContactNumberResponse.name)
        bundle.putString("image", sendContactNumberResponse.image)
        bundle.putString("chat_id", "")
        bundle.putString("blockedFor", sendContactNumberResponse.blockedFor)
        val intent = Intent(this, ConversationActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    val contactList: Unit
        get() {
            val arrayList = ArrayList<ContactModel>()
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
                        println(number + "this is the number")
                        val model = ContactModel()
                        model.name = name
                        model.number = number
                        println(cursor.count.toString() + "this is " + cursor.position)
                        if (cursor.position > 50) {
                            break
                        }
                        arrayList.add(model)
                    }
                }
                cursor.close()
            }
            contactNumberViewModel.loadData(arrayList, myId)
        }
}