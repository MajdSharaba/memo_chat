package com.yawar.memo.jetbackCompose.favouriteActivity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.yawar.memo.R
import com.yawar.memo.domain.model.SpecialMessageModel
import com.yawar.memo.jetbackCompose.components.*
import com.yawar.memo.jetbackCompose.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow


@AndroidEntryPoint
class FavouriteActivity : ComponentActivity() {
    private val mainViewModel by viewModels<FavouriteViewModel>()
    lateinit var count: Flow<List<SpecialMessageModel>>
    lateinit var showMenu : MutableState<Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
             showMenu = remember {
                mutableStateOf(false)
            }
            this.window.statusBarColor = if (isNightMode()) {
                TealDark200.toArgb()

            } else {
                Teal200.toArgb()
            }

            this.window.navigationBarColor = if (isNightMode()) {
                Color.Black.toArgb()

            } else {
                Color.White.toArgb()
            }


            val backgroundColor =
                colorResource(id = R.color.backgroundColor)
            ChatMemoTheme {

                var devicesState = count.collectAsState(initial = emptyList())


                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    CreateScaffold(devicesState, showMenu)
                }
            }
        }
    }

    override fun onResume() {
        Log.d("FavouriteActivity", "onResume: ")
        count = mainViewModel.getSpecialMessages()

        super.onResume()
    }


    @Composable
    fun Greeting(name: State<List<SpecialMessageModel>>) {
        Log.d(name.value.size.toString(), "Greeting: ")
        showMenu.value = false

//    var devicesState = name.collectAsState(initial = emptyList())
        LazyColumn {

            itemsIndexed(items = name.value) { index, item ->
                if(item.isChecked){
                    showMenu.value = true
                }
                when (item.message_type) {

                    "imageWeb" -> {
                        ChatImageItem(item) {
                            if (item.isChecked) {
                                mainViewModel.setMessageChecked(
                                    item.message_id,
                                    false
                                )
                            } else {
                                mainViewModel.setMessageChecked(
                                    item.message_id,
                                    true
                                )
                            }
                        }
                    }
                    "voice" -> {
                        ChatVoiceItem(item){
                            if (item.isChecked) {
                                mainViewModel.setMessageChecked(
                                    item.message_id,
                                    false
                                )
                            } else {
                                mainViewModel.setMessageChecked(
                                    item.message_id,
                                    true
                                )
                            }
                        }
                    }
                    "text" -> {
                        ChatTextItem(item)
                        {
                            if (item.isChecked) {
                                mainViewModel.setMessageChecked(
                                    item.message_id,
                                    false
                                )
                            } else {
                                mainViewModel.setMessageChecked(
                                    item.message_id,
                                    true
                                )
                            }
                        }
                    }
                    "video" -> {
                        ChatVideoItem(item)
                        {
                            if (item.isChecked) {
                                mainViewModel.setMessageChecked(
                                    item.message_id,
                                    false
                                )
                            } else {
                                mainViewModel.setMessageChecked(
                                    item.message_id,
                                    true
                                )
                            }
                        }
                        }

                    "location" -> {
                        ChatLocationItem(item)
                        {
                            if (item.isChecked) {
                                mainViewModel.setMessageChecked(
                                    item.message_id,
                                    false
                                )
                            } else {
                                mainViewModel.setMessageChecked(
                                    item.message_id,
                                    true
                                )
                            }
                        }
                    }
                    "file" -> {
                        ChatFileItem(item)
                        {
                            if (item.isChecked) {
                                mainViewModel.setMessageChecked(
                                    item.message_id,
                                    false
                                )
                            } else {
                                mainViewModel.setMessageChecked(
                                    item.message_id,
                                    true
                                )
                            }
                        }
    }
                    "contact" -> {
                        ChatContactNumberItem(item)
                        {
                            if (item.isChecked) {
                                mainViewModel.setMessageChecked(
                                    item.message_id,
                                    false
                                )
                            } else {
                                mainViewModel.setMessageChecked(
                                    item.message_id,
                                    true
                                )
                            }
                        }
                    }

                }
            }
        }
    }


    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    fun CreateScaffold(
        devicesState: State<List<SpecialMessageModel>>,
        showMenu: MutableState<Boolean>
    ) {
        val scaffoldState = rememberScaffoldState()
        Scaffold(
            topBar = { TopAppBarCompose(showMenu){
                finish()
            } }, scaffoldState = scaffoldState
        ) {
            Greeting(devicesState)
        }
    }


    @Composable
    fun loadPicture(picUri: Uri): MutableState<Bitmap?> {
        // the persisted state of helper
        val bitmapState: MutableState<Bitmap?> = remember {
            mutableStateOf(null)
        }
        // current context
        val context = LocalContext.current
        // glide execution control
        LaunchedEffect(picUri) {
            Log.d("utils", "loading image: $picUri")
            Glide.with(context).asBitmap().load(picUri)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {

                        Log.d("utils", "setting bitmap")
                        bitmapState.value = resource

                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        bitmapState.value = null
                    }
                })
        }

        return bitmapState
    }

}


