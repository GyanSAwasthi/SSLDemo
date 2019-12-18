package com.example.sslimplementation.pubkeypin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import java.io.InputStream;
import java.io.StreamTokenizer;
import java.net.URL;
import java.security.cert.Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.Request;

// http://android-developers.blogspot.com/2009/05/painless-threading.html
public class FetchSecretTask extends AsyncTask<Void, Void, Object> {

	/*@Override
	protected void onPreExecute() {

		assert (null != MainActivity.m_secret);
		if (null != MainActivity.m_secret) {
			MainActivity.m_secret.setText("    Fetching...");
		}

		assert (null != MainActivity.m_button);
		if (null != MainActivity.m_button) {
			MainActivity.m_button.setEnabled(false);
		}

		assert (null != MainActivity.m_progress1);
		if (null != MainActivity.m_progress1) {
			MainActivity.m_progress1.setVisibility(ProgressBar.VISIBLE);
			MainActivity.m_progress1.setProgress(0);
		}

		assert (null != MainActivity.m_progress2);
		if (null != MainActivity.m_progress2) {
			MainActivity.m_progress2.setVisibility(ProgressBar.VISIBLE);
			MainActivity.m_progress2.setProgress(0);
		}
	}*/

    @Override
    protected Object doInBackground(Void... params) {

        Object result = null;

		/*try {

			byte[] secret = null;

            //Getting the keystore
			KeyPinStore keystore = KeyPinStore.getInstance();

            // Tell the URLConnection to use a SocketFactory from our SSLContext
			URL url = new URL( "https://sitohs.jio.com:4443");
            HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
            urlConnection.setSSLSocketFactory(keystore.getContext().getSocketFactory());
            InputStream instream = urlConnection.getInputStream();

            // Following OWASP example https://www.owasp.org/index.php/Certificate_and_Public_Key_Pinning
			StreamTokenizer tokenizer = new StreamTokenizer(instream);
			assert (null != tokenizer);

			secret = new byte[16];
			assert (null != secret);

			int idx = 0, token;
			while (idx < secret.length) {
				token = tokenizer.nextToken();
				if (token == StreamTokenizer.TT_EOF)
					break;
				if (token != StreamTokenizer.TT_NUMBER)
					continue;

				secret[idx++] = (byte) tokenizer.nval;
			}

			// Prepare return value
			result = (Object) secret;

		} catch (Exception ex) {

			// Log error
			Log.e("doInBackground", ex.toString());

			// Prepare return value
			result = (Object) ex;
		}*/
        try {
            int port = 4443;

            String hostname = "sitohs.jio.com";

            SSLSocketFactory factory = HttpsURLConnection
                    .getDefaultSSLSocketFactory();

            System.out.println("Creating a SSL Socket For " + hostname + " on port " + port);

            SSLSocket socket = (SSLSocket) factory.createSocket(hostname, port);
            socket.startHandshake();
            System.out.println("Handshaking Complete");

            Certificate[] serverCerts = socket.getSession().getPeerCertificates();
            System.out.println("Retreived Server's Certificate Chain");

            System.out.println(serverCerts.length + "Certifcates Foundnnn");
            for (int i = 0; i < serverCerts.length; i++) {
                Certificate myCert = serverCerts[i];
                System.out.println("====Certificate:" + (i + 1) + "====");
                System.out.println("-Public Key-n" + myCert.getPublicKey());
                Log.v("<<<<< ====Certificate: ",""+ (i + 1) + "====");
                Log.v("-Public Key-n",""+ myCert.getPublicKey());
                Log.v("-Public Key-n",""+ myCert.getPublicKey());

                CertificatePinner certificatePinner = new CertificatePinner.Builder()
                        .add(hostname, "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
                        .build();
                OkHttpClient client = new OkHttpClient.Builder()
                        .certificatePinner(certificatePinner)
                        .build();

                Request request = new Request.Builder()
                        .url("https://" + hostname)
                        .build();
                client.newCall(request).execute();
                System.out.println();
            }

            socket.close();
        } catch (Exception e) {

        }

        return result;


    }

	/*@Override
	protected void onPostExecute(Object result) {

		assert (null != result);

		assert (null != MainActivity.m_button);
		if (null != MainActivity.m_button) {
			MainActivity.m_button.setEnabled(true);
		}

		assert (null != MainActivity.m_progress1);
		if (null != MainActivity.m_progress1) {
			MainActivity.m_progress1.setVisibility(ProgressBar.INVISIBLE);
		}

		assert (null != MainActivity.m_progress2);
		if (null != MainActivity.m_progress2) {
			MainActivity.m_progress2.setVisibility(ProgressBar.INVISIBLE);
		}

		assert (null != MainActivity.m_secret);
		if (null != MainActivity.m_secret) {
			MainActivity.m_secret.setText("");
		}

		assert (null != result);
		if (null == result)
			return;

		assert (result instanceof Exception || result instanceof byte[]);
		if (!(result instanceof Exception || result instanceof byte[]))
			return;

		if (result instanceof Exception) {
			ExitWithException((Exception) result);
			return;
		}

		ExitWithSecret((byte[]) result);
	}*/

    protected void ExitWithException(Exception ex) {

        assert (null != ex);

        if (null != MainActivity.m_secret) {
            MainActivity.m_secret.setText("    Error fetching secret");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(
                MainActivity.m_this);
        builder.setMessage(ex.toString()).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    protected void ExitWithSecret(byte[] secret) {

        assert (null != secret);

        StringBuilder sb = new StringBuilder(secret.length * 3 + 1);
        assert (null != sb);

        for (int i = 0; i < secret.length; i++) {
            sb.append(String.format("%02X ", secret[i]));
            secret[i] = 0;
        }

        MainActivity.m_secret.setText(sb.toString());
        Log.v("SECRET KEY", sb.toString());
    }
}
