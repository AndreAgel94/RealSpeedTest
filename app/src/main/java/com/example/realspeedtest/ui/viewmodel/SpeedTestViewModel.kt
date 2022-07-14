package com.example.realspeedtest.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.flow
import java.io.InputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.HttpURLConnection
import java.net.URL


class SpeedTestViewModel(

) : ViewModel() {

    val url = "http://speedtest.arrobanettelecom.com.br:8080/speedtest/upload.php"

    var fileURL = url.replace(
        url.split("/").toTypedArray()
            .get(url.split("/").toTypedArray().size - 1), ""
    )

    var startTime: Long = 0

    var endTime: Long = 0

    var downloadElapsedTime = 0.0

    var downloadedByte = 0

    var finalDownloadRatee = 0.0

    var finished = false

    var instantDownloadRatee = 0.0

    var timeout = 15

    var httpConn: HttpURLConnection? = null


    private fun round(value: Double, places: Int): Double {
        require(places >= 0) // o parâmetro place é invalido se menor que 0
        var bd = BigDecimal.valueOf(value) // pega uma instância de um big decimal de valor value
        bd = bd.setScale(
            places,
            RoundingMode.HALF_UP
        ) // seta uma escala para o big decimal(places), como o parâmetro para arredondar para cima
        return bd.toDouble() // retorna o big decimal arredondado como um double
    }


    fun getInstantDownloadRate(): Double {
        return instantDownloadRatee
    }

    fun setInstantDownloadRate(downloadedByte: Int, elapsedTime: Double): Double {
        return if (downloadedByte >= 0) {
            round(downloadedByte * 8 / (1000 * 1000) / elapsedTime, 2)
        } else {
            0.0
        }
    }

    fun getFinalDownloadRate(): Double {
        return round(finalDownloadRatee, 2)
    }

    fun isFinished(): Boolean {
        return finished
    }

    val downloadRate = flow<Double> {
        var url: URL? = null
        downloadedByte = 0
        var responseCode = 0
        val fileUrls: MutableList<String> = ArrayList()
//        fileUrls.add(fileURL + "random4000x4000.jpg")
//        fileUrls.add(fileURL + "random3000x3000.jpg")
//        fileUrls.add(fileURL + "random3000x3000.jpg")
//        fileUrls.add(fileURL + "random3000x3000.jpg")
//        fileUrls.add(fileURL + "random3000x3000.jpg")
//        fileUrls.add(fileURL + "random3000x3000.jpg")
//        fileUrls.add(fileURL + "random3000x3000.jpg")
//        fileUrls.add(fileURL + "random3000x3000.jpg")
//        fileUrls.add(fileURL + "random3000x3000.jpg")
//        fileUrls.add(fileURL + "random3000x3000.jpg")
          fileUrls.add("http://stest.larcnet.com.br:8080/speedtest/random4000x4000.jpg")
          fileUrls.add("http://stest.larcnet.com.br:8080/speedtest/random4000x4000.jpg")

        while (!isFinished()) {
            startTime = System.currentTimeMillis()
            outer@ for (link in fileUrls) {
                try {
                    url = URL(link)
                    httpConn = url.openConnection() as HttpURLConnection
                    responseCode = httpConn!!.getResponseCode()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                try {
                    Log.d("ViewModel", "responseCode: $responseCode")
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val buffer = ByteArray(10240)
                        val inputStream: InputStream = httpConn!!.getInputStream()
                        var len = 0
                        while (inputStream.read(buffer).also { len = it } != -1) {
                            downloadedByte += len
                            endTime = System.currentTimeMillis()
                            downloadElapsedTime = (endTime - startTime) / 1000.0
                            emit(setInstantDownloadRate(downloadedByte, downloadElapsedTime))
                            if (downloadElapsedTime >= timeout) {
                                break@outer
                            }
                        }
                        inputStream.close()
                        httpConn!!.disconnect()
                    } else {
                        Log.d("TAG", "Link not found...")
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
            endTime = System.currentTimeMillis()
            downloadElapsedTime = (endTime - startTime) / 1000.0
            finalDownloadRatee = downloadedByte * 8 / (1000 * 1000.0) / downloadElapsedTime
            finished = true
        }
    }
}
