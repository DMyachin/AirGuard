package de.seemoo.at_tracking_detection.ui.debug

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.seemoo.at_tracking_detection.util.SharedPrefs
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class DebugViewModel @Inject constructor(
    sharedPreferences: SharedPreferences
) : ViewModel() {

    private var sharedPreferencesListener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            when (key) {
                "isScanningInBackground" -> {
                   updateScanText()
                }
            }
        }

    var scanText = MutableLiveData<String>("Not scanning")


    var nextScanDate = MutableLiveData<String>(SharedPrefs.nextScanDate.toString())
    var lastScanDate = MutableLiveData<String>(SharedPrefs.lastScanDate.toString())

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferencesListener)
        updateScanText()
    }

    private fun updateScanText() {
        if (SharedPrefs.isScanningInBackground) {
            scanText.postValue("Scanning in background")
        }else {
            scanText.postValue("Not scanning")
        }
        nextScanDate.postValue(SharedPrefs.nextScanDate.toString())
        lastScanDate.postValue(SharedPrefs.lastScanDate.toString())
    }
}