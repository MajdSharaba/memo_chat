package com.yawar.memo.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yawar.memo.R
import com.yawar.memo.adapter.GroupSelectorAdapter
import com.yawar.memo.model.ChatMessage
import com.yawar.memo.model.ChatRoomModel
import com.yawar.memo.model.SendContactNumberResponse
import com.yawar.memo.modelView.ForwardDialogViewModel
import com.yawar.memo.service.SocketIOService
import com.yawar.memo.sessionManager.ClassSharedPreferences
import com.yawar.memo.utils.BaseApp
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [ForwardDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ForwardDialogFragment : DialogFragment(), java.util.Observer,
    GroupSelectorAdapter.CallbackInterface {
    // TODO: Rename and change types of parameters
    private lateinit var mParam1: String
    private lateinit var mParam2: String
    lateinit var myBase: BaseApp
    var forwordList = ArrayList<String?>()
    var sendContactNumberResponses = ArrayList<SendContactNumberResponse>()
    lateinit var mainAdapter: GroupSelectorAdapter
    lateinit var recyclerView: RecyclerView
    lateinit var send: Button
    lateinit var cancel: Button
    var chatMessageListId = ArrayList<String>()
    var chatMessageListId2 = ArrayList<String>()
    var id = ""
    lateinit var classSharedPreferences: ClassSharedPreferences
    lateinit var my_id: String
    lateinit var forwardDialogViewModel: ForwardDialogViewModel
    private lateinit var select_title2: TextView
    var textSize = 14.0f
    lateinit var sharedPreferences: SharedPreferences
    private fun forwardMessage() {
        val service = Intent(context, SocketIOService::class.java)
        val `object` = JSONObject()
        try {
            `object`.put("id", forwordList.toString())
            `object`.put("message_id", chatMessageListId.toString())
            `object`.put("sender_id", "\"" + my_id + "\"")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        forwardDialogViewModel.clearSelectedMessage()
        service.putExtra(SocketIOService.EXTRA_FORWARD_MESSAGE_PARAMTERS, `object`.toString())
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_Forward)
        requireActivity().startService(service)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = requireArguments().getString(ARG_PARAM1).toString()
            mParam2 = requireArguments().getString(ARG_PARAM2).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dialog_forward, container, false)
        sharedPreferences = requireActivity().getSharedPreferences("txtFontSize", Context.MODE_PRIVATE)
        select_title2 = view.findViewById(R.id.select_title2)
        select_title2.textSize = sharedPreferences.getString("txtFontSize", "16")!!.toFloat()


//        view.setBackground(getActivity().getResources().getDrawable(R.drawable.dialog_bg));
        classSharedPreferences = BaseApp.getInstance().classSharedPreferences
        my_id = classSharedPreferences.user.userId.toString()
        myBase = BaseApp.getInstance()
        forwardDialogViewModel = ViewModelProvider(this).get(
            ForwardDialogViewModel::class.java
        )

        for ( ChatMessage in chatMessageArrayList) {
            if (ChatMessage != null) {
                chatMessageListId.add("\"" + ChatMessage.id + "\"")

                id = id+ChatMessage.id+","
            }
        }
        recyclerView = view.findViewById(R.id.recycler_view)
        mainAdapter = GroupSelectorAdapter(this, sendContactNumberResponses)
        forwardDialogViewModel.loadData().observe(
            requireActivity(),
            Observer<ArrayList<ChatRoomModel?>?> { chatRoomModels ->
                if (chatRoomModels != null) {
                    sendContactNumberResponses.clear()
                    for (chatRoomModel in chatRoomModels) {
                        if (chatRoomModel != null) {
                            sendContactNumberResponses.add( SendContactNumberResponse(chatRoomModel.other_id,chatRoomModel.username,chatRoomModel.sn,chatRoomModel.image,"true",chatRoomModel.id,chatRoomModel.user_token,chatRoomModel.blocked_for,"ll"))
                        }
                    }
                    mainAdapter.updateList(sendContactNumberResponses)
                }
            })


        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = mainAdapter
        send = view.findViewById(R.id.send)
        cancel = view.findViewById(R.id.cancel)
        send.setOnClickListener(View.OnClickListener {
            forwardMessage()
            dismiss()
        })
        cancel.setOnClickListener(View.OnClickListener { dismiss() })
        return view
    }

    override fun update(observable: Observable, o: Any) {}
    override fun onHandleSelection(
        position: Int,
        sendContactNumberResponse: SendContactNumberResponse?,
        isChecked: Boolean
    ) {
        if (isChecked) {
            if (sendContactNumberResponse != null) {
                forwordList.add(sendContactNumberResponse.id)
            }
        } else {
            forwordList.remove(sendContactNumberResponse!!.id)
        }
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        var chatMessageArrayList = ArrayList<ChatMessage?>()


        fun newInstance(param1: ArrayList<ChatMessage?>, param2: String?): ForwardDialogFragment {
            val fragment = ForwardDialogFragment()
            val args = Bundle()
            chatMessageArrayList = param1
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}