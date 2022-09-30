package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.utils.LocationPermissionsUtil
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.android.material.snackbar.Snackbar
import java.util.Locale
private const val TAG = "RepresentativeFragment"
class RepresentativeFragment : Fragment(),  LocationPermissionsUtil.PermissionListener {

    companion object {
        //TODO: Add Constant for Location request
        private const val PERMISSIONS_ACCESS_FINE_LOCATION = 1
        private const val REQUEST_TURN_DEVICE_LOCATION_ON = 2

    }

    //TODO: Declare ViewModel
    private val viewModel: RepresentativeViewModel by lazy {
        val application = requireNotNull(this.activity).application
        val viewModelFactory =  RepresentativeViewModel.Factory(application)
        ViewModelProvider(this,viewModelFactory)
            .get(RepresentativeViewModel::class.java)
    }

    private lateinit var binding: FragmentRepresentativeBinding
    private val permissionUtil = LocationPermissionsUtil(this)
    lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //TODO: Establish bindings
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_representative,
            container,
            false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.address = Address("", "", "", "", "")
        //TODO: Define and assign Representative adapter
        val states = resources.getStringArray(R.array.states)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, states)
        binding.state.adapter = adapter
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        binding.buttonLocation.setOnClickListener {
            permissionUtil.requestPermissions(this)
        }

        //TODO: Establish button listeners for field and location search
        binding.buttonSearch.setOnClickListener {
            hideKeyboard()
            viewModel.getListRepresentatives()
        }

        //TODO: Populate Representative adapter
        binding.representativesRecyclerView.adapter = RepresentativeListAdapter(RepresentativeListAdapter.RepresentativeListener {
            println(it)
        })

        return binding.root

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON ) {
            var permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

            val foregroundLocationApproved = (
                    PackageManager.PERMISSION_GRANTED ==
                            ActivityCompat.checkSelfPermission(requireContext(),
                                Manifest.permission.ACCESS_FINE_LOCATION))

            if(foregroundLocationApproved){
                if(resultCode == Activity.RESULT_OK){
                    getLocation()
                    Log.i(
                        TAG,
                        "REQUEST_TURN_DEVICE_LOCATION_ON SUCCESS!"
                    )
                }else{
                    checkDeviceLocationSettingsAndStartGeofence()
                }
            }else{
                if(!foregroundLocationApproved){
                    requestPermissions(
                        permissionsArray,
                        PERMISSIONS_ACCESS_FINE_LOCATION
                    )
                }
            }

        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //TODO: Handle location permission result to get location on permission granted
        when (requestCode) {
            PERMISSIONS_ACCESS_FINE_LOCATION -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    checkDeviceLocationSettingsAndStartGeofence()
                } else {
                    // do nothing

                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun checkLocationPermissions(view: View): Boolean {
        return if (isPermissionGranted()) {
            getLocation()
            true
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                checkDeviceLocationSettingsAndStartGeofence()
//                getLocation()
            } else {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_ACCESS_FINE_LOCATION)
            }
            false
        }


    }

    private fun isPermissionGranted() : Boolean {
        //TODO: Check if permission is already granted and return (true = granted, false = denied/other)
        return (ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        //TODO: Get location from LocationServices
        //TODO: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
        fusedLocationClient.getCurrentLocation(100, object : CancellationToken() {
            override fun isCancellationRequested(): Boolean {
                return false
            }

            override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
                return this
            }

        }).addOnSuccessListener { location ->
                if (location != null) {
//DONE: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
                    val address = geoCodeLocation(location)
                    viewModel.address.value = address

                    val states = resources.getStringArray(R.array.states)
                    val selectedStateIndex = states.indexOf(address.state)
                    binding.state.setSelection(selectedStateIndex)

                    viewModel.getListRepresentatives()
                }

        }.addOnFailureListener { e -> e.printStackTrace() }


//        val locationClient: FusedLocationProviderClient =
//            LocationServices.getFusedLocationProviderClient(requireContext())
//
//        locationClient?.lastLocation
//            .addOnSuccessListener { location ->
//                if (location != null) {
////DONE: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
//                    val address = geoCodeLocation(location)
//                    viewModel.address.value = address
//
//                    val states = resources.getStringArray(R.array.states)
//                    val selectedStateIndex = states.indexOf(address.state)
//                    binding.state.setSelection(selectedStateIndex)
//
//                    viewModel.getListRepresentatives()
//                }
//            }
//            .addOnFailureListener { e -> e.printStackTrace() }

    }
    private fun checkDeviceLocationSettingsAndStartGeofence(resolve:Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireContext())
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())
        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve){
                try {
                    startIntentSenderForResult(exception.resolution.intentSender, REQUEST_TURN_DEVICE_LOCATION_ON, null, 0, 0, 0, null)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(TAG, "Error getting location settings resolution: " + sendEx.message)
                }
            } else {
                // Explain user why app needs this permission
            }
        }
    }

    @RequiresPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
    override fun onGranted() {
        getLocation()
    }

    override fun onDenied() {
        showSnackbar(getString(R.string.error_location_denied))
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode ?: "")
                }
                .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    private fun showSnackbar(text: String) {
        Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
    }
}