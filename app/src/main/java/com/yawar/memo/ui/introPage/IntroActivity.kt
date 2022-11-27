package com.yawar.memo.ui.introPage

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.yawar.memo.R
import com.yawar.memo.databinding.ActivityIntroBinding
import com.yawar.memo.model.ChatRoomModel
import com.yawar.memo.sessionManager.ClassSharedPreferences
import com.yawar.memo.ui.dashBoard.DashBord
import com.yawar.memo.utils.BaseApp

class IntroActivity : AppCompatActivity() {
    lateinit var classSharedPreferences: ClassSharedPreferences
    lateinit var myBase: BaseApp
    lateinit var myId: String
    lateinit var binding : ActivityIntroBinding
    var introActModelView: IntroActModelView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro)
        classSharedPreferences = BaseApp.getInstance().classSharedPreferences
        myId = classSharedPreferences.user.userId.toString()
        myBase = BaseApp.getInstance()
        introActModelView = ViewModelProvider(this)[IntroActModelView::class.java]
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
                    binding.progressCircular.visibility = View.VISIBLE
                } else {
                    binding.progressCircular.visibility = View.GONE
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
