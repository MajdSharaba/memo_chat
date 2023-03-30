package com.yawar.memo.jetbackCompose.components
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.yawar.memo.R
import com.yawar.memo.jetbackCompose.ui.theme.Teal200
import com.yawar.memo.jetbackCompose.ui.theme.TealDark200
import com.yawar.memo.jetbackCompose.ui.theme.isNightMode

@SuppressLint("SuspiciousIndentation")
@Composable
fun TopAppBarCompose(showMenu: MutableState<Boolean>, finish: () -> Unit) {

    TopAppBar(contentColor = Color.White, title = { Text(text = stringResource(id = R.string.title_activity_favourite)) }, navigationIcon = {
//        if(showMenu.value)
        IconButton(onClick = {
            Log.d("TopAppBarCompose", "TopAppBar menu clicked")
            finish()

        }) {

            Icon(painter = painterResource(R.drawable.ic_arrow_back), contentDescription = null)
        }
    },
        actions = {
            if(showMenu.value)
            IconButton(onClick = {
                finish()

            }) {

                Icon(painter = painterResource(R.drawable.ic_un_special),
                    contentDescription = null,
                tint = colorResource(id = R.color.white))
            }
        }
    , backgroundColor = if (isNightMode()) {
        TealDark200

    } else {
        Teal200
    })
}