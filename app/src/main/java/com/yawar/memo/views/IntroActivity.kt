package com.yawar.memo.views

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.yawar.memo.R
import com.yawar.memo.model.ChatRoomModel
import com.yawar.memo.modelView.IntroActModelView
import com.yawar.memo.sessionManager.ClassSharedPreferences
import com.yawar.memo.utils.BaseApp

class IntroActivity : AppCompatActivity() {
    lateinit var classSharedPreferences: ClassSharedPreferences
    lateinit var myBase: BaseApp
    lateinit var myId: String
    var introActModelView: IntroActModelView? = null
    lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        progressBar = findViewById(R.id.progress_circular)
        classSharedPreferences = BaseApp.getInstance().classSharedPreferences
        myId = classSharedPreferences.getUser().userId.toString()
        myBase = BaseApp.getInstance()
        introActModelView = ViewModelProvider(this).get(IntroActModelView::class.java)
        introActModelView!!.loadData().observe(this, object : Observer<ArrayList<ChatRoomModel?>?> {
            override fun onChanged(chatRoomModels: ArrayList<ChatRoomModel?>?) {
                if (chatRoomModels != null) {
                    introActModelView!!.loadData().removeObserver(this)
                    val intent = Intent(this@IntroActivity, DashBord::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        })
        introActModelView!!.getLoading().observe(
            this
        ) { aBoolean ->
            if (aBoolean != null) {
                if (aBoolean) {
                    progressBar.setVisibility(View.VISIBLE)
                } else {
                    progressBar.setVisibility(View.GONE)
                }
            }
        }
        introActModelView!!.getErrorMessage().observe(
            this
        ) { aBoolean ->
            if (aBoolean != null) {
                if (aBoolean) {
                    introActModelView!!.setErrorMessage(false)
                }
            }
        }
    }
    fun openAppPermission() {
        val intent = Intent()
        intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, this.packageName)
        this.startActivity(intent)
    }

}
