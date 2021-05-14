package com.schuster.permissionsusers

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.schuster.permissionsusers.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {

        private const val PERMISSION_PICK_IMAGE_CODE = 1000
        private const val PICK_IMAGE_CODE = 1001
        private const val PERMISSION_CAMERA_CAPTURE_CODE = 2000
        private const val OPEN_CAMERA_CODE = 2001
    }

    var image_uri: Uri? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initButtons()
    }

    private fun initButtons() {
        binding.pickButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED) {
                    val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permission, PERMISSION_PICK_IMAGE_CODE)

                } else {
                    pickImageFromGallery()
                }

            } else {
                pickImageFromGallery()
            }
        }

        binding.openCameraButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED) {

                    val permission = arrayOf(Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    requestPermissions(permission, PERMISSION_CAMERA_CAPTURE_CODE)

                } else {
                    openCamera()
                }

            } else {
                openCamera()
            }
        }
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "nova foto")
        values.put(MediaStore.Images.Media.DESCRIPTION, "imagem caopturada pela câmera")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, OPEN_CAMERA_CODE)
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_CODE) {
            binding.imageFind.setImageURI(data?.data)
        }
        if (resultCode == Activity.RESULT_OK && requestCode == OPEN_CAMERA_CODE) {
            binding.imageFind.setImageURI(image_uri)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_PICK_IMAGE_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery()

                } else {
                    Toast.makeText(this, "Permissão negada", Toast.LENGTH_SHORT).show()
                }
            }

            PERMISSION_CAMERA_CAPTURE_CODE -> {
                if (grantResults.size > 1 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    Toast.makeText(this, "Permissão negada", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}