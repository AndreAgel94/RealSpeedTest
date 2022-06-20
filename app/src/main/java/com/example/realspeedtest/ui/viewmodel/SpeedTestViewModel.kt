package com.example.realspeedtest.ui.viewmodel

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.realspeedtest.utils.tests.HttpDownloadTest
import com.example.realspeedtest.utils.tests.HttpUploadTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SpeedTestViewModel(
    activity: ComponentActivity
) : ViewModel() {

    val _downLoadRate = MutableLiveData<Float>(0.0f)

    val _uploadRate = MutableLiveData<Double>(0.0)

    val _isDownloadFinished = MutableLiveData<Boolean>(false)


    val url = "http://speedtest1.flynet.net.br:8080/speedtest/upload.php"
    var state by mutableStateOf(0.0f)


    fun testDownloadSpeed() {
        var hasDownloadStarted = false

        val downloadTest = HttpDownloadTest(
            url.replace(
                url.split("/").toTypedArray()
                    .get(url.split("/").toTypedArray().size - 1), ""
            )
        )

        viewModelScope.launch(Dispatchers.IO){
            while (true) {
                if (!hasDownloadStarted) {
                    downloadTest.start()
                    hasDownloadStarted = true
                }
                // Log.d("MainActivity", "instant download rate: ${downloadTest.instantDownloadRate} \n")
                state = downloadTest.instantDownloadRate.toFloat()
               // Log.d("MainActivity", "instant download rate: $state \n")
                Log.d("the value of state","state is: $state \n")



                if (downloadTest.isFinished) {
                    Log.d("MainActivity", "final download rate: ${downloadTest.finalDownloadRate} \n")
                    _downLoadRate.postValue(downloadTest.finalDownloadRate.toFloat())
                    _isDownloadFinished.postValue(true)
                    break
                }
            }
        }
    }

    fun testUploadSpeed() {
        var hasUploadStarted = false
        val uploadTest = HttpUploadTest(url)

        while (true) {
            if (!hasUploadStarted) {
                uploadTest.start()
                hasUploadStarted = true
                Thread.sleep(3000)
            }

            Log.d("MainActivity", "instant upload rate: ${uploadTest.instantUploadRate} \n")

            if (uploadTest.isFinished) {
                Thread.sleep(3000)
                Log.d("MainActivity", "final upload rate: ${uploadTest.finalUploadRate} \n")
                break
            }
        }
    }
}
