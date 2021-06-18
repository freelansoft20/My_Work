package com.freelansoft.mywork.ui.main

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.freelansoft.mywork.R
import com.freelansoft.mywork.dto.Plant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.main_fragment.*
import java.util.ArrayList
import java.util.jar.Manifest

class MainFragment : Fragment() {

    private val CAMERA_REQUEST_CODE: Int = 1998
    private val IMAGE_GALLERY_REQUEST_CODE: Int = 2001
    private val LOCATION_PERMISSION_REQUEST_CODE = 2000
    private val AUTH_REQUEST_CODE = 2002
    val CAMERA_PERMISSION_REQUEST_CODE = 1997
//    private lateinit var applicationViewModel: ApplicationViewModel
//    private var _plantId = 0
//    private var user : FirebaseUser? = null
//    private var photos : ArrayList<Photo> = ArrayList<Photo>()
//    private var specimen = Specimen()
//    private var _events = ArrayList<Event>()
//    var selectedPlant: Plant = Plant("", "", "")

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.plants.observe(viewLifecycleOwner, Observer {
            plants -> actPlantName.setAdapter(ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, plants))
        })

        btnTakePhoto.setOnClickListener {
            prepTakePhoto()
        }
    }

    private fun prepTakePhoto() {

        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            takePhoto()
        }else{
            val permissionRequest = arrayOf(android.Manifest.permission.CAMERA);
            requestPermissions(permissionRequest, CAMERA_PERMISSION_REQUEST_CODE)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==  PackageManager.PERMISSION_GRANTED) {
                    takePhoto()
                } else {
                    Toast.makeText(context, "Unable to update location without permission", Toast.LENGTH_LONG).show()
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    private fun takePhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
            takePictureIntent -> takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
        }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE)  {
                // now we can get the thumbnail
                val imageBitmap = data!!.extras!!.get("data") as Bitmap

            }
        }
    }

}