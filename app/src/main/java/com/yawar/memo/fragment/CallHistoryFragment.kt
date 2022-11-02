import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yawar.memo.R
import com.yawar.memo.adapter.CallAdapter
import com.yawar.memo.call.RequestCallActivity
import com.yawar.memo.model.CallModel
import com.yawar.memo.modelView.CallHistoryModelView
import com.yawar.memo.sessionManager.ClassSharedPreferences
import com.yawar.memo.utils.BaseApp


class CallHistoryFragment : Fragment(), CallAdapter.CallbackInterface {

    lateinit var callHistoryModelView: CallHistoryModelView
    lateinit var recyclerView: RecyclerView
    lateinit var itemAdapter: CallAdapter
    lateinit var searchView: SearchView
    lateinit var classSharedPreferences: ClassSharedPreferences
    lateinit var linerNoCalls: LinearLayout
    lateinit var progressBar: ProgressBar


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_call_history, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        linerNoCalls = view.findViewById(R.id.liner_no_call_history)
        progressBar = view.findViewById(R.id.progress_circular)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = linearLayoutManager
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
                            linerNoCalls.visibility = View.VISIBLE
                            recyclerView.visibility = View.GONE
                        } else {
                            linerNoCalls.visibility = View.GONE
                            recyclerView.visibility = View.VISIBLE
                            for (callModel in callModels) {
                                if (callModel != null) {
                                    list.add(callModel.clone())
                                }
                            }
                            itemAdapter.setData(list)
                        }
                    }
                })
        recyclerView.adapter = itemAdapter
        callHistoryModelView.loadingMutableLiveData.observe(
            requireActivity()
        ) { aBoolean ->
            if (aBoolean != null) {
                if (aBoolean) {
                    recyclerView.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                    linerNoCalls.visibility = View.GONE
                } else {
                    recyclerView.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    linerNoCalls.visibility = View.VISIBLE
                }
            }
        }
        searchView = view.findViewById(R.id.search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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





