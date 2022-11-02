package com.yawar.memo.fragment

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.MediaController
import android.widget.VideoView
import androidx.fragment.app.DialogFragment
import com.yawar.memo.R
import com.yawar.memo.utils.CallbackListener


class DialogFragmentVideoBeforeSend (private val callbackListener: CallbackListener,private  val path:String) : DialogFragment() {
    var mediaControls: MediaController? = null
    lateinit var videoView: VideoView
    var bundle: Bundle? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isCancelable = false
        return inflater.inflate(R.layout.fragment_dialog_video_before_send, container, false)
    }
    override fun getTheme(): Int {
        return R.style.DialogTheme
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val bundle = requireActivity().extras
//        val path = bundle!!.getString("path", "1")
        videoView = view.findViewById(R.id.simpleVideoView)
        videoView.requestFocus()
        videoView.setOnPreparedListener(MediaPlayer.OnPreparedListener { mediaPlayer ->
            mediaPlayer.setOnVideoSizeChangedListener { mp, width, height ->
                mediaControls = MediaController(requireActivity())
                videoView.setMediaController(mediaControls)
                mediaControls!!.setAnchorView(videoView)
            }
        })
        videoView.setMediaController(mediaControls)
        videoView.setVideoURI(Uri.parse(path))
        videoView.start()
        videoView.setOnCompletionListener(MediaPlayer.OnCompletionListener { dismiss() })
        videoView.setOnErrorListener(MediaPlayer.OnErrorListener { mp, what, extra ->
            dismiss()
            false
        })
    }
//        val button = view.findViewById<Button>(R.id.button)
//        val editText = view.findViewById<EditText>(R.id.editText)
//
//        button.setOnClickListener {
//            //send back data to PARENT fragment using callback
//            callbackListener.onDataReceived(editText.text.toString())
//            // Now dismiss the fragment
//            dismiss()
//        }
//
//    }
}