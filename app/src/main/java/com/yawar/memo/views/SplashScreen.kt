package com.yawar.memo.views

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.yawar.memo.R
import com.yawar.memo.call.CallProperty
import com.yawar.memo.databinding.ActivitySplashScreenBinding
import com.yawar.memo.modelView.SplachActViewModel
import com.yawar.memo.repositry.AuthRepo
import com.yawar.memo.sessionManager.ClassSharedPreferences
import com.yawar.memo.utils.BaseApp
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

class SplashScreen : AppCompatActivity() {
    lateinit var classSharedPreferences: ClassSharedPreferences
    lateinit var authRepo: AuthRepo
    lateinit var myBase: BaseApp
    lateinit var binding: ActivitySplashScreenBinding

    var splachActViewModel: SplachActViewModel? = null
//    var progressDialog: ProgressDialog? = null
//    var progressBar: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        CallProperty.setStatusBarOrScreenStatus(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen)
        classSharedPreferences = BaseApp.getInstance().classSharedPreferences
        myBase = BaseApp.getInstance()
        authRepo = myBase.authRepo

        splachActViewModel = ViewModelProvider(this).get(SplachActViewModel::class.java)
        Handler().postDelayed({
            val intent: Intent
            if (classSharedPreferences.user != null && !classSharedPreferences.user.secretNumber.equals("") ) {
                Log.d("classSharedPreferences", classSharedPreferences.user.secretNumber.toString())
                intent = Intent(
                    this,
                    IntroActivity::class.java
                )
                startActivity(intent)
                finish()
            } else if (classSharedPreferences.verficationNumber == null) {

                //Intent is used to switch from one activity to another.
                intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                splachActViewModel!!.getSpecialNumber(classSharedPreferences.verficationNumber)
                    .observe(this, object : Observer<JSONObject?> {
                        override fun onChanged(jsonObject: JSONObject?) {
                            if (jsonObject != null) {
                                Log.d("getUserrr", "onChanged: $jsonObject")
                                authRepo.jsonObjectMutableLiveData.removeObserver(this)
                                var sn = ""
                                var user_id = ""
                                var first_name = ""
                                var last_name = ""
                                var email = ""
                                var profile_image = ""
                                var secret_number = ""
                                var number = ""
                                var status = ""
                                try {
                                    val userObject = jsonObject.getJSONObject("user")
//                                    sn = userObject.getString("sn")
                                    user_id = userObject.getString("id")
                                    first_name = userObject.getString("first_name")
                                    last_name = userObject.getString("last_name")
                                    email = userObject.getString("email")
                                    profile_image = userObject.getString("profile_image")
                                    secret_number = userObject.getString("sn")
                                    number = userObject.getString("phone")
                                    status = userObject.getString("status")
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                                if (sn.isEmpty()) {
                                    val intent = Intent(
                                        this@SplashScreen,
                                        RegisterActivity::class.java
                                    )
                                    startActivity(intent)
                                    finish()
                                } else {
                                    val intent = Intent(
                                        this@SplashScreen,
                                        RegisterActivity::class.java
                                    )
                                    startActivity(intent)
                                    finish()
//                                    val userModel = UserModel(
//                                        user_id,
//                                        first_name,
//                                        last_name,
//                                        email,
//                                        number,
//                                        secret_number,
//                                        profile_image,
//                                        status
//                                    )
//                                    classSharedPreferences.user = userModel
//                                    val intent =
//                                        Intent(this@SplashScreen, IntroActivity::class.java)
//                                    startActivity(intent)
//                                    finish()
                                }
                            }
                        }
                    })

                splachActViewModel!!.getErrorMessage().observe(
                    this
                ) { aBoolean ->
                    if (aBoolean != null) {
                        if (aBoolean) {
                            Toast.makeText(
                                this,
                                R.string.internet_message,
                                Toast.LENGTH_LONG
                            ).show()
                            splachActViewModel!!.setErrorMessage(false)
                        }
                    }
                }
            }


        }, SPLASH_SCREEN_TIME_OUT.toLong())


    }

    fun checkPermission(permission: String, requestCode: Int) {
        if (ActivityCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            // Requesting the permission
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        } else {
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )
        if (requestCode == STORAGE_PERMISSION_CODE) {
            println("Camera Permission Granted")
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun createDirectory(dName: String) {
        val yourAppDir =
            File(Environment.getExternalStorageDirectory().toString() + File.separator + dName)
        if (!yourAppDir.exists() && !yourAppDir.isDirectory) {
            try {
                Files.createDirectory(Paths.get(yourAppDir.absolutePath))
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(applicationContext, "problem", Toast.LENGTH_LONG).show()
            }


        } else {
            Log.i("CreateDir", "App dir already exists")
        }
    }

    companion object {
        private const val SPLASH_SCREEN_TIME_OUT = 3000
        private const val STORAGE_PERMISSION_CODE = 101
    }
}