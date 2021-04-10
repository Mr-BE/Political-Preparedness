package com.example.android.politicalpreparedness.representative

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.App
import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.data.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListener
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.util.Locale
import java.util.jar.Manifest

class DetailFragment : Fragment(), AdapterView.OnItemSelectedListener {
    //COMPLETED: Declare ViewModel
    private lateinit var repViewModel: RepresentativeViewModel

    private lateinit var binding: FragmentRepresentativeBinding
    private lateinit var repsAdapter: RepresentativeListAdapter

    companion object {
        //COMPLETED: Add Constant for Location request
        private const val LOCATION_REQUEST_CODE = 19
    }

//17939 KIETH HARROW BLVD STE 106,HOUSTON, TX 77084-5724


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        //COMPLETED: Establish bindings
        binding = FragmentRepresentativeBinding.inflate(inflater, container, false)

        val source = ElectionDatabase.getInstance(
                requireNotNull(this.activity).application).electionDao


        repViewModel = ViewModelProvider(this,
                RepresentativeViewModel.RepresentativeViewModelFactory(source))
                .get(RepresentativeViewModel::class.java)

        binding.viewModel = repViewModel
        binding.lifecycleOwner = this

        //Spinner setup
        val spinner: Spinner = binding.stateSpinner
        spinner.onItemSelectedListener = this


// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(requireContext(),
                R.array.states_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            //specify layout
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        binding.buttonSearch.setOnClickListener {
            repViewModel.setUpAddress()
            hideKeyboard()
        }
//        repViewModel.state.observe(viewLifecycleOwner, {
//            val s = it
//        })


        repViewModel.userAddress.observe(viewLifecycleOwner, {
            repViewModel.fetchReps(it)
        })


        //COMPLETED: Define and assign Representative adapter
        repsAdapter = RepresentativeListAdapter()
        binding.repsList.adapter = repsAdapter
//        repViewModel.representatives.observe(viewLifecycleOwner, {
//            repsAdapter.submitList(it)
//        })

        //COMPLETED in XML: Populate Representative adapter

        //TODO: Establish button listeners for field and location search
        binding.buttonLocation.setOnClickListener {
            checkLocationPermissions()
            hideKeyboard()
        }
        return binding.root

    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //COMPLETED: Handle location permission result to get location on permission granted
        if (grantResults.isEmpty()
                || (requestCode == LOCATION_REQUEST_CODE
                        && grantResults[0] == PackageManager.PERMISSION_DENIED)) {
            Snackbar.make(binding.root, "Please grant permissions first", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Okay") {
                        startActivity(Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                    }.show()
        } else {
            //prevent infinite loop
            checkLocationPermissions()
        }
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            getLocation()
            true
        } else {
            val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            requestPermissions(
                    permissions,
                    LOCATION_REQUEST_CODE
            )
            false
        }
    }

    //COMPLETED: Check if permission is already granted and return (true = granted, false = denied/other)
    private fun isPermissionGranted(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ContextCompat
                .checkSelfPermission(requireContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        //COMPLETED: Get location from LocationServices
        val locationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        locationClient.lastLocation
                .addOnSuccessListener {
                    if (it != null) {
                        repViewModel.getAddressFromGeo(geoCodeLocation(it))
                    } else {
                        Timber.e("User Location not valid")
                        Toast.makeText(requireContext(),
                                "Error: location not valid", Toast.LENGTH_SHORT).show()

                    }
                }.addOnFailureListener { error ->
                    Timber.e("Could not get user Location: Reason -> ${error.message}")
                }
    }
    //COMPLETED: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                }
                .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    //handle spinner click event
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        repViewModel.state.value = parent?.getItemAtPosition(pos).toString()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        repViewModel.state.value = ""
    }


}