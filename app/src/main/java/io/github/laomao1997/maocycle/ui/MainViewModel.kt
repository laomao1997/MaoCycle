package io.github.laomao1997.maocycle.ui

import android.app.Application
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.github.laomao1997.maocycle.Constants
import io.github.laomao1997.maocycle.LocationProvider
import kotlin.math.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private var locationProvider: LocationProvider = LocationProvider(getApplication())

    val avgSpeed = MutableLiveData<Float>()

    val maxSpeed = MutableLiveData<Float>()

    val currentLocation = MutableLiveData<Location>()

    val distance = MutableLiveData<Double>()


    private var requestTimes = 0

    init {
        maxSpeed.postValue(0F)
        distance.value = 0.0
    }

    fun startLocating() {
        locationProvider.requestLocation(object : LocationListener {
            override fun onLocationChanged(location: Location?) {
                location?.let {
                    distance.value =
                        distance.value?.plus(calculateDistance(currentLocation.value, it))
                    currentLocation.value = it
                    if (maxSpeed.value == null || maxSpeed.value!! < it.speed) {
                        maxSpeed.value = it.speed
                    }
                    calculateAvgSpeed(it.speed)
                }
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

            }

            override fun onProviderEnabled(provider: String?) {

            }

            override fun onProviderDisabled(provider: String?) {

            }

        })
    }

    private fun calculateAvgSpeed(speed: Float) {
        requestTimes++
        val average: Float = if (this.avgSpeed.value == null) {
            0F
        } else {
            this.avgSpeed.value!!
        }
        this.avgSpeed.value = (average + speed) / requestTimes
    }

    private fun calculateDistance(oldLocation: Location?, newLocation: Location?): Double {
        if (oldLocation == null || newLocation == null) {
            return 0.0
        }
        val lon1 = oldLocation.longitude
        val lat1 = oldLocation.latitude
        val lon2 = newLocation.longitude
        val lat2 = newLocation.latitude
        val radLat1 = rad(lat1);
        val radLat2 = rad(lat2);
        val a = radLat1 - radLat2;
        val b = rad(lon1) - rad(lon2);
        var s =
            2 * asin(sqrt(sin(a / 2).pow(2.0) + cos(radLat1) * cos(radLat2) * sin(b / 2).pow(2.0)))
        s *= Constants.EARTH_RADIUS;
        return s // 单位米
    }

    private fun rad(d: Double): Double {
        return d * Math.PI / 180.0;
    }
}