package com.example.sslimplementation.pubkeypin;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.sslimplementation.PostInterface;
import com.example.sslimplementation.R;
import com.example.sslimplementation.client.APIClient;

import java.io.IOException;
import java.security.PublicKey;
import java.security.cert.Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends Activity {
	
	public static TextView m_secret;
	public static Button m_button;
	public static ProgressBar m_progress1, m_progress2;
	public static Activity m_this;
	// Tip from here http://littlesvr.ca/grumble/2014/07/21/android-programming-connect-to-an-https-server-with-self-signed-certificate/
    public static Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {

        // Tip from here http://littlesvr.ca/grumble/2014/07/21/android-programming-connect-to-an-https-server-with-self-signed-certificate/
        MainActivity.context = getApplicationContext();
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		m_this = this;
		final TextView tvResponse = (TextView) findViewById(R.id.tvResponse);
		m_secret = (TextView)findViewById(R.id.text_secret_data);
		assert(null != m_secret);
		
		m_button = (Button)findViewById(R.id.button_fetch);
		assert(null != m_button);

		m_progress1 = (ProgressBar)findViewById(R.id.progress_bar1);
		assert(null != m_progress1);
		if (null != MainActivity.m_progress1) {
			MainActivity.m_progress1.setVisibility(ProgressBar.INVISIBLE);
		}

		m_progress2 = (ProgressBar)findViewById(R.id.progress_bar2);
		assert(null != m_progress2);
		if (null != MainActivity.m_progress2) {
			MainActivity.m_progress2.setVisibility(ProgressBar.INVISIBLE);
		}

		Button btnClick = (Button)findViewById(R.id.btn);
		btnClick.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				APIClient client = new APIClient();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
					PostInterface postInterface = client.get(m_this);
					Call<String> call = postInterface.getLogin();

					call.enqueue(new Callback<String>() {
						@Override
						public void onResponse(Call<String> call, Response<String> response) {
							Log.v("<<<<< Response >>>>",response.body().toString());
							tvResponse.setText(response.body().toString());

						}

						@Override
						public void onFailure(Call<String> call, Throwable t) {
						}
					});
				}
			}
		});
	}

	public void onFetchSecretClick(View v) throws IOException {

		/*String hostname = "sitohs.jio.com";
		SSLSocketFactory factory = HttpsURLConnection.getDefaultSSLSocketFactory();
		SSLSocket socket = (SSLSocket) factory.createSocket(hostname, 4443);
		socket.startHandshake();
		Certificate[] certs = socket.getSession().getPeerCertificates();
		Certificate cert = certs[0];
		PublicKey key = cert.getPublicKey();
		System.out.println(key);
*/

		/*int port = 4443;

		String hostname = "sitohs.jio.com";

		SSLSocketFactory factory = HttpsURLConnection
				.getDefaultSSLSocketFactory();

		System.out.println("Creating a SSL Socket For "+hostname+" on port "+port);

		SSLSocket socket = (SSLSocket) factory.createSocket(hostname, port);
		socket.startHandshake();
		System.out.println("Handshaking Complete");

		Certificate[] serverCerts = socket.getSession().getPeerCertificates();
		System.out.println("Retreived Server's Certificate Chain");

		System.out.println(serverCerts.length + "Certifcates Foundnnn");
		for (int i = 0; i < serverCerts.length; i++) {
			Certificate myCert = serverCerts[i];
			System.out.println("====Certificate:" + (i+1) + "====");
			System.out.println("-Public Key-n" + myCert.getPublicKey());
			System.out.println("-Certificate Type-n " + myCert.getType());

			System.out.println();
		}

		socket.close();*/

		 new FetchSecretTask().execute();


	}
}