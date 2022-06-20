package com.example.realspeedtest.utils.tests;

import android.util.Log;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * The type Http download test.
 */
public class HttpDownloadTest extends Thread {
	/**
	 * A url do arquivo a ser baixado
	 */
	public String fileURL = "";
	/**
	 * tempo de início do teste
	 */
	long startTime = 0;
	/**
	 * tempo de término do teste
	 */
	long endTime = 0;
	/**
	 * tempo decorrido de download.
	 */
	double downloadElapsedTime = 0;
	/**
	 * The Downloaded byte.
	 */
	int downloadedByte = 0;
	/**
	 * taxa de download final.
	 */
	double finalDownloadRate = 0.0;
	/**
	 * The Finished. booleano da finalização do teste
	 */
	boolean finished = false;
	/**
	 * A taxa de download instantâneo.
	 */
	double instantDownloadRate = 0;
	/**
	 * The Timeout.  tempo limite
	 */
	int timeout = 15;
	/**
	 * The Http conn. a conexão http
	 */
	HttpURLConnection httpConn = null;



	/**
	 * Instantiates a new Http download test.
	 * construtor para instânciar um teste de download
	 * @param fileURL the file url
	 */
	public HttpDownloadTest (String fileURL) {
		this.fileURL = fileURL;
	}

	private double round (double value, int places) {
		if (places < 0) throw new IllegalArgumentException (); // o parâmetro place é invalido se menor que 0
		BigDecimal bd = BigDecimal.valueOf (value); // pega uma instância de um big decimal de valor value
		bd = bd.setScale (places, RoundingMode.HALF_UP); // seta uma escala para o big decimal(places), como o parâmetro para arredondar para cima
		return bd.doubleValue (); // retorna o big decimal arredondado como um double
	}

	/**
	 * Gets instant download rate.
	 * pega a taxa instantânea de download
	 * @return the instant download rate
	 * retorna a taxa instantânea de download
	 */
	public double getInstantDownloadRate () {
		return instantDownloadRate;
	}

	/**
	 * Sets instant download rate.
	 * seta a taxa instantânea de download
	 * @param downloadedByte the downloaded byte
	 * byte baixado
	 * @param elapsedTime    the elapsed time
	 * o tempo decorrido
	 */
	public void setInstantDownloadRate (int downloadedByte, double elapsedTime) {
		if (downloadedByte >= 0) {
			this.instantDownloadRate = round (((downloadedByte * 8) / (1000 * 1000)) / elapsedTime, 2);
		} else {
			this.instantDownloadRate = 0.0;
		}
	}

	/**
	 * Gets final download rate.
	 *
	 * @return the final download rate
	 */
	public double getFinalDownloadRate () {
		return round (finalDownloadRate, 2);
	}

	/**
	 * Is finished boolean.
	 *
	 * @return the boolean
	 */
	public boolean isFinished () {
		return finished;
	}

	@Override
	public void run () {
		URL url = null;
		downloadedByte = 0;
		int responseCode = 0;
		List<String> fileUrls = new ArrayList<> ();
		fileUrls.add (fileURL + "random3000x3000.jpg");
		fileUrls.add (fileURL + "random3000x3000.jpg");
		fileUrls.add (fileURL + "random3000x3000.jpg");
		fileUrls.add (fileURL + "random3000x3000.jpg");
		fileUrls.add (fileURL + "random3000x3000.jpg");
		fileUrls.add (fileURL + "random3000x3000.jpg");
		fileUrls.add (fileURL + "random3000x3000.jpg");
		fileUrls.add (fileURL + "random3000x3000.jpg");
		fileUrls.add (fileURL + "random3000x3000.jpg");
		fileUrls.add (fileURL + "random3000x3000.jpg");


		startTime = System.currentTimeMillis ();
		outer:
		for (String link : fileUrls) {
			try {
				url = new URL (link);
				httpConn = (HttpURLConnection) url.openConnection ();
				responseCode = httpConn.getResponseCode ();
			} catch (Exception ex) {
				ex.printStackTrace ();
			}
			try {
				if (responseCode == HttpURLConnection.HTTP_OK) {
					byte[] buffer = new byte[ 10240 ];
					InputStream inputStream = httpConn.getInputStream ();
					int len = 0;
					while ((len = inputStream.read (buffer)) != -1) {
						downloadedByte += len;
						endTime = System.currentTimeMillis ();
						downloadElapsedTime = (endTime - startTime) / 1000.0;
						setInstantDownloadRate (downloadedByte, downloadElapsedTime);
						if (downloadElapsedTime >= timeout) {
							break outer;
						}
					}
					inputStream.close ();
					httpConn.disconnect ();
				} else {
					Log.d ("TAG", "Link not found...");
				}
			} catch (Exception ex) {
				ex.printStackTrace ();
			}
		}
		endTime = System.currentTimeMillis ();
		downloadElapsedTime = (endTime - startTime) / 1000.0;
		finalDownloadRate = ((downloadedByte * 8) / (1000 * 1000.0)) / downloadElapsedTime;
		finished = true;
	}
}
