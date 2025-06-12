package com.krishhh.weatherapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.krishhh.weatherapp.BuildConfig

object Constants {

    val APP_ID: String = BuildConfig.WEATHER_API_KEY
    const val BASE_URL: String = "https://api.openweathermap.org/data/"
    const val METRIC_UNIT: String = "metric"
    const val PREFERENCE_NAME = "WeatherAppPreference"
    const val WEATHER_RESPONSE_DATA = "weather_response_data"

    // This function is used check the weather the device is connected to the Internet or not.
    fun isNetworkAvailable(context: Context): Boolean {
        // It answers the queries about the state of network connectivity.
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}
