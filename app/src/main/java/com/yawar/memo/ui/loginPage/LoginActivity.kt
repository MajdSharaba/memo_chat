package com.yawar.memo.ui.loginPage

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.GoogleAuthProvider
import com.yawar.memo.BaseApp
import com.yawar.memo.network.AuthApi
import com.yawar.memo.R
import com.yawar.memo.databinding.ActivityLoginBinding
import com.yawar.memo.domain.model.UserModel
import com.yawar.memo.repositry.AuthRepo
import com.yawar.memo.sessionManager.ClassSharedPreferences
import com.yawar.memo.ui.registerPage.RegisterActivity
import com.yawar.memo.utils.CallProperty
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener,
    Observer {
    private val REQUEST_WIFI_PERMISSIONS = 1
    private val WIFI_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE
    )
    private val mCallbackManager = CallbackManager.Factory.create()
    lateinit var binding: ActivityLoginBinding
    private val EMAIL = "email"
    private val mAuth: FirebaseAuth? = null
    var name: String? = null
    var email: String? = null
    @Inject
    lateinit var authRepo: AuthRepo
     val loginModelView by viewModels<LoginModelView>()
    var progressDialog: ProgressDialog? = null
    private val verificationId: String? = null
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        authApi = AuthApi(this)
        classSharedPreferences = BaseApp.instance?.classSharedPreferences!!
        firebaseAuth = FirebaseAuth.getInstance()
//        loginModelView = ViewModelProvider(this).get(
//            LoginModelView::class.java
//        )
        loginModelView.getCountry()

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
        binding.btnGoogle.setOnClickListener(View.OnClickListener {
            val intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient!!)
            startActivityForResult(intent, RC_SIGN_IN)
        })
        binding.btnFacebook.setReadPermissions(Arrays.asList(EMAIL));
        binding.btnFacebook.registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
            }
        })
        binding.customFacebookBtn.setOnClickListener(View.OnClickListener {
            binding.btnFacebook.performClick();

        })
        binding.ccp.showNameCode(false)
//


        binding.btnSendCode.setOnClickListener(View.OnClickListener {
            if (TextUtils.isEmpty(binding.editTextPhone.text.toString())) {

                Toast.makeText(this, R.string.valied_message, Toast.LENGTH_SHORT)
                    .show()
            } else {

                val code = binding.ccp.selectedCountryCode
                val phone = "+" + code + binding.editTextPhone.text.toString()
                classSharedPreferences.number = phone
                authApi!!.sendVerificationCode(phone, this)
            }
            //
        })

        authApi!!.loading.observe(this) { aBoolean ->
            if (aBoolean) {
                progressDialog = ProgressDialog(this)
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
        loginModelView.country.observe(this, androidx.lifecycle.Observer {
            binding.ccp.setDefaultCountryUsingNameCode(it)
            binding.ccp.resetToDefaultCountry()
            Log.d(TAG, "onRequestPermissionsResult:${it} ")

        })
        loginModelView.getSpecialNumber().observe(this, object :
            androidx.lifecycle.Observer<JSONObject?> {
            override fun onChanged(jsonObject: JSONObject?) {
                if (jsonObject != null) {
                    loginModelView.getSpecialNumber().removeObserver(this)
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
//                        sn = userObject.getString("sn")
                        user_id = userObject.getString("id")
                        first_name = userObject.getString("first_name")
                        last_name = userObject.getString("last_name")
                        email = userObject.getString("email")
                        profile_image = userObject.getString("profile_image")
//                        secret_number = userObject.getString("sn")
                        number = userObject.getString("phone")
                        status = userObject.getString("status")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    if (sn.isEmpty()) {
                        Log.d(TAG, "isEmptyyyyy: ${loginModelView.name}")
                        val userModel = UserModel(
                            user_id,
                            loginModelView.name,
                            last_name,
                            email,
                            number,
                            secret_number,
                            loginModelView.image,
                            status)
                        classSharedPreferences.user = userModel
                        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.d(TAG, "onChangeddddddddddddd: "+sn)
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
//                        val intent = Intent(this@LoginActivity, IntroActivity::class.java)
//                        startActivity(intent)
//                        finish()

                        val userModel = UserModel(
                            user_id,
                            loginModelView.name,
                            last_name,
                            email,
                            number,
                            secret_number,
                            loginModelView.image,
                            status)
//                        classSharedPreferences.user = userModel
                        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                }
            }
        })
        loginModelView.getLoading().observe(
            this
        ) { aBoolean ->
            if (aBoolean != null) {
                if (aBoolean) {
                    progressDialog = ProgressDialog(this)
                    progressDialog!!.setMessage(resources.getString(R.string.prograss_message))
                    progressDialog!!.setCancelable(false)
                    progressDialog!!.show()
                } else {
                    progressDialog?.dismiss()
                }
            }
        }
        loginModelView.getErrorMessage().observe(
            this
        ) { aBoolean ->
            if (aBoolean != null) {
                if (aBoolean) {
                    Toast.makeText(
                        this,
                        R.string.internet_message,
                        Toast.LENGTH_LONG
                    ).show()
                    loginModelView.setErrorMessage(false)
                }
            }
        }


    }










override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)
            Log.d("onActivityResult","resulttttttttttt${result?.signInAccount?.displayName}")
            handleSignInResult(result)
        }

    }

    private fun handleFacebookAccessToken(token: AccessToken) {
//        val authApi: AuthRepo = BaseApp.getInstance().getAuthRepo()

        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success${task.result.user?.displayName+task.result.user?.photoUrl }")
                    val user = firebaseAuth?.currentUser

                    classSharedPreferences.verficationNumber = task.result.user!!.uid
                    loginModelView.name = task.result.user!!.displayName.toString()
                    loginModelView.image = task.result.user!!.photoUrl.toString()+"?type=large&redirect=true&width=500&height=500"


                    authRepo?.getspecialNumbers(classSharedPreferences.verficationNumber)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
    //                    updateUI(null)
                }
            }
    }
    private fun handleSignInResult(result: GoogleSignInResult?) {
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
//        val authApi: AuthRepo = BaseApp.getInstance().getAuthRepo()

        firebaseAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task ->

                Log.d(
                    TAG,
                    "signInWithCredential:onComplete:" + task.isSuccessful
                )
                if (task.isSuccessful) {
                    classSharedPreferences.verficationNumber = task.result.user!!.uid
                    loginModelView.name = task.result.user!!.displayName.toString()
                    loginModelView.image = task.result.user!!.photoUrl.toString().replace("=s96-c","=s1024-c")
                    Log.d(TAG, "firebaseAuthWithGoogle: ${task.result.user!!.photoUrl.toString().replace("=s96-c","=s1024-c")} ")


                    authRepo?.getspecialNumbers(classSharedPreferences.verficationNumber)

//                    gotoProfile()
                } else {
                    Log.w(
                        TAG,
                        "signInWithCredential" + task.exception!!.message
                    )
                    task.exception!!.printStackTrace()
                    Toast.makeText(
                        this, "Authentication failed.",
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
            LoginManager.getInstance().logOut();

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
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val v = currentFocus
        if (v != null && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) &&
            v is EditText &&
            !v.javaClass.name.startsWith("android.webkit.")
        ) {
            val sourceCoordinates = IntArray(2)
            v.getLocationOnScreen(sourceCoordinates)
            val x = ev.rawX + v.getLeft() - sourceCoordinates[0]
            val y = ev.rawY + v.getTop() - sourceCoordinates[1]
            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
                hideKeyboard(this)
            }
        }
        return super.dispatchTouchEvent(ev)
    }
    private fun hideKeyboard(activity: Activity?) {
        if (activity != null && activity.window != null) {
            activity.window.decorView
            val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm?.hideSoftInputFromWindow(activity.window.decorView.windowToken, 0)
        }
    }
    override fun onConnectionFailed(connectionResult: ConnectionResult) {}
    override fun update(observable: Observable, o: Any) {}
    companion object {
        private const val TAG = "Loginnnnnnnnnnn"
        private const val RC_SIGN_IN = 1
    }
}