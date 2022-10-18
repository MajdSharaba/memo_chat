package com.yawar.memo.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.android.volley.*
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.dhaval2404.imagepicker.ImagePicker
import com.yawar.memo.Api.ServerApi
import com.yawar.memo.R
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.language.BottomSheetFragment
import com.yawar.memo.model.UserModel
import com.yawar.memo.repositry.AuthRepo
import com.yawar.memo.repositry.BlockUserRepo
import com.yawar.memo.repositry.ChatRoomRepoo
import com.yawar.memo.sessionManager.ClassSharedPreferences
import com.yawar.memo.utils.BaseApp
import com.yawar.memo.utils.VolleyMultipartRequest
import com.yawar.memo.views.BlockedUsersActivity
import com.yawar.memo.views.DevicesLinkActivity
import com.yawar.memo.views.SettingsActivity
import com.yawar.memo.views.SplashScreen
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONArray
import org.json.JSONException
import java.io.*

class SettingsFragment : Fragment() {
    lateinit var imageView: CircleImageView
    lateinit var myBase: BaseApp
    private lateinit var rQueue: RequestQueue
    var currentLanguage: String? = "en"
     var currentLang: String? = null
    var seekValue = 2

    //  TextView name ;
    lateinit var userName: TextView
    lateinit var phoneNumber: TextView
    lateinit var setPhoto: TextView
    lateinit var setUserName: TextView
    lateinit var devises: CardView
    lateinit var dev: TextView
    lateinit var bitmap: Bitmap
    lateinit var recentCalls: CardView
    lateinit var recentCall: TextView
    lateinit var notificationAndSounds: CardView
    lateinit var notificationAnd: TextView
    lateinit var appearance: CardView
    lateinit var Appearanc: TextView
    lateinit var language: CardView
    lateinit var languag: TextView
    lateinit var serverApi: ServerApi
    lateinit var imageUri: Uri
    lateinit var fontSize: CardView
    lateinit var fontSiz: TextView
    lateinit var blockList: CardView
    lateinit var askMemoQuesti: TextView
    lateinit var deleteAccount: CardView
    lateinit var preferene: TextView
    lateinit var tellafriend: CardView
    lateinit var tellafri: TextView
    lateinit var logOut: CardView
    lateinit var hel: TextView

    var userModel: UserModel? = null

    lateinit var classSharedPreferences: ClassSharedPreferences
    var imageString = ""
    var firstName: String? = ""
    var lastName: String? = ""
    lateinit var authRepo: AuthRepo

    lateinit var blockUserRepo: BlockUserRepo
    lateinit var chatRoomRepoo: ChatRoomRepoo
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        currentLanguage = requireActivity().intent.getStringExtra(currentLang)
        classSharedPreferences = BaseApp.getInstance().classSharedPreferences
        userModel = classSharedPreferences.user
        serverApi = ServerApi(activity)
        myBase = BaseApp.getInstance()
        chatRoomRepoo = myBase.chatRoomRepoo
        blockUserRepo = myBase.blockUserRepo
        authRepo = myBase.authRepo



        imageView = view.findViewById(R.id.imageView)
        if (!userModel!!.image!!.isEmpty()) {
            Glide.with(imageView.context).load(AllConstants.imageUrl + userModel!!.image)
                .apply(RequestOptions.placeholderOf(R.drawable.th).error(R.drawable.th))
                .into(imageView)
        }

        userName = view.findViewById(R.id.username)
        userName.text = userModel!!.userName + " " + userModel!!.lastName
        phoneNumber = view.findViewById(R.id.phoneNumber)
        if (userModel!!.phone != null) {
            val firstString = userModel!!.phone!!.substring(0, 4)
            val secondString = userModel!!.phone!!.substring(4, 7)
            val thirtyString = userModel!!.phone!!.substring(7, 10)
            val lastString = userModel!!.phone!!.substring(10)
            phoneNumber.text = "$firstString-$secondString-$thirtyString-$lastString"
        }
        setPhoto = view.findViewById(R.id.selectImage)
        setUserName = view.findViewById(R.id.setUserName)
        devises = view.findViewById(R.id.devices)
        dev = view.findViewById(R.id.dev)
        appearance = view.findViewById(R.id.Appearance)
        Appearanc = view.findViewById(R.id.Appearanc)
        language = view.findViewById(R.id.language)
        languag = view.findViewById(R.id.languag)
        blockList = view.findViewById(R.id.contact_number_blocked)
        askMemoQuesti = view.findViewById(R.id.askMemoQuesti)
        deleteAccount = view.findViewById(R.id.delete_accont)
        preferene = view.findViewById(R.id.preferene)
        logOut = view.findViewById(R.id.log_out)
        hel = view.findViewById(R.id.hel)



        imageView.setOnClickListener {
            Toast.makeText(
                activity,
                "This Image View",
                Toast.LENGTH_SHORT
            ).show()
        }

        userName.setOnClickListener {
            Toast.makeText(
                activity,
                "This User Name",
                Toast.LENGTH_SHORT
            ).show()
        }
        phoneNumber.setOnClickListener {
            Toast.makeText(
                activity,
                "This phoneNumber",
                Toast.LENGTH_SHORT
            ).show()
        }
        setPhoto.setOnClickListener {

            openGallery()
        }
        setUserName.setOnClickListener {
            val alert = AlertDialog.Builder(
                requireActivity()
            )
            val mView = layoutInflater.inflate(R.layout.input_name_dialog, null)
            val txt_inputFirstName = mView.findViewById<EditText>(R.id.ed_first_name)
            val txt_inputLastName = mView.findViewById<EditText>(R.id.ed_last_name)
            txt_inputFirstName.hint = userModel!!.userName
            txt_inputLastName.hint = userModel!!.lastName
            val btn_cancel = mView.findViewById<Button>(R.id.btn_cancel)
            val btn_okay = mView.findViewById<Button>(R.id.btn_add)
            alert.setView(mView)
            val alertDialog = alert.create()
            alertDialog.setCanceledOnTouchOutside(false)
            btn_cancel.setOnClickListener { alertDialog.dismiss() }
            btn_okay.setOnClickListener {
                firstName = if (!txt_inputFirstName.text.toString().isEmpty()) {
                    txt_inputFirstName.text.toString()
                } else {
                    userModel!!.userName
                }
                lastName = if (!txt_inputLastName.text.toString().isEmpty()) {
                    txt_inputLastName.text.toString()
                } else {
                    userModel!!.lastName
                }
                serverApi.updateProfile(firstName, lastName, "", "", userModel!!.userId)
                userName.text = "$firstName $lastName"
                userModel!!.userName = firstName
                userModel!!.lastName = lastName
                alertDialog.dismiss()
            }
            alertDialog.show()
        }
        devises.setOnClickListener {
            val intent = Intent(activity, DevicesLinkActivity::class.java)
            startActivity(intent)
        }

        appearance.setOnClickListener {
            val intent = Intent(context, SettingsActivity::class.java)
            startActivity(intent) //                Toast.makeText(getActivity(), "This Notification And Sounds", Toast.LENGTH_SHORT).show();
        }
        language.setOnClickListener {
            val fragment = BottomSheetFragment()
            fragment.show(
                requireActivity().supportFragmentManager,
                TAG
            )
        }

        blockList.setOnClickListener {
            val intent = Intent(context, BlockedUsersActivity::class.java)
            startActivity(intent)
        }
        deleteAccount.setOnClickListener {
            val dialog = android.app.AlertDialog.Builder(
                activity
            )
            dialog.setTitle(R.string.alert_delete_account)
            dialog.setPositiveButton(
                R.string.delete_account
            ) { dialog, which -> serverApi.deleteAccount() }
            dialog.setNegativeButton(
                R.string.cancel
            ) { dialog, which -> dialog.dismiss() }
            val alertDialog = dialog.create()
            alertDialog.show()
        }

        logOut.setOnClickListener {
            val dialog = android.app.AlertDialog.Builder(
                activity
            )
            dialog.setTitle(R.string.alert_log_out)
            dialog.setPositiveButton(
                R.string.logout
            ) { dialog, which ->
                classSharedPreferences.user = null
                classSharedPreferences.verficationNumber = null
                authRepo.setjsonObjectMutableLiveData(null)
                chatRoomRepoo.setChatRoomListMutableLiveData(null)
                blockUserRepo.setUserBlockListMutableLiveData(null)
                val intent = Intent(context, SplashScreen::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
            dialog.setNegativeButton(
                R.string.cancel
            ) { dialog, which -> dialog.dismiss() }
            val alertDialog = dialog.create()
            alertDialog.show()
        }
        return view

    }

    private fun openGallery() {
        ImagePicker.with(this)
            .crop() //Crop image(Optional), Check Customization for more option
            .compress(1024) //Final image size will be less than 1 MB(Optional)
            .maxResultSize(
                1080,
                1080
            ) //Final image resolution will be less than 1080 x 1080(Optional)
            .start()
        //        startActivityForResult( PICK_IMAGE);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            imageUri = data!!.data!!
            val myFileImage = File(imageUri.toString())
            if (imageUri.toString().startsWith("content://")) {
                var cursor: Cursor? = null
                try {
                    cursor = requireActivity().contentResolver.query(imageUri, null, null, null, null)
                    if (cursor != null && cursor.moveToFirst()) {
                        @SuppressLint("Range") val displayNamee =
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        uploadImage(displayNamee, imageUri)
                    }
                } finally {
                    cursor!!.close()
                }
            } else if (imageUri.toString().startsWith("file://")) {
                val displayNamee = myFileImage.name
                uploadImage(displayNamee, imageUri)
            }
            imageView.setImageURI(imageUri)
        }
    }

    private fun uploadImage(imageName: String, pdfFile: Uri?) {
        var iStream: InputStream? = null
        try {
            iStream = requireActivity().contentResolver.openInputStream(pdfFile!!)
            println(pdfFile)
            val inputData = getBytes(iStream)
            val volleyMultipartRequest: VolleyMultipartRequest = object : VolleyMultipartRequest(
                Method.POST, AllConstants.base_url_final + "upadteImageProfile",
                Response.Listener { response ->
                    rQueue.cache.clear()
                    try {
                        val jsonArray = JSONArray(String(response.data))
                        val respObj = jsonArray.getJSONObject(0)
                        println(respObj)
                        val user_id = respObj.getString("id")
                        val first_name = respObj.getString("first_name")
                        val last_name = respObj.getString("last_name")
                        val email = respObj.getString("email")
                        val profile_image = respObj.getString("profile_image")
                        val secret_number = respObj.getString("sn")
                        val number = respObj.getString("phone")
                        val status = respObj.getString("status")
                        val userModel = UserModel(
                            user_id,
                            first_name,
                            last_name,
                            email,
                            number,
                            secret_number,
                            profile_image,
                            status
                        )
                        classSharedPreferences.user = userModel
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener {
                }) {

                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String>? {
                    val params: MutableMap<String, String> = HashMap()
                    params["id"] = userModel!!.userId!!
                    return params
                }

                /*
                 *pass files using below method
                 * */
                override fun getByteData(): Map<String, DataPart> {
                    val params: MutableMap<String, DataPart> = HashMap()
                    params["img_profile"] = DataPart(imageName, inputData, "plan/text")
                    return params
                }
            }
            volleyMultipartRequest.retryPolicy = DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
            rQueue = Volley.newRequestQueue(activity)
            //            rQueue.add(volleyMultipartRequest);
            myBase.addToRequestQueue(volleyMultipartRequest)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    fun getBytes(inputStream: InputStream?): ByteArray {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        var len = 0
        while (inputStream!!.read(buffer).also { len = it } != -1) {
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
    }

    companion object {
        const val TAG = "bottom_sheet"
    }
}
