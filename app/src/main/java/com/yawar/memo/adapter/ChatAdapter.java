package com.yawar.memo.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;


import com.app.adprogressbarlib.AdCircleProgress;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.yawar.memo.BuildConfig;
import com.yawar.memo.R;
import com.yawar.memo.model.ChatMessage;
import com.yawar.memo.utils.ImageProperties;
import com.yawar.memo.utils.TimeProperties;


import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatAdapter  extends ListAdapter<ChatMessage,RecyclerView.ViewHolder> {
    private final Activity context;

    private ChatAdapter.CallbackInterface mCallback;

      public  String userNameeee ;

    public interface CallbackInterface {


        void onHandleSelection(int position, ChatMessage groupSelectorRespone, boolean myMessage);

        void downloadFile(int position, ChatMessage chatMessage, boolean myMessage);

        void downloadVoice(int position, ChatMessage chatMessage, boolean myMessage);

        void downloadVideo(int position, ChatMessage chatMessage, boolean myMessage);

        void downloadImage(int position, ChatMessage chatMessage, boolean myMessage);

        void onClickLocation(int position, ChatMessage chatMessage, boolean myMessage);


        void onLongClick(int position, ChatMessage chatMessage, boolean isChecked);

        void playVideo(Uri path);


    }


    //    public ChatAdapter(Activity context, List<ChatMessage> chatMessages) {
    public ChatAdapter(Activity context) {
        super(new MyDiffUtilChatMessage());

        this.context = context;
//        this.chatMessages = chatMessages;
        try {

            mCallback = (ChatAdapter.CallbackInterface) context;

        } catch (ClassCastException ex) {

            //.. should log the error or throw and exception

        }

    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage chatMessage = getItem(position);


        switch (chatMessage.getType()) {
            case "imageWeb":
                return 0;
            case "voice":
                return 1;
            case "video":
                return 2;
            case "file":
                return 3;
            case "contact":
                return 4;
            case "location":
                return 5;
            default:
                return 6;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        {
            switch (viewType) {
                case 0:
                    View layoutOne
                            = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.image_item_chat_meesage, parent, false);
                    return new LayoutImageViewHolder(layoutOne);
                case 1:
                    View layoutTwo
                            = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.voice_record_item_chat_message, parent, false);
                    return new ChatAdapter.LayoutVoiceViewHolder(layoutTwo);
                case 2:
                    View layoutthree
                            = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.video_item_chat_message, parent, false);
                    return new ChatAdapter.LayoutVideoViewHolder(layoutthree);
                case 3:
                    View layoutFour
                            = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.pdf_item_chat_message, parent, false);
                    return new ChatAdapter.LayoutPdfViewHolder(layoutFour);
                case 4:
                    View layoutFive
                            = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.contact_item_chat_message, parent, false);
                    return new LayoutContactViewHolder(layoutFive);
                case 5:
                    View layoutSex
                            = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.location_item_chat_message, parent, false);
                    return new LayoutLocationViewHolder(layoutSex);
                case 6:
                    View layoutSeven
                            = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_item_chat_message, parent, false);
                    return new LayoutTextViewHolder(layoutSeven);
                default:
                    return null;
            }
        }

//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_message, parent, false);
//        ChatAdapter.ViewHolder holder = new ChatAdapter.ViewHolder(v);
//        return holder;

    }
    float prex;
    boolean isdraged;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

//        ChatMessage chatMessage = chatMessages.get(position);
        ChatMessage chatMessage = getItem(position);

        boolean myMsg = chatMessage.isMe();
        TimeProperties timeProperties = new TimeProperties();


        userNameeee = chatMessage.getFileName();

//        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent e) {
//                float x = e.getX();
//                float y = e.getY();
//                switch (e.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        isdraged = true;
//                        if((prex>x || prex<x) &&  (prex-x >= 5 || prex-x <= 5)) {
//                            try {
//                                ConversationActivity.close.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        ConversationActivity.cardview.setVisibility(View.GONE);
//
//                                    }
//                                });
//
//
//                                ConversationActivity.reply.setText( chatMessage.message.toString());
//                                ConversationActivity.username.setText(chatMessage.getFileName().toString());
//
//
//                            } catch (Exception exception) {
//                                exception.printStackTrace();
//                            }
//                            ConversationActivity.reply.setVisibility(View.VISIBLE);
//                            ConversationActivity.username.setVisibility(View.VISIBLE);
//                            ConversationActivity.cardview.setVisibility(View.VISIBLE);
//                            ConversationActivity.close.setVisibility(View.VISIBLE);
//
//                            return true;
//                        }else{
//
//                        }
//
//                    case MotionEvent.ACTION_UP:
//                        isdraged = false;
//                        break;
//                }
//
//                prex=x;
//                return false;
//            }
//        });

        switch (chatMessage.getType()) {
            case "imageWeb":
                setAlignment(holder, myMsg, chatMessage.getState(), chatMessage.getType());
                ((LayoutImageViewHolder) holder).txtDate.setText(TimeProperties.getDate(Long.parseLong(chatMessage.getDateTime()), "hh:mm"));

                File imageFile;
                if (myMsg) {

                    File d = context.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send/video");  // -> filename = maven.pdf
                    imageFile = new File(d, chatMessage.getFileName());

                } else {
                    File d = context.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/recive/video");  // -> filename = maven.pdf

                    imageFile = new File(d, chatMessage.getImage());

                }
                if (!imageFile.exists()) {
                    ((LayoutImageViewHolder) holder).imageView.setEnabled(false);


                    Glide.with(((LayoutImageViewHolder) holder).imageView.getContext()).load(R.drawable.backgrounblack).centerCrop()
                            .into(((LayoutImageViewHolder) holder).imageView);
                    if (chatMessage.isDownload()) {
                        ((LayoutImageViewHolder) holder).downloadImage.setVisibility(View.GONE);
                        ((LayoutImageViewHolder) holder).adCircleProgress.setVisibility(View.VISIBLE);
                    } else {

                        ((LayoutImageViewHolder) holder).downloadImage.setVisibility(View.VISIBLE);
                        ((LayoutImageViewHolder) holder).adCircleProgress.setVisibility(View.GONE);

                        ((LayoutImageViewHolder) holder).downloadImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                ((LayoutImageViewHolder) holder).downloadImage.setVisibility(View.GONE);
                                ((LayoutImageViewHolder) holder).adCircleProgress.setVisibility(View.VISIBLE);

//                                final Timer t = new Timer();
//                                t.scheduleAtFixedRate(new TimerTask() {
//                                    public void run() {
//                                        context.runOnUiThread(new Runnable() {
//                                            public void run() {
//
//
//                                                ((LayoutImageViewHolder) holder).adCircleProgress.setAdProgress(((LayoutImageViewHolder) holder).l);
//
//                                                ((LayoutImageViewHolder) holder).l++;
//                                            }
//                                        });
//                                    }
//                                }, 0, 100);
                                if (mCallback != null) {
                                    mCallback.downloadImage(position, chatMessage, myMsg);
                                }
                            }
                        });
                    }
                } else {
                    ((LayoutImageViewHolder) holder).imageView.setEnabled(true);

                    ((LayoutImageViewHolder) holder).downloadImage.setVisibility(View.GONE);
                    Uri path = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", imageFile);

                    Glide.with(((LayoutImageViewHolder) holder).imageView.getContext()).load(path).centerCrop()
                            .into(((LayoutImageViewHolder) holder).imageView);

                    if (chatMessage.getUpload()) {
                        ((LayoutImageViewHolder) holder).adCircleProgress.setVisibility(View.VISIBLE);

                    } else {
                        ((LayoutImageViewHolder) holder).adCircleProgress.setVisibility(View.GONE);
                        ((LayoutImageViewHolder) holder).imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
//                        androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder(view.getContext());
//                        View mView = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_image_cht, null);
//                        ImageView photoView =  mView.findViewById(R.id.photo_view);
////                        Glide.with(photoView.getContext()).load("https://th.bing.com/th/id/OIP.DP48uGkldTg01Wx5KTExXAHaE6?pid=ImgDet&rs=1").into(photoView);
//                        photoView.setImageResource(R.drawable.ic_send_done);
//                        mBuilder.setView(mView);
////                        androidx.appcompat.app.AlertDialog mDialog = mBuilder.create();
//                        mBuilder.show();
                                Dialog dialog = new Dialog(context);
                                dialog.setContentView(R.layout.dialog_image_cht);
                                dialog.setTitle("Title...");

                                // set the custom dialog components - text, image and button
                                dialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);


                                PhotoView image = dialog.findViewById(R.id.photo_view);
                                Glide.with(image.getContext()).load(path).centerCrop().into(image);
                                dialog.show();

                            }
                        });


                    }
                }

                break;

            case "voice":
                System.out.println("case voiceeeeee");
                setAlignment(holder, myMsg, chatMessage.getState(), chatMessage.getType());

                ((LayoutVoiceViewHolder) holder).contentRecord.setVisibility(View.VISIBLE);
                ((LayoutVoiceViewHolder) holder).textDate.setText(TimeProperties.getDate(Long.parseLong(chatMessage.getDateTime()), "hh:mm"));


                ((LayoutVoiceViewHolder) holder).mediaPlayer = new MediaPlayer();
                ((LayoutVoiceViewHolder) holder).playerSeekBar.setMax(100);

                File voiceFile;

                if (myMsg) {
                    File d = context.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send/voiceRecord");  // -> filename = maven.pdf

                    voiceFile = new File(d, chatMessage.getFileName());

                } else {
                    File d = context.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/recive/voiceRecord");  // -> filename = maven.pdf
                    voiceFile = new File(d, chatMessage.getMessage());

                }
                if (!voiceFile.exists()) {
                    System.out.println("!voiceFile.exists()");
                    ((LayoutVoiceViewHolder) holder).imagePlayerPause.setVisibility(View.GONE);
                    ((LayoutVoiceViewHolder) holder).playerSeekBar.setProgress(0);
                    ((LayoutVoiceViewHolder) holder).playerSeekBar.setEnabled(false);


                    ((LayoutVoiceViewHolder) holder).textCurrentTime.setText("0.00");
                    ((LayoutVoiceViewHolder) holder).textTotalDouration.setText("0.00");
                    if (chatMessage.isDownload()) {

                        ((LayoutVoiceViewHolder) holder).downloadRecordIB.setVisibility(View.GONE);
                        ((LayoutVoiceViewHolder) holder).adCircleProgress.setVisibility(View.VISIBLE);

                    } else {
                        System.out.println("voiceFile.exists()");


                        ((LayoutVoiceViewHolder) holder).downloadRecordIB.setVisibility(View.VISIBLE);
                        ((LayoutVoiceViewHolder) holder).adCircleProgress.setVisibility(View.GONE);
                        ((LayoutVoiceViewHolder) holder).downloadRecordIB.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (mCallback != null) {
                                    ((LayoutVoiceViewHolder) holder).downloadRecordIB.setVisibility(View.GONE);
                                    ((LayoutVoiceViewHolder) holder).adCircleProgress.setVisibility(View.VISIBLE);

                                    if (mCallback != null)
                                        mCallback.downloadVoice(position, chatMessage, myMsg);
                                }
                            }
                        });
                    }


                    System.out.println(chatMessage.getFileName() + "name");
                } else {

                    if (!((LayoutVoiceViewHolder) holder).mediaPlayer.isPlaying()) {
                        ((LayoutVoiceViewHolder) holder).playerSeekBar.setEnabled(true);
                        ((LayoutVoiceViewHolder) holder).playerSeekBar.setProgress(0);
                        ((LayoutVoiceViewHolder) holder).textCurrentTime.setText("0.00");
                        ((LayoutVoiceViewHolder) holder).textTotalDouration.setText("0.00");

                        ((LayoutVoiceViewHolder) holder).imagePlayerPause.setImageResource(R.drawable.ic_play_audio);
                        ((LayoutVoiceViewHolder) holder).downloadRecordIB.setVisibility(View.GONE);

                        ((LayoutVoiceViewHolder) holder).imagePlayerPause.setVisibility(View.VISIBLE);
                    } else {
                        System.out.println("is playing" + chatMessage.getMessage());
                    }

                    try {
                        ((LayoutVoiceViewHolder) holder).mediaPlayer.setDataSource(voiceFile.getAbsolutePath());
                        ((LayoutVoiceViewHolder) holder).mediaPlayer.prepare();
                        ((LayoutVoiceViewHolder) holder).textTotalDouration.setText(milliSecondsToTimer((long) ((LayoutVoiceViewHolder) holder).mediaPlayer.getDuration()));
                    } catch (Exception exceptione) {
//                        Toast.makeText(context, exceptione.getMessage(), Toast.LENGTH_SHORT).show();
                    }


                    if (chatMessage.getUpload()) {
                        ((LayoutVoiceViewHolder) holder).adCircleProgress.setVisibility(View.VISIBLE);
                        ((LayoutVoiceViewHolder) holder).imagePlayerPause.setVisibility(View.GONE);


                    } else {

                        ((LayoutVoiceViewHolder) holder).adCircleProgress.setVisibility(View.GONE);
                        ((LayoutVoiceViewHolder) holder).imagePlayerPause.setVisibility(View.VISIBLE);


//                        holder.downloadRecordIB.setVisibility(View.GONE);
//                        holder.imagePlayerPause.setVisibility(View.VISIBLE);

                    }
                }
                ////////////////media player tools
                ((LayoutVoiceViewHolder) holder).playerSeekBar.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        SeekBar seekBar = (SeekBar) view;
                        int payPosition = (((LayoutVoiceViewHolder) holder).mediaPlayer.getDuration() / 100) * seekBar.getProgress();
                        ((LayoutVoiceViewHolder) holder).mediaPlayer.seekTo(payPosition);
                        ((LayoutVoiceViewHolder) holder).textCurrentTime.setText(milliSecondsToTimer((long) ((LayoutVoiceViewHolder) holder).mediaPlayer.getCurrentPosition()));
                        return false;
                    }
                });
                ((LayoutVoiceViewHolder) holder).mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                        ((LayoutVoiceViewHolder) holder).playerSeekBar.setSecondaryProgress(i);
                    }

                });
                ((LayoutVoiceViewHolder) holder).mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        ((LayoutVoiceViewHolder) holder).playerSeekBar.setProgress(0);
                        ((LayoutVoiceViewHolder) holder).imagePlayerPause.setImageResource(R.drawable.ic_play_audio);
                        ((LayoutVoiceViewHolder) holder).textCurrentTime.setText("0.00");
                        mediaPlayer.reset();
                        try {
                            ((LayoutVoiceViewHolder) holder).mediaPlayer.setDataSource(voiceFile.getAbsolutePath());
                            ((LayoutVoiceViewHolder) holder).mediaPlayer.prepare();
                            ((LayoutVoiceViewHolder) holder).textTotalDouration.setText(milliSecondsToTimer((long) ((LayoutVoiceViewHolder) holder).mediaPlayer.getDuration()));
                        } catch (Exception exceptione) {
                            Toast.makeText(context, exceptione.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        //prepeareMediaPlayer();
                    }
                });

                ((LayoutVoiceViewHolder) holder).updater = new Runnable() {
                    @Override
                    public void run() {
                        if (((LayoutVoiceViewHolder) holder).mediaPlayer.isPlaying()) {
                            ((LayoutVoiceViewHolder) holder).playerSeekBar.setProgress((int) (((float) ((LayoutVoiceViewHolder) holder).mediaPlayer.getCurrentPosition() / ((LayoutVoiceViewHolder) holder).mediaPlayer.getDuration() * 100)));
                            ((LayoutVoiceViewHolder) holder).handler.postDelayed(((LayoutVoiceViewHolder) holder).updater, 1000);
                        }
                        long currentDuration = ((LayoutVoiceViewHolder) holder).mediaPlayer.getCurrentPosition();
                        ((LayoutVoiceViewHolder) holder).textCurrentTime.setText(milliSecondsToTimer(currentDuration));
                    }
                };
                ((LayoutVoiceViewHolder) holder).imagePlayerPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (((LayoutVoiceViewHolder) holder).mediaPlayer.isPlaying()) {
                            ((LayoutVoiceViewHolder) holder).handler.removeCallbacks(((LayoutVoiceViewHolder) holder).updater);
                            ((LayoutVoiceViewHolder) holder).mediaPlayer.pause();
                            ((LayoutVoiceViewHolder) holder).imagePlayerPause.setImageResource(R.drawable.ic_play_audio);

                        } else {
                            ((LayoutVoiceViewHolder) holder).mediaPlayer.start();
                            ((LayoutVoiceViewHolder) holder).imagePlayerPause.setImageResource(R.drawable.ic_pause);
                            if (((LayoutVoiceViewHolder) holder).mediaPlayer.isPlaying()) {
                                ((LayoutVoiceViewHolder) holder).playerSeekBar.setProgress((int) (((float) ((LayoutVoiceViewHolder) holder).mediaPlayer.getCurrentPosition() / ((LayoutVoiceViewHolder) holder).mediaPlayer.getDuration() * 100)));
                                ((LayoutVoiceViewHolder) holder).handler.postDelayed(((LayoutVoiceViewHolder) holder).updater, 1000);
                            }
                        }
                    }
                });

                break;
            //// voice end
            case "video":
                setAlignment(holder, myMsg, chatMessage.getState(), chatMessage.getType());
                ((LayoutVideoViewHolder) holder).textDate.setText(TimeProperties.getDate(Long.parseLong(chatMessage.getDateTime()), "hh:mm"));

                File videoFile;
                if (myMsg) {

                    File d = context.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send/video");  // -> filename = maven.pdf
                    videoFile = new File(d, chatMessage.getFileName());

                } else {
                    File d = context.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/recive/video");  // -> filename = maven.pdf
                    videoFile = new File(d, chatMessage.getMessage());

                }


                if (!videoFile.exists()) {
//                    ((LayoutVideoViewHolder) holder).adCircleProgress.setVisibility(View.GONE);

                    Glide.with(((LayoutVideoViewHolder) holder).imageVideo.getContext()).load(R.drawable.backgrounblack).centerCrop()
                            .into(((LayoutVideoViewHolder) holder).imageVideo);
                    if (chatMessage.isDownload()) {
                        ((LayoutVideoViewHolder) holder).videoImageDownload.setVisibility(View.GONE);
                        ((LayoutVideoViewHolder) holder).adCircleProgress.setVisibility(View.VISIBLE);
                        ((LayoutVideoViewHolder) holder).videoImageButton.setVisibility(View.GONE);

                    } else {

                        ((LayoutVideoViewHolder) holder).videoImageDownload.setVisibility(View.VISIBLE);
                        ((LayoutVideoViewHolder) holder).adCircleProgress.setVisibility(View.GONE);
                        ((LayoutVideoViewHolder) holder).videoImageButton.setVisibility(View.GONE);
                        ((LayoutVideoViewHolder) holder).videoImageDownload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ((LayoutVideoViewHolder) holder).videoImageDownload.setVisibility(View.GONE);
                                ((LayoutVideoViewHolder) holder).adCircleProgress.setVisibility(View.VISIBLE);

                                if (mCallback != null) {
                                    mCallback.downloadVideo(position, chatMessage, myMsg);
                                }
                            }

                        });
                    }
                } else {
                    ((LayoutVideoViewHolder) holder).videoImageDownload.setVisibility(View.GONE);
                    Uri path = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", videoFile);

                    Glide.with(((LayoutVideoViewHolder) holder).imageVideo.getContext()).load(path).centerCrop()
                            .into(((LayoutVideoViewHolder) holder).imageVideo);

                    if (chatMessage.getUpload()) {
                        ((LayoutVideoViewHolder) holder).adCircleProgress.setVisibility(View.VISIBLE);
                        ((LayoutVideoViewHolder) holder).videoImageButton.setVisibility(View.GONE);

                    } else {

                        ((LayoutVideoViewHolder) holder).adCircleProgress.setVisibility(View.GONE);
                        ((LayoutVideoViewHolder) holder).videoImageButton.setVisibility(View.VISIBLE);


                        ((LayoutVideoViewHolder) holder).videoImageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (mCallback != null) {
                                    mCallback.playVideo(path);
                                }


                            }
                        });
                    }
                }

                break;
            ////  video end
            ///////////////////////
            //// file begin
            case "file":
                setAlignment(holder, myMsg, chatMessage.getState(), chatMessage.getType());
                ((LayoutPdfViewHolder) holder).txtDate.setText(TimeProperties.getDate(Long.parseLong(chatMessage.getDateTime()), "hh:mm"));


                File pdfFile;
                if (myMsg) {
                    File d = context.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/send");  // -> filename = maven.pdf
                    pdfFile = new File(d, chatMessage.getFileName());

                    userNameeee = chatMessage.getFileName();

                } else {
                    File d = context.getExternalFilesDir(Environment.DIRECTORY_DCIM + File.separator + "memo/recive");  // -> filename = maven.pdf
                    pdfFile = new File(d, chatMessage.getMessage());

                }

                System.out.println(chatMessage.getMessage());
                ((LayoutPdfViewHolder) holder).txtFile.setText(chatMessage.getFileName());


                ((LayoutPdfViewHolder) holder).contentFile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mCallback != null) {
                            mCallback.onHandleSelection(position, chatMessage, myMsg);
                        }
                    }
                });

//                ////////////////
                if (!pdfFile.exists()) {
                    ((LayoutPdfViewHolder) holder).pdfImage.setVisibility(View.GONE);
                    if (chatMessage.isDownload()) {
                        ((LayoutPdfViewHolder) holder).adCircleProgress.setVisibility(View.VISIBLE);
                        ((LayoutPdfViewHolder) holder).fileImageButton.setVisibility(View.GONE);
                    } else {


                        ((LayoutPdfViewHolder) holder).adCircleProgress.setVisibility(View.GONE);

                        ((LayoutPdfViewHolder) holder).fileImageButton.setVisibility(View.VISIBLE);
                        ((LayoutPdfViewHolder) holder).fileImageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (mCallback != null) {

                                    ((LayoutPdfViewHolder) holder).fileImageButton.setVisibility(View.GONE);
                                    ((LayoutPdfViewHolder) holder).adCircleProgress.setVisibility(View.VISIBLE);

                                    if (mCallback != null) {

                                        mCallback.downloadFile(position, chatMessage, myMsg);
                                    }
                                }
                            }
                        });
                    }
                } else {
                    ((LayoutPdfViewHolder) holder).fileImageButton.setVisibility(View.GONE);
                    ((LayoutPdfViewHolder) holder).adCircleProgress.setVisibility(View.GONE);

                    Uri path = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", pdfFile);
                    Bitmap bmp = ImageProperties.getImageFromPdf(path, context);
                    System.out.println(bmp);
                    ((LayoutPdfViewHolder) holder).pdfImage.setImageBitmap(bmp);
                    if (bmp != null) {
                        ((LayoutPdfViewHolder) holder).pdfImage.setVisibility(View.VISIBLE);

                    } else {

                        ((LayoutPdfViewHolder) holder).pdfImage.setVisibility(View.GONE);


                    }

                    if (chatMessage.getUpload()) {
                        ((LayoutPdfViewHolder) holder).adCircleProgress.setVisibility(View.VISIBLE);

                    }
                    else {
                        ((LayoutPdfViewHolder) holder).adCircleProgress.setVisibility(View.GONE);


                }
        }

                break;
            case "contact":
                setAlignment(holder, myMsg, chatMessage.getState(), chatMessage.getType());

                ((LayoutContactViewHolder) holder).txtDate.setText(TimeProperties.getDate(Long.parseLong(chatMessage.getDateTime()), "hh:mm"));

                ((LayoutContactViewHolder) holder).txtNumber.setText(chatMessage.getMessage());

                ((LayoutContactViewHolder) holder).txtName.setText(chatMessage.getFileName());
                ((LayoutContactViewHolder) holder).addContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        final LayoutInflater inflater = LayoutInflater.from(context);
                        final View dialogView = inflater.inflate(R.layout.add_contact_dialog, null);
                        builder.setView(dialogView);
                        final AlertDialog dialogadd = builder.create();


                        float textSize = 14.0F;
                        SharedPreferences sharedPreferences;
                        sharedPreferences = context.getSharedPreferences("txtFontSize", Context.MODE_PRIVATE);

                        final TextView name = dialogView.findViewById(R.id.name);
                        name.setTextSize(textSize);
                        name.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));


                        final TextView nameAddContact = dialogView.findViewById(R.id.nameAddContact);
                        nameAddContact.setTextSize(textSize);
                        nameAddContact.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));
                        nameAddContact.setText(chatMessage.getFileName());

                        final TextView numberAddContact = dialogView.findViewById(R.id.numberAddContact);
                        numberAddContact.setTextSize(textSize);
                        numberAddContact.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));
                        numberAddContact.setText(chatMessage.getMessage());

                        Button btnAddContact = dialogView.findViewById(R.id.btnAddContact);
                        btnAddContact.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!nameAddContact.getText().toString().isEmpty() && !numberAddContact.getText().toString().isEmpty()) {
                                    Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
                                    intent.setType(ContactsContract.RawContacts.CONTENT_ITEM_TYPE);
                                    intent.putExtra(ContactsContract.Intents.Insert.NAME, nameAddContact.getText().toString());
                                    intent.putExtra(ContactsContract.Intents.Insert.PHONE, numberAddContact.getText().toString());
                                    context.startActivity(intent);
                                    dialogadd.cancel();
                  /*          if (intent.resolveActivity(getPackageManager()) != null){

                            }
                            else {
                                Toast.makeText(MainActivity.this, "There is no app that support this action", Toast.LENGTH_SHORT).show();

                            }*/

                                } else {

                                    Toast.makeText(context, "please fill all the fields", Toast.LENGTH_SHORT).show();
                                }


                            }
                        });


                        dialogadd.show();
                    }
                });
                break;
            case "location":

                setAlignment(holder, myMsg, chatMessage.getState(), chatMessage.getType());
                ((LayoutLocationViewHolder) holder).txtDate.setText(TimeProperties.getDate(Long.parseLong(chatMessage.getDateTime()), "hh:mm"));

                ((LayoutLocationViewHolder) holder).cardOpenLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mCallback != null) {
                            System.out.println(chatMessage.getFileName());
                            mCallback.onClickLocation(position, chatMessage, myMsg);
                        }
                    }
                });


                break;
            default:

                setAlignment(holder, myMsg, chatMessage.getState(), chatMessage.getType());
                ((LayoutTextViewHolder) holder).txtDate.setText(TimeProperties.getDate(Long.parseLong(chatMessage.getDateTime()), "hh:mm"));
                if (chatMessage.isUpdate().equals("1")) {
                    ((LayoutTextViewHolder) holder).txtUpdate.setVisibility(View.VISIBLE);

                } else {
                    ((LayoutTextViewHolder) holder).txtUpdate.setVisibility(View.GONE);
                }

                ((LayoutTextViewHolder) holder).txtMessage.setText(chatMessage.getMessage());
                Linkify.addLinks(((LayoutTextViewHolder) holder).txtMessage, Linkify.WEB_URLS);
                ((LayoutTextViewHolder) holder).txtMessage.setLinkTextColor(ContextCompat.getColor(context,
                        R.color.blue_200));


                ((LayoutTextViewHolder) holder).txtMessage.setVisibility(View.VISIBLE);


                break;


        }

//                ((ViewHolder)holder).txtInfo.setText(chatMessage.getDate());
        if (chatMessage.isChecked()) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.background_onLong_click));
            holder.itemView.getBackground().setAlpha(60);

        } else {
            holder.itemView.setBackground(null);
        }
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                System.out.println(chatMessage.getMessage() + "mjjjjjjjjjjj");

                if (!chatMessage.isChecked()) {
                    if (mCallback != null) {

                        mCallback.onLongClick(position, chatMessage, true);

                    }


                    chatMessage.setChecked(true);
                    holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.background_onLong_click));
                    holder.itemView.getBackground().setAlpha(60);


                } else {
                    if (mCallback != null) {
                        mCallback.onLongClick(position, chatMessage, false);

                    }
                    chatMessage.setChecked(false);
                    holder.itemView.setBackground(null);


                }


                return false;
            }
        });


    }


    @Override
    public long getItemId(int position) {
        return position;
    }

//    @Override
//    public int getItemCount() {
//        if (chatMessages != null) {
//            return chatMessages.size();
//        } else {
//            return 0;
//        }
//    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        System.out.println("holder type" + holder.getItemViewType());
        if (holder.getItemViewType() == 1) {
//        holder.itemView.
            if (((LayoutVoiceViewHolder) holder).mediaPlayer != null)
                if (((LayoutVoiceViewHolder) holder).mediaPlayer.isPlaying()) {
                    ((LayoutVoiceViewHolder) holder).mediaPlayer.pause();
                }

            // holder.voicePlayerView.onPause();
//       holder.mediaPlayer.pause();
        }
    }


//    public void add(ChatMessage message) {
//        chatMessages.add(message);
//    }
//
//    public void add(List<ChatMessage> messages) {
//        chatMessages.addAll(messages);
//    }
public void setData(ArrayList<ChatMessage> newData) {

    submitList(newData);

}

    private void setAlignment(RecyclerView.ViewHolder holder, boolean isMe, String state, String type) {
        switch (type) {
            case "imageWeb":
                if (isMe) {
                    ((LayoutImageViewHolder) holder).contentwithB.setBackgroundResource(R.drawable.in_message_bg);

                    LinearLayout.LayoutParams layoutParams =
                            (LinearLayout.LayoutParams) ((LayoutImageViewHolder) holder).contentwithB.getLayoutParams();
                    layoutParams.gravity = Gravity.RIGHT;
                    ((LayoutImageViewHolder) holder).contentwithB.setLayoutParams(layoutParams);

                    RelativeLayout.LayoutParams lp =
                            (RelativeLayout.LayoutParams) ((LayoutImageViewHolder) holder).content.getLayoutParams();
                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    ((LayoutImageViewHolder) holder).content.setLayoutParams(lp);
                    layoutParams.gravity = Gravity.RIGHT;

                    ((LayoutImageViewHolder) holder).txtDate.setTextColor(context.getResources().getColor(R.color.white));


                    if (state.equals("3")) {
                        ((LayoutImageViewHolder) holder).imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_recive_done_green));
                    } else if (state.equals("2")) {
                        ((LayoutImageViewHolder) holder).imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_recive_done));
                    } else if (state.equals("1")) {
                        ((LayoutImageViewHolder) holder).imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_send_done));
                    } else {
                        ((LayoutImageViewHolder) holder).imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_not_send));

                    }
                    ((LayoutImageViewHolder) holder).imageSeen.setVisibility(View.VISIBLE);

                } else if (!isMe) {
                    ((LayoutImageViewHolder) holder).imageSeen.setVisibility(View.GONE);

                    ((LayoutImageViewHolder) holder).contentwithB.setBackgroundResource(R.drawable.out_message_bg);

                    LinearLayout.LayoutParams layoutParams =
                            (LinearLayout.LayoutParams) ((LayoutImageViewHolder) holder).contentwithB.getLayoutParams();
                    layoutParams.gravity = Gravity.LEFT;
                    ((LayoutImageViewHolder) holder).contentwithB.setLayoutParams(layoutParams);
//                    ((LayoutImageViewHolder) holder).txtDate.setTextColor(context.getResources().getColor(R.color.textColor));
                    ((LayoutImageViewHolder) holder).txtDate.setTextColor(context.getResources().getColor(R.color.textColor));



                    RelativeLayout.LayoutParams lp =
                            (RelativeLayout.LayoutParams) ((LayoutImageViewHolder) holder).content.getLayoutParams();
                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    ((LayoutImageViewHolder) holder).content.setLayoutParams(lp);

                }
                break;
            case "voice":
                if (isMe) {
                    ((LayoutVoiceViewHolder) holder).contentWithBG.setBackgroundResource(R.drawable.in_message_bg);

                    LinearLayout.LayoutParams layoutParams =
                            (LinearLayout.LayoutParams) ((LayoutVoiceViewHolder) holder).contentWithBG.getLayoutParams();
                    layoutParams.gravity = Gravity.RIGHT;
                    ((LayoutVoiceViewHolder) holder).contentWithBG.setLayoutParams(layoutParams);

                    RelativeLayout.LayoutParams lp =
                            (RelativeLayout.LayoutParams) ((LayoutVoiceViewHolder) holder).content.getLayoutParams();
                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    ((LayoutVoiceViewHolder) holder).content.setLayoutParams(lp);
//                    layoutParams = (LinearLayout.LayoutParams) ((ViewHolder)holder).txtMessage.getLayoutParams();
//                    layoutParams.gravity = Gravity.RIGHT;
//                    ((ViewHolder)holder).txtMessage.setLayoutParams(layoutParams);
                    ((LayoutVoiceViewHolder) holder).imagePlayerPause.setColorFilter(context.getResources().getColor(R.color.white));
                    ((LayoutVoiceViewHolder) holder).downloadRecordIB.setColorFilter(context.getResources().getColor(R.color.white));


                    ((LayoutVoiceViewHolder) holder).textTotalDouration.setTextColor(context.getResources().getColor(R.color.white));
                    ((LayoutVoiceViewHolder) holder).textCurrentTime.setTextColor(context.getResources().getColor(R.color.white));
                    ((LayoutVoiceViewHolder) holder).timeSeparator.setTextColor(context.getResources().getColor(R.color.white));


                    ((LayoutVoiceViewHolder)holder).textDate.setTextColor(context.getResources().getColor(R.color.white));


                    if (state.equals("3")) {
                        ((LayoutVoiceViewHolder) holder).imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_recive_done_green));
                    } else if (state.equals("2")) {
                        ((LayoutVoiceViewHolder) holder).imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_recive_done));
                    } else if (state.equals("1")) {
                        ((LayoutVoiceViewHolder) holder).imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_send_done));
                    } else {
                        ((LayoutVoiceViewHolder) holder).imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_not_send));

                    }
                    ((LayoutVoiceViewHolder) holder).imageSeen.setVisibility(View.VISIBLE);


//                    layoutParams = (LinearLayout.LayoutParams) ((ViewHolder)holder).txtInfo.getLayoutParams();
//                    layoutParams.gravity = Gravity.RIGHT;
//                    ((ViewHolder)holder).txtInfo.setLayoutParams(layoutParams);
                } else if (!isMe) {
                    ((LayoutVoiceViewHolder) holder).imageSeen.setVisibility(View.GONE);

                    ((LayoutVoiceViewHolder) holder).contentWithBG.setBackgroundResource(R.drawable.out_message_bg);

                    LinearLayout.LayoutParams layoutParams =
                            (LinearLayout.LayoutParams) ((LayoutVoiceViewHolder) holder).contentWithBG.getLayoutParams();
                    layoutParams.gravity = Gravity.LEFT;
                    ((LayoutVoiceViewHolder) holder).contentWithBG.setLayoutParams(layoutParams);
//                    ((LayoutVoiceViewHolder)holder).txtMessage.setTextColor(context.getResources().getColor(R.color.textColor));
//                    ((LayoutVoiceViewHolder)holder).txtDate.setTextColor(context.getResources().getColor(R.color.textColor));
//                    ((LayoutVoiceViewHolder) holder).imagePlayerPause.setColorFilter(context.getResources().getColor(R.color.textColor));
//                    ((LayoutVoiceViewHolder) holder).downloadRecordIB.setColorFilter(context.getResources().getColor(R.color.textColor));
//
//                    ((LayoutVoiceViewHolder) holder).textTotalDouration.setTextColor(context.getResources().getColor(R.color.textColor));
//                    ((LayoutVoiceViewHolder) holder).textCurrentTime.setTextColor(context.getResources().getColor(R.color.textColor));
//                    ((LayoutVoiceViewHolder) holder).timeSeparator.setTextColor(context.getResources().getColor(R.color.textColor));
//                    ((LayoutVoiceViewHolder) holder).textDate.setTextColor(context.getResources().getColor(R.color.textColor));
                    ((LayoutVoiceViewHolder) holder).imagePlayerPause.setColorFilter(context.getResources().getColor(R.color.textColor));
                    ((LayoutVoiceViewHolder) holder).downloadRecordIB.setColorFilter(context.getResources().getColor(R.color.textColor));


                    ((LayoutVoiceViewHolder) holder).textTotalDouration.setTextColor(context.getResources().getColor(R.color.textColor));
                    ((LayoutVoiceViewHolder) holder).textCurrentTime.setTextColor(context.getResources().getColor(R.color.textColor));
                    ((LayoutVoiceViewHolder) holder).timeSeparator.setTextColor(context.getResources().getColor(R.color.textColor));


                    ((LayoutVoiceViewHolder)holder).textDate.setTextColor(context.getResources().getColor(R.color.textColor));


                    RelativeLayout.LayoutParams lp =
                            (RelativeLayout.LayoutParams) ((LayoutVoiceViewHolder) holder).content.getLayoutParams();
                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    ((LayoutVoiceViewHolder) holder).content.setLayoutParams(lp);
//                    layoutParams = (LinearLayout.LayoutParams) ((ViewHolder)holder).txtMessage.getLayoutParams();
//                    layoutParams.gravity = Gravity.LEFT;
//                    ((ViewHolder)holder).txtMessage.setLayoutParams(layoutParams);
//
//                    layoutParams = (LinearLayout.LayoutParams) ((ViewHolder)holder).txtInfo.getLayoutParams();
//                    layoutParams.gravity = Gravity.LEFT;
//                    ((ViewHolder)holder).txtInfo.setLayoutParams(layoutParams);
                }
                break;
            case "video":
                if (isMe) {
                    ((LayoutVideoViewHolder) holder).contentWithBG.setBackgroundResource(R.drawable.in_message_bg);

                    LinearLayout.LayoutParams layoutParams =
                            (LinearLayout.LayoutParams) ((LayoutVideoViewHolder) holder).contentWithBG.getLayoutParams();
                    layoutParams.gravity = Gravity.RIGHT;
                    ((LayoutVideoViewHolder) holder).contentWithBG.setLayoutParams(layoutParams);

                    RelativeLayout.LayoutParams lp =
                            (RelativeLayout.LayoutParams) ((LayoutVideoViewHolder) holder).content.getLayoutParams();
                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    ((LayoutVideoViewHolder) holder).content.setLayoutParams(lp);
                    ((LayoutVideoViewHolder) holder).textDate.setTextColor(context.getResources().getColor(R.color.white));


                    if (state.equals("3")) {
                        ((LayoutVideoViewHolder) holder).imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_recive_done_green));
                    } else if (state.equals("2")) {
                        ((LayoutVideoViewHolder) holder).imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_recive_done));
                    } else if (state.equals("1")) {
                        ((LayoutVideoViewHolder) holder).imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_send_done));
                    } else {
                        ((LayoutVideoViewHolder) holder).imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_not_send));

                    }
                    ((LayoutVideoViewHolder) holder).imageSeen.setVisibility(View.VISIBLE);


                } else if (!isMe) {
                    ((LayoutVideoViewHolder) holder).imageSeen.setVisibility(View.GONE);

                    ((LayoutVideoViewHolder) holder).contentWithBG.setBackgroundResource(R.drawable.out_message_bg);

                    LinearLayout.LayoutParams layoutParams =
                            (LinearLayout.LayoutParams) ((LayoutVideoViewHolder) holder).contentWithBG.getLayoutParams();
                    layoutParams.gravity = Gravity.LEFT;
                    ((LayoutVideoViewHolder) holder).contentWithBG.setLayoutParams(layoutParams);
//                    ((LayoutVideoViewHolder) holder).textDate.setTextColor(context.getResources().getColor(R.color.textColor));
                    ((LayoutVideoViewHolder) holder).textDate.setTextColor(context.getResources().getColor(R.color.textColor));


//


                    RelativeLayout.LayoutParams lp =
                            (RelativeLayout.LayoutParams) ((LayoutVideoViewHolder) holder).content.getLayoutParams();
                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    ((LayoutVideoViewHolder) holder).content.setLayoutParams(lp);
//
                }

                break;
            case "file":
                if (isMe) {

                    ((LayoutPdfViewHolder) holder).contentWithBG.setBackgroundResource(R.drawable.in_message_bg);

                    LinearLayout.LayoutParams layoutParams =
                            (LinearLayout.LayoutParams) ((LayoutPdfViewHolder) holder).contentWithBG.getLayoutParams();
                    layoutParams.gravity = Gravity.RIGHT;
                    ((LayoutPdfViewHolder) holder).contentWithBG.setLayoutParams(layoutParams);

                    RelativeLayout.LayoutParams lp =
                            (RelativeLayout.LayoutParams) ((LayoutPdfViewHolder) holder).content.getLayoutParams();
                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    ((LayoutPdfViewHolder) holder).content.setLayoutParams(lp);
                    layoutParams.gravity = Gravity.RIGHT;
                    ((LayoutPdfViewHolder) holder).txtDate.setTextColor(context.getResources().getColor(R.color.white));


                    if (state.equals("3")) {
                        ((LayoutPdfViewHolder) holder).imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_recive_done_green));
                    } else if (state.equals("2")) {
                        ((LayoutPdfViewHolder) holder).imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_recive_done));
                    } else if (state.equals("1")) {
                        ((LayoutPdfViewHolder) holder).imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_send_done));
                    } else {
                        ((LayoutPdfViewHolder) holder).imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_not_send));

                    }
                    ((LayoutPdfViewHolder) holder).imageSeen.setVisibility(View.VISIBLE);

                } else if (!isMe) {
                    ((LayoutPdfViewHolder) holder).imageSeen.setVisibility(View.GONE);

                    ((LayoutPdfViewHolder) holder).contentWithBG.setBackgroundResource(R.drawable.out_message_bg);

                    LinearLayout.LayoutParams layoutParams =
                            (LinearLayout.LayoutParams) ((LayoutPdfViewHolder) holder).contentWithBG.getLayoutParams();
                    layoutParams.gravity = Gravity.LEFT;
                    ((LayoutPdfViewHolder) holder).contentWithBG.setLayoutParams(layoutParams);
//                    ((LayoutPdfViewHolder) holder).txtDate.setTextColor(context.getResources().getColor(R.color.textColor));
                    ((LayoutPdfViewHolder) holder).txtDate.setTextColor(context.getResources().getColor(R.color.textColor));



                    RelativeLayout.LayoutParams lp =
                            (RelativeLayout.LayoutParams) ((LayoutPdfViewHolder) holder).content.getLayoutParams();
                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    ((LayoutPdfViewHolder) holder).content.setLayoutParams(lp);

                }
                break;

            case "contact":
                if (isMe) {

                    ((LayoutContactViewHolder) holder).contentWithBG.setBackgroundResource(R.drawable.in_message_bg);

                    LinearLayout.LayoutParams layoutParams =
                            (LinearLayout.LayoutParams) ((LayoutContactViewHolder) holder).contentWithBG.getLayoutParams();
                    layoutParams.gravity = Gravity.RIGHT;
                    ((LayoutContactViewHolder) holder).contentWithBG.setLayoutParams(layoutParams);

                    RelativeLayout.LayoutParams lp =
                            (RelativeLayout.LayoutParams) ((LayoutContactViewHolder) holder).content.getLayoutParams();
                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    ((LayoutContactViewHolder) holder).content.setLayoutParams(lp);
                    layoutParams.gravity = Gravity.RIGHT;
                    ((LayoutContactViewHolder) holder).txtDate.setTextColor(context.getResources().getColor(R.color.white));
                    ((LayoutContactViewHolder) holder).addContact.setTextColor(context.getResources().getColor(R.color.white));
                    ((LayoutContactViewHolder) holder).txtName.setTextColor(context.getResources().getColor(R.color.white));
                    ((LayoutContactViewHolder) holder).txtNumber.setTextColor(context.getResources().getColor(R.color.white));






                    if (state.equals("3")) {
                        ((LayoutContactViewHolder) holder).imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_recive_done_green));
                    } else if (state.equals("2")) {
                        ((LayoutContactViewHolder) holder).imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_recive_done));
                    } else if (state.equals("1")) {
                        ((LayoutContactViewHolder) holder).imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_send_done));
                    } else {
                        ((LayoutContactViewHolder) holder).imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_not_send));

                    }
                    ((LayoutContactViewHolder) holder).imageSeen.setVisibility(View.VISIBLE);

                } else if (!isMe) {
                    ((LayoutContactViewHolder) holder).imageSeen.setVisibility(View.GONE);

                    ((LayoutContactViewHolder) holder).contentWithBG.setBackgroundResource(R.drawable.out_message_bg);

                    LinearLayout.LayoutParams layoutParams =
                            (LinearLayout.LayoutParams) ((LayoutContactViewHolder) holder).contentWithBG.getLayoutParams();
                    layoutParams.gravity = Gravity.LEFT;
                    ((LayoutContactViewHolder) holder).contentWithBG.setLayoutParams(layoutParams);
//                    ((LayoutContactViewHolder) holder).txtDate.setTextColor(context.getResources().getColor(R.color.textColor));
//                    ((LayoutContactViewHolder) holder).txtName.setTextColor(context.getResources().getColor(R.color.textColor));
//                    ((LayoutContactViewHolder) holder).txtNumber.setTextColor(context.getResources().getColor(R.color.textColor));
//                    ((LayoutContactViewHolder)holder).view1.setBackgroundColor(context.getResources().getColor(R.color.textColor));
//                    ((LayoutContactViewHolder)holder).view2.setBackgroundColor(context.getResources().getColor(R.color.textColor));
//                    ((LayoutContactViewHolder)holder).sendMessage.setTextColor(context.getResources().getColor(R.color.textColor));
//                    ((LayoutContactViewHolder) holder).addContact.setTextColor(context.getResources().getColor(R.color.textColor));
                    ((LayoutContactViewHolder) holder).txtDate.setTextColor(context.getResources().getColor(R.color.textColor));
                    ((LayoutContactViewHolder) holder).addContact.setTextColor(context.getResources().getColor(R.color.textColor));
                    ((LayoutContactViewHolder) holder).txtName.setTextColor(context.getResources().getColor(R.color.textColor));
                    ((LayoutContactViewHolder) holder).txtNumber.setTextColor(context.getResources().getColor(R.color.textColor));


                    RelativeLayout.LayoutParams lp =
                            (RelativeLayout.LayoutParams) ((LayoutContactViewHolder) holder).content.getLayoutParams();
                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    ((LayoutContactViewHolder) holder).content.setLayoutParams(lp);

                }
                break;
            case "location":
                if (isMe) {
                    ((LayoutLocationViewHolder) holder).contentWithBG.setBackgroundResource(R.drawable.in_message_bg);

                    LinearLayout.LayoutParams layoutParams =
                            (LinearLayout.LayoutParams) ((LayoutLocationViewHolder) holder).contentWithBG.getLayoutParams();
                    layoutParams.gravity = Gravity.RIGHT;
                    ((LayoutLocationViewHolder) holder).contentWithBG.setLayoutParams(layoutParams);

                    RelativeLayout.LayoutParams lp =
                            (RelativeLayout.LayoutParams) ((LayoutLocationViewHolder) holder).content.getLayoutParams();
                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    ((LayoutLocationViewHolder) holder).content.setLayoutParams(lp);
                    layoutParams.gravity = Gravity.RIGHT;

                    ((LayoutLocationViewHolder) holder).txtDate.setTextColor(context.getResources().getColor(R.color.white));


                    if (state.equals("3")) {
                        ((LayoutLocationViewHolder) holder).imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_recive_done_green));
                    } else if (state.equals("2")) {
                        ((LayoutLocationViewHolder) holder).imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_recive_done));
                    } else if (state.equals("1")) {
                        ((LayoutLocationViewHolder) holder).imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_send_done));
                    } else {
                        ((LayoutLocationViewHolder) holder).imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_not_send));

                    }
                    ((LayoutLocationViewHolder) holder).imageSeen.setVisibility(View.VISIBLE);

                } else if (!isMe) {
                    ((LayoutLocationViewHolder) holder).imageSeen.setVisibility(View.GONE);

                    ((LayoutLocationViewHolder) holder).contentWithBG.setBackgroundResource(R.drawable.out_message_bg);

                    LinearLayout.LayoutParams layoutParams =
                            (LinearLayout.LayoutParams) ((LayoutLocationViewHolder) holder).contentWithBG.getLayoutParams();
                    layoutParams.gravity = Gravity.LEFT;
                    ((LayoutLocationViewHolder) holder).contentWithBG.setLayoutParams(layoutParams);
//                    ((LayoutLocationViewHolder) holder).txtDate.setTextColor(context.getResources().getColor(R.color.textColor));
                    ((LayoutLocationViewHolder) holder).txtDate.setTextColor(context.getResources().getColor(R.color.textColor));



                    RelativeLayout.LayoutParams lp =
                            (RelativeLayout.LayoutParams) ((LayoutLocationViewHolder) holder).content.getLayoutParams();
                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    ((LayoutLocationViewHolder) holder).content.setLayoutParams(lp);

                }
                break;

            default:


                if (isMe) {
                    ((LayoutTextViewHolder) holder).contentWithBG.setBackgroundResource(R.drawable.in_message_bg);

                    LinearLayout.LayoutParams layoutParams =
                            (LinearLayout.LayoutParams) ((LayoutTextViewHolder) holder).contentWithBG.getLayoutParams();
                    layoutParams.gravity = Gravity.RIGHT;
                    ((LayoutTextViewHolder) holder).contentWithBG.setLayoutParams(layoutParams);

                    RelativeLayout.LayoutParams lp =
                            (RelativeLayout.LayoutParams) ((LayoutTextViewHolder) holder).content.getLayoutParams();
                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    ((LayoutTextViewHolder) holder).content.setLayoutParams(lp);
                    layoutParams = (LinearLayout.LayoutParams) ((LayoutTextViewHolder) holder).txtMessage.getLayoutParams();
                    layoutParams.gravity = Gravity.RIGHT;
                    ((LayoutTextViewHolder)holder).txtMessage.setLayoutParams(layoutParams);

                    ((LayoutTextViewHolder) holder).txtMessage.setTextColor(context.getResources().getColor(R.color.white));


                    ((LayoutTextViewHolder) holder).txtDate.setTextColor(context.getResources().getColor(R.color.white));


                    if (state.equals("3")) {
                        ((LayoutTextViewHolder) holder).imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_recive_done_green));
                    } else if (state.equals("2")) {
                        ((LayoutTextViewHolder) holder).imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_recive_done));
                    } else if (state.equals("1")) {
                        ((LayoutTextViewHolder) holder).imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_send_done));
                    } else {
                        ((LayoutTextViewHolder) holder).imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_not_send));

                    }
                    ((LayoutTextViewHolder) holder).imageSeen.setVisibility(View.VISIBLE);


//                    layoutParams = (LinearLayout.LayoutParams) ((LayoutTextViewHolder)holder).txtInfo.getLayoutParams();
//                    layoutParams.gravity = Gravity.RIGHT;
//                    ((LayoutTextViewHolder)holder).txtInfo.setLayoutParams(layoutParams);
                } else if (!isMe) {
                    ((LayoutTextViewHolder) holder).imageSeen.setVisibility(View.GONE);

                    ((LayoutTextViewHolder) holder).contentWithBG.setBackgroundResource(R.drawable.out_message_bg);

                    LinearLayout.LayoutParams layoutParams =
                            (LinearLayout.LayoutParams) ((LayoutTextViewHolder) holder).contentWithBG.getLayoutParams();
                    layoutParams.gravity = Gravity.LEFT;
                    ((LayoutTextViewHolder) holder).contentWithBG.setLayoutParams(layoutParams);

                    ((LayoutTextViewHolder) holder).txtMessage.setTextColor(context.getResources().getColor(R.color.textColor));


                    ((LayoutTextViewHolder) holder).txtDate.setTextColor(context.getResources().getColor(R.color.textColor));


                    RelativeLayout.LayoutParams lp =
                            (RelativeLayout.LayoutParams) ((LayoutTextViewHolder) holder).content.getLayoutParams();
                    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    ((LayoutTextViewHolder) holder).content.setLayoutParams(lp);
                    layoutParams = (LinearLayout.LayoutParams) ((LayoutTextViewHolder) holder).txtMessage.getLayoutParams();
                    layoutParams.gravity = Gravity.LEFT;
                    ((LayoutTextViewHolder) holder).txtMessage.setLayoutParams(layoutParams);

//                    layoutParams = (LinearLayout.LayoutParams) ((LayoutTextViewHolder)holder).txtInfo.getLayoutParams();
//                    layoutParams.gravity = Gravity.LEFT;
//                    ((LayoutTextViewHolder)holder).txtInfo.setLayoutParams(layoutParams);
                }
                break;
        }
    }

    //    public void filter(String charText) {
//        for (ChatMessage wp : chatMessages) {
//            if (wp.getMessage() != null) {
//                System.out.println(wp.getMessage());
//                if (wp.getMessage().toLowerCase(Locale.getDefault()).contains(charText)) {
//                    int index = wp.getMessage().toLowerCase(Locale.getDefault()).indexOf(charText);
//
//                    Spannable WordtoSpan = new SpannableString(wp.getMessage());
//                    WordtoSpan.setSpan(new ForegroundColorSpan(Color.BLUE), index, index + charText.length(),
//                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    //wp.setMessage(WordtoSpan);
//                }
//
//                notifyDataSetChanged();
//            }
//        }
//    }
    String milliSecondsToTimer(Long milliSeconds) {
        String timerString = "";
        String secondString;
        int hours = (int) (milliSeconds / (1000 * 60 * 60));
        int minute = (int) (milliSeconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) (milliSeconds % (1000 * 60 * 60) % (1000 * 60) / 1000);

        if (hours > 0) {
            timerString = hours + ":";

        }
        if (seconds < 10) {
            secondString = "0" + seconds;
        } else {
            secondString = "" + seconds;

        }
        timerString = timerString + minute + ":" + secondString;
        return timerString;

    }

    //    private ViewHolder createViewHolder(View v) {
//
//        ViewHolder holder = new ViewHolder();
//        holder.txtMessage = (TextView) v.findViewById(R.id.txtMessage);
//        holder.imageMessage= v.findViewById(R.id.imgMessage);
//        holder.content = (LinearLayout) v.findViewById(R.id.content);
//        holder.contentWithBG = (LinearLayout) v.findViewById(R.id.contentWithBackground);
//        holder.contentFile = (LinearLayout) v.findViewById(R.id.liner_file);
//        holder.txtInfo = (TextView) v.findViewById(R.id.txtInfo);
//        holder.txtDate = v.findViewById(R.id.tv_date);
//        holder.imageSeen = v.findViewById(R.id.iv_state);
//        holder.txtFile = v.findViewById(R.id.text_file);
//        holder.imageFile = v.findViewById(R.id.image_file);
//        holder.fileImageButton = v.findViewById(R.id.image_button_file);
//        return holder;
//    }
    class LayoutImageViewHolder
            extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final ImageButton downloadImage;
        int l = 0;

        private final LinearLayout content;
        private final LinearLayout contentwithB;
        private final ImageView imageSeen;
        public TextView txtDate;
        ProgressBar adCircleProgress;


        float textSize = 14.0F;
        SharedPreferences sharedPreferences;

        public LayoutImageViewHolder(@NonNull View itemView) {
            super(itemView);


            // Find the Views
            imageView = itemView.findViewById(R.id.imgMessage);
            content = itemView.findViewById(R.id.content);
            contentwithB = itemView.findViewById(R.id.contentWithBackground);

            txtDate = itemView.findViewById(R.id.tv_date);
//        txtDate.setTextSize(textSize);
//        txtDate.setTextSize(Float.parseFloat(sharedPreferences.getString("txtFontSize", "16")));

            imageSeen = itemView.findViewById(R.id.iv_state);
            downloadImage = itemView.findViewById(R.id.image_download);
            adCircleProgress = itemView.findViewById(R.id.pgb_progress);


        }

    }

    class LayoutVoiceViewHolder
            extends RecyclerView.ViewHolder {

        private final LinearLayout content;
        int l = 0;
        private final LinearLayout contentWithBG;
        private final ImageView imageSeen;
        public TextView textDate;
        public LinearLayout contentRecord;
        ImageView imagePlayerPause;
        TextView textCurrentTime, textTotalDouration, timeSeparator;
        SeekBar playerSeekBar;
        MediaPlayer mediaPlayer;
        RelativeLayout relativeLayout;
        Handler handler = new Handler();
        Runnable updater;
        ImageButton downloadRecordIB;
        ProgressBar adCircleProgress;


        float textSize = 14.0F;
        SharedPreferences sharedPreferences;

        public LayoutVoiceViewHolder(@NonNull View itemView) {
            super(itemView);

            sharedPreferences = context.getSharedPreferences("txtFontSize", Context.MODE_PRIVATE);

            content = itemView.findViewById(R.id.content);
            contentWithBG = itemView.findViewById(R.id.contentWithBackground);

            textDate = itemView.findViewById(R.id.tv_date);


            contentRecord = itemView.findViewById(R.id.Liner_record);
            imagePlayerPause = itemView.findViewById(R.id.image_play_pause);

            textCurrentTime = itemView.findViewById(R.id.text_current_time);


            textTotalDouration = itemView.findViewById(R.id.text_total_duration);

            timeSeparator = itemView.findViewById(R.id.time_separator);


            playerSeekBar = itemView.findViewById(R.id.player_seek_bar);
            imageSeen = itemView.findViewById(R.id.iv_state);

            downloadRecordIB = itemView.findViewById(R.id.image_download_audio);
            adCircleProgress = itemView.findViewById(R.id.pgb_progress);


        }

}
    class LayoutVideoViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout content;
        private final LinearLayout contentWithBG;
        private final ImageView imageSeen;
        public TextView textDate;
        FrameLayout contentVideo;
        ImageButton videoImageButton;
        ImageButton videoImageDownload;
        ImageView imageVideo;
        int l = 0;
        ProgressBar adCircleProgress;


        public LayoutVideoViewHolder(@NonNull View itemView) {
            super(itemView);

            // Find the Views
            content = itemView.findViewById(R.id.content);
            contentWithBG = itemView.findViewById(R.id.contentWithBackground);

            textDate = itemView.findViewById(R.id.tv_date);

            imageSeen = itemView.findViewById(R.id.iv_state);
            videoImageButton = itemView.findViewById(R.id.video_image_button);
            contentVideo = itemView.findViewById(R.id.frame_video);
            imageVideo = itemView.findViewById(R.id.img_video);
            videoImageDownload = itemView.findViewById(R.id.video_image_download);
            adCircleProgress = itemView.findViewById(R.id.pgb_progress);


        }
    }

    public static class LayoutPdfViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageSeen;

        public TextView txtDate;
        public TextView txtFile;
        public ImageView imageFile;
        ImageButton fileImageButton;
        ImageView pdfImage;
        public LinearLayout content;
        public LinearLayout contentFile;
        public LinearLayout contentWithBG;
        int l = 0;

        //        float textSize = 14.0F ;
        SharedPreferences sharedPreferences;
        private Activity context;

        ProgressBar adCircleProgress;


        public LayoutPdfViewHolder(@NonNull View itemView) {
            super(itemView);

            content = itemView.findViewById(R.id.content);
            contentWithBG = itemView.findViewById(R.id.contentWithBackground);
            contentFile = itemView.findViewById(R.id.liner_file);

            txtDate = itemView.findViewById(R.id.tv_date);


            imageSeen = itemView.findViewById(R.id.iv_state);

            txtFile = itemView.findViewById(R.id.text_file);


            imageFile = itemView.findViewById(R.id.image_file);

            fileImageButton = itemView.findViewById(R.id.image_button_file);
            pdfImage = itemView.findViewById(R.id.image_pdf);
            adCircleProgress = itemView.findViewById(R.id.pgb_progress);


        }
    }

    public static class LayoutContactViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageSeen;

        public TextView txtDate;
        public CircleImageView imageProfile;
        public LinearLayout content;
        public CardView contentWithBG;

        public TextView txtName;
        public TextView txtNumber;
        public TextView addContact;
//        public  TextView sendMessage;


        public View view1;
        public View view2;


        float textSize = 14.0F;
        SharedPreferences sharedPreferences;

        public LayoutContactViewHolder(@NonNull View itemView) {
            super(itemView);

            content = itemView.findViewById(R.id.content);
            contentWithBG = itemView.findViewById(R.id.card);

            txtDate = itemView.findViewById(R.id.tv_date);


            imageSeen = itemView.findViewById(R.id.iv_state);

            txtNumber = itemView.findViewById(R.id.phone);


            txtName = itemView.findViewById(R.id.name);


            imageProfile = itemView.findViewById(R.id.profile);

            addContact = itemView.findViewById(R.id.addContact);


            view1 = itemView.findViewById(R.id.view1);


        }
    }

    public static class LayoutLocationViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageSeen;

        public TextView txtDate;
        public LinearLayout content;
        public LinearLayout contentWithBG;
        public CardView cardOpenLocation;
//        public MapView map;


        public LayoutLocationViewHolder(@NonNull View itemView) {
            super(itemView);

            content = itemView.findViewById(R.id.content);
            contentWithBG = itemView.findViewById(R.id.contentWithBackground);
            txtDate = itemView.findViewById(R.id.tv_date);
            imageSeen = itemView.findViewById(R.id.iv_state);
            cardOpenLocation = itemView.findViewById(R.id.cardOpenItLocation);
//            map = itemView.findViewById(R.id.map);


        }
    }

    public static class LayoutTextViewHolder extends RecyclerView.ViewHolder {
        public TextView txtMessage;
        private final ImageView imageSeen;
        public TextView txtInfo;
        public TextView txtDate;
        public TextView txtUpdate;


        float textSize = 14.0F;
        SharedPreferences sharedPreferences;
//        private Activity activity;


        public LinearLayout content;
        public RelativeLayout contentWithBG;


        public LayoutTextViewHolder(@NonNull View itemView) {
            super(itemView);


            txtMessage = itemView.findViewById(R.id.txtMessage);


            content = itemView.findViewById(R.id.content);
            contentWithBG = itemView.findViewById(R.id.contentWithBackground);


            txtInfo = itemView.findViewById(R.id.txtInfo);


            txtDate = itemView.findViewById(R.id.tv_date);


            imageSeen = itemView.findViewById(R.id.iv_state);
            txtUpdate = itemView.findViewById(R.id.text_update);

        }
    }


    public static class MyDiffUtilChatMessage extends DiffUtil.ItemCallback<ChatMessage> {


        @Override
        public boolean areItemsTheSame(@NonNull ChatMessage oldItem, @NonNull ChatMessage newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull ChatMessage oldItem, @NonNull ChatMessage newItem) {
//            return 0 == oldItem.compareTo(newItem);
            return oldItem.equals(newItem);

        }

    }
}

