package com.yawar.memo.views

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.github.dhaval2404.imagepicker.ImagePicker
import com.yawar.memo.Api.ServerApi
import com.yawar.memo.R
import com.yawar.memo.call.CallProperty
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.model.UserModel
import com.yawar.memo.repositry.AuthRepo
import com.yawar.memo.sessionManager.ClassSharedPreferences
import com.yawar.memo.utils.BaseApp
import com.yawar.memo.utils.VolleyMultipartRequest
import org.json.JSONArray
import org.json.JSONException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream

class RegisterActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    var image: ImageView? = null
    var dropdown: Spinner? = null
    var spennerItem: MutableList<String> = ArrayList()
    var spennerItemChooser: String? = null
    var edFname: EditText? = null
    var edLname: EditText? = null
    var inputData = byteArrayOf()
    var imageBytes = byteArrayOf()

    //    ChatRoomRepo chatRoomRepo;
    var classSharedPreferences: ClassSharedPreferences? = null
    var btnRegister: Button? = null
    var btnSkip: Button? = null
    var imageUri = Uri.parse("n")
    var bitmap: Bitmap? = null
    var progressDialog: ProgressDialog? = null
    var fName = "user"
    var lName = ""
    var email = ""
    var userId: String? = null
    var imageString = ""
    var serverApi: ServerApi? = null
    private var rQueue: RequestQueue? = null
    lateinit var myBase: BaseApp
    var displayNamee = ""
    var authRepo: AuthRepo? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CallProperty.setStatusBarOrScreenStatus(this)
        setContentView(R.layout.activity_register)
        if (Build.MANUFACTURER == "Xiaomi") {
//
            showXhaomiDialog()
        }
        initView()
        initAction()
    }

    private fun initView() {
        image = findViewById(R.id.imageProfile)
        spennerItemChooser = resources.getString(R.string.choose_special_number)
        myBase = BaseApp.getInstance()
        authRepo = myBase.authRepo
        edFname = findViewById(R.id.et_fName)
        edLname = findViewById(R.id.et_lName)
        image = findViewById(R.id.imageProfile)
        btnRegister = findViewById(R.id.btn_Register)
        //        btnSkip = findViewById(R.id.btn_skip);
        classSharedPreferences = BaseApp.getInstance().classSharedPreferences
        serverApi = ServerApi(this)
        dropdown = findViewById(R.id.spinner1)
    }

    private fun initAction() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, spennerItem)
        val adapter1: ArrayAdapter<String> = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            spennerItem
        ) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                if (position == 0) {
                    (v.findViewById<View>(android.R.id.text1) as CheckedTextView).text =
                        ""
                    (v.findViewById<View>(android.R.id.text1) as CheckedTextView).hint =
                        resources.getString(R.string.choose_special_number) //"Hint to be displayed"
                }
                return v
            }

            override fun getDropDownView(
                position: Int, convertView: View,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as CheckedTextView
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY)
                } else {
//                    tv.setTextColor(Color.BLACK);
                }
                return view
            }
        }
        spennerItem.add(resources.getString(R.string.choose_special_number))
        authRepo!!.jsonObjectMutableLiveData.observe(
            this
        ) { jsonObject ->
            if (jsonObject != null) {
                try {
                    val jsonArray = jsonObject.getJSONArray("numbers")
                    val userObject = jsonObject.getJSONObject("user")
                    userId = userObject.getString("id")
                    println(userId + "userId")
                    for (i in 0 until jsonArray.length()) {
                        val item = jsonArray.getString(i)
                        spennerItem.add(item)
                    }
                    adapter.notifyDataSetChanged()
                    adapter1.notifyDataSetChanged()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }

        dropdown!!.adapter = adapter1
        dropdown!!.onItemSelectedListener = this
        image!!.setOnClickListener { openGallery() }
        btnRegister!!.setOnClickListener {
            fName = edFname!!.text.toString()
            lName = edLname!!.text.toString()
            //                email = edEmail.getText().toString();
            if (CheckAllFields()) {
                uploadImage(displayNamee, imageUri)
            }
        }

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
    }

    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
//            String displayNamee = null;
            imageUri = data!!.data
            val myFileImage = File(imageUri.toString())
            if (imageUri.toString().startsWith("content://")) {
                var cursor: Cursor? = null
                try {
                    cursor = this.contentResolver.query(imageUri!!, null, null, null, null)
                    if (cursor != null && cursor.moveToFirst()) {
                        displayNamee =
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        Log.d("nameeeee>>>>  ", displayNamee)

                    }
                } finally {
                    cursor!!.close()
                }
            } else if (imageUri.toString().startsWith("file://")) {
                displayNamee = myFileImage.name
            }
            image!!.setImageURI(imageUri)
        }

    }

    private fun CheckAllFields(): Boolean {

        if (spennerItemChooser == resources.getString(R.string.choose_special_number)) {
            Toast.makeText(this, R.string.choose_special_number, Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
        spennerItemChooser = adapterView.getItemAtPosition(i).toString()
        //        System.out.println(spennerItemChooser);
    }

    override fun onNothingSelected(adapterView: AdapterView<*>?) {}

    private fun uploadImage(imageName: String, pdfFile: Uri?) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage(resources.getString(R.string.prograss_message))
        progressDialog.show()
        val iStream: InputStream? = null
        if (pdfFile.toString() != "n") {

            try {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val baos = ByteArrayOutputStream()
            //
            if (bitmap != null) {
                bitmap!!.compress(Bitmap.CompressFormat.JPEG, 40, baos)
                imageBytes = baos.toByteArray()
            }
        }
        val url = AllConstants.base_url_final + "uploadImgProfile"
        println(url + "base_url_final")
        val volleyMultipartRequest: VolleyMultipartRequest = object : VolleyMultipartRequest(
            Method.POST, url,
            Response.Listener { response ->
                progressDialog.dismiss()
                rQueue!!.cache.clear()
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
                    classSharedPreferences!!.user = userModel
                    val intent = Intent(this@RegisterActivity, IntroActivity::class.java)
                    startActivity(intent)
                    finish()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                progressDialog.dismiss()

            }) {

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
                params["email"] = email
                params["first_name"] = fName
                params["last_name"] = lName
                params["sn"] = spennerItemChooser!!
                params["id"] = userId!!
                return params
            }

            /*
         *pass files using below method
         * */
            override fun getByteData(): Map<String, DataPart> {
                val params: MutableMap<String, DataPart> = HashMap()
                params["img_profile"] = DataPart(imageName, imageBytes, "plan/text")
                return params
            }
        }
        volleyMultipartRequest.retryPolicy = DefaultRetryPolicy(
            0,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        rQueue = Volley.newRequestQueue(this@RegisterActivity)
        //            rQueue.add(volleyMultipartRequest);
        myBase.addToRequestQueue(volleyMultipartRequest)
    }

    @Throws(IOException::class)
    fun getBytes(inputStream: InputStream): ByteArray {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        var len = 0
        while (inputStream.read(buffer).also { len = it } != -1) {
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
    }

    fun showXhaomiDialog() {
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setCancelable(false)
        alertBuilder.setTitle(resources.getString(R.string.alert))
        alertBuilder.setMessage(resources.getString(R.string.xhaomi_message))
        alertBuilder.setPositiveButton(
            R.string.settings
        ) { dialog, which -> openAppPermission() }
        alertBuilder.setNegativeButton(
            R.string.cancel
        ) { dialog, which -> dialog.dismiss() }
        val alert = alertBuilder.create()
        alert.show()
    }

    fun openAppPermission() {
        val intent = Intent()
        intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, this.packageName)
        this.startActivity(intent)
    }


}