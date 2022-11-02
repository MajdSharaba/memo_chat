package com.yawar.memo.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.volley.RequestQueue
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.chrisbanes.photoview.PhotoView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.yawar.memo.Api.ServerApi
import com.yawar.memo.R
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.language.BottomSheetFragment
import com.yawar.memo.model.UserModel
import com.yawar.memo.modelView.SettingsFragmentViewModel
import com.yawar.memo.repositry.AuthRepo
import com.yawar.memo.repositry.BlockUserRepo
import com.yawar.memo.repositry.ChatRoomRepoo
import com.yawar.memo.service.SocketIOService
import com.yawar.memo.sessionManager.ClassSharedPreferences
import com.yawar.memo.utils.BaseApp
import com.yawar.memo.views.BlockedUsersActivity
import com.yawar.memo.views.DevicesLinkActivity
import com.yawar.memo.views.SettingsActivity
import com.yawar.memo.views.SplashScreen
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream


class SettingsFragment : Fragment() {
    lateinit var imageView: CircleImageView
    lateinit var myBase: BaseApp
    private lateinit var rQueue: RequestQueue
    var currentLanguage: String? = "en"
    var currentLang: String? = null
    var seekValue = 2
    var progressDialog: ProgressDialog? = null
    var imageBytes = byteArrayOf()
    lateinit var settingsFragmentViewModel:SettingsFragmentViewModel
    var storage = FirebaseStorage.getInstance()
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
        progressDialog = ProgressDialog(requireActivity())
        progressDialog!!.setMessage(resources.getString(R.string.prograss_message))
        myBase = BaseApp.getInstance()
        chatRoomRepoo = myBase.chatRoomRepoo
        blockUserRepo = myBase.blockUserRepo
        authRepo = myBase.authRepo
        settingsFragmentViewModel = ViewModelProvider(this).get(
            SettingsFragmentViewModel::class.java
        )


        settingsFragmentViewModel.userModelRespone.observe(requireActivity(),
            Observer<UserModel?> { userModel ->
                if (userModel != null) {
                    println(userModel.image + "userModel.getImage()")
                    classSharedPreferences.user = userModel

                }
            })

        settingsFragmentViewModel.showErrorMessage.observe(requireActivity(),
            Observer<Boolean> { aBoolean ->
                if (aBoolean) {
                    Toast.makeText(
                        requireActivity(),
                        R.string.internet_message,
                        Toast.LENGTH_LONG
                    ).show()
                    settingsFragmentViewModel.setErrorMessage(false)
                }
            })

        settingsFragmentViewModel.loadingMutableLiveData.observe(requireActivity(),
            Observer<Boolean> { aBoolean ->
                if (aBoolean) {
                    if (!progressDialog!!.isShowing()) {
                        progressDialog!!.show()
                    }
                } else {
                    progressDialog?.dismiss()
                }
            })

        settingsFragmentViewModel.isDeleteAccount.observe(requireActivity(),
            Observer<Boolean> { aBoolean ->
                if (aBoolean) {
                    classSharedPreferences.user = null
                    classSharedPreferences.verficationNumber = null
                    classSharedPreferences.number = null

                    authRepo.setjsonObjectMutableLiveData(null)
                    chatRoomRepoo.setChatRoomListMutableLiveData(null)
                    blockUserRepo.setUserBlockListMutableLiveData(null)
                    val service = Intent( requireActivity(), SocketIOService::class.java)
                    service.putExtra(
                        SocketIOService.EXTRA_EVENT_TYPE,
                        SocketIOService.EVENT_TYPE_DISCONNECT
                    )
                    requireActivity().startService(service)
                    settingsFragmentViewModel.setIsDeleted(false)
                    val intent = Intent( requireActivity(), SplashScreen::class.java)
                    requireActivity().startActivity(intent)
                    requireActivity().finish()

                } else {

                }
            })





        ///// register Button




        imageView = view.findViewById(R.id.imageView)
        Log.d(TAG, userModel!!.image!!+"imageeee")
        if (userModel!=null) {

            Glide.with(imageView.context).load(AllConstants.imageUrl + userModel!!.image)
                .apply(RequestOptions.placeholderOf(R.drawable.th).error(R.drawable.th))
                .into(imageView)

            Log.d(TAG, AllConstants.imageUrl + userModel!!.image)
        }
        userName = view.findViewById(R.id.username)
        userName.text = userModel!!.userName + " " + userModel!!.lastName
        phoneNumber = view.findViewById(R.id.phoneNumber)
        if (userModel!!.phone != null) {
//            val firstString = userModel!!.phone!!.substring(0, 4)
//            val secondString = userModel!!.phone!!.substring(4, 7)
//            val thirtyString = userModel!!.phone!!.substring(7, 10)
//            val lastString = userModel!!.phone!!.substring(10)
//            phoneNumber.text = "$firstString-$secondString-$thirtyString-$lastString"
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
            val dialog = Dialog(requireActivity())
            dialog.setContentView(R.layout.dialog_image_cht)
            dialog.setTitle("Title...")
            dialog.window!!
                .setLayout(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT
                )
            val image: PhotoView = dialog.findViewById(R.id.photo_view)
            Glide.with(image.context).load(AllConstants.imageUrl + userModel!!.image)
                .apply(RequestOptions.placeholderOf(R.drawable.th).error(R.drawable.th))
                .centerCrop()
                .into(image)
            dialog.show()
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
                settingsFragmentViewModel.updateProfile(userModel!!.userId.toString(),
                    firstName.toString(), lastName.toString()
                )
//                serverApi.updateProfile(firstName, lastName, "", "", userModel!!.userId)
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
            ) { dialog, which ->
                settingsFragmentViewModel.deleteAccount(
                    userModel!!.userId.toString(),
                    userModel!!.secretNumber.toString()
                )}
//                serverApi.deleteAccount() }
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
                classSharedPreferences.number = null
                authRepo.setjsonObjectMutableLiveData(null)
                chatRoomRepoo.setChatRoomListMutableLiveData(null)
                blockUserRepo.setUserBlockListMutableLiveData(null)
                val service = Intent(context, SocketIOService::class.java)
                service.putExtra(
                    SocketIOService.EXTRA_EVENT_TYPE,
                    SocketIOService.EVENT_TYPE_DISCONNECT
                )
                requireActivity().startService(service)
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
                    cursor =
                        requireActivity().contentResolver.query(imageUri, null, null, null, null)
                    if (cursor != null && cursor.moveToFirst()) {
                        @SuppressLint("Range") val displayNamee =
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        deleteFireBaseImage(displayNamee, imageUri)
                    }
                } finally {
                    cursor!!.close()
                }
            } else if (imageUri.toString().startsWith("file://")) {
                val displayNamee = myFileImage.name
                deleteFireBaseImage(displayNamee, imageUri)
            }
            Log.d(TAG,imageUri.toString() +"imageUri" )

            Glide.with(imageView.context).load(imageUri)
                .apply(RequestOptions.placeholderOf(R.drawable.th).error(R.drawable.th))
                .into(imageView)
        }
    }



     fun uploadImageToFireBase(imageName: String, pdfFile: Uri) {
        val message_id = System.currentTimeMillis().toString() + "_" + (userModel?.userId ?: "")
//        registerViewModel.setLoading(true)
        val iStream: InputStream? = null
        if (pdfFile.toString() != "n") {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), pdfFile)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val baos = ByteArrayOutputStream()
            //
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos)
                imageBytes = baos.toByteArray()
                val path = "profile_images/$message_id.png"
                val storageRef: StorageReference = storage.getReference(path)
                val metadata = StorageMetadata.Builder()
                    .setContentType("image/jpg")
                    .build()
                val uploadTask = storageRef.putBytes(imageBytes, metadata)
                uploadTask.addOnFailureListener { exception ->
                    Log.d(
                        "uploadTask",
                        "onFailure \${}: " + exception.message
                    )
                }.addOnSuccessListener { taskSnapshot ->
                    Log.d("uploadTask", "onSuccess: " + taskSnapshot.uploadSessionUri)
//                    registerViewModel.register(
//                        email,
//                        "$message_id.png",
//                        fName,
//                        lName,
//                        spennerItemChooser,
//                        "",
//                        classSharedPreferences.verficationNumber
//                    )
                    settingsFragmentViewModel.updateImage(userModel?.userId.toString(),"$message_id.png" )
                }

            }
        }
    }


    fun deleteFireBaseImage(imageName: String, pdfFile: Uri){

        settingsFragmentViewModel.setLoading(true)

        if(!classSharedPreferences.user.image!!.isEmpty()) {

            val storageRef = storage.reference

            val path = "profile_images/${classSharedPreferences.user.image}"

            Log.d(TAG, "deleteFireBaseImage: ${path}")
            val desertRef = storage.getReference(path);
//        val desertRef = storageRef.child("profile_images/" + classSharedPreferences.user.image + ".png")

            desertRef.delete().addOnSuccessListener {
                Log.d(TAG, "deleteFireBaseImage: success")
                uploadImageToFireBase(imageName, pdfFile)
                // File deleted successfully
            }.addOnFailureListener {
                Log.d(TAG, "deleteFireBaseImage: success")
            }
        }
        else{
            uploadImageToFireBase(imageName, pdfFile)

        }

    }


    companion object {
        const val TAG = "bottom_sheet"
    }



}
