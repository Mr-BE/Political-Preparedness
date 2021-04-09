package com.example.android.politicalpreparedness.representative

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.data.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import java.util.Locale

class DetailFragment : Fragment(), AdapterView.OnItemClickListener {
    //COMPLETED: Declare ViewModel
    private lateinit var repViewModel: RepresentativeViewModel

    private lateinit var binding: FragmentRepresentativeBinding

    companion object {
        //TODO: Add Constant for Location request
    }

//17939 KIETH HARROW BLVD STE 106,HOUSTON, TX 77084-5724


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //COMPLETED: Establish bindings
        binding = FragmentRepresentativeBinding.inflate(inflater)

        val source = ElectionDatabase.getInstance(
                requireNotNull(this.activity).application).electionDao


        repViewModel = ViewModelProvider(this,
                RepresentativeViewModel.RepresentativeViewModelFactory(source))
                .get(RepresentativeViewModel::class.java)

        binding.viewModel = repViewModel
        binding.lifecycleOwner = this

        //Spinner setup
        val spinner: Spinner = binding.stateSpinner


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
        }


        repViewModel.userAddress.observe(viewLifecycleOwner, {
            repViewModel.fetchReps(it)
        })


        //TODO: Define and assign Representative adapter

        //TODO: Populate Representative adapter

        //TODO: Establish button listeners for field and location search
        return binding.root

    }

    //handle spinner click event
    override fun onItemClick(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        repViewModel.state.postValue(parent?.getItemAtPosition(pos).toString())
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //TODO: Handle location permission result to get location on permission granted
    }

    private fun checkLocationPermissions(): Boolean {
        return isPermissionGranted()
    }

    private fun isPermissionGranted(): Boolean {
        //TODO: Check if permission is already granted and return (true = granted, false = denied/other)
        return false
    }

    private fun getLocation() {
        //TODO: Get location from LocationServices
        //TODO: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
    }

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
//        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }


}