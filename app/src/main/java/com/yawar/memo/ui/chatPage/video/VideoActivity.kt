package com.yawar.memo.ui.chatPage.video

import android.media.MediaPlayer.*
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.yawar.memo.R
import com.yawar.memo.databinding.ActivityVideoBinding

class VideoActivity : AppCompatActivity() {
    var mediaControls: MediaController? = null
    lateinit var binding: ActivityVideoBinding
    var bundle: Bundle? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video)
        val bundle = intent.extras
        val path = bundle!!.getString("path", "1")
        binding.simpleVideoView.requestFocus()
        binding.simpleVideoView.setOnPreparedListener(OnPreparedListener { mediaPlayer ->
            mediaPlayer.setOnVideoSizeChangedListener { mp, width, height ->
                mediaControls = MediaController(this)
                binding.simpleVideoView.setMediaController(mediaControls)
                mediaControls!!.setAnchorView(binding.simpleVideoView)
            }
        })
        binding.simpleVideoView.setMediaController(mediaControls)
        binding.simpleVideoView.setVideoURI(Uri.parse(path))
        binding.simpleVideoView.start()
        binding.simpleVideoView.setOnCompletionListener(OnCompletionListener { finish() })
        binding.simpleVideoView.setOnErrorListener(OnErrorListener { mp, what, extra ->
            finish()
            false
        })
    }
}