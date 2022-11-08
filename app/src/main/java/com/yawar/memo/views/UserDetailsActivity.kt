package com.yawar.memo.views

import android.app.ProgressDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.yawar.memo.R
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.databinding.ActivityUserDetailsBinding
import org.json.JSONException
import org.json.JSONObject

class UserDetailsActivity : AppCompatActivity() {
      lateinit var  binding : ActivityUserDetailsBinding
      lateinit var progressDialog : ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_details)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = ""
        initViews()
        initAction()
    }

    private fun initViews() {
        val bundle = intent.extras
        val user_id = bundle!!.getString("user_id", "Default")
        getUserInfo(user_id)
    }

    private fun initAction() {}
    private fun getUserInfo(user_id: String) {
//        val url = AllConstants.base_url_final + "APIS/getuserinfo.php"
        val url = AllConstants.base_url + "APIS/getuserinfo.php"
        Log.d("getUserInfo", "majd")
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading, please wait...")
        progressDialog.show()
        val queue = Volley.newRequestQueue(this)
        val request: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                progressDialog.dismiss()
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
                    Log.d("getUserInfo", "majd")
                    //UserModel userModel = new UserModel(user_id,first_name,last_name,email,number,secret_number,profile_image,status);
                    supportActionBar!!.title = first_name + last_name
                    binding.content.tvNumber.text = number
                    binding.content.tvEmail.text = email
                    binding.content.tvStatus.text = status
                    binding.content.tvSpecialNumber.text = secret_number
                    if (!profile_image.isEmpty()) {
                        Glide.with( binding.fullImage.context).load(profile_image).into( binding.fullImage!!)
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
            override fun getParams(): Map<String, String>{
                val params: MutableMap<String, String> = HashMap()
                params["id"] = user_id
                return params
            }
        }
        queue.add(request)
    }
}