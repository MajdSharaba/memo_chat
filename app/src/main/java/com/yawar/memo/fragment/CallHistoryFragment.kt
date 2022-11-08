import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yawar.memo.R
import com.yawar.memo.adapter.CallAdapter
import com.yawar.memo.call.RequestCallActivity
import com.yawar.memo.databinding.FragmentCallHistoryBinding
import com.yawar.memo.databinding.FragmentSearchBinding
import com.yawar.memo.model.CallModel
import com.yawar.memo.modelView.CallHistoryModelView
import com.yawar.memo.sessionManager.ClassSharedPreferences
import com.yawar.memo.utils.BaseApp


class CallHistoryFragment : Fragment(), CallAdapter.CallbackInterface {

    lateinit var callHistoryModelView: CallHistoryModelView
    lateinit var itemAdapter: CallAdapter
    lateinit var classSharedPreferences: ClassSharedPreferences
    lateinit var binding: FragmentCallHistoryBinding



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_call_history,container,false)
        val view = binding.root
        binding.recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.recyclerView.layoutManager = linearLayoutManager
        classSharedPreferences = BaseApp.getInstance().classSharedPreferences
        itemAdapter = CallAdapter(this)
        callHistoryModelView = ViewModelProvider(this)[CallHistoryModelView::class.java]
        callHistoryModelView.loadData(classSharedPreferences.user.userId!!)
            .observe(
                requireActivity(),
                Observer<ArrayList<CallModel?>?> { callModels ->
                    val list = ArrayList<CallModel?>()
                    if (callModels != null) {
                        println("no call")
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

    override fun onHandleSelection(position: Int, callModel: CallModel?) {
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
    }





