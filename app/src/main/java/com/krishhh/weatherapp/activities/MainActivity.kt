package com.krishhh.weatherapp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.krishhh.weatherapp.utils.Constants
import com.krishhh.weatherapp.R
import com.krishhh.weatherapp.databinding.ActivityMainBinding
import com.krishhh.weatherapp.models.WeatherResponse
import com.krishhh.weatherapp.network.WeatherService
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private var mProgressDialog: Dialog? = null

    private lateinit var mSharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mSharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)

        setupUI()

        if (!isLocationEnabled()) {
            Toast.makeText(
                this,
                "Your location provider is turned off. Please turn it on.",
                Toast.LENGTH_SHORT
            ).show()

            // This will redirect you to settings from where you need to turn on the location provider.
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        } else {

            // Asking the location permission on runtime.
            Dexter.withActivity(this)
                .withPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted()) {
                            requestLocationData()
                        }

                        if (report.isAnyPermissionPermanentlyDenied) {
                            Toast.makeText(
                                this@MainActivity,
                                "You have denied location permission. Please allow it is mandatory.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        showRationalDialogForPermissions()
                    }
                }).onSameThread()
                .check()
        }
    }

    // A function which is used to verify that the location or GPS is enable or not of the user's device.
    private fun isLocationEnabled(): Boolean {

        // This provides access to the system location services.
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    // A function used to show the alert dialog when the permissions are denied and need to allow it from settings app info.
    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
            .setPositiveButton("GO TO SETTINGS") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    // A function to request the current location. Using the fused location provider client.
    @SuppressLint("MissingPermission")
    private fun requestLocationData() {

        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    // A location callback object of fused location provider client where we will get the current location details.
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {

            val mLastLocation: Location = locationResult.lastLocation
            val latitude = mLastLocation.latitude
            Log.i("Current Latitude", "$latitude")

            val longitude = mLastLocation.longitude
            Log.i("Current Longitude", "$longitude")

            getLocationWeatherDetails(latitude, longitude)
        }
    }

    // Function is used to get the weather details of the current location based on the latitude longitude
    private fun getLocationWeatherDetails(latitude: Double, longitude: Double) {
        if (Constants.isNetworkAvailable(this@MainActivity)) {

            // Add the built-in converter factory first. This prevents overriding its
            // behavior but also ensures correct behavior when using converters that consume all types.
            val retrofit: Retrofit = Retrofit.Builder()
                // API base URL.
                .baseUrl(Constants.BASE_URL)
                /** Add converter factory for serialization and deserialization of objects. */
                /**
                 * Create an instance using a default {@link Gson} instance for conversion. Encoding to JSON and
                 * decoding from JSON (when no charset is specified by a header) will use UTF-8.
                 */
                .addConverterFactory(GsonConverterFactory.create())
                /** Create the Retrofit instances. */
                .build()

            /**
             * Here we map the service interface in which we declares the end point and the API type
             *i.e GET, POST and so on along with the request parameter which are required.
             */
            val service: WeatherService =
                retrofit.create<WeatherService>(WeatherService::class.java)

            /** An invocation of a Retrofit method that sends a request to a web-server and returns a response.
             * Here we pass the required param in the service
             */
            val listCall: Call<WeatherResponse> = service.getWeather(
                latitude,
                longitude,
                Constants.METRIC_UNIT,
                Constants.APP_ID
            )

            // Show the progress dialog
            showCustomProgressDialog()

            // Callback methods are executed using the Retrofit callback executor.
            listCall.enqueue(object : Callback<WeatherResponse> {

                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    // Check weather the response is success or not.
                    if (response.isSuccessful) {

                        // Hide the progress dialog
                        hideProgressDialog() // Hides the progress dialog

                        // The de-serialized response body of a successful response.
                        val weatherList: WeatherResponse? = response.body()

                        val weatherResponseJsonString = Gson().toJson(weatherList)
                        val editor = mSharedPreferences.edit()
                        editor.putString(Constants.WEATHER_RESPONSE_DATA, weatherResponseJsonString)
                        editor.apply()

                        if (weatherList != null) {
                            setupUI()
                        }
                        Log.i("Response Result", "$weatherList")
                    } else {
                        // If the response is not "success" then we check the response code.
                        val sc = response.code()
                        when (sc) {
                            400 -> Log.e("Error 400", "Bad Request")
                            404 -> Log.e("Error 404", "Not Found")
                            else -> Log.e("Error", "Generic Error")
                        }
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    // Hide the progress dialog
                    hideProgressDialog() // Hides the progress dialog
                    Log.e("Errorrrrr", t!!.message.toString())
                }
            })
        } else {
            Toast.makeText(
                this@MainActivity,
                "No internet connection available.",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    // Method is used to show the Custom Progress Dialog.
    private fun showCustomProgressDialog() {
        mProgressDialog = Dialog(this)

        /* Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        mProgressDialog!!.setContentView(R.layout.dialog_custom_progress)

        //Start the dialog and display it on screen.
        mProgressDialog!!.show()
    }

    // This function is used to dismiss the progress dialog if it is visible to user.
    private fun hideProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog!!.dismiss()
        }
    }

    // Function is used to set the result in the UI elements.
    @SuppressLint("SetTextI18n")
    private fun setupUI() {

        val weatherResponseJsonString = mSharedPreferences.getString(Constants.WEATHER_RESPONSE_DATA, "")

        if(!weatherResponseJsonString.isNullOrEmpty()){
            val weatherList = Gson().fromJson(weatherResponseJsonString, WeatherResponse::class.java)

            // For loop to get the required data. And all are populated in the UI.
            for (z in weatherList.weather.indices) {
                Log.i("NAMEEEEEEEE", weatherList.weather[z].main)

                binding.tvMain.text = weatherList.weather[z].main
                binding.tvMainDescription.text = weatherList.weather[z].description
                binding.tvTemp.text = weatherList.main.temp.toString() + getUnit(application.resources.configuration.locales.toString())
                binding.tvHumidity.text = weatherList.main.humidity.toString() + " per cent"
                binding.tvMin.text = weatherList.main.temp_min.toString() + " min"
                binding.tvMax.text = weatherList.main.temp_max.toString() + " max"
                binding.tvSpeed.text = weatherList.wind.speed.toString()
                binding.tvName.text = weatherList.name
                binding.tvCountry.text = weatherList.sys.country
                binding.tvSunriseTime.text = unixTime(weatherList.sys.sunrise.toLong())
                binding.tvSunsetTime.text = unixTime(weatherList.sys.sunset.toLong())

                // Here we update the main icon
                when (weatherList.weather[z].icon) {
                    "01d" -> binding.ivMain.setImageResource(R.drawable.sunny)
                    "02d" -> binding.ivMain.setImageResource(R.drawable.cloud)
                    "03d" -> binding.ivMain.setImageResource(R.drawable.cloud)
                    "04d" -> binding.ivMain.setImageResource(R.drawable.cloud)
                    "04n" -> binding.ivMain.setImageResource(R.drawable.cloud)
                    "10d" -> binding.ivMain.setImageResource(R.drawable.rain)
                    "11d" -> binding.ivMain.setImageResource(R.drawable.storm)
                    "13d" -> binding.ivMain.setImageResource(R.drawable.snowflake)
                    "01n" -> binding.ivMain.setImageResource(R.drawable.cloud)
                    "02n" -> binding.ivMain.setImageResource(R.drawable.cloud)
                    "03n" -> binding.ivMain.setImageResource(R.drawable.cloud)
                    "10n" -> binding.ivMain.setImageResource(R.drawable.cloud)
                    "11n" -> binding.ivMain.setImageResource(R.drawable.rain)
                    "13n" -> binding.ivMain.setImageResource(R.drawable.snowflake)
                }
            }
        }


    }

    // Function is used to get the temperature unit value.
    private fun getUnit(value: String): String? {
        Log.i("unitttttt", value)
        var value = "°C"
        if ("US" == value || "LR" == value || "MM" == value) {
            value = "°F"
        }
        return value
    }

    // The function is used to get the formatted time based on the Format and the LOCALE we pass to it.
    private fun unixTime(timex: Long): String? {
        val date = Date(timex * 1000L)
        @SuppressLint("SimpleDateFormat")
        val sdf = SimpleDateFormat("HH:mm")
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(date)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                requestLocationData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



}