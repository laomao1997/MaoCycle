package io.github.laomao1997.maocycle

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.app.ActivityCompat

class LocationProvider(private var context: Context) {
    private val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    fun requestLocation(locationListener: LocationListener) {
        val criteria = Criteria()
        criteria.isAltitudeRequired = false
        criteria.isBearingRequired = false
        criteria.isCostAllowed = false
        criteria.powerRequirement = Criteria.POWER_LOW
        criteria.accuracy = Criteria.ACCURACY_COARSE
        val providerList: List<String> = locationManager.getProviders(true)
        val providerName = when {
            providerList.contains(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
            providerList.contains(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
            else -> Constants.EMPTY_STRING
        }

        // 校验权限
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val location: Location? = locationManager.getLastKnownLocation(providerName)
        if (location != null) {
            locationListener.onLocationChanged(location)
        }
        locationManager.requestLocationUpdates(providerName, 100, 0.1F, locationListener)

//        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            showToast("Please turn on GPS.")
//        } else {
//            val bestProvider: String = locationManager . getBestProvider (
//                    getLocationCriteria(), true);
//            // 获取位置信息
//            // 如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER
//            Location location = locationManager
//                    .getLastKnownLocation(bestProvider);
//            // 监听状态
//            locationManager.addGpsStatusListener(gpsStatusListener);
//            // 绑定监听，有4个参数
//            // 参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种
//            // 参数2，位置信息更新周期，单位毫秒
//            // 参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息
//            // 参数4，监听
//            // 备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新
//
//            // 1秒更新一次，或最小位移变化超过1米更新一次；
//            // 注意：此处更新准确度非常低，推荐在service里面启动一个Thread，在run中sleep(10000);然后执行handler.sendMessage(),更新位置
//            locationManager.requestLocationUpdates(
//                LocationManager.GPS_PROVIDER, 1000, 1, locationListener
//            );
//        }
    }
}