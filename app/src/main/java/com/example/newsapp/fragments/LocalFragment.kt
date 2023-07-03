package com.example.newsapp.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.R
import com.example.newsapp.adapters.NewsAdapter
import com.example.newsapp.ui.NewsViewModel
import com.example.newsapp.util.Resource
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.fragment_local.*
import java.util.*


class LocalFragment : Fragment(R.layout.fragment_local) {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    val TAG = "LocalFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        setupRecycler()
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        //open article if clicked
        newsAdapter.setOnItemClickListener {
            val intent = Intent(context, ArticleActivity::class.java)
            startActivity(intent.putExtra("data", it.url))
        }

        getCurrentLocation()

        //Populate recyclerview with content
        viewModel.localNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hidePB()
                    response.data?.let { newsResponse -> newsAdapter.differ.submitList(newsResponse.articles) }
                }
                is Resource.Error -> {
                    hidePB()
                    response.message?.let { message ->
                        Log.e(TAG, "An Error occurred: $message")
                    }
                }
                is Resource.Loading -> {
                    showPB()
                }
            }
        })
    }

    private fun hidePB() {
        paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun showPB() {
        paginationProgressBar.visibility = View.VISIBLE
    }

    private fun setupRecycler() {
        newsAdapter = NewsAdapter()
        rv_local.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    //gets the current location of the user if they have their location turned on and have
    // granted permission
    private fun getCurrentLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermission()
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        Toast.makeText(context, "Null Received", Toast.LENGTH_SHORT).show()
                    } else {
                        var geocoder: Geocoder = Geocoder(requireContext(), Locale.getDefault())

                        //returns the current location and converts that into a city name, this is
                        //then displayed to the user and news is fetched with querying the location
                        //name
                        var addresses: MutableList<Address> =
                            geocoder.getFromLocation(
                                location.latitude,
                                location.longitude,
                                1
                            ) as MutableList<Address>

                        tvCurrentLocation.text = ("Current Location : " + addresses.get(0).locality)
                        viewModel.getLocalNews(addresses.get(0).locality)

                    }
                }
            } else {
                Toast.makeText(context, "Turn on location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermission()
        }
    }

    //check if the location is enabled
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    //request permission to use the location
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_REQUEST_LOCATION
        )
    }

    companion object {
        private const val PERMISSION_REQUEST_LOCATION = 100
    }

    //check if the app has permission to use the location
    private fun checkPermissions(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}