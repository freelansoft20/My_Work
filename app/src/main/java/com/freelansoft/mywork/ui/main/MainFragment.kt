package com.freelansoft.mywork.ui.main

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.freelansoft.mywork.R
import com.freelansoft.mywork.dto.Event
import com.freelansoft.mywork.dto.Photo
import com.freelansoft.mywork.dto.Plant
import com.freelansoft.mywork.dto.Specimen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.main_fragment.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainFragment : DiaryFragment() {

    private val CAMERA_REQUEST_CODE: Int = 1998
    private val IMAGE_GALLERY_REQUEST_CODE: Int = 2001
    private val LOCATION_PERMISSION_REQUEST_CODE = 2000
    private val AUTH_REQUEST_CODE = 2002

//    private lateinit var applicationViewModel: ApplicationViewModel
    private var _plantId = 0
    private var user : FirebaseUser? = null
    private var specimen = Specimen()
    private var _events = ArrayList<Event>()
    var selectedPlant: Plant = Plant("", "", "")

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

        viewModel.specimens.observe(viewLifecycleOwner, Observer {
            specimens -> spnSpecimens.setAdapter(ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, specimens))
        })


        /**
         * An existing item was clicked from the predefined autocomplete list.
         */
        actPlantName.setOnItemClickListener { parent, view, position, id ->
            selectedPlant = parent.getItemAtPosition(position) as Plant
            _plantId = selectedPlant.plantId
        }

        btnTakePhoto.setOnClickListener {
            prepTakePhoto()
        }

        btnLogon.setOnClickListener {
            logon()
//            prepOpenImageGallery()
        }

        btnSave.setOnClickListener {
            saveSpecimen()
        }

        spnSpecimens.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            /**
             * Callback method to be invoked when the selection disappears from this
             * view. The selection can disappear for instance when touch is activated
             * or when the adapter becomes empty.
             *
             * @param parent The AdapterView that now contains no selected item.
             */
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            /**
             *
             * Callback method to be invoked when an item in this view has been
             * selected. This callback is invoked only when the newly selected
             * position is different from the previously selected position or if
             * there was no selected item.
             *
             * Implementers can call getItemAtPosition(position) if they need to access the
             * data associated with the selected item.
             *
             * @param parent The AdapterView where the selection happened
             * @param view The view within the AdapterView that was clicked
             * @param position The position of the view in the adapter
             * @param id The row id of the item that is selected
             */
            override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
            ) {
                specimen = parent?.getItemAtPosition(position) as Specimen
                // use this specimen object to populate our UI fields
                actPlantName.setText(specimen.plantName)
                txtDescription.setText(specimen.description)
                btnDatePlanted.setText(specimen.datePlanted)
                viewModel.specimen = specimen
                // trigger an update of the events for this specimen.
                viewModel.fetchEvents()
            }

        }

        rcyEventsForSpecimens.hasFixedSize()
        rcyEventsForSpecimens.layoutManager = LinearLayoutManager(context)
        rcyEventsForSpecimens.itemAnimator = DefaultItemAnimator()
        rcyEventsForSpecimens.adapter = EventsAdapter(_events, R.layout.rowlayout)

        viewModel.events.observe(viewLifecycleOwner, Observer {
            events ->
            // remove everthing that is in there.
            _events.removeAll(_events)
            // update with the new events that we have observed.
            _events.addAll(events)
            // tell the recycler view to update.
            rcyEventsForSpecimens.adapter!!.notifyDataSetChanged()
        })
    }

    private fun logon() {
        var providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build()
//                ,
//                AuthUI.IdpConfig.GoogleBuilder().build()
        )
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(), AUTH_REQUEST_CODE
        )
    }

    internal fun saveSpecimen() {
        if (user == null){
            storeSpecimen()
        }
        user ?: return

        storeSpecimen()

        viewModel.save(specimen, photos, user!!)

        specimen = Specimen()
        photos  = ArrayList<Photo>()
    }

    /**
     * Populate a specimen object based on the details entered into the user interface.
     */
    internal fun storeSpecimen() {
        specimen.apply {
            latitude = lblLatitudeValue.text.toString()
            longitude = lblLongitudeValue.text.toString()
            plantName = actPlantName.text.toString()
            description = txtDescription.text.toString()
            datePlanted = btnDatePlanted.text.toString()
            plantId = _plantId
        }
        viewModel.specimen = specimen
    }

        private fun prepOpenImageGallery() {
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
            startActivityForResult(this, IMAGE_GALLERY_REQUEST_CODE)
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

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE)  {
                if (requestCode == CAMERA_REQUEST_CODE) {
                    // now we can get the thumbnail
                    val imageBitmap = data!!.extras!!.get("data") as Bitmap
                } else if (requestCode == SAVE_IMAGE_REQUEST_CODE) {
                    Toast.makeText(context, "Image Saved", Toast.LENGTH_LONG).show()
                    var photo = Photo(localUri = photoURI.toString())
                    photos.add(photo)
                } else if (requestCode == IMAGE_GALLERY_REQUEST_CODE) {
                    if (data != null && data.data != null) {
                        val image = data.data
                        val source = ImageDecoder.createSource(requireActivity().contentResolver, image!!)
                        val bitmap = ImageDecoder.decodeBitmap(source)

                    }
                }else if (requestCode == AUTH_REQUEST_CODE) {
                    user = FirebaseAuth.getInstance().currentUser
                }
            }
        }
    }


}