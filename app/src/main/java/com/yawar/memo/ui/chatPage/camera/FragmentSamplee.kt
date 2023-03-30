package com.yawar.memo

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.yawar.memo.pix.helpers.*
import com.yawar.memo.ui.chatPage.ConversationModelView
import com.yawar.memo.ui.chatPage.camera.Adapter
import dagger.hilt.android.AndroidEntryPoint
import io.ak1.pix.models.*

/**
 * Created By Akshay Sharma on 20,June,2021
 * https://ak1.io
 */
public lateinit var uri : Uri
@AndroidEntryPoint
class FragmentSamplee() : AppCompatActivity() {

    private val resultsFragment = ResultsFragment {
        showCameraFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_sample)
        setupScreen()
        supportActionBar?.hide()
        showCameraFragment()
    }

    private fun showCameraFragment() {
        val options = Options().apply {
            ratio = Ratio.RATIO_AUTO //Image/video capture ratio
            count =
                1                                                   //Number of images to restrict selection count
            spanCount = 4                                               //Number for columns in grid
            path =
                "Pix/Camera"                                         //Custom Path For media Storage
            isFrontFacing =
                false                                       //Front Facing camera on start
            videoOptions = VideoOptions().apply {
                videoDurationLimitInSeconds = 30
            }
            mode =
                Mode.All                                             //Option to select only pictures or videos or both
            flash =
                Flash.Auto                                          //Option to select flash type
            preSelectedUrls = ArrayList<Uri>()                          //Pre selected Image Urls
        }
        addPixToActivity(R.id.container, options) {
            when (it.status) {
                PixEventCallback.Status.SUCCESS -> {
//                    finish()
                    showResultsFragment()

                    it.data.forEach {
                        Log.e("showCameraFragment", "showCameraFragment: ${it.path}")
                    }
                    if (it.data != null) {
                        uri = it.data[0]
                        resultsFragment.setList(it.data)
                    }
                }
                PixEventCallback.Status.BACK_PRESSED -> {
                    supportFragmentManager.popBackStack()
                }
            }

        }
    }


    private fun showResultsFragment() {
        showStatusBar()
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, resultsFragment).commit()
    }

    override fun onBackPressed() {
        Log.e("onBackPressed", "onBackPressed: ")
        val f = supportFragmentManager.findFragmentById(R.id.container)
        if (f is ResultsFragment)
            super.onBackPressed()
        else {
            finish()
            Log.e("onBackPressed", " PixBus.onBackPressedEvent: ")

//            PixBus.onBackPressedEvent()
        }
    }

}
@AndroidEntryPoint
class ResultsFragment(private val clickCallback: View.OnClickListener) : Fragment() {
    private val customAdapter = Adapter()
    public val conversationModelView by viewModels<ConversationModelView>()

    fun setList(list: List<Uri>) {
        customAdapter.apply {
            this.list.clear()
            this.list.addAll(list)
            notifyDataSetChanged()
        }
    }

    public fun isVideo(uri: Uri): Boolean {
        Log.d("initAction", "initAction: ${uri.toString()}")
        var isVideo = false
        val selectedMediaUriGallery = uri
        val columns = arrayOf(
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.MIME_TYPE
        )
        val cursor1 =
            context?.contentResolver?.query(
                uri,
                columns,
                null,
                null,
                null
            )
        cursor1!!.moveToFirst()
        val pathColumnIndex = cursor1.getColumnIndex(columns[0])
        val mimeTypeColumnIndex = cursor1.getColumnIndex(columns[1])
        val contentPath = cursor1.getString(pathColumnIndex)
        val mimeType = cursor1.getString(mimeTypeColumnIndex)
        cursor1.close()
        if (mimeType.startsWith("image")) {
            isVideo = false
        } else if (mimeType.startsWith("video")) {
            isVideo =  true
        }

        return isVideo
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = if (isVideo(uri)) {
        fragmentBody2(requireActivity(), uri, conversationModelView) {
            requireActivity().finish()
        }
    } else {
        fragmentBody(requireActivity(), uri, conversationModelView) {
            requireActivity().finish()
        }
    }
}