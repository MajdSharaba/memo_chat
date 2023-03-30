package com.yawar.memo.jetbackCompose.components

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.size.Scale
import coil.transform.CircleCropTransformation
import com.yawar.memo.BaseApp
import com.yawar.memo.BuildConfig
import com.yawar.memo.R
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.domain.model.AnthorUserInChatRoomId
import com.yawar.memo.domain.model.SpecialMessageModel
import com.yawar.memo.jetbackCompose.ui.theme.*
import com.yawar.memo.ui.chatPage.ConversationActivity
import com.yawar.memo.utils.TimeProperties
import com.yawar.memo.utils.getFile

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatImageItem(chatMessage: SpecialMessageModel, function: () -> Unit) {
    val userModel = BaseApp.instance?.classSharedPreferences?.user
    val isMe = chatMessage.sender_id == userModel?.userId
    val  anthorUserInChatRoomId = AnthorUserInChatRoomId.getInstance("","","","","","","","")

    var stateImage = R.drawable.ic_recive_done
    val showDialog: MutableState<Boolean> = remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val file = getFile(chatMessage, isMe, "/video")
    val path = if (file.exists()) FileProvider.getUriForFile(
        LocalContext.current, BuildConfig.APPLICATION_ID + ".fileprovider", file
    ) else R.drawable.backgrounblack

    val background = if (isMe) {
        SelectedColor
    } else if(isNightMode()){

        DarkSendMessage
    }

    else{
        LightReciveMessage
    }
    val textColor = if (isMe) {
        Color.White

    } else {
        MaterialTheme.colors.onBackground
    }
    val timeProperties = TimeProperties()

    when (chatMessage.state) {
        "3" -> {
            stateImage = R.drawable.ic_recive_done_green
        }
        "2" -> {
            stateImage = R.drawable.ic_recive_done

        }
        "1" -> {
            stateImage = R.drawable.ic_send_done

        }
    }


    val backgroundColor = if(chatMessage.isChecked){
        SelectedColor
    }
    else{
        MaterialTheme.colors.onPrimary

    }
    val alpha = if(chatMessage.isChecked){
        0.5f
    }
    else{
        1.0f

    }

    Card(
        modifier = Modifier
            .padding(8.dp, 4.dp)
            .fillMaxWidth()
            .combinedClickable(
                enabled = true,
                onLongClick = {
                    function()
                },
                onClick = {

                val bundle = Bundle()
                bundle.putString("reciver_id", chatMessage.other_id)
                bundle.putString(
                    "sender_id",
                    BaseApp.instance?.classSharedPreferences?.user?.userId
                )
                bundle.putString("fcm_token", chatMessage.user_token)
                bundle.putString("name", chatMessage.fullname)
                bundle.putString("image", chatMessage.profile_image)
                bundle.putString("chat_id", "")
                bundle.putString("blockedFor", chatMessage.blocked_for)
                anthorUserInChatRoomId.id = chatMessage.other_id
                anthorUserInChatRoomId.fcmToken = chatMessage.user_token
                anthorUserInChatRoomId.blockedFor = chatMessage.blocked_for
                anthorUserInChatRoomId.specialNumber = chatMessage.sn
                anthorUserInChatRoomId.userName = chatMessage.fullname
                anthorUserInChatRoomId.chatId = ""
                anthorUserInChatRoomId.imageUrl = chatMessage.profile_image
                anthorUserInChatRoomId.messageId = chatMessage.message_id
                val intent = Intent(context, ConversationActivity::class.java)
                intent.putExtras(bundle)
                context.startActivity(intent)
            },
            ),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp,

    ) {
        Surface(


                    color = backgroundColor.copy(alpha),
        ) {

            Row(
                Modifier
                    .padding(6.dp)
                    .fillMaxSize()


            ) {

                Image(
                    painter = rememberImagePainter(
                        data = if (isMe){AllConstants.imageUrl + userModel?.image
                        }else
                        {
                            AllConstants.imageUrl + chatMessage.profile_image
                        },

                        builder = {
                            scale(Scale.FILL)
                            error(R.drawable.th)

                            transformations(CircleCropTransformation())
                            placeholder(R.drawable.th)
                        }
                    ),

                    contentDescription = chatMessage.fullname,
                    modifier = Modifier
                        .weight(0.11f)
                        .alignByBaseline()
                        .size(40.dp)
                        .clip(RoundedCornerShape(80)),
                    contentScale = ContentScale.Crop,

                    )


                Column(
                    verticalArrangement = Arrangement.Bottom,

                    modifier = Modifier
                        .padding(7.dp, 0.dp, 10.dp, 10.dp)
                        .alignByBaseline()
                        .weight(0.8f)
                ) {
                    Row(
                        Modifier.padding(1.dp, 0.dp, 0.dp, 10.dp)
                    ) {


                        Text(
                            text = if (isMe) {
                                stringResource(id = R.string.you)
                            } else {
                                chatMessage.fullname
                            },
                            style = MaterialTheme.typography.subtitle1,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(1.dp)
                                .alignByBaseline(),
                            color = MaterialTheme.colors.onBackground


                        )
                        Icon(painter = painterResource(R.drawable.ic_arrow_rightt) ,
                            contentDescription = "" ,
                            modifier = Modifier
                                .align(
                                    Alignment.CenterVertically
                                )
                                .padding(1.dp),
                            tint = MaterialTheme.colors.onBackground,
                        )
                        Text(
                            text = if (!isMe) {
                                stringResource(id = R.string.you)
                            } else {
                                chatMessage.fullname
                            },
                            style = MaterialTheme.typography.subtitle1,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(1.dp)
                                .alignByBaseline(),
                                    color = MaterialTheme.colors.onBackground


                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(
                                color = background,
                                shape = RoundedCornerShape(15.dp),


                                )
                            .padding(3.dp)

                    ) {

                        Image(
                            painter = rememberAsyncImagePainter(
                                model = path,
                                placeholder = painterResource(R.drawable.backgrounblack),
                                error = painterResource(R.drawable.backgrounblack)

                            ),

                            contentDescription = chatMessage.fullname,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .height(200.dp)
                                .width(200.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .fillMaxSize()
                                .clickable {
                                    if (file.exists()) {
                                        Log.d("ChatImageItem", "ChatImageItem: ")
//                                        fullScreenDialog(true)
                                        showDialog.value = true
                                    }
                                }
                        )
                        if (!file.exists())
                            Image(
                                painterResource(R.drawable.ic_download_arrow),
                                "content description",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        Row(
                            Modifier
                                .padding(6.dp)
                                .align(Alignment.BottomEnd)

                        ) {

                            Box(
                                contentAlignment = Alignment.BottomStart,
                                modifier = Modifier
                                    .padding(1.dp, 10.dp, 1.dp, 2.dp)
                                    .alignByBaseline()


                            ) {
                                Text(
                                    text = TimeProperties.getDate(
                                        chatMessage.created_at!!.toLong(),
                                        "hh:mm"
                                    ),
                                    style = MaterialTheme.typography.caption,
                                    color = textColor
                                )
                            }
                            if (isMe)
                                Box(
                                    contentAlignment = Alignment.BottomStart,
                                    modifier = Modifier
                                        .padding(1.dp, 10.dp, 1.dp, 2.dp)
                                        .alignByBaseline(),


                                    ) {
                                    Image(

                                        painterResource(stateImage),
                                        "content description"
                                    )


                                }
                        }
                        if (showDialog.value)
                            fullScreenDialog({ showDialog.value = false }, path as Uri)
//                        Text(text = "Happy Birthday!", modifier = Modifier.align(Alignment.BottomCenter))
                    }
                }
            }
        }
    }
}


    @Composable
    fun fullScreenDialog(function: () -> Unit, path : Uri) {
        val scale = remember {
            mutableStateOf(1f)
        }
        Dialog(onDismissRequest = function) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(16.dp),
                color = Color.LightGray
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = path,
                        placeholder = painterResource(R.drawable.backgrounblack),
                        error = painterResource(R.drawable.backgrounblack)

                    ),

                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTransformGestures { _, _, zoom, _ ->
                                scale.value *= zoom
                            }

                        }
                        .graphicsLayer(
                            scaleX = maxOf(.5f, minOf(scale.value, 3f)),
                            scaleY = maxOf(.5f, minOf(scale.value, 3f))

                        )

                )

            }
        }
    }




