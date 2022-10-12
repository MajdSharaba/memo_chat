package com.yawar.memo.adapter

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
import android.view.*
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.yawar.memo.BuildConfig
import com.yawar.memo.R
import com.yawar.memo.model.ChatMessage
import com.yawar.memo.utils.ImageProperties
import com.yawar.memo.utils.TimeProperties
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.util.*

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
        } catch (ex: ClassCastException) {
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
        submitList(newData)
    }
    class LayoutImageViewHolder  private  constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imgMessage)
        val downloadImage: ImageButton = itemView.findViewById(R.id.image_download)
        var l = 0
        val content: LinearLayout = itemView.findViewById(R.id.content)
        val contentwithB: LinearLayout = itemView.findViewById(R.id.contentWithBackground)
        val imageSeen: ImageView = itemView.findViewById(R.id.iv_state)
        var txtDate: TextView = itemView.findViewById(R.id.tv_date)
        var adCircleProgress: ProgressBar = itemView.findViewById(R.id.pgb_progress)


         fun bind(
            myMsg: Boolean,
            chatMessage: ChatMessage,
            position: Int,
            context: Activity,
            mCallback: CallbackInterface?
        ) {
            setAlignment(myMsg, chatMessage.state, context)
            txtDate.text =
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
                imageView.isEnabled = false
                Glide.with(imageView.context).load(R.drawable.backgrounblack)
                    .centerCrop()
                    .into(imageView)
                if (chatMessage.isDownload) {
                    downloadImage.visibility = View.GONE
                    adCircleProgress.visibility = View.VISIBLE
                } else {
                    downloadImage.visibility = View.VISIBLE
                    adCircleProgress.visibility = View.GONE
                    downloadImage.setOnClickListener {
                        downloadImage.visibility =
                            View.GONE
                        adCircleProgress.visibility =
                            View.VISIBLE

                        mCallback?.downloadImage(position, chatMessage, myMsg)
                    }
                }
            } else {
                imageView.isEnabled = true
                downloadImage.visibility = View.GONE
                val path = FileProvider.getUriForFile(
                    context, BuildConfig.APPLICATION_ID + ".fileprovider", imageFile)
                Glide.with(imageView.context).load(path).centerCrop()
                    .into(imageView)
                if (chatMessage.upload) {
                    adCircleProgress.visibility = View.VISIBLE
                } else {
                    adCircleProgress.visibility = View.GONE
                    imageView.setOnClickListener {
                        val dialog = Dialog(context)
                        dialog.setContentView(R.layout.dialog_image_cht)
                        dialog.setTitle("Title...")
                        dialog.window!!
                            .setLayout(ViewGroup.LayoutParams.FILL_PARENT,
                                ViewGroup.LayoutParams.FILL_PARENT)
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
                contentwithB.setBackgroundResource(R.drawable.in_message_bg)
                val layoutParams = contentwithB.layoutParams as LinearLayout.LayoutParams
                layoutParams.gravity = Gravity.RIGHT
                contentwithB.layoutParams = layoutParams
                val lp = content.layoutParams as RelativeLayout.LayoutParams
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0)
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                content.layoutParams = lp
                layoutParams.gravity = Gravity.RIGHT
                txtDate.setTextColor(context.resources.getColor(R.color.white))
                when (state) {
                    "3" -> {
                        imageSeen.setImageDrawable(context.resources.getDrawable(R.drawable.ic_recive_done_green))
                    }
                    "2" -> {
                        imageSeen.setImageDrawable(context.resources.getDrawable(R.drawable.ic_recive_done))
                    }
                    "1" -> {
                        imageSeen.setImageDrawable(context.resources.getDrawable(R.drawable.ic_send_done))
                    }
                    else -> {
                        imageSeen.setImageDrawable(context.resources.getDrawable(R.drawable.ic_not_send))
                    }
                }
                imageSeen.visibility = View.VISIBLE
            } else if (!isMe) {
                imageSeen.visibility = View.GONE
                contentwithB.setBackgroundResource(R.drawable.out_message_bg)
                val layoutParams = contentwithB.layoutParams as LinearLayout.LayoutParams
                layoutParams.gravity = Gravity.LEFT
                contentwithB.layoutParams = layoutParams
                txtDate.setTextColor(context.resources.getColor(R.color.textColor))
                val lp = content.layoutParams as RelativeLayout.LayoutParams
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                content.layoutParams = lp
            }
        }

        companion object {
            fun from(parent: ViewGroup): LayoutImageViewHolder {
                val layoutOne: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.image_item_chat_meesage, parent, false)
                return LayoutImageViewHolder(layoutOne)
            }
        }
     }

     class LayoutVoiceViewHolder private  constructor (itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val content: LinearLayout = itemView.findViewById(R.id.content)
        var l = 0
        val contentWithBG: LinearLayout = itemView.findViewById(R.id.contentWithBackground)
        val imageSeen: ImageView = itemView.findViewById(R.id.iv_state)
        var textDate: TextView = itemView.findViewById(R.id.tv_date)
        var contentRecord: LinearLayout = itemView.findViewById(R.id.Liner_record)
        var imagePlayerPause: ImageView = itemView.findViewById(R.id.image_play_pause)
        var textCurrentTime: TextView = itemView.findViewById(R.id.text_current_time)
        var textTotalDouration: TextView = itemView.findViewById(R.id.text_total_duration)
        var timeSeparator: TextView = itemView.findViewById(R.id.time_separator)
        var playerSeekBar: SeekBar = itemView.findViewById(R.id.player_seek_bar)
        var mediaPlayer: MediaPlayer? = null
        var relativeLayout: RelativeLayout? = null
        var handler = Handler()
        var updater: Runnable? = null
        var downloadRecordIB: ImageButton = itemView.findViewById(R.id.image_download_audio)
        var adCircleProgress: ProgressBar = itemView.findViewById(R.id.pgb_progress)


         @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
          fun bind(
             myMsg: Boolean,
             chatMessage: ChatMessage,
             position: Int,
             context: Activity,
             mCallback: CallbackInterface?
         ) {
             setAlignment( myMsg, chatMessage.state, context)
             (this as LayoutVoiceViewHolder).contentRecord.visibility = View.VISIBLE
             textDate.text = TimeProperties.getDate(
                 chatMessage.dateTime.toLong(), "hh:mm")
             mediaPlayer = MediaPlayer()
             playerSeekBar.max = 100
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
                 imagePlayerPause.visibility = View.GONE
                 playerSeekBar.progress = 0
                 playerSeekBar.isEnabled = false
                 textCurrentTime.text = "0.00"
                 textTotalDouration.text = "0.00"
                 if (chatMessage.isDownload) {
                     downloadRecordIB.visibility = View.GONE
                     adCircleProgress.visibility = View.VISIBLE
                 } else {
                     downloadRecordIB.visibility = View.VISIBLE
                     adCircleProgress.visibility = View.GONE
                     downloadRecordIB.setOnClickListener {
                         if (mCallback != null) {
                             downloadRecordIB.visibility =
                                 View.GONE
                             adCircleProgress.visibility =
                                 View.VISIBLE
                             mCallback.downloadVoice(position,
                                 chatMessage,
                                 myMsg)
                         }
                     }
                 }
             } else {
                 if (!mediaPlayer!!.isPlaying) {
                     playerSeekBar.isEnabled = true
                     playerSeekBar.progress = 0
                     textCurrentTime.text = "0.00"
                     textTotalDouration.text = "0.00"
                     imagePlayerPause.setImageResource(R.drawable.ic_play_audio)
                     downloadRecordIB.visibility = View.GONE
                     imagePlayerPause.visibility = View.VISIBLE
                 } else {
                     println("is playing" + chatMessage.message)
                 }
                 try {
                     mediaPlayer!!.setDataSource(voiceFile.absolutePath)
                     mediaPlayer!!.prepare()
                     textTotalDouration.text =
                         TimeProperties.milliSecondsToTimer(
                             mediaPlayer!!.duration.toLong())
                 } catch (exceptione: Exception) {
                     //                        Toast.makeText(context, exceptione.getMessage(), Toast.LENGTH_SHORT).show();
                 }
                 if (chatMessage.upload) {
                     adCircleProgress.visibility = View.VISIBLE
                     imagePlayerPause.visibility = View.GONE
                 } else {
                     adCircleProgress.visibility = View.GONE
                     imagePlayerPause.visibility = View.VISIBLE
                 }
             }
             ////////////////media player tools
             playerSeekBar.setOnTouchListener { view, motionEvent ->
                 val seekBar = view as SeekBar
                 val payPosition =
                     mediaPlayer!!.duration / 100 * seekBar.progress
                 mediaPlayer!!.seekTo(payPosition)
                 textCurrentTime.text = TimeProperties.milliSecondsToTimer(
                     mediaPlayer!!.currentPosition.toLong())
                 false
             }
             mediaPlayer!!.setOnBufferingUpdateListener { mediaPlayer, i ->
                 playerSeekBar.secondaryProgress = i
             }
             mediaPlayer!!.setOnCompletionListener { mediaPlayer ->
                 playerSeekBar.progress = 0
                 imagePlayerPause.setImageResource(R.drawable.ic_play_audio)
                 textCurrentTime.text = "0.00"
                 mediaPlayer.reset()
                 try {
                     this.mediaPlayer!!.setDataSource(voiceFile.absolutePath)
                     this.mediaPlayer!!.prepare()
                     textTotalDouration.text =
                         TimeProperties.milliSecondsToTimer(
                             this.mediaPlayer!!.duration.toLong())
                 } catch (exceptione: Exception) {
                     Toast.makeText(context, exceptione.message, Toast.LENGTH_SHORT).show()
                 }
             }
             updater = Runnable {
                 if (mediaPlayer!!.isPlaying) {
                     playerSeekBar.progress = (mediaPlayer!!.currentPosition
                         .toFloat() / mediaPlayer!!.duration * 100).toInt()
                     handler.postDelayed(updater!!, 1000)
                 }
                 val currentDuration = mediaPlayer!!.currentPosition.toLong()
                 textCurrentTime.text = TimeProperties.milliSecondsToTimer(currentDuration)
             }
             imagePlayerPause.setOnClickListener {
                 if (mediaPlayer!!.isPlaying) {
                     handler.removeCallbacks(updater!!)
                     mediaPlayer!!.pause()
                     imagePlayerPause.setImageResource(R.drawable.ic_play_audio)
                 } else {
                     mediaPlayer!!.start()
                     imagePlayerPause.setImageResource(R.drawable.ic_pause)
                     if (mediaPlayer!!.isPlaying) {
                         playerSeekBar.progress =
                             (mediaPlayer!!.currentPosition
                                 .toFloat() / mediaPlayer!!.duration * 100).toInt()
                         handler.postDelayed(updater!!,
                             1000)
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
                 (this as LayoutVoiceViewHolder).contentWithBG.setBackgroundResource(R.drawable.in_message_bg)
                 val layoutParams = contentWithBG.layoutParams as LinearLayout.LayoutParams
                 layoutParams.gravity = Gravity.RIGHT
                 contentWithBG.layoutParams = layoutParams
                 val lp = content.layoutParams as RelativeLayout.LayoutParams
                 lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0)
                 lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                 content.layoutParams = lp
                 imagePlayerPause.setColorFilter(context.resources.getColor(R.color.white))
                 downloadRecordIB.setColorFilter(context.resources.getColor(R.color.white))
                 textTotalDouration.setTextColor(context.resources.getColor(R.color.white))
                 textCurrentTime.setTextColor(context.resources.getColor(R.color.white))
                 timeSeparator.setTextColor(context.resources.getColor(R.color.white))
                 textDate.setTextColor(context.resources.getColor(R.color.white))
                 when (state) {
                     "3" -> {
                         imageSeen.setImageDrawable(context.resources.getDrawable(R.drawable.ic_recive_done_green))
                     }
                     "2" -> {
                         imageSeen.setImageDrawable(context.resources.getDrawable(R.drawable.ic_recive_done))
                     }
                     "1" -> {
                         imageSeen.setImageDrawable(context.resources.getDrawable(R.drawable.ic_send_done))
                     }
                     else -> {
                         imageSeen.setImageDrawable(context.resources.getDrawable(R.drawable.ic_not_send))
                     }
                 }
                 imageSeen.visibility = View.VISIBLE

             } else if (!isMe) {
                 (this as LayoutVoiceViewHolder).imageSeen.visibility = View.GONE
                 contentWithBG.setBackgroundResource(R.drawable.out_message_bg)
                 val layoutParams = contentWithBG.layoutParams as LinearLayout.LayoutParams
                 layoutParams.gravity = Gravity.LEFT
                 contentWithBG.layoutParams = layoutParams
                 imagePlayerPause.setColorFilter(context.resources.getColor(R.color.textColor))
                 downloadRecordIB.setColorFilter(context.resources.getColor(R.color.textColor))
                 textTotalDouration.setTextColor(context.resources.getColor(R.color.textColor))
                 textCurrentTime.setTextColor(context.resources.getColor(R.color.textColor))
                 timeSeparator.setTextColor(context.resources.getColor(R.color.textColor))
                 textDate.setTextColor(context.resources.getColor(R.color.textColor))
                 val lp = content.layoutParams as RelativeLayout.LayoutParams
                 lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
                 lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                 content.layoutParams = lp

             }
         }

         companion object {
             fun from(parent: ViewGroup): LayoutVoiceViewHolder {
                 val layoutTwo: View = LayoutInflater.from(parent.context)
                     .inflate(R.layout.voice_record_item_chat_message, parent, false)
                 return LayoutVoiceViewHolder(layoutTwo)
             }
         }

     }

     class LayoutVideoViewHolder private  constructor (itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val content: LinearLayout = itemView.findViewById(R.id.content)
        val contentWithBG: LinearLayout = itemView.findViewById(R.id.contentWithBackground)
        val imageSeen: ImageView = itemView.findViewById(R.id.iv_state)
        var textDate: TextView = itemView.findViewById(R.id.tv_date)
        var contentVideo: FrameLayout = itemView.findViewById(R.id.frame_video)
        var videoImageButton: ImageButton = itemView.findViewById(R.id.video_image_button)
        var videoImageDownload: ImageButton = itemView.findViewById(R.id.video_image_download)
        var imageVideo: ImageView = itemView.findViewById(R.id.img_video)
        var l = 0
        var adCircleProgress: ProgressBar = itemView.findViewById(R.id.pgb_progress)

        fun bind(
            myMsg: Boolean,
            chatMessage: ChatMessage,
            position: Int,
            context: Activity,
            mCallback: CallbackInterface?
        ) {
            setAlignment(myMsg, chatMessage.state, context)
            textDate.text = TimeProperties.getDate(
                chatMessage.dateTime.toLong(), "hh:mm")
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
                Glide.with(imageVideo.context).load(R.drawable.backgrounblack)
                    .centerCrop()
                    .into(imageVideo)
                if (chatMessage.isDownload) {
                    videoImageDownload.visibility = View.GONE
                    adCircleProgress.visibility = View.VISIBLE
                    videoImageButton.visibility = View.GONE
                } else {
                    videoImageDownload.visibility = View.VISIBLE
                    adCircleProgress.visibility = View.GONE
                    videoImageButton.visibility = View.GONE
                    videoImageDownload.setOnClickListener {
                        videoImageDownload.visibility =
                            View.GONE
                        adCircleProgress.visibility =
                            View.VISIBLE
                        mCallback?.downloadVideo(position, chatMessage, myMsg)
                    }
                }
            } else {
                videoImageDownload.visibility = View.GONE
                val path = FileProvider.getUriForFile(
                    context, BuildConfig.APPLICATION_ID + ".fileprovider", videoFile)
                Glide.with(imageVideo.context).load(path).centerCrop()
                    .into(imageVideo)
                if (chatMessage.upload) {
                    adCircleProgress.visibility = View.VISIBLE
                    videoImageButton.visibility = View.GONE
                } else {
                    adCircleProgress.visibility = View.GONE
                    videoImageButton.visibility = View.VISIBLE
                    videoImageButton.setOnClickListener {
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
                 (this as LayoutVideoViewHolder).contentWithBG.setBackgroundResource(R.drawable.in_message_bg)
                 val layoutParams = contentWithBG.layoutParams as LinearLayout.LayoutParams
                 layoutParams.gravity = Gravity.RIGHT
                 contentWithBG.layoutParams = layoutParams
                 val lp = content.layoutParams as RelativeLayout.LayoutParams
                 lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0)
                 lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                 content.layoutParams = lp
                 textDate.setTextColor(context.resources.getColor(R.color.white))
                 when (state) {
                     "3" -> {
                         imageSeen.setImageDrawable(context.resources.getDrawable(R.drawable.ic_recive_done_green))
                     }
                     "2" -> {
                         imageSeen.setImageDrawable(context.resources.getDrawable(R.drawable.ic_recive_done))
                     }
                     "1" -> {
                         imageSeen.setImageDrawable(context.resources.getDrawable(R.drawable.ic_send_done))
                     }
                     else -> {
                         imageSeen.setImageDrawable(context.resources.getDrawable(R.drawable.ic_not_send))
                     }
                 }
                 imageSeen.visibility = View.VISIBLE
             } else if (!isMe) {
                 (this as LayoutVideoViewHolder).imageSeen.visibility = View.GONE
                 contentWithBG.setBackgroundResource(R.drawable.out_message_bg)
                 val layoutParams = contentWithBG.layoutParams as LinearLayout.LayoutParams
                 layoutParams.gravity = Gravity.LEFT
                 contentWithBG.layoutParams = layoutParams
                 textDate.setTextColor(context.resources.getColor(R.color.textColor))
                 val lp = content.layoutParams as RelativeLayout.LayoutParams
                 lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
                 lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                 content.layoutParams = lp
             }
         }
         companion object {
             fun from(parent: ViewGroup): LayoutVideoViewHolder {
                 val layoutthree: View = LayoutInflater.from(parent.context)
                     .inflate(R.layout.video_item_chat_message, parent, false)
                 return LayoutVideoViewHolder(layoutthree)
             }
         }

     }

    class LayoutPdfViewHolder private  constructor (itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val imageSeen: ImageView = itemView.findViewById(R.id.iv_state)
        var txtDate: TextView = itemView.findViewById(R.id.tv_date)
        var txtFile: TextView = itemView.findViewById(R.id.text_file)
        var imageFile: ImageView = itemView.findViewById(R.id.image_file)
        var fileImageButton: ImageButton = itemView.findViewById(R.id.image_button_file)
        var pdfImage: ImageView = itemView.findViewById(R.id.image_pdf)
        var content: LinearLayout = itemView.findViewById(R.id.content)
        var contentFile: LinearLayout = itemView.findViewById(R.id.liner_file)
        var contentWithBG: LinearLayout = itemView.findViewById(R.id.contentWithBackground)
        var l = 0
        var adCircleProgress: ProgressBar = itemView.findViewById(R.id.pgb_progress)


        fun bind(
            myMsg: Boolean,
            chatMessage: ChatMessage,
            position: Int,
            context: Activity,
            mCallback: CallbackInterface?
        ) {
            var userNameeee = chatMessage.fileName

            setAlignment( myMsg, chatMessage.state, context)
            txtDate.text =
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
            txtFile.text = chatMessage.fileName
            contentFile.setOnClickListener {
                mCallback?.onHandleSelection(position, chatMessage, myMsg)
            }
            if (!pdfFile.exists()) {
                pdfImage.visibility = View.GONE
                if (chatMessage.isDownload) {
                    adCircleProgress.visibility = View.VISIBLE
                    fileImageButton.visibility = View.GONE
                } else {
                    adCircleProgress.visibility = View.GONE
                    fileImageButton.visibility = View.VISIBLE
                    fileImageButton.setOnClickListener {
                        if (mCallback != null) {
                            fileImageButton.visibility =
                                View.GONE
                            adCircleProgress.visibility =
                                View.VISIBLE
                            mCallback.downloadFile(position, chatMessage, myMsg)
                        }
                    }
                }
            } else {
                fileImageButton.visibility = View.GONE
                adCircleProgress.visibility = View.GONE
                val path = FileProvider.getUriForFile(
                    context, BuildConfig.APPLICATION_ID + ".fileprovider", pdfFile)
                val bmp = ImageProperties.getImageFromPdf(path, context)
                println(bmp)
                pdfImage.setImageBitmap(bmp)
                if (bmp != null) {
                    pdfImage.visibility = View.VISIBLE
                } else {
                    pdfImage.visibility = View.GONE
                }
                if (chatMessage.upload) {
                    adCircleProgress.visibility = View.VISIBLE
                } else {
                    adCircleProgress.visibility = View.GONE
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
                contentWithBG.setBackgroundResource(R.drawable.in_message_bg)
                val layoutParams = contentWithBG.layoutParams as LinearLayout.LayoutParams
                layoutParams.gravity = Gravity.RIGHT
                contentWithBG.layoutParams = layoutParams
                val lp = content.layoutParams as RelativeLayout.LayoutParams
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0)
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                content.layoutParams = lp
                layoutParams.gravity = Gravity.RIGHT
                txtDate.setTextColor(context.resources.getColor(R.color.white))
                when (state) {
                    "3" -> {
                        imageSeen.setImageDrawable(context.resources.getDrawable(R.drawable.ic_recive_done_green))
                    }
                    "2" -> {
                        imageSeen.setImageDrawable(context.resources.getDrawable(R.drawable.ic_recive_done))
                    }
                    "1" -> {
                        imageSeen.setImageDrawable(context.resources.getDrawable(R.drawable.ic_send_done))
                    }
                    else -> {
                        imageSeen.setImageDrawable(context.resources.getDrawable(R.drawable.ic_not_send))
                    }
                }
                imageSeen.visibility = View.VISIBLE
            } else if (!isMe) {
                imageSeen.visibility = View.GONE
                contentWithBG.setBackgroundResource(R.drawable.out_message_bg)
                val layoutParams = contentWithBG.layoutParams as LinearLayout.LayoutParams
                layoutParams.gravity = Gravity.LEFT
                contentWithBG.layoutParams = layoutParams
                txtDate.setTextColor(context.resources.getColor(R.color.textColor))
                val lp = content.layoutParams as RelativeLayout.LayoutParams
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                content.layoutParams = lp
            }
        }
        companion object {
            fun from(parent: ViewGroup): LayoutPdfViewHolder {
                val layoutFour: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.pdf_item_chat_message, parent, false)
                return LayoutPdfViewHolder(layoutFour)
            }
        }


    }

    class LayoutContactViewHolder private  constructor (itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val imageSeen: ImageView = itemView.findViewById(R.id.iv_state)
        var txtDate: TextView = itemView.findViewById(R.id.tv_date)
        var imageProfile: CircleImageView = itemView.findViewById(R.id.profile)
        var content: LinearLayout = itemView.findViewById(R.id.content)
        var contentWithBG: CardView = itemView.findViewById(R.id.card)
        var txtName: TextView = itemView.findViewById(R.id.name)
        var txtNumber: TextView = itemView.findViewById(R.id.phone)
        var addContact: TextView = itemView.findViewById(R.id.addContact)
        var view1: View = itemView.findViewById(R.id.view1)
        var view2: View? = null


         fun bind(
            myMsg: Boolean,
            chatMessage: ChatMessage,
            context: Activity
        ) {
            setAlignment( myMsg, chatMessage.state, context)
            (this as LayoutContactViewHolder).txtDate.text = TimeProperties.getDate(
                chatMessage.dateTime.toLong(), "hh:mm")
            txtNumber.text = chatMessage.message
            txtName.text = chatMessage.fileName
            addContact.setOnClickListener {
                val builder = AlertDialog.Builder(
                    context)
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
                        intent.putExtra(ContactsContract.Intents.Insert.NAME,
                            nameAddContact.text.toString())
                        intent.putExtra(ContactsContract.Intents.Insert.PHONE,
                            numberAddContact.text.toString())
                        context.startActivity(intent)
                        dialogadd.cancel()
                    } else {
                        Toast.makeText(context,
                            "please fill all the fields",
                            Toast.LENGTH_SHORT).show()
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
                contentWithBG.setBackgroundResource(R.drawable.in_message_bg)
                val layoutParams = contentWithBG.layoutParams as LinearLayout.LayoutParams
                layoutParams.gravity = Gravity.RIGHT
                contentWithBG.layoutParams = layoutParams
                val lp = content.layoutParams as RelativeLayout.LayoutParams
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0)
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                content.layoutParams = lp
                layoutParams.gravity = Gravity.RIGHT
                txtDate.setTextColor(context.resources.getColor(R.color.white))
                addContact.setTextColor(context.resources.getColor(R.color.white))
                txtName.setTextColor(context.resources.getColor(R.color.white))
                txtNumber.setTextColor(context.resources.getColor(R.color.white))
                when (state) {
                    "3" -> {
                        imageSeen.setImageDrawable(context.resources.getDrawable(R.drawable.ic_recive_done_green))
                    }
                    "2" -> {
                        imageSeen.setImageDrawable(context.resources.getDrawable(R.drawable.ic_recive_done))
                    }
                    "1" -> {
                        imageSeen.setImageDrawable(context.resources.getDrawable(R.drawable.ic_send_done))
                    }
                    else -> {
                        imageSeen.setImageDrawable(context.resources.getDrawable(R.drawable.ic_not_send))
                    }
                }
                imageSeen.visibility = View.VISIBLE
            } else if (!isMe) {
                imageSeen.visibility = View.GONE
                contentWithBG.setBackgroundResource(R.drawable.out_message_bg)
                val layoutParams = contentWithBG.layoutParams as LinearLayout.LayoutParams
                layoutParams.gravity = Gravity.LEFT
                contentWithBG.layoutParams = layoutParams
                txtDate.setTextColor(context.resources.getColor(R.color.textColor))
                addContact.setTextColor(context.resources.getColor(R.color.textColor))
                txtName.setTextColor(context.resources.getColor(R.color.textColor))
                txtNumber.setTextColor(context.resources.getColor(R.color.textColor))
                val lp = content.layoutParams as RelativeLayout.LayoutParams
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                content.layoutParams = lp
            }
        }

        companion object {
            fun from(parent: ViewGroup): LayoutContactViewHolder {
                val layoutFive: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.contact_item_chat_message, parent, false)
                return LayoutContactViewHolder(layoutFive)
            }
        }





    }

    class LayoutLocationViewHolder private  constructor  (itemView: View)  :
        RecyclerView.ViewHolder(itemView) {
        val imageSeen: ImageView = itemView.findViewById(R.id.iv_state)
        var txtDate: TextView = itemView.findViewById(R.id.tv_date)
        var content: LinearLayout = itemView.findViewById(R.id.content)
        var contentWithBG: LinearLayout = itemView.findViewById(R.id.contentWithBackground)
        var cardOpenLocation: CardView = itemView.findViewById(R.id.cardOpenItLocation)

        fun bind(
            myMsg: Boolean,
            chatMessage: ChatMessage,
            position: Int,
            context: Activity,
            mCallback: CallbackInterface?
        ) {
            setAlignment( myMsg, chatMessage.state, context )
            txtDate.text = TimeProperties.getDate(
                chatMessage.dateTime.toLong(), "hh:mm")
            cardOpenLocation.setOnClickListener {
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
                contentWithBG.setBackgroundResource(R.drawable.in_message_bg)
                val layoutParams = contentWithBG.layoutParams as LinearLayout.LayoutParams
                layoutParams.gravity = Gravity.RIGHT
                contentWithBG.layoutParams = layoutParams
                val lp = content.layoutParams as RelativeLayout.LayoutParams
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0)
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                content.layoutParams = lp
                layoutParams.gravity = Gravity.RIGHT
                txtDate.setTextColor(context.resources.getColor(R.color.white))
                when (state) {
                    "3" -> {
                        imageSeen.setImageDrawable(context.resources.getDrawable(R.drawable.ic_recive_done_green))
                    }
                    "2" -> {
                        imageSeen.setImageDrawable(context.resources.getDrawable(R.drawable.ic_recive_done))
                    }
                    "1" -> {
                        imageSeen.setImageDrawable(context.resources.getDrawable(R.drawable.ic_send_done))
                    }
                    else -> {
                        imageSeen.setImageDrawable(context.resources.getDrawable(R.drawable.ic_not_send))
                    }
                }
                imageSeen.visibility = View.VISIBLE
            } else if (!isMe) {
                imageSeen.visibility = View.GONE
                contentWithBG.setBackgroundResource(R.drawable.out_message_bg)
                val layoutParams = contentWithBG.layoutParams as LinearLayout.LayoutParams
                layoutParams.gravity = Gravity.LEFT
                contentWithBG.layoutParams = layoutParams
                //                    ((LayoutLocationViewHolder) holder).txtDate.setTextColor(context.getResources().getColor(R.color.textColor));
                txtDate.setTextColor(context.resources.getColor(R.color.textColor))
                val lp = content.layoutParams as RelativeLayout.LayoutParams
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                content.layoutParams = lp
            }
        }
        companion object {
            fun from(parent: ViewGroup): LayoutLocationViewHolder {
                val layoutSex: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.location_item_chat_message, parent, false)
                return LayoutLocationViewHolder(layoutSex)
            }
        }


    }

    class LayoutTextViewHolder private  constructor  (itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtMessage: TextView = itemView.findViewById(R.id.txtMessage)
        val imageSeen: ImageView = itemView.findViewById(R.id.iv_state)
        var txtDate: TextView = itemView.findViewById(R.id.tv_date)
        var txtUpdate: TextView = itemView.findViewById(R.id.text_update)
        var content: LinearLayout = itemView.findViewById(R.id.content)
        var contentWithBG: RelativeLayout = itemView.findViewById(R.id.contentWithBackground)


        fun bind(
            myMsg: Boolean,
            chatMessage: ChatMessage,
            context: Activity
        ) {
            setAlignment( myMsg, chatMessage.state, context)
            txtDate.text =
                TimeProperties.getDate(chatMessage.dateTime.toLong(), "hh:mm")
            if (chatMessage.isUpdate == "1") {
                txtUpdate.visibility = View.VISIBLE
            } else {
                txtUpdate.visibility = View.GONE
            }
            txtMessage.text = chatMessage.message
            Linkify.addLinks(txtMessage, Linkify.WEB_URLS)
            txtMessage.setLinkTextColor(ContextCompat.getColor(
                context,
                R.color.blue_200))
            txtMessage.visibility = View.VISIBLE
        }


        @SuppressLint("RtlHardcoded", "UseCompatLoadingForDrawables")
        fun setAlignment(
            isMe: Boolean,
            state: String,
            context: Activity
        ) {
            if (isMe) {
                contentWithBG.setBackgroundResource(R.drawable.in_message_bg)
                var layoutParams = contentWithBG.layoutParams as LinearLayout.LayoutParams
                layoutParams.gravity = Gravity.RIGHT
                contentWithBG.layoutParams = layoutParams
                val lp = content.layoutParams as RelativeLayout.LayoutParams
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0)
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                content.layoutParams = lp
                layoutParams = txtMessage.layoutParams as LinearLayout.LayoutParams
                layoutParams.gravity = Gravity.RIGHT
                txtMessage.layoutParams = layoutParams
                txtMessage.setTextColor(context.resources.getColor(R.color.white))
                txtDate.setTextColor(context.resources.getColor(R.color.white))
                when (state) {
                    "3" -> {
                        imageSeen.setImageDrawable(context.resources.getDrawable(R.drawable.ic_recive_done_green))
                    }
                    "2" -> {
                        imageSeen.setImageDrawable(context.resources.getDrawable(R.drawable.ic_recive_done))
                    }
                    "1" -> {
                        imageSeen.setImageDrawable(context.resources.getDrawable(R.drawable.ic_send_done))
                    }
                    else -> {
                        imageSeen.setImageDrawable(context.resources.getDrawable(R.drawable.ic_not_send))
                    }
                }
                imageSeen.visibility = View.VISIBLE

            } else if (!isMe) {
               imageSeen.visibility = View.GONE
                contentWithBG.setBackgroundResource(R.drawable.out_message_bg)
                var layoutParams = contentWithBG.layoutParams as LinearLayout.LayoutParams
                layoutParams.gravity = Gravity.LEFT
                contentWithBG.layoutParams = layoutParams
                txtMessage.setTextColor(context.resources.getColor(R.color.textColor))
                txtDate.setTextColor(context.resources.getColor(R.color.textColor))
                val lp = content.layoutParams as RelativeLayout.LayoutParams
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                content.layoutParams = lp
                layoutParams = txtMessage.layoutParams as LinearLayout.LayoutParams
                layoutParams.gravity = Gravity.LEFT
                txtMessage.layoutParams = layoutParams

            }
        }
        companion object {
            fun from(parent: ViewGroup): LayoutTextViewHolder {
                val layoutSeven: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_chat_message, parent, false)
                return LayoutTextViewHolder(layoutSeven)
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

