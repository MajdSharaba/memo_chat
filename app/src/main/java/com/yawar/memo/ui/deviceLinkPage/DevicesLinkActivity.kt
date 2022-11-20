package com.yawar.memo.ui.deviceLinkPage

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.yawar.memo.R
import com.yawar.memo.databinding.ActivityDevicesLinkBinding
import com.yawar.memo.model.DeviceLinkModel
import com.yawar.memo.service.SocketIOService
import com.yawar.memo.sessionManager.ClassSharedPreferences
import com.yawar.memo.utils.BaseApp
import org.json.JSONException
import org.json.JSONObject
import java.text.DateFormat
import java.util.*


class DevicesLinkActivity : AppCompatActivity() {
    var deviceLinkModels = ArrayList<DeviceLinkModel>()
    lateinit var mainAdapter: DeviceLinkAdapter
    lateinit var resultQr: String
    lateinit var classSharedPreferences: ClassSharedPreferences
    lateinit var myId: String
    lateinit var binding: ActivityDevicesLinkBinding

    private fun checkQr() {
        val service = Intent(this, SocketIOService::class.java)
        val `object` = JSONObject()
        try {
            `object`.put("key", resultQr)
            `object`.put("id", myId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        service.putExtra(SocketIOService.EXTRA_CHECK_QR_PARAMTERS, `object`.toString())
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_CHECK_QR)
        startService(service)
    }

    private val iDQr: Unit
        get() {
            val service = Intent(this, SocketIOService::class.java)
            val `object` = JSONObject()
            try {
                `object`.put("key", resultQr)
                `object`.put("id", myId)
                `object`.put("checkQr", true)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            service.putExtra(SocketIOService.EXTRA_GET_QR_PARAMTERS, `object`.toString())
            service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_GET_QR)
            startService(service)
        }
    private val recivecheckQr: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val objectString = intent.extras!!.getString("scan qr")
            var jsonObject: JSONObject? = null
            try {
                jsonObject = JSONObject(objectString.toString())
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            var check = false
            try {
                check = jsonObject!!.getBoolean("checkQr")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            if (check) {
                iDQr
            }
        } //

    }
    private val reciveGetQr: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val objectString = intent.extras!!.getString("get qr")
            var jsonObject: JSONObject? = null
            try {
                jsonObject = JSONObject(objectString.toString())
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            var device: String? = "undefine"
            try {
                device = jsonObject!!.getString("device")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            deviceLinkModels.add(
                DeviceLinkModel(
                    device, "", DateFormat.getDateTimeInstance().format(
                        Date()
                    )
                )
            )
            mainAdapter!!.notifyDataSetChanged()
        } //

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = DataBindingUtil.setContentView(this, R.layout.activity_devices_link)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(recivecheckQr, IntentFilter(SCAN_QR))
        LocalBroadcastManager.getInstance(this).registerReceiver(reciveGetQr, IntentFilter(GET_QR))
        classSharedPreferences = BaseApp.getInstance().classSharedPreferences
        myId = classSharedPreferences.getUser().userId.toString()
        //        deviceLinkModels.add(new DeviceLinkModel("chrome",""));
        binding.recyclerView.setLayoutManager(LinearLayoutManager(this))
        mainAdapter = DeviceLinkAdapter(this, deviceLinkModels)
        binding.recyclerView.setAdapter(mainAdapter)
        binding.btnLink.setOnClickListener(View.OnClickListener { barcodeLauncher.launch(ScanOptions()) })
    }

    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
        } else {

            resultQr = result.contents
            checkQr()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(recivecheckQr)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciveGetQr)
    }

    companion object {
        const val SCAN_QR = "scan qr"
        const val GET_QR = "get qr"
    }
}