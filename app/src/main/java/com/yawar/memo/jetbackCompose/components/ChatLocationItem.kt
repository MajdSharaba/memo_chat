package com.yawar.memo.jetbackCompose.components

import android.content.Intent
import android.os.Bundle
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
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.size.Scale
import coil.transform.CircleCropTransformation
import com.yawar.memo.BaseApp
import com.yawar.memo.R
import com.yawar.memo.constant.AllConstants
import com.yawar.memo.domain.model.AnthorUserInChatRoomId
import com.yawar.memo.domain.model.SpecialMessageModel
import com.yawar.memo.jetbackCompose.ui.theme.DarkSendMessage
import com.yawar.memo.jetbackCompose.ui.theme.LightReciveMessage
import com.yawar.memo.jetbackCompose.ui.theme.SelectedColor
import com.yawar.memo.jetbackCompose.ui.theme.isNightMode
import com.yawar.memo.ui.chatPage.ConversationActivity
import com.yawar.memo.utils.TimeProperties
import com.yawar.memo.utils.getFile

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatLocationItem(chatMessage: SpecialMessageModel,function: () -> Unit) {
    val userModel = BaseApp.instance?.classSharedPreferences?.user
    val context = LocalContext.current
    val  anthorUserInChatRoomId = AnthorUserInChatRoomId.getInstance("","","","","","","","")
    val  isMe = chatMessage.sender_id == userModel?.userId
    var stateImage  = R.drawable.ic_recive_done
    val file = getFile(chatMessage,isMe,"video")
    val imageForDownloadOrPlay = if (file.exists()) R.drawable.ic_play
    else R.drawable.ic_download_arrow
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
            ),

        shape = RoundedCornerShape(8.dp), elevation = 4.dp
    ) {
        Surface(color = backgroundColor.copy(alpha)) {

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
                            modifier = Modifier.padding(1.dp).alignByBaseline(),
                           color = MaterialTheme.colors.onBackground


                        )
                        Icon(painter = painterResource(R.drawable.ic_arrow_rightt) ,
                            contentDescription = "" ,
                            modifier = Modifier.align(Alignment.CenterVertically
                            ).padding(1.dp),
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
                            modifier = Modifier.padding(1.dp).alignByBaseline(),
                            color = MaterialTheme.colors.onBackground



                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(
                                color = background,
                                shape = RoundedCornerShape(15.dp),


                                )
                            .padding(2.dp)
                            .clickable {
                                val intent = Intent(context, GoogleMapView::class.java)
                                val bundle = Bundle()
                                bundle.putString("location", chatMessage.message)
                                intent.putExtras(bundle)
                                context.startActivity(intent)
                            }

                    ) {
                        Column(modifier = Modifier
                            .height(150.dp)
                            .width(250.dp)

                            .clip(RoundedCornerShape(10.dp))
                            .fillMaxSize()) {

                            Row(
                                Modifier.background(
                                    color = colorResource(R.color.colorPrimaryDark),
                                    shape = RoundedCornerShape(0.dp),
                                ).fillMaxWidth()
                            ) {
                                Image(
                                    painter = rememberImagePainter(
                                        data = R.mipmap.ic_logo,

                                        builder = {
                                            scale(Scale.FILL)
                                            error(R.mipmap.ic_logo)
                                            transformations(CircleCropTransformation())
                                            placeholder(R.mipmap.ic_logo)
                                        }
                                    ),

                                    contentDescription = chatMessage.sender_id,
                                    modifier = Modifier
                                        .alignByBaseline()
                                        .size(40.dp)
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(80)),
                                    contentScale = ContentScale.Crop,

                                    )

                                Text(
                                    text = stringResource(R.string.location),
                                    color = textColor,
                                    style = MaterialTheme.typography.subtitle1,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.CenterVertically)

                                )
                            }

                            Image(
                                painter = rememberAsyncImagePainter(
                                    model = R.drawable.image_google_map,
                                    placeholder = painterResource(R.drawable.backgrounblack),
                                    error = painterResource(R.drawable.backgrounblack)

                                ),

                                contentDescription = chatMessage.fullname,
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(2.dp))
                                    .fillMaxWidth()
                                    .weight(1.1f)



                            )
                            Row(
                                Modifier
                                    .padding(1.dp)
                                    .weight(0.4f)
                                    .align(Alignment.End)

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