package io.github.laomao1997.maocycle

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlin.math.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private var locationProvider: LocationProvider = LocationProvider(getApplication())

    val longitude = MutableLiveData<Double>()

    val latitude = MutableLiveData<Double>()

    val speed = MutableLiveData<Float>()

    val altitude = MutableLiveData<Double>()

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
        locationProvider.requestLocation { newLocation ->
            distance.value =
                distance.value?.plus(calculateDistance(currentLocation.value, newLocation))
            this.longitude.value = newLocation.longitude
            this.latitude.value = newLocation.latitude
            this.altitude.value = newLocation.altitude
            currentLocation.value = newLocation
            this.speed.value = newLocation.speed
            if (this.maxSpeed.value == null || this.maxSpeed.value!! < newLocation.speed) {
                this.maxSpeed.value = newLocation.speed
            }
            calculateAvgSpeed(newLocation.speed)
        }
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