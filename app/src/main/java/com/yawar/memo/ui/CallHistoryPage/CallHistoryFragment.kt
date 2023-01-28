package com.yawar.memo.ui.CallHistoryPage

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.yawar.memo.BaseApp
import com.yawar.memo.R
import com.yawar.memo.ui.requestCall.RequestCallActivity
import com.yawar.memo.databinding.FragmentCallHistoryBinding
import com.yawar.memo.domain.model.CallHistoryModel
import com.yawar.memo.sessionManager.ClassSharedPreferences
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CallHistoryFragment : Fragment(), CallAdapter.CallbackInterface {
     val callHistoryModelView by viewModels<CallHistoryModelView> ()
    lateinit var itemAdapter: CallAdapter
    lateinit var classSharedPreferences: ClassSharedPreferences
    lateinit var binding: FragmentCallHistoryBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_call_history,container,false)
        val view = binding.root
        closeMessingCallCurrentNotification()
        binding.recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.recyclerView.layoutManager = linearLayoutManager
        classSharedPreferences = BaseApp.instance?.classSharedPreferences!!
        itemAdapter = CallAdapter(this)
//        callHistoryModelView = ViewModelProvider(this)[CallHistoryModelView::class.java]
        callHistoryModelView.loadData()
            .observe(
                requireActivity(),
                Observer<List<CallHistoryModel?>?> { callModels ->
                    val list = ArrayList<CallHistoryModel?>()
                    if (callModels != null) {
                        println("no call"+callModels)
                        if (callModels.isEmpty()) {
                            binding.linerNoCallHistory.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.GONE
                        } else {
                            binding.linerNoCallHistory.visibility = View.GONE
                            binding.recyclerView.visibility = View.VISIBLE
                            for (callModel in callModels) {
                                if (callModel != null) {
                                    list.add(callModel.clone())
                                }
                            }
                            itemAdapter.setData(list)
                        }
                    }
                })
        binding.recyclerView.adapter = itemAdapter
        callHistoryModelView.loadingMutableLiveData.observe(
            requireActivity()
        ) { aBoolean ->
            if (aBoolean != null) {
                if (aBoolean) {
                    binding.recyclerView.visibility = View.GONE
                    binding.progressCircular.visibility = View.VISIBLE
                    binding.linerNoCallHistory.visibility = View.GONE
                } else {
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.progressCircular.visibility = View.GONE
                    binding.linerNoCallHistory.visibility = View.VISIBLE
                }
            }
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                itemAdapter.filter.filter(newText)
                return false
            }
        })
        return view
    }

    override fun onHandleSelection(position: Int, callModel: CallHistoryModel?) {
        if (callModel != null) {
            val id : String
            if(callModel.answer_id == classSharedPreferences.user.userId){
                  id = callModel.caller_id.toString()
            }
            else{
                id = callModel.answer_id.toString()

            }

            if(callModel.call_type == "video"){
                val intent = Intent(requireActivity(), RequestCallActivity::class.java)
                //                Intent intent = new Intent(ConversationActivity.this, CompleteActivity.class);

                intent.putExtra("anthor_user_id", id)
                intent.putExtra("user_name", callModel.username)
                intent.putExtra("isVideo", true)
                intent.putExtra("fcm_token", "")
                intent.putExtra("image_profile", callModel.image)
                startActivity(intent)
            }
            else{
                val intent = Intent(requireActivity(), RequestCallActivity::class.java)
                //                Intent intent = new Intent(ConversationActivity.this, CompleteActivity.class);

                intent.putExtra("anthor_user_id", id)
                intent.putExtra("user_name", callModel.username)
                intent.putExtra("isVideo", false)
                intent.putExtra("fcm_token", "")
                intent.putExtra("image_profile", callModel.image)
                startActivity(intent)
            }
            }

        }

    private fun closeMessingCallCurrentNotification() {
        val mNotificationManager = context?.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        /// for check current notification
        for (statusBarNotification in mNotificationManager.activeNotifications) {
            println(statusBarNotification.notification.channelId + "statusBarNotification.getId()")
            if (statusBarNotification.notification.channelId  == "notification_messing_call") {
                mNotificationManager.cancel(statusBarNotification.id)

                break
            }
        }
    }
    }





