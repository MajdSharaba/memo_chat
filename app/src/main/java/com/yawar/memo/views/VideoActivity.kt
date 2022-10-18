package com.yawar.memo.views

import android.media.MediaPlayer.*
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.yawar.memo.R

class VideoActivity : AppCompatActivity() {
    var mediaControls: MediaController? = null
    lateinit var videoView: VideoView
    var bundle: Bundle? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_video)
        val bundle = intent.extras
        val path = bundle!!.getString("path", "1")
        videoView = findViewById(R.id.simpleVideoView)
        videoView.requestFocus()
        videoView.setOnPreparedListener(OnPreparedListener { mediaPlayer ->
            mediaPlayer.setOnVideoSizeChangedListener { mp, width, height ->
                mediaControls = MediaController(this)
                videoView.setMediaController(mediaControls)
                mediaControls!!.setAnchorView(videoView)
            }
        })
        videoView.setMediaController(mediaControls)
        videoView.setVideoURI(Uri.parse(path))
        videoView.start()
        videoView.setOnCompletionListener(OnCompletionListener { finish() })
        videoView.setOnErrorListener(OnErrorListener { mp, what, extra ->
            finish()
            false
        })
    }
}