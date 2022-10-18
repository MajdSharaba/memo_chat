package com.yawar.memo.views

import android.app.ProgressDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.yawar.memo.R
import com.yawar.memo.constant.AllConstants
import org.json.JSONException
import org.json.JSONObject

class UserDetailsActivity : AppCompatActivity() {
    var toolbar: Toolbar? = null
    var progressDialog: ProgressDialog? = null
    var tvNumber: TextView? = null
    var tvEmail: TextView? = null
    var tvSpecialNumber: TextView? = null
    var tvStatus: TextView? = null
    var sts: TextView? = null
    var tv_number_title: TextView? = null
    var spicialNumber: TextView? = null
    var home: TextView? = null
    var fullImage: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = ""
        initViews()
        initAction()
    }

    private fun initViews() {
        tvNumber = findViewById(R.id.tv_number)
        tvEmail = findViewById(R.id.tv_email)
        tvSpecialNumber = findViewById(R.id.tv_special_number)
        tvStatus = findViewById(R.id.tv_status)
        sts = findViewById(R.id.sts)
        home = findViewById(R.id.home)
        tv_number_title = findViewById(R.id.tv_number_title)
        spicialNumber = findViewById(R.id.spicialNumber)
        fullImage = findViewById(R.id.full_image)
        val bundle = intent.extras
        val user_id = bundle!!.getString("user_id", "Default")
        getUserInfo(user_id)
    }

    private fun initAction() {}
    private fun getUserInfo(user_id: String) {
        val url = AllConstants.base_url_final + "APIS/getuserinfo.php"
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Uploading, please wait...")
        progressDialog!!.show()
        val queue = Volley.newRequestQueue(this)
        val request: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                progressDialog!!.dismiss()
                println("Data added to API+$response")
                try {
                    // on below line we are passing our response
                    // to json object to extract data from it.
                    val respObj = JSONObject(response)
                    println(respObj)
                    val data = respObj.getJSONObject("data")
                    val user_id = data.getString("id")
                    val first_name = data.getString("first_name")
                    val last_name = data.getString("last_name")
                    val email = data.getString("email")
                    val profile_image = data.getString("profile_image")
                    val secret_number = data.getString("sn")
                    val number = data.getString("phone")
                    val status = data.getString("status")

                    //UserModel userModel = new UserModel(user_id,first_name,last_name,email,number,secret_number,profile_image,status);
                    supportActionBar!!.title = first_name + last_name
                    tvNumber!!.text = number
                    tvEmail!!.text = email
                    tvStatus!!.text = status
                    tvSpecialNumber!!.text = secret_number
                    if (!profile_image.isEmpty()) {
                        Glide.with(fullImage!!.context).load(profile_image).into(fullImage!!)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error -> // method to handle errors.
                Toast.makeText(
                    this,
                    "Fail to get response = $error",
                    Toast.LENGTH_SHORT
                ).show()
            }) {
            override fun getParams(): Map<String, String>? {

                val params: MutableMap<String, String> = HashMap()

                params["id"] = user_id

                return params
            }
        }

        queue.add(request)
    }
}