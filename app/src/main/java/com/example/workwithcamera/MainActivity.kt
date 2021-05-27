package com.example.workwithcamera

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var currentPhotoPath: String;
    lateinit var photoURI:Uri;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bt_open_camera.setOnClickListener {
            var permissions =
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            requestPermission.launch(permissions)
        }
    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    var requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGranted ->
            if (isGranted.getValue("android.permission.WRITE_EXTERNAL_STORAGE") &&
                isGranted.getValue("android.permission.CAMERA") &&
                isGranted.getValue("android.permission.READ_EXTERNAL_STORAGE")
            ) {
                dispatchTakePictureIntent()
            }
        }

    var getCamera = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            imageView.setImageURI(photoURI)
        }
    }

    private fun dispatchTakePictureIntent() {
        // Criando o caminho em que a foto será inserida
        val photoFile: File? = try {
            createImageFile()
        } catch (e: IOException) {
            Log.e("ERROR", e.toString())
            null
        }
        photoFile?.also { file ->
            photoURI = FileProvider.getUriForFile(
                this,
                packageName,
                file
            )
            getCamera.launch(photoURI)
        }
    }

}