package com.yawar.memo.ui.chatPage

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.provider.ContactsContract
import android.text.util.Linkify
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.yawar.memo.BuildConfig
import com.yawar.memo.R
import com.yawar.memo.databinding.*
import com.yawar.memo.model.ChatMessage
import com.yawar.memo.utils.ImageProperties
import com.yawar.memo.utils.TimeProperties
import java.io.File
import java.util.*
var previousMediaPlayer : MediaPlayer? = null
var previousImageView : ImageView? = null

class ChatAdapter(private val context: Activity) :
    ListAdapter<ChatMessage, RecyclerView.ViewHolder?>(MyDiffUtilChatMessage()) {
    private var mCallback: CallbackInterface? = null
    var userNameeee: String? = null
    interface CallbackInterface {
        fun onHandleSelection(position: Int, groupSelectorRespone: ChatMessage?, myMessage: Boolean)
        fun downloadFile(position: Int, chatMessage: ChatMessage?, myMessage: Boolean)
        fun downloadVoice(position: Int, chatMessage: ChatMessage?, myMessage: Boolean)
        fun downloadVideo(position: Int, chatMessage: ChatMessage?, myMessage: Boolean)
        fun downloadImage(position: Int, chatMessage: ChatMessage?, myMessage: Boolean)
        fun onClickLocation(position: Int, chatMessage: ChatMessage?, myMessage: Boolean)
        fun onLongClick(position: Int, chatMessage: ChatMessage?, isChecked: Boolean)
        fun playVideo(path: Uri?)

    }
    init {
        try {
            mCallback = context as CallbackInterface
        }
        catch (ex: ClassCastException) {
        }
    }
    override fun getItemViewType(position: Int): Int {
        val chatMessage = getItem(position)
        return when (chatMessage!!.type) {
            "imageWeb" -> 0
            "voice" -> 1
            "video" -> 2
            "file" -> 3
            "contact" -> 4
            "location" -> 5
            else -> 6
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        run {
            return when (viewType) {
                0 -> {
                    LayoutImageViewHolder.from(parent)
                }
                1 -> {
                    LayoutVoiceViewHolder.from(parent)
                }
                2 -> {
                    LayoutVideoViewHolder.from(parent)
                }
                3 -> {
                    LayoutPdfViewHolder.from(parent)
                }
                4 -> {
                    LayoutContactViewHolder.from(parent)
                }
                5 -> {
                    LayoutLocationViewHolder.from(parent)
                }
                6 -> {
                    LayoutTextViewHolder.from(parent)
                }
                else -> {
                    LayoutTextViewHolder.from(parent)
                }
            }
        }

    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val chatMessage = getItem(position)
        val myMsg = chatMessage!!.isMe
        userNameeee = chatMessage.fileName
        when (chatMessage.type) {
            "imageWeb" -> {
                (holder as LayoutImageViewHolder).bind(myMsg, chatMessage, position,context,mCallback)
            }
            "voice" -> {
                (holder as LayoutVoiceViewHolder).bind(myMsg, chatMessage, position,context,mCallback)
            }
            "video" -> {
                (holder as LayoutVideoViewHolder).bind(myMsg, chatMessage, position,context,mCallback)
            }
            "file" -> {
                (holder as LayoutPdfViewHolder).bind(myMsg, chatMessage, position,context,mCallback)
            }
            "contact" -> {
                (holder as LayoutContactViewHolder).bind(myMsg, chatMessage,context)
            }
            "location" -> {
                (holder as LayoutLocationViewHolder).bind(myMsg, chatMessage, position,context,mCallback)
            }
            else -> {
                (holder as LayoutTextViewHolder).bind(myMsg, chatMessage,context)
            }
        }
        if (chatMessage.isChecked) {
            holder.itemView.setBackgroundColor(context.resources.getColor(R.color.background_onLong_click))
            holder.itemView.background.alpha = 60
        } else {
            holder.itemView.background = null
        }
        holder.itemView.setOnLongClickListener {
            if (!chatMessage.isChecked) {
                mCallback?.onLongClick(position, chatMessage, true)
                chatMessage.isChecked = true
                holder.itemView.setBackgroundColor(context.resources.getColor(R.color.background_onLong_click))
                holder.itemView.background.alpha = 60
            } else {
                mCallback?.onLongClick(position, chatMessage, false)
                chatMessage.isChecked = false
                holder.itemView.background = null
            }
            false
        }
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder.itemViewType == 1) {
            if ((holder as LayoutVoiceViewHolder).mediaPlayer != null) if (holder.mediaPlayer!!.isPlaying) {
                holder.mediaPlayer!!.pause()
            }

        }
    }
    fun setData(newData: ArrayList<ChatMessage?>?) {
        Log.d("adapter", "setDataaaaaa: ")
        submitList(newData)
    }
    class LayoutImageViewHolder  private  constructor(val binding: ImageItemChatMeesageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var l = 0


        fun bind(
            myMsg: Boolean,
            chatMessage: ChatMessage,
            position: Int,
            context: Activity,
            mCallback: CallbackInterface?
        ) {
             setAlignment(myMsg, chatMessage.state, context)
             binding.tvDate.text =
                 TimeProperties.getDate(chatMessage.dateTime.toLong(), "hh:mm")
             val imageFile: File = if (myMsg) {
                 val d =
                     context.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send/video") // -> filename = maven.pdf
                 File(d, chatMessage.fileName)
             } else {
                 val d =
                     context.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/recive/video") // -> filename = maven.pdf
                 File(d, chatMessage.image)
             }
             if (!imageFile.exists()) {
                 binding.imgMessage.isEnabled = false
                 Glide.with(binding.imgMessage.context).load(R.drawable.backgrounblack)
                     .centerCrop()
                     .into(binding.imgMessage)
                 if (chatMessage.isDownload) {
                     binding.imageDownload.visibility = View.GONE
                     binding.pgbProgress.visibility = View.VISIBLE
                 } else {
                     binding.imageDownload.visibility = View.VISIBLE
                     binding.pgbProgress.visibility = View.GONE
                     binding.imageDownload.setOnClickListener {
                         binding.imageDownload.visibility =
                             View.GONE
                         binding.pgbProgress.visibility =
                             View.VISIBLE

                         mCallback?.downloadImage(position, chatMessage, myMsg)
                     }
                 }
             } else {
                 binding.imgMessage.isEnabled = true
                 binding.imageDownload.visibility = View.GONE
                 val path = FileProvider.getUriForFile(
                     context, BuildConfig.APPLICATION_ID + ".fileprovider", imageFile
                 )
                 Glide.with(binding.imgMessage.context).load(path).centerCrop()
                     .into(binding.imgMessage)
                 if (chatMessage.upload) {
                     binding.pgbProgress.visibility = View.VISIBLE
                 } else {
                     binding.pgbProgress.visibility = View.GONE
                     binding.imgMessage.setOnClickListener {
                         val dialog = Dialog(context)
                         dialog.setContentView(R.layout.dialog_image_cht)
                         dialog.setTitle("Title...")
                         dialog.window!!
                             .setLayout(
                                 ViewGroup.LayoutParams.FILL_PARENT,
                                 ViewGroup.LayoutParams.FILL_PARENT
                             )
                         val image: PhotoView = dialog.findViewById(R.id.photo_view)
                         Glide.with(image.context).load(path).centerCrop().into(image)
                         dialog.show()
                     }
                 }
             }
         }


        @SuppressLint("UseCompatLoadingForDrawables", "RtlHardcoded")
        fun setAlignment(
            isMe: Boolean,
            state: String,
            context: Activity
        ) {
            if (isMe) {
                binding.contentWithBackground.setBackgroundResource(R.drawable.in_message_bg)
                val layoutParams =
                    binding.contentWithBackground.layoutParams as LinearLayout.LayoutParams
                layoutParams.gravity = Gravity.RIGHT
                binding.contentWithBackground.layoutParams = layoutParams
                val lp = binding.content.layoutParams as RelativeLayout.LayoutParams
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0)
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                binding.content.layoutParams = lp
                layoutParams.gravity = Gravity.RIGHT
                binding.tvDate.setTextColor(context.resources.getColor(R.color.white))
                when (state) {
                    "3" -> {
                        binding.ivState.setImageDrawable(context.resources.getDrawable(R.drawable.ic_recive_done_green))
                    }
                    "2" -> {
                        binding.ivState.setImageDrawable(context.resources.getDrawable(R.drawable.ic_recive_done))
                    }
                    "1" -> {
                        binding.ivState.setImageDrawable(context.resources.getDrawable(R.drawable.ic_send_done))
                    }
                    else -> {
                        binding.ivState.setImageDrawable(context.resources.getDrawable(R.drawable.ic_not_send))
                    }
                }
                binding.ivState.visibility = View.VISIBLE
            } else if (!isMe) {
                binding.ivState.visibility = View.GONE
                binding.contentWithBackground.setBackgroundResource(R.drawable.out_message_bg)
                val layoutParams =
                    binding.contentWithBackground.layoutParams as LinearLayout.LayoutParams
                layoutParams.gravity = Gravity.LEFT
                binding.contentWithBackground.layoutParams = layoutParams
                binding.tvDate.setTextColor(context.resources.getColor(R.color.textColor))
                val lp = binding.content.layoutParams as RelativeLayout.LayoutParams
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                binding.content.layoutParams = lp
            }
        }

        companion object {
            fun from(parent: ViewGroup): LayoutImageViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ImageItemChatMeesageBinding.inflate(layoutInflater,parent, false)
                return LayoutImageViewHolder(binding)
            }
        }
     }

     class LayoutVoiceViewHolder private  constructor (val  binding: VoiceRecordItemChatMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
         var l = 0
         var mediaPlayer: MediaPlayer? = null
         var relativeLayout: RelativeLayout? = null
         var handler = Handler()
         var updater: Runnable? = null
         @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
         fun bind(
             myMsg: Boolean,
             chatMessage: ChatMessage,
             position: Int,
             context: Activity,
             mCallback: CallbackInterface?
         ) {
             setAlignment(myMsg, chatMessage.state, context)
             binding.content.visibility = View.VISIBLE
             binding.tvDate.text = TimeProperties.getDate(
                 chatMessage.dateTime.toLong(), "hh:mm"
             )
             mediaPlayer = MediaPlayer()
             binding.playerSeekBar.max = 100
             val voiceFile: File = if (myMsg) {
                 val d =
                     context.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send/voiceRecord") // -> filename = maven.pdf
                 File(d, chatMessage.fileName)
             } else {
                 val d =
                     context.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/recive/voiceRecord") // -> filename = maven.pdf
                 File(d, chatMessage.message)
             }
             if (!voiceFile.exists()) {
                 binding.imagePlayPause.visibility = View.GONE
                 binding.playerSeekBar.progress = 0
                 binding.playerSeekBar.isEnabled = false
                 binding.textCurrentTime.text = "0.00"
                 binding.textTotalDuration.text = "0.00"
                 if (chatMessage.isDownload) {
                     binding.imageDownloadAudio.visibility = View.GONE
                     binding.pgbProgress.visibility = View.VISIBLE
                 } else {
                     binding.imageDownloadAudio.visibility = View.VISIBLE
                     binding.pgbProgress.visibility = View.GONE
                     binding.imageDownloadAudio.setOnClickListener {
                         if (mCallback != null) {
                             binding.imageDownloadAudio.visibility =
                                 View.GONE
                             binding.pgbProgress.visibility =
                                 View.VISIBLE
                             mCallback.downloadVoice(
                                 position,
                                 chatMessage,
                                 myMsg
                             )
                         }
                     }
                 }
             } else {
                 if (!mediaPlayer!!.isPlaying) {
                     binding.playerSeekBar.isEnabled = true
                     binding.playerSeekBar.progress = 0
                     binding.textCurrentTime.text = "0.00"
                     binding.textTotalDuration.text = "0.00"
                     binding.imagePlayPause.setImageResource(R.drawable.ic_play_audio)
                     binding.imageDownloadAudio.visibility = View.GONE
                     binding.imagePlayPause.visibility = View.VISIBLE
                 } else {
                     println("is playing" + chatMessage.message)
                 }
                 try {
                     mediaPlayer!!.setDataSource(voiceFile.absolutePath)
                     mediaPlayer!!.prepare()
                     binding.textTotalDuration.text =
                         TimeProperties.milliSecondsToTimer(
                             mediaPlayer!!.duration.toLong()
                         )
                 } catch (exceptione: Exception) {
                     //                        Toast.makeText(context, exceptione.getMessage(), Toast.LENGTH_SHORT).show();
                 }
                 if (chatMessage.upload) {
                     binding.pgbProgress.visibility = View.VISIBLE
                     binding.imagePlayPause.visibility = View.GONE
                 } else {
                     binding.pgbProgress.visibility = View.GONE

                     binding.imagePlayPause.visibility = View.VISIBLE
                 }
             }
             ////////////////media player tools
             binding.playerSeekBar.setOnTouchListener { view, motionEvent ->
                 val seekBar = view as SeekBar
                 val payPosition =
                     mediaPlayer!!.duration / 100 * seekBar.progress
                 mediaPlayer!!.seekTo(payPosition)
                 binding.textCurrentTime.text = TimeProperties.milliSecondsToTimer(
                     mediaPlayer!!.currentPosition.toLong()
                 )
                 false
             }
             mediaPlayer!!.setOnBufferingUpdateListener { mediaPlayer, i ->
                 binding.playerSeekBar.secondaryProgress = i
             }
             mediaPlayer!!.setOnCompletionListener { mediaPlayer ->
                 binding.playerSeekBar.progress = 0
                 binding.imagePlayPause.setImageResource(R.drawable.ic_play_audio)
                 binding.textCurrentTime.text = "0.00"
                 previousMediaPlayer = null
                 mediaPlayer.reset()
                 try {
                     this.mediaPlayer!!.setDataSource(voiceFile.absolutePath)
                     this.mediaPlayer!!.prepare()
                     binding.textTotalDuration.text =
                         TimeProperties.milliSecondsToTimer(
                             this.mediaPlayer!!.duration.toLong()
                         )
                 } catch (exceptione: Exception) {
                     Toast.makeText(context, exceptione.message, Toast.LENGTH_SHORT).show()
                 }
             }
             updater = Runnable {
                 if (mediaPlayer!!.isPlaying) {
                     binding.playerSeekBar.progress = (mediaPlayer!!.currentPosition
                         .toFloat() / mediaPlayer!!.duration * 100).toInt()
                     handler.postDelayed(updater!!, 1000)
                 }
                 val currentDuration = mediaPlayer!!.currentPosition.toLong()
                 binding.textCurrentTime.text = TimeProperties.milliSecondsToTimer(currentDuration)
             }
             binding.imagePlayPause.setOnClickListener {
                 if (mediaPlayer!!.isPlaying) {
                     previousMediaPlayer = null
                     handler.removeCallbacks(updater!!)
                     mediaPlayer!!.pause()
                     binding.imagePlayPause.setImageResource(R.drawable.ic_play_audio)
                 } else {
                     mediaPlayer!!.start()
                     ////
                     previousMediaPlayer?.pause()
                      previousImageView?.setImageResource(R.drawable.ic_play_audio)
                     /////
                     previousMediaPlayer = mediaPlayer
                     previousImageView = binding.imagePlayPause
                     binding.imagePlayPause.setImageResource(R.drawable.ic_pause)
                     if (mediaPlayer!!.isPlaying) {
                         binding.playerSeekBar.progress =
                             (mediaPlayer!!.currentPosition
                                 .toFloat() / mediaPlayer!!.duration * 100).toInt()
                         handler.postDelayed(
                             updater!!,
                             1000
                         )
                     }
                 }
             }
         }

         @SuppressLint("UseCompatLoadingForDrawables", "RtlHardcoded")
         private fun setAlignment(
             isMe: Boolean,
             state: String,
             context: Activity
         ) {
             if (isMe) {
                 (this as LayoutVoiceViewHolder).binding.contentWithBackground.setBackgroundResource(
                     R.drawable.in_message_bg
                 )
                 val layoutParams =
                     binding.contentWithBackground.layoutParams as LinearLayout.LayoutParams
                 layoutParams.gravity = Gravity.RIGHT
                 binding.contentWithBackground.layoutParams = layoutParams
                 val lp = binding.content.layoutParams as RelativeLayout.LayoutParams
                 lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0)
                 lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                 binding.content.layoutParams = lp
                 binding.imagePlayPause.setColorFilter(context.resources.getColor(R.color.white))
                 binding.imageDownloadAudio.setColorFilter(context.resources.getColor(R.color.white))
                 binding.textTotalDuration.setTextColor(context.resources.getColor(R.color.white))
                 binding.textCurrentTime.setTextColor(context.resources.getColor(R.color.white))
                 binding.timeSeparator.setTextColor(context.resources.getColor(R.color.white))
                 binding.tvDate.setTextColor(context.resources.getColor(R.color.white))
                 when (state) {
                     "3" -> {
                         binding.ivState.setImageDrawable(context.resources.getDrawable(R.drawable.ic_recive_done_green))
                     }
                     "2" -> {
                         binding.ivState.setImageDrawable(context.resources.getDrawable(R.drawable.ic_recive_done))
                     }
                     "1" -> {
                         binding.ivState.setImageDrawable(context.resources.getDrawable(R.drawable.ic_send_done))
                     }
                     else -> {
                         binding.ivState.setImageDrawable(context.resources.getDrawable(R.drawable.ic_not_send))
                     }
                 }
                 binding.ivState.visibility = View.VISIBLE

             } else if (!isMe) {
                 (this as LayoutVoiceViewHolder).binding.ivState.visibility = View.GONE
                 binding.contentWithBackground.setBackgroundResource(R.drawable.out_message_bg)
                 val layoutParams =
                     binding.contentWithBackground.layoutParams as LinearLayout.LayoutParams
                 layoutParams.gravity = Gravity.LEFT
                 binding.contentWithBackground.layoutParams = layoutParams
                 binding.imagePlayPause.setColorFilter(context.resources.getColor(R.color.textColor))
                 binding.imageDownloadAudio.setColorFilter(context.resources.getColor(R.color.textColor))
                 binding.textTotalDuration.setTextColor(context.resources.getColor(R.color.textColor))
                 binding.textCurrentTime.setTextColor(context.resources.getColor(R.color.textColor))
                 binding.timeSeparator.setTextColor(context.resources.getColor(R.color.textColor))
                 binding.tvDate.setTextColor(context.resources.getColor(R.color.textColor))
                 val lp = binding.content.layoutParams as RelativeLayout.LayoutParams
                 lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
                 lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                 binding.content.layoutParams = lp

             }
         }

         companion object {
             fun from(parent: ViewGroup): LayoutVoiceViewHolder {
//                 val layoutTwo: View = LayoutInflater.from(parent.context)
//                     .inflate(R.layout.voice_record_item_chat_message, parent, false)
//                 return LayoutVoiceViewHolder(layoutTwo)
                 val layoutInflater = LayoutInflater.from(parent.context)
                 val binding = VoiceRecordItemChatMessageBinding.inflate(layoutInflater,parent, false)

                 return LayoutVoiceViewHolder(binding)
             }
         }

     }

     class LayoutVideoViewHolder private  constructor (val binding: VideoItemChatMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
         var l = 0

         fun bind(
            myMsg: Boolean,
            chatMessage: ChatMessage,
            position: Int,
            context: Activity,
            mCallback: CallbackInterface?
        ) {
            setAlignment(myMsg, chatMessage.state, context)
            binding.tvDate.text = TimeProperties.getDate(
                chatMessage.dateTime.toLong(), "hh:mm"
            )
            val videoFile: File = if (myMsg) {
                val d =
                    context.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send/video") // -> filename = maven.pdf
                File(d, chatMessage.fileName)
            } else {
                val d =
                    context.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/recive/video") // -> filename = maven.pdf
                File(d, chatMessage.message)
            }
            if (!videoFile.exists()) {
                //                    ((LayoutVideoViewHolder) holder).adCircleProgress.setVisibility(View.GONE);
                Glide.with(binding.imgVideo.context).load(R.drawable.backgrounblack)
                    .centerCrop()
                    .into(binding.imgVideo)
                if (chatMessage.isDownload) {
                    binding.videoImageDownload.visibility = View.GONE
                    binding.pgbProgress.visibility = View.VISIBLE
                    binding.videoImageButton.visibility = View.GONE
                } else {
                    binding.videoImageDownload.visibility = View.VISIBLE
                    binding.pgbProgress.visibility = View.GONE
                    binding.videoImageButton.visibility = View.GONE
                    binding.videoImageDownload.setOnClickListener {
                        binding.videoImageDownload.visibility =
                            View.GONE
                        binding.pgbProgress.visibility =
                            View.VISIBLE
                        mCallback?.downloadVideo(position, chatMessage, myMsg)
                    }
                }
            } else {
                binding.videoImageDownload.visibility = View.GONE
                val path = FileProvider.getUriForFile(
                    context, BuildConfig.APPLICATION_ID + ".fileprovider", videoFile
                )
                Glide.with(binding.imgVideo.context).load(path).centerCrop()
                    .into(binding.imgVideo)
                if (chatMessage.upload) {
                    binding.pgbProgress.visibility = View.VISIBLE
                    binding.videoImageButton.visibility = View.GONE
                } else {
                    binding.pgbProgress.visibility = View.GONE
                    binding.videoImageButton.visibility = View.VISIBLE
                    binding.videoImageButton.setOnClickListener {
                        mCallback?.playVideo(path)
                    }
                }
            }
        }

         @SuppressLint("UseCompatLoadingForDrawables", "RtlHardcoded")
         fun setAlignment(
             isMe: Boolean,
             state: String,
             context: Activity
         ) {
             if (isMe) {
                 (this as LayoutVideoViewHolder).binding.contentWithBackground.setBackgroundResource(
                     R.drawable.in_message_bg
                 )
                 val layoutParams =
                     binding.contentWithBackground.layoutParams as LinearLayout.LayoutParams
                 layoutParams.gravity = Gravity.RIGHT
                 binding.contentWithBackground.layoutParams = layoutParams
                 val lp = binding.content.layoutParams as RelativeLayout.LayoutParams
                 lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0)
                 lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                 binding.content.layoutParams = lp
                 binding.tvDate.setTextColor(context.resources.getColor(R.color.white))
                 when (state) {
                     "3" -> {
                         binding.ivState.setImageDrawable(context.resources.getDrawable(R.drawable.ic_recive_done_green))
                     }
                     "2" -> {
                         binding.ivState.setImageDrawable(context.resources.getDrawable(R.drawable.ic_recive_done))
                     }
                     "1" -> {
                         binding.ivState.setImageDrawable(context.resources.getDrawable(R.drawable.ic_send_done))
                     }
                     else -> {
                         binding.ivState.setImageDrawable(context.resources.getDrawable(R.drawable.ic_not_send))
                     }
                 }
                 binding.ivState.visibility = View.VISIBLE
             } else if (!isMe) {
                 (this as LayoutVideoViewHolder).binding.ivState.visibility = View.GONE
                 binding.contentWithBackground.setBackgroundResource(R.drawable.out_message_bg)
                 val layoutParams =
                     binding.contentWithBackground.layoutParams as LinearLayout.LayoutParams
                 layoutParams.gravity = Gravity.LEFT
                 binding.contentWithBackground.layoutParams = layoutParams
                 binding.tvDate.setTextColor(context.resources.getColor(R.color.textColor))
                 val lp = binding.content.layoutParams as RelativeLayout.LayoutParams
                 lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
                 lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                 binding.content.layoutParams = lp
             }
         }
         companion object {
             fun from(parent: ViewGroup): LayoutVideoViewHolder {

                 val layoutInflater = LayoutInflater.from(parent.context)
                 val binding = VideoItemChatMessageBinding.inflate(layoutInflater,parent, false)
                 return LayoutVideoViewHolder(binding)
             }
         }

     }

    class LayoutPdfViewHolder private  constructor (val binding: PdfItemChatMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var l = 0


        fun bind(
            myMsg: Boolean,
            chatMessage: ChatMessage,
            position: Int,
            context: Activity,
            mCallback: CallbackInterface?
        ) {
            var userNameeee = chatMessage.fileName

            setAlignment(myMsg, chatMessage.state, context)
            binding.tvDate.text =
                TimeProperties.getDate(chatMessage.dateTime.toLong(), "hh:mm")
            val pdfFile: File
            if (myMsg) {
                val d =
                    context.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send") // -> filename = maven.pdf
                pdfFile = File(d, chatMessage.fileName)
                userNameeee = chatMessage.fileName
            } else {
                val d =
                    context.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/recive") // -> filename = maven.pdf
                pdfFile = File(d, chatMessage.message)
            }
            println(chatMessage.message)
            binding.textFile.text = chatMessage.fileName
            binding.linerFile.setOnClickListener {
                mCallback?.onHandleSelection(position, chatMessage, myMsg)
            }
            if (!pdfFile.exists()) {
                binding.imagePdf.visibility = View.GONE
                if (chatMessage.isDownload) {
                    binding.pgbProgress.visibility = View.VISIBLE
                    binding.imageButtonFile.visibility = View.GONE
                } else {
                    binding.pgbProgress.visibility = View.GONE
                    binding.imageButtonFile.visibility = View.VISIBLE
                    binding.imageButtonFile.setOnClickListener {
                        if (mCallback != null) {
                            binding.imageButtonFile.visibility =
                                View.GONE
                            binding.pgbProgress.visibility =
                                View.VISIBLE
                            mCallback.downloadFile(position, chatMessage, myMsg)
                        }
                    }
                }
            } else {
                binding.imageButtonFile.visibility = View.GONE
                binding.pgbProgress.visibility = View.GONE
                val path = FileProvider.getUriForFile(
                    context, BuildConfig.APPLICATION_ID + ".fileprovider", pdfFile
                )
                val bmp = ImageProperties.getImageFromPdf(path, context)
                println(bmp)
                binding.imagePdf.setImageBitmap(bmp)
                if (bmp != null) {
                    binding.imagePdf.visibility = View.VISIBLE
                } else {
                    binding.imagePdf.visibility = View.GONE
                }
                if (chatMessage.upload) {
                    binding.pgbProgress.visibility = View.VISIBLE
                } else {
                    binding.pgbProgress.visibility = View.GONE
                }
            }
        }

        @SuppressLint("UseCompatLoadingForDrawables", "RtlHardcoded")
        private fun setAlignment(
            isMe: Boolean,
            state: String,
            context: Activity
        ) {
            if (isMe) {
                binding.contentWithBackground.setBackgroundResource(R.drawable.in_message_bg)
                val layoutParams =
                    binding.contentWithBackground.layoutParams as LinearLayout.LayoutParams
                layoutParams.gravity = Gravity.RIGHT
                binding.contentWithBackground.layoutParams = layoutParams
                val lp = binding.content.layoutParams as RelativeLayout.LayoutParams
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0)
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                binding.content.layoutParams = lp
                layoutParams.gravity = Gravity.RIGHT
                binding.tvDate.setTextColor(context.resources.getColor(R.color.white))
                when (state) {
                    "3" -> {
                        binding.ivState.setImageDrawable(context.resources.getDrawable(R.drawable.ic_recive_done_green))
                    }
                    "2" -> {
                        binding.ivState.setImageDrawable(context.resources.getDrawable(R.drawable.ic_recive_done))
                    }
                    "1" -> {
                        binding.ivState.setImageDrawable(context.resources.getDrawable(R.drawable.ic_send_done))
                    }
                    else -> {
                        binding.ivState.setImageDrawable(context.resources.getDrawable(R.drawable.ic_not_send))
                    }
                }
                binding.ivState.visibility = View.VISIBLE
            } else if (!isMe) {
                binding.ivState.visibility = View.GONE
                binding.contentWithBackground.setBackgroundResource(R.drawable.out_message_bg)
                val layoutParams =
                    binding.contentWithBackground.layoutParams as LinearLayout.LayoutParams
                layoutParams.gravity = Gravity.LEFT
                binding.contentWithBackground.layoutParams = layoutParams
                binding.tvDate.setTextColor(context.resources.getColor(R.color.textColor))
                val lp = binding.content.layoutParams as RelativeLayout.LayoutParams
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                binding.content.layoutParams = lp
            }
        }
        companion object {
            fun from(parent: ViewGroup): LayoutPdfViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = PdfItemChatMessageBinding.inflate(layoutInflater,parent, false)
                return LayoutPdfViewHolder(binding)
            }
        }


    }

    class LayoutContactViewHolder private  constructor (val binding: ContactItemChatMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var view2: View? = null


        fun bind(
            myMsg: Boolean,
            chatMessage: ChatMessage,
            context: Activity
        ) {
            setAlignment(myMsg, chatMessage.state, context)


                binding.tvDate.text
                TimeProperties.getDate(
                chatMessage.dateTime.toLong(), "hh:mm"
            )
            binding.phone.text = chatMessage.message
            binding.name.text = chatMessage.fileName
            binding.addContact.setOnClickListener {
                val builder = AlertDialog.Builder(
                    context
                )
                val inflater = LayoutInflater.from(context)
                val dialogView: View =
                    inflater.inflate(R.layout.add_contact_dialog, null)
                builder.setView(dialogView)
                val dialogadd = builder.create()

                val name = dialogView.findViewById<TextView>(R.id.name)
                val nameAddContact = dialogView.findViewById<TextView>(R.id.nameAddContact)
                nameAddContact.text = chatMessage.fileName
                val numberAddContact = dialogView.findViewById<TextView>(R.id.numberAddContact)
                numberAddContact.text = chatMessage.message
                val btnAddContact =
                    dialogView.findViewById<Button>(R.id.btnAddContact)
                btnAddContact.setOnClickListener {
                    if (!nameAddContact.text.toString()
                            .isEmpty() && !numberAddContact.text.toString().isEmpty()
                    ) {
                        val intent = Intent(Intent.ACTION_INSERT_OR_EDIT)
                        intent.type = ContactsContract.RawContacts.CONTENT_ITEM_TYPE
                        intent.putExtra(
                            ContactsContract.Intents.Insert.NAME,
                            nameAddContact.text.toString()
                        )
                        intent.putExtra(
                            ContactsContract.Intents.Insert.PHONE,
                            numberAddContact.text.toString()
                        )
                        context.startActivity(intent)
                        dialogadd.cancel()
                    } else {
                        Toast.makeText(
                            context,
                            "please fill all the fields",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                dialogadd.show()
            }
        }

        @SuppressLint("RtlHardcoded", "UseCompatLoadingForDrawables")
        fun setAlignment(
            isMe: Boolean,
            state: String,
            context: Activity

            ) {
            if (isMe) {
                binding.card.setBackgroundResource(R.drawable.in_message_bg)
                val layoutParams = binding.card.layoutParams as LinearLayout.LayoutParams
                layoutParams.gravity = Gravity.RIGHT
                binding.card.layoutParams = layoutParams
                val lp = binding.content.layoutParams as RelativeLayout.LayoutParams
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0)
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                binding.content.layoutParams = lp
                layoutParams.gravity = Gravity.RIGHT
                binding.tvDate.setTextColor(context.resources.getColor(R.color.white))
                binding.addContact.setTextColor(context.resources.getColor(R.color.white))
                binding.name.setTextColor(context.resources.getColor(R.color.white))
                binding.phone.setTextColor(context.resources.getColor(R.color.white))
                when (state) {
                    "3" -> {
                        binding.ivState.setImageDrawable(context.resources.getDrawable(R.drawable.ic_recive_done_green))
                    }
                    "2" -> {
                        binding.ivState.setImageDrawable(context.resources.getDrawable(R.drawable.ic_recive_done))
                    }
                    "1" -> {
                        binding.ivState.setImageDrawable(context.resources.getDrawable(R.drawable.ic_send_done))
                    }
                    else -> {
                        binding.ivState.setImageDrawable(context.resources.getDrawable(R.drawable.ic_not_send))
                    }
                }
                binding.ivState.visibility = View.VISIBLE
            } else if (!isMe) {
                binding.ivState.visibility = View.GONE
                binding.card.setBackgroundResource(R.drawable.out_message_bg)
                val layoutParams = binding.card.layoutParams as LinearLayout.LayoutParams
                layoutParams.gravity = Gravity.LEFT
                binding.card.layoutParams = layoutParams
                binding.tvDate.setTextColor(context.resources.getColor(R.color.textColor))
                binding.addContact.setTextColor(context.resources.getColor(R.color.textColor))
                binding.name.setTextColor(context.resources.getColor(R.color.textColor))
                binding.phone.setTextColor(context.resources.getColor(R.color.textColor))
                val lp = binding.content.layoutParams as RelativeLayout.LayoutParams
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                binding.content.layoutParams = lp
            }
        }

        companion object {
            fun from(parent: ViewGroup): LayoutContactViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ContactItemChatMessageBinding.inflate(layoutInflater,parent, false)
                return LayoutContactViewHolder(binding)
            }
        }





    }

    class LayoutLocationViewHolder private  constructor  (val binding: LocationItemChatMessageBinding)  :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            myMsg: Boolean,
            chatMessage: ChatMessage,
            position: Int,
            context: Activity,
            mCallback: CallbackInterface?
        ) {
            setAlignment(myMsg, chatMessage.state, context)
            binding.tvDate.text = TimeProperties.getDate(
                chatMessage.dateTime.toLong(), "hh:mm"
            )
            binding.cardOpenItLocation.setOnClickListener {
                mCallback?.onClickLocation(position, chatMessage, myMsg)
            }
        }
        @SuppressLint("UseCompatLoadingForDrawables", "RtlHardcoded")
        private fun setAlignment(
            isMe: Boolean,
            state: String,
            context: Activity
        ) {
            if (isMe) {
                binding.contentWithBackground.setBackgroundResource(R.drawable.in_message_bg)
                val layoutParams =
                    binding.contentWithBackground.layoutParams as LinearLayout.LayoutParams
                layoutParams.gravity = Gravity.RIGHT
                binding.contentWithBackground.layoutParams = layoutParams
                val lp = binding.content.layoutParams as RelativeLayout.LayoutParams
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0)
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                binding.content.layoutParams = lp
                layoutParams.gravity = Gravity.RIGHT
                binding.tvDate.setTextColor(context.resources.getColor(R.color.white))
                when (state) {
                    "3" -> {
                        binding.ivState.setImageDrawable(context.resources.getDrawable(R.drawable.ic_recive_done_green))
                    }
                    "2" -> {
                        binding.ivState.setImageDrawable(context.resources.getDrawable(R.drawable.ic_recive_done))
                    }
                    "1" -> {
                        binding.ivState.setImageDrawable(context.resources.getDrawable(R.drawable.ic_send_done))
                    }
                    else -> {
                        binding.ivState.setImageDrawable(context.resources.getDrawable(R.drawable.ic_not_send))
                    }
                }
                binding.ivState.visibility = View.VISIBLE
            } else if (!isMe) {
                binding.ivState.visibility = View.GONE
                binding.contentWithBackground.setBackgroundResource(R.drawable.out_message_bg)
                val layoutParams =
                    binding.contentWithBackground.layoutParams as LinearLayout.LayoutParams
                layoutParams.gravity = Gravity.LEFT
                binding.contentWithBackground.layoutParams = layoutParams
                //                    ((LayoutLocationViewHolder) holder).txtDate.setTextColor(context.getResources().getColor(R.color.textColor));
                binding.tvDate.setTextColor(context.resources.getColor(R.color.textColor))
                val lp = binding.content.layoutParams as RelativeLayout.LayoutParams
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                binding.content.layoutParams = lp
            }
        }
        companion object {
            fun from(parent: ViewGroup): LayoutLocationViewHolder {

                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = LocationItemChatMessageBinding.inflate(layoutInflater,parent, false)
                return LayoutLocationViewHolder(binding)
            }
        }


    }

    class LayoutTextViewHolder private  constructor  (val binding: ListItemChatMessageBinding) : RecyclerView.ViewHolder(binding.root) {


        fun bind(
            myMsg: Boolean,
            chatMessage: ChatMessage,
            context: Activity
        ) {
            setAlignment(myMsg, chatMessage.state, context)
            binding.tvDate.text =
                TimeProperties.getDate(chatMessage.dateTime.toLong(), "hh:mm")
            if (chatMessage.isUpdate == "1") {
                binding.textUpdate.visibility = View.VISIBLE
            } else {
                binding.textUpdate.visibility = View.GONE
            }
            binding.txtMessage.text = chatMessage.message
            Linkify.addLinks(binding.txtMessage, Linkify.WEB_URLS)
            binding.txtMessage.setLinkTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.blue_200
                )
            )
            binding.txtMessage.visibility = View.VISIBLE
        }


        @SuppressLint("RtlHardcoded", "UseCompatLoadingForDrawables")
        fun setAlignment(
            isMe: Boolean,
            state: String,
            context: Activity
        ) {
            if (isMe) {
                binding.contentWithBackground.setBackgroundResource(R.drawable.in_message_bg)
                var layoutParams =
                    binding.contentWithBackground.layoutParams as LinearLayout.LayoutParams
                layoutParams.gravity = Gravity.RIGHT
                binding.contentWithBackground.layoutParams = layoutParams
                val lp = binding.content.layoutParams as RelativeLayout.LayoutParams
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0)
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                binding.content.layoutParams = lp
                layoutParams = binding.txtMessage.layoutParams as LinearLayout.LayoutParams
                layoutParams.gravity = Gravity.RIGHT
                binding.txtMessage.layoutParams = layoutParams
                binding.txtMessage.setTextColor(context.resources.getColor(R.color.white))
                binding.tvDate.setTextColor(context.resources.getColor(R.color.white))
                when (state) {
                    "3" -> {
                        binding.ivState.setImageDrawable(context.resources.getDrawable(R.drawable.ic_recive_done_green))
                    }
                    "2" -> {
                        binding.ivState.setImageDrawable(context.resources.getDrawable(R.drawable.ic_recive_done))
                    }
                    "1" -> {
                        binding.ivState.setImageDrawable(context.resources.getDrawable(R.drawable.ic_send_done))
                    }
                    else -> {
                        binding.ivState.setImageDrawable(context.resources.getDrawable(R.drawable.ic_not_send))
                    }
                }
                binding.ivState.visibility = View.VISIBLE

            } else if (!isMe) {
                binding.ivState.visibility = View.GONE
                binding.contentWithBackground.setBackgroundResource(R.drawable.out_message_bg)
                var layoutParams =
                    binding.contentWithBackground.layoutParams as LinearLayout.LayoutParams
                layoutParams.gravity = Gravity.LEFT
                binding.contentWithBackground.layoutParams = layoutParams
                binding.txtMessage.setTextColor(context.resources.getColor(R.color.textColor))
                binding.tvDate.setTextColor(context.resources.getColor(R.color.textColor))
                val lp = binding.content.layoutParams as RelativeLayout.LayoutParams
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                binding.content.layoutParams = lp
                layoutParams = binding.txtMessage.layoutParams as LinearLayout.LayoutParams
                layoutParams.gravity = Gravity.LEFT
                binding.txtMessage.layoutParams = layoutParams

            }
        }
        companion object {
            fun from(parent: ViewGroup): LayoutTextViewHolder {

                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemChatMessageBinding.inflate(layoutInflater,parent, false)
                return LayoutTextViewHolder(binding)
            }
        }

    }
    class MyDiffUtilChatMessage : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem == newItem
        }
    }
}

