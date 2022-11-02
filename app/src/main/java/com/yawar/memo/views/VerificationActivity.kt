package com.yawar.memo.views
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.yawar.memo.Api.AuthApi
import com.yawar.memo.R
import com.yawar.memo.call.CallProperty
import com.yawar.memo.model.UserModel
import com.yawar.memo.modelView.VerficationViewModel
import com.yawar.memo.repositry.AuthRepo
import com.yawar.memo.sessionManager.ClassSharedPreferences
import com.yawar.memo.utils.BaseApp
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class VerificationActivity : AppCompatActivity(), java.util.Observer {
    lateinit var virvectbtn: Button
    lateinit var resendbtn: TextView
    lateinit var classSharedPreferences: ClassSharedPreferences
    private lateinit var edtOTP: EditText
    lateinit var myBase: BaseApp
    var count = 60
    lateinit var authRepo: AuthRepo
    lateinit var verficationViewModel: VerficationViewModel
    lateinit var T: Timer
    lateinit var forceResendingToken: ForceResendingToken
    lateinit var text: TextView
    lateinit var authApi: AuthApi
    lateinit var orText: TextView
    var progressDialog: ProgressDialog?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CallProperty.setStatusBarOrScreenStatus(this)
        setContentView(R.layout.activity_verification)
        virvectbtn = findViewById(R.id.btn_verification)
        authRepo = BaseApp.getInstance().authRepo
        text = findViewById(R.id.text)
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage(resources.getString(R.string.prograss_message))
        orText = findViewById(R.id.orText)
        verficationViewModel = ViewModelProvider(this).get(
            VerficationViewModel::class.java
        )
        authApi = AuthApi(this)
        verficationViewModel.getSpecialNumber().observe(this, object : Observer<JSONObject?> {
            override fun onChanged(jsonObject: JSONObject?) {
                if (jsonObject != null) {
                    verficationViewModel.getSpecialNumber().removeObserver(this)
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
                        sn = userObject.getString("sn")
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
                        val intent = Intent(this@VerificationActivity, RegisterActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
//                        val userModel = UserModel(
//                            user_id,
//                            first_name,
//                            last_name,
//                            email,
//                            number,
//                            secret_number,
//                            profile_image,
//                            status
//                        )
//                        classSharedPreferences.user = userModel
                        val intent = Intent(this@VerificationActivity, RegisterActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                }
            }
        })
        verficationViewModel.getLoading().observe(
            this
        ) { aBoolean ->
            if (aBoolean != null) {
                if (aBoolean) {
                    if(progressDialog!=null) {
                        if (!progressDialog!!.isShowing) {

                            progressDialog!!.setCancelable(false)
                            progressDialog!!.show()
                        }
                    }
                } else {
                    progressDialog?.dismiss()
                }
            }
        }
        verficationViewModel.getErrorMessage().observe(
            this
        ) { aBoolean ->
            if (aBoolean != null) {
                if (aBoolean) {
                    Toast.makeText(
                        this,
                        R.string.internet_message,
                        Toast.LENGTH_LONG
                    ).show()
                    verficationViewModel.setErrorMessage(false)
                }
            }
        }
        timer()
        edtOTP = findViewById(R.id.et_verifiction)
        resendbtn = findViewById(R.id.btn_resendCode)
        resendbtn.isEnabled = false
        myBase = BaseApp.getInstance()
        forceResendingToken = myBase.forceResendingToken.getForceResendingToken()
        classSharedPreferences = BaseApp.getInstance().classSharedPreferences
        virvectbtn.setOnClickListener(View.OnClickListener {
            if (TextUtils.isEmpty(edtOTP.text.toString())) {
                Toast.makeText(
                    this,
                    R.string.valied_message,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                verficationViewModel.setLoading(true)
                if (classSharedPreferences.verficationNumber == null) {
                    authApi.verifyCode(edtOTP.text.toString())
                } else {
                    authRepo.getspecialNumbers(classSharedPreferences.verficationNumber)
                }
            }
        })
        resendbtn.setOnClickListener(View.OnClickListener {
            resendbtn.isEnabled = false
            timer()
            authApi.resendVerificationCode(
                classSharedPreferences.number,
                forceResendingToken,
                this
            )
        })
        authApi.loading.observe(this) { aBoolean ->
            if (aBoolean) {
                progressDialog = ProgressDialog(this)
                progressDialog!!.setMessage(resources.getString(R.string.prograss_message))
                progressDialog!!.setCancelable(false)
                progressDialog!!.show()
            } else {
                progressDialog?.dismiss()
            }
        }
        authApi.showErrorMessage.observe(
            this
        ) { aBoolean ->
            if (aBoolean) {
                Toast.makeText(this, authApi.errorMessage, Toast.LENGTH_LONG)
                    .show()
                authApi.showErrorMessage.setValue(false)
            }
        }
    }

    fun timer() {
        T = Timer()
        T.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    resendbtn.text = count.toString() + ""
                    count--
                    if (count < 0) {
                        resendbtn.isEnabled = true
                        resendbtn.setText(R.string.resend)
                        count = 60
                        T.cancel()
                    }
                }
            }
        }, 1000, 1000)
    }

    override fun onDestroy() {
        progressDialog?.dismiss()
        super.onDestroy()
    }

    override fun update(observable: Observable, o: Any) {
        forceResendingToken = myBase.forceResendingToken.getForceResendingToken()
    }
}