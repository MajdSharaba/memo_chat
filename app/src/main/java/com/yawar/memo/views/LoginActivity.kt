package com.yawar.memo.views

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.GoogleAuthProvider
import com.hbb20.CountryCodePicker
import com.yawar.memo.Api.AuthApi
import com.yawar.memo.R
import com.yawar.memo.call.CallProperty
import com.yawar.memo.sessionManager.ClassSharedPreferences
import com.yawar.memo.utils.BaseApp
import java.util.*

class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener,
    Observer {
    lateinit var sendBtn: Button
    lateinit var googleBtn: Button
    private val mAuth: FirebaseAuth? = null
    var name: String? = null
    var email: String? = null
    private lateinit var ccp: CountryCodePicker
    private var text: TextView? = null
    var progressDialog: ProgressDialog? = null
    private val verificationId: String? = null
    private lateinit var edtPhone: EditText
    private var firebaseAuth: FirebaseAuth? = null
    private var authStateListener: AuthStateListener? = null
    private var googleApiClient: GoogleApiClient? = null
    private val gso: GoogleSignInOptions? = null
    var idToken: String? = null
    lateinit var classSharedPreferences: ClassSharedPreferences
    var authApi: AuthApi? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CallProperty.setStatusBarOrScreenStatus(this)
        setContentView(R.layout.activity_login)
        text = findViewById(R.id.text)
        authApi = AuthApi(this)
        classSharedPreferences = BaseApp.getInstance().classSharedPreferences
        firebaseAuth = FirebaseAuth.getInstance()
        authStateListener = AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                Log.d("dd", "onAuthStateChanged:signed_in:" + user.uid)
            } else {
                Log.d("dd", "onAuthStateChanged:signed_out")
            }
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) //you can also use R.string.default_web_client_id
            .requestEmail()
            .build()
        googleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()
        googleBtn = findViewById(R.id.btn_google)
        googleBtn.setOnClickListener(View.OnClickListener {
            val intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient!!)
            startActivityForResult(intent, RC_SIGN_IN)
        })
        sendBtn = findViewById(R.id.btn_send_code)
        ccp = findViewById(R.id.ccp)
        ccp.showNameCode(false)
        edtPhone = findViewById(R.id.editTextPhone)
        sendBtn.setOnClickListener(View.OnClickListener {
            if (TextUtils.isEmpty(edtPhone.text.toString())) {

                Toast.makeText(this, R.string.valied_message, Toast.LENGTH_SHORT)
                    .show()
            } else {

                val code = ccp.selectedCountryCode
                val phone = "+" + code + edtPhone.text.toString()
                classSharedPreferences.number = phone
                authApi!!.sendVerificationCode(phone, this@LoginActivity)
            }
            //
        })
        authApi!!.loading.observe(this) { aBoolean ->
            if (aBoolean) {
                progressDialog = ProgressDialog(this@LoginActivity)
                progressDialog!!.setMessage(resources.getString(R.string.prograss_message))
                progressDialog!!.setCancelable(false)
                progressDialog!!.show()
            } else {
                if (progressDialog != null) {
                    progressDialog!!.dismiss()
                }
            }
        }
        authApi!!.showErrorMessage.observe(
            this
        ) { aBoolean ->
            if (aBoolean) {
                Toast.makeText(this, authApi!!.errorMessage, Toast.LENGTH_LONG)
                    .show()
                authApi!!.showErrorMessage.value = false
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)
            handleSignInResult(result)
        }
    }

    private fun handleSignInResult(result: GoogleSignInResult?) {
        val authApi = AuthApi(this)
        if (result!!.isSuccess) {
            val account = result.signInAccount
            idToken = account!!.idToken
            name = account.displayName
            email = account.email
            classSharedPreferences.name = name
            // you can store user data to SharedPreference
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuthWithGoogle(credential)
        } else {
            Log.e(
                TAG,
                "Login Unsuccessful. $result"
            )
            Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(credential: AuthCredential) {
        firebaseAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task ->
                Log.d(
                    TAG,
                    "signInWithCredential:onComplete:" + task.isSuccessful
                )
                if (task.isSuccessful) {
                    Log.d(
                        TAG,
                        "onComplete: " + task.result.user!!
                            .uid
                    )
                    //                            (LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    gotoProfile()
                } else {
                    Log.w(
                        TAG,
                        "signInWithCredential" + task.exception!!.message
                    )
                    task.exception!!.printStackTrace()
                    Toast.makeText(
                        this@LoginActivity, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun gotoProfile() {
//        Intent intent = new Intent(LoginActivity.this, ChatRoomFragment.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//        finish();
    }

    override fun onStart() {
        super.onStart()
        if (authStateListener != null) {
            FirebaseAuth.getInstance().signOut()
        }
        firebaseAuth!!.addAuthStateListener(authStateListener!!)
    }

    override fun onStop() {
        super.onStop()
        if (authStateListener != null) {
            firebaseAuth!!.removeAuthStateListener(authStateListener!!)
        }
    }

    override fun onDestroy() {
        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }
        super.onDestroy()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {}
    override fun update(observable: Observable, o: Any) {}

    companion object {
        private const val TAG = "Login"
        private const val RC_SIGN_IN = 1
    }
}