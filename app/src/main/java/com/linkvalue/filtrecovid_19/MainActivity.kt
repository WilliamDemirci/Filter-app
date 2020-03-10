package com.linkvalue.filtrecovid_19

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object {
        private var buttonState: Boolean = false
        private var counter: Int = 0
        private const val PERMISSION_FINE_LOCATION = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()

        filterButton.setOnClickListener {
            when {
                buttonState -> {
                    setDisabledButton()
                }
                else -> {
                    setActivatedButton()
                }
            }
        }
    }

    private fun init() {
        disabledButtonTemplate() // by default, filter is disabled
        buttonState = false
        Log.i("MainActivity", "Initialised as disabled")
    }

    private fun setDisabledButton() {
        progressBarDisconnection.visibility = View.VISIBLE
        Handler().postDelayed(
            {
                progressBarDisconnection.visibility = View.INVISIBLE
                disabledButtonTemplate()
            },
            1500
        )
        buttonState = false

        Log.i("MainActivity", "Filter disabled")
    }

    private fun setActivatedButton() {
        if (permissionGranted() && isInternetConnection()) {

            progressBarConnection.visibility = View.VISIBLE
            Handler().postDelayed(
                {
                    progressBarConnection.visibility = View.GONE
                    activatedButtonTemplate()
                },
                3000
            )

            buttonState = true
            Log.i("MainActivity", "Filter activated")

            activationCounter() // count the number of activation to propose the Premium version
        } else if (!permissionGranted()) {
            locationSnackbar()
        } else if (!isInternetConnection()) {
            internetConnectionSnackbar()
        }
    }

    private fun permissionGranted(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_FINE_LOCATION
            )
            Log.i("MainActivity", "Ask for permission : ACCESS_FINE_LOCATION")
            false
        } else {
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("MainActivity", "Permission granted")
                    setActivatedButton()
                }
            }
        }
    }

    private fun activatedButtonTemplate() {
        filterButton.background = ContextCompat.getDrawable(this, R.color.colorPrimary)
        filterButton.text = getString(R.string.actived)
    }

    private fun disabledButtonTemplate() {
        filterButton.background = ContextCompat.getDrawable(this, R.color.colorAccent)
        filterButton.text = getString(R.string.disabled)
    }

    private fun activationCounter() {
        if (++counter > 5) { // if activated at least six times
            showPremiumSnackbar()
        }
        Log.i("MainActivity", "counter = $counter")
    }

    private fun showPremiumSnackbar() {
        val snackbar = Snackbar.make(
            rootLayout,
            getString(R.string.premium_snackbar_text),
            Snackbar.LENGTH_INDEFINITE
        )
        snackbar.setAction(getString(R.string.close_snackbar_text)) {
            Log.i("MainActivity", "Clicked on Snackbar : showPremiumSnackbar")
        }
        snackbar.setActionTextColor(Color.WHITE)
        snackbar.show()
        Log.i("MainActivity", "Show Snackbar : showPremiumSnackbar")
    }

    private fun internetConnectionSnackbar() {
        val snackbarNetwork = Snackbar.make(
            rootLayout,
            getString(R.string.internet_connection_error_snackbar_text),
            Snackbar.LENGTH_INDEFINITE
        )
        snackbarNetwork.setAction(getString(R.string.close_snackbar_text)) {
            Log.i("MainActivity", "Clicked on Snackbar : internetConnectionSnackbar")
        }
        snackbarNetwork.setActionTextColor(Color.WHITE)
        snackbarNetwork.show()
        Log.i("MainActivity", "Show Snackbar : internetConnectionSnackbar")
    }

    private fun locationSnackbar() {
        val snackbarNetwork = Snackbar.make(
            rootLayout,
            getString(R.string.location_error_snackbar_text),
            Snackbar.LENGTH_INDEFINITE
        )
        snackbarNetwork.setAction(getString(R.string.close_snackbar_text)) {
            Log.i("MainActivity", "Clicked on Snackbar : locationSnackbar")
        }
        snackbarNetwork.setActionTextColor(Color.WHITE)
        snackbarNetwork.show()
        Log.i("MainActivity", "Show Snackbar : locationSnackbar")
    }

    private fun isInternetConnection(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capability =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capability?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
    }
}
