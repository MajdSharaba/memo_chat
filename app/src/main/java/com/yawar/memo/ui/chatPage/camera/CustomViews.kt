package com.yawar.memo

import android.content.Context
import android.content.res.ColorStateList
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yawar.memo.domain.model.AnthorUserInChatRoomId
import com.yawar.memo.pix.helpers.toPx
import com.yawar.memo.ui.chatPage.ConversationActivity
import com.yawar.memo.ui.chatPage.ConversationModelView
import com.yawar.memo.utils.FileUtil
import java.io.File

/**
 * Created By Akshay Sharma on 20,June,2021
 * https://ak1.io
 */
fun fragmentBody(
    context: Context,
    uri:Uri,
    viewModelView: ConversationModelView,
    clickCallback: () -> Unit
): View {
    val layoutParams = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.FILL_PARENT,
        FrameLayout.LayoutParams.FILL_PARENT
    )
    return FrameLayout(context).apply {


        val myId = BaseApp.instance?.classSharedPreferences?.user?.userId
        val  anthorUserInChatRoomId = AnthorUserInChatRoomId.getInstance("","","","","","","")

        this.layoutParams = layoutParams
        addView(PhotoView(context).apply {
            Glide.with(context).load(uri).centerCrop()
                .into(this)
            setLayoutParams(layoutParams)
            setBackgroundResource(R.color.black)

        })
        addView(FloatingActionButton(context).apply {
            this.layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(32, 32, 32, 200)
                this.gravity = Gravity.END or Gravity.BOTTOM
            }
            imageTintList = ColorStateList.valueOf(Color.WHITE)
            setImageResource(R.drawable.ic_arrow_send)
            setOnClickListener {
                Log.d("fragmentBody", "fragmentBody: ")
                val fileNmae = System.currentTimeMillis().toString() + "_" + myId
                var displayNamee: String? = null

                val myFileImage = File(uri.toString())
                FileUtil.copyFileOrDirectory(
                    FileUtil.getPath(context, uri),
                    context.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send/video")!!
                        .absolutePath, fileNmae
                )
                if (uri.toString().startsWith("content://")) {
                    var cursor: Cursor? = null
                    try {
                        cursor = context.contentResolver.query(
                            uri!!,
                            null,
                            null,
                            null,
                            null
                        )
                        if (cursor != null && cursor.moveToFirst()) {
//                            displayNamee = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                            val chatMessage = FileUtil.uploadImage(
                                fileNmae,
                                uri,
                                ConversationActivity(),
                                myId,
                                anthorUserInChatRoomId.id,
                                anthorUserInChatRoomId.blockedFor, anthorUserInChatRoomId.fcmToken
                            )
                            viewModelView.addMessage(chatMessage)
                        }
                    } finally {
                        cursor!!.close()
                    }
                } else if (uri.toString().startsWith("file://")) {
//                    displayNamee = myFileImage.name
                    val chatMessage = FileUtil.uploadImage(
                        fileNmae,
                        uri,
                        ConversationActivity(),
                        myId,
                        anthorUserInChatRoomId.id,
                        anthorUserInChatRoomId.blockedFor, anthorUserInChatRoomId.fcmToken
                    )
                    viewModelView.addMessage(chatMessage)

                }
                clickCallback()
            }
        })
    }

}



fun fragmentBody2(
    context: Context,
    uri:Uri,
    viewModelView: ConversationModelView,
    clickCallback: () -> Unit
): View {
    var mediaControl: MediaController? = null
    val layoutParams = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT
    ).apply {
        this.gravity = Gravity.CENTER
    }
    val myId = BaseApp.instance?.classSharedPreferences?.user?.userId
    val anthorUserInChatRoomId = AnthorUserInChatRoomId.getInstance("", "", "", "", "", "", "")
    return FrameLayout(context).apply {
        this.layoutParams = layoutParams
        setBackgroundResource(R.color.black)
        addView(VideoView(context).apply {
            this.layoutParams = layoutParams
            mediaControl = MediaController(context)
            this.requestFocus()
            this.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.setOnVideoSizeChangedListener { mp, width, height ->

                    this.setMediaController(mediaControl)

                    mediaControl!!.setAnchorView(this)
                    mediaControl!!.setMediaPlayer(this)
                }
            }
            this.setMediaController(mediaControl)
            mediaControl!!.setAnchorView(this)
            mediaControl!!.setMediaPlayer(this)
            this.setMediaController(mediaControl)
            this.setVideoURI(uri)
            this.start()
            this.setOnCompletionListener {
            }
            this.setOnErrorListener { mp, what, extra -> //                finish();
                false
            }
        })
        addView(FloatingActionButton(context).apply {
            this.layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(32, 32, 32, 200)
                this.gravity = Gravity.END or Gravity.BOTTOM

            }
            imageTintList = ColorStateList.valueOf(Color.WHITE)
            setImageResource(R.drawable.ic_arrow_send)
            setOnClickListener {
                Log.d("fragmentBody", "fragmentBody: ")
                val fileNmae = System.currentTimeMillis().toString() + "_" + myId
                var displayNamee: String? = null

                val myFileImage = File(uri.toString())
                FileUtil.copyFileOrDirectory(
                    FileUtil.getPath(context, uri),
                    context.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send/video")!!
                        .absolutePath, fileNmae
                )
                if (uri.toString().startsWith("content://")) {
                    var cursor: Cursor? = null
                    try {
                        cursor = context.contentResolver.query(
                            uri!!,
                            null,
                            null,
                            null,
                            null
                        )
                        if (cursor != null && cursor.moveToFirst()) {
//                            displayNamee = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                            val chatMessage = FileUtil.uploadVideo(
                                fileNmae,
                                uri,
                                ConversationActivity(),
                                myId,
                                anthorUserInChatRoomId.id,
                                anthorUserInChatRoomId.blockedFor, anthorUserInChatRoomId.fcmToken
                            )
                            viewModelView.addMessage(chatMessage)
                        }
                    } finally {
                        cursor!!.close()
                    }
                } else if (uri.toString().startsWith("file://")) {
//                    displayNamee = myFileImage.name
                    val chatMessage = FileUtil.uploadVideo(
                        fileNmae,
                        uri,
                        ConversationActivity(),
                        myId,
                        anthorUserInChatRoomId.id,
                        anthorUserInChatRoomId.blockedFor, anthorUserInChatRoomId.fcmToken
                    )
                    viewModelView.addMessage(chatMessage)

                }
                clickCallback()
            }
        })

    }
}

