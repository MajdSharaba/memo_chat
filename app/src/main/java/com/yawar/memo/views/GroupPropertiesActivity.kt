package com.yawar.memo.views

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yawar.memo.Api.ServerApi
import com.yawar.memo.R
import com.yawar.memo.adapter.ContactNumberAdapter
import com.yawar.memo.model.SendContactNumberResponse
import java.io.ByteArrayOutputStream
import java.io.IOException


class GroupPropertiesActivity : AppCompatActivity() {
    var sendContactNumberResponses: ArrayList<SendContactNumberResponse?>? = ArrayList()
    lateinit var recyclerView: RecyclerView
    lateinit var mainAdapter: ContactNumberAdapter
    lateinit var bitmap: Bitmap
    lateinit var edName: EditText
    var imageString = ""
    var imageUri: Uri? = null
    private lateinit var image: ImageView
    lateinit var group_name: TextView
    lateinit var memebers: TextView
    lateinit var serverApi: ServerApi
    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_properties)
        val intent = intent
        val bundle = intent.extras
        serverApi = ServerApi(this)
        image = findViewById(R.id.imageProfile)
        edName = findViewById(R.id.et_gName)
        group_name = findViewById(R.id.group_name)
        memebers = findViewById(R.id.memebers)
        sendContactNumberResponses =
            bundle!!.getSerializable("newPlaylist") as ArrayList<SendContactNumberResponse?>?
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mainAdapter = ContactNumberAdapter(this, sendContactNumberResponses)
        recyclerView.adapter = mainAdapter
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val id = ArrayList<String?>()
            for (`object` in sendContactNumberResponses!!) {
                id.add(`object`!!.id)
            }
            val name = edName.text.toString()

        }
        image.setOnClickListener(View.OnClickListener { openGallery() })
    }

    private fun openGallery() {
        ImagePicker.with(this)
            .crop() //Crop image(Optional), Check Customization for more option
            .compress(1024) //Final image size will be less than 1 MB(Optional)
            .maxResultSize(
                1080,
                1080
            ) //Final image resolution will be less than 1080 x 1080(Optional)
            .start()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            imageUri = data!!.data
            try {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val imageBytes = baos.toByteArray()
            imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT)
            image.setImageURI(imageUri)
        }
    }
}