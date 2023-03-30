package com.yawar.memo.jetbackCompose.components

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
import com.yawar.memo.jetbackCompose.ui.theme.DarkSendMessage
import com.yawar.memo.jetbackCompose.ui.theme.LightReciveMessage
import com.yawar.memo.jetbackCompose.ui.theme.SelectedColor
import com.yawar.memo.jetbackCompose.ui.theme.isNightMode
import com.yawar.memo.ui.chatPage.ConversationActivity
import com.yawar.memo.utils.ImageProperties
import com.yawar.memo.utils.TimeProperties
import com.yawar.memo.utils.getFile

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatFileItem(chatMessage: SpecialMessageModel, function: () -> Unit) {
    val userModel = BaseApp.instance?.classSharedPreferences?.user
    val  isMe = chatMessage.sender_id == userModel?.userId
    val  anthorUserInChatRoomId = AnthorUserInChatRoomId.getInstance("","","","","","","","")
    var stateImage  = R.drawable.ic_recive_done
    val context = LocalContext.current
    val file = getFile(chatMessage,isMe,"")
    val path = if (file.exists()) FileProvider.getUriForFile(
        LocalContext.current, BuildConfig.APPLICATION_ID + ".fileprovider", file
    ) else R.drawable.backgrounblack

    val bmp = if(file.exists()) ImageProperties.getImageFromPdf(path as Uri, context) else null
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

    when (chatMessage.state) {
        "3" -> {
            stateImage =  R.drawable.ic_recive_done_green
        }
        "2" -> {
            stateImage =  R.drawable.ic_recive_done

        }
        "1" -> {
            stateImage =  R.drawable.ic_send_done
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

                }
            )



                ,
        shape = RoundedCornerShape(8.dp), elevation = 4.dp
    ) {
        Surface(color = backgroundColor.copy(alpha)) {

            Row(
                Modifier
                    .padding(6.dp)
                    .fillMaxSize()

            ) {

                Image(
                    painter = rememberAsyncImagePainter(
//                        model = AllConstants.imageUrl + chatMessage.profile_image,
                        model = if (isMe){
                            AllConstants.imageUrl + userModel?.image
                            }else {
                            AllConstants.imageUrl + chatMessage.profile_image
                        },
                            placeholder = painterResource(R.drawable.th),
                            error = painterResource(R.drawable.th),

                        ),
                    contentDescription = chatMessage.sender_id,
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
                            color = MaterialTheme.colors.onBackground,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(
                                color = background,
                                shape = RoundedCornerShape(15.dp),


                                )
                            .padding(3.dp)
                            .clickable {
                                if (file.exists()) {
                                    Log.d("clickable", "ChatFileItem: ")
                                    val pdfIntent = Intent(Intent.ACTION_VIEW)
                                    pdfIntent.setDataAndType(path as Uri, "application/pdf")
                                    pdfIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    try {
                                        context.startActivity(pdfIntent)
                                    } catch (e: ActivityNotFoundException) {
                                    }
                                }
                            }

                    ) {
                        Column(modifier = Modifier
                            .width(200.dp)
                            .height(IntrinsicSize.Max)
                            .clip(RoundedCornerShape(10.dp))
                            .fillMaxSize()) {

                            if(file.exists())
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        model = bmp,
                                        placeholder = painterResource(R.drawable.backgrounblack),
                                        error = painterResource(R.drawable.backgrounblack)

                                    ),
                                    contentDescription = chatMessage.fullname,
                                    contentScale = ContentScale.FillBounds,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(2.dp))
                                        .fillMaxWidth()
                                        .weight(1.1f)
                                        .height(100.dp)
                                        .clickable {
                                            Log.d("imageClick", "ChatFileItem: ")
                                        }
                                )
                            Row(
                                Modifier
                                    .padding(1.dp)
                                    .weight(0.3f)
                                    .fillMaxWidth()

                                    .background(
                                        color = colorResource(R.color.cardview_light_background),
                                        shape = RoundedCornerShape(5.dp),


                                        ),
                                verticalAlignment = Alignment.CenterVertically

                            ) {

                                Image(painter = painterResource(R.drawable.ic_pdf_icon) ,
                                    contentDescription = "" , Modifier.weight(0.5f))

                                Text(text = chatMessage.orginalName,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .padding(3.dp)
                                        .weight(2f),
                                    color = colorResource(R.color.gray),
                                )
                                if(!file.exists())
                                    Image(painter = painterResource(R.drawable.ic_download) ,
                                        contentDescription = "" , Modifier.weight(0.5f))

                            }

                            Row(
                                Modifier
                                    .padding(6.dp)
                                    .align(Alignment.End)
                                    .weight(0.3f)


                            ) {

                                Box(
                                    contentAlignment = Alignment.BottomStart,
                                    modifier = Modifier
                                        .padding(1.dp, 10.dp, 1.dp, 2.dp)
                                        .alignByBaseline()


                                ) {
                                    Text(
                                        text =   TimeProperties.getDate(chatMessage.created_at!!.toLong(), "hh:mm") ,
                                        style = MaterialTheme.typography.caption ,
                                        color = textColor
                                    )
                                }
                                if(isMe)
                                    Box(
                                        contentAlignment = Alignment.BottomStart,
                                        modifier = Modifier
                                            .padding(1.dp, 10.dp, 1.dp, 2.dp)
                                            .alignByBaseline(),




                                        ) {
                                        Image(

                                            painterResource(stateImage),
                                            "content description")


                                    }
                            }

                        }


                    }
                }
            }
        }
    }

}