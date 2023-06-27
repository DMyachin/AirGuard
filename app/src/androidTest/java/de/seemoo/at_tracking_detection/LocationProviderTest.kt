package de.seemoo.at_tracking_detection

import android.location.Location
import android.location.LocationManager
import androidx.core.content.getSystemService
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.seemoo.at_tracking_detection.detection.LocationProvider
import de.seemoo.at_tracking_detection.detection.LocationRequester
import de.seemoo.at_tracking_detection.util.DefaultBuildVersionProvider
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


@RunWith(AndroidJUnit4::class)
class LocationProviderTest {

    @Test
    fun testGettingLocation() {
        val lock = CountDownLatch(1)
        val context = ATTrackingDetectionApplication.getAppContext()
        val locationManager = context.getSystemService<LocationManager>()
        assert(locationManager != null)
        val locationProvider = LocationProvider(locationManager!!, DefaultBuildVersionProvider())

        // Getting the current location
        val startTime = LocalDateTime.now()
        locationProvider.lastKnownOrRequestLocationUpdates(object : LocationRequester() {
            override fun receivedAccurateLocationUpdate(location: Location) {
                lock.countDown()
                val endTime = LocalDateTime.now()
                val timeDiff = ChronoUnit.SECONDS.between(startTime, endTime)
                Timber.d("Took ${timeDiff}s to get a valid location")
            }
        }, 60_000)


        lock.await(60, TimeUnit.SECONDS)
    }

    @Test
    fun testGettingLocationPreSDK31() {
        val lock = CountDownLatch(1)
        val context = ATTrackingDetectionApplication.getAppContext()
        val locationManager = context.getSystemService<LocationManager>()
        assert(locationManager != null)
        val locationProvider = LocationProvider(locationManager!!, TestBuildVersionProvider(21))

        // Getting the current location
        val startTime = LocalDateTime.now()
        locationProvider.lastKnownOrRequestLocationUpdates(object : LocationRequester() {
            override fun receivedAccurateLocationUpdate(location: Location) {
            lock.countDown()
            val endTime = LocalDateTime.now()
            val timeDiff = ChronoUnit.SECONDS.between(startTime, endTime)
            Timber.d("Took ${timeDiff}s to get a valid location")
            assert(location.provider == "network" || location.provider == "gps")
            }
        }, 60_000)

        lock.await(60, TimeUnit.SECONDS)
    }
}