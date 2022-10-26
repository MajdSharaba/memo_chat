package com.yawar.memo.views

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
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
import com.hbb20.CountryCodePicker
import com.yawar.memo.Api.AuthApi
import com.yawar.memo.R
import com.yawar.memo.call.CallProperty
import com.yawar.memo.model.UserModel
import com.yawar.memo.modelView.LoginModelView
import com.yawar.memo.repositry.AuthRepo
import com.yawar.memo.sessionManager.ClassSharedPreferences
import com.yawar.memo.utils.BaseApp
import org.json.JSONException
import org.json.JSONObject
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener,
    Observer {
    private val mCallbackManager = CallbackManager.Factory.create()
    lateinit var sendBtn: Button
    lateinit var googleBtn: ImageButton
    lateinit var facebookBtn: LoginButton
    lateinit var customFacebookBtn: ImageButton

    private val EMAIL = "email"

    private val mAuth: FirebaseAuth? = null
    var name: String? = null
    var email: String? = null
    private lateinit var ccp: CountryCodePicker
    private var text: TextView? = null
    lateinit var loginModelView : LoginModelView


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

//        try {
//            val info = packageManager.getPackageInfo(
//                "com.yawar.memo",
//                PackageManager.GET_SIGNATURES
//            )
//            for (signature in info.signatures) {
//                val md: MessageDigest = MessageDigest.getInstance("SHA")
//                md.update(signature.toByteArray())
//                Log.d("KeyHash:", android.util.Base64.encodeToString(md.digest(), android.util.Base64.DEFAULT))
//            }
//        } catch (e: PackageManager.NameNotFoundException) {
//        } catch (e: NoSuchAlgorithmException) {
//        }
        loginModelView = ViewModelProvider(this).get(
            LoginModelView::class.java
        )
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
        facebookBtn = findViewById(R.id.btn_facebook)
        customFacebookBtn = findViewById(R.id.custom_facebook_btn)
        googleBtn.setOnClickListener(View.OnClickListener {
            val intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient!!)
            startActivityForResult(intent, RC_SIGN_IN)
        })
        facebookBtn.setReadPermissions(Arrays.asList(EMAIL));

        facebookBtn.registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
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
        customFacebookBtn.setOnClickListener(View.OnClickListener {
            facebookBtn.performClick();

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
        val authApi: AuthRepo = BaseApp.getInstance().getAuthRepo()

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


                    authApi?.getspecialNumbers(classSharedPreferences.verficationNumber)
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
        val authApi: AuthRepo = BaseApp.getInstance().getAuthRepo()

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


                    authApi?.getspecialNumbers(classSharedPreferences.verficationNumber)

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
    override fun onConnectionFailed(connectionResult: ConnectionResult) {}
    override fun update(observable: Observable, o: Any) {}
    companion object {
        private const val TAG = "Loginnnnnnnnnnn"
        private const val RC_SIGN_IN = 1
    }
}