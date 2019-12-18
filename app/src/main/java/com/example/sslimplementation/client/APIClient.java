package com.example.sslimplementation.client;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.sslimplementation.Constants;
import com.example.sslimplementation.PostInterface;
import com.example.sslimplementation.R;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static android.content.Context.MODE_PRIVATE;

public class APIClient {
    private static Retrofit.Builder builder;

    private static Retrofit retrofit;

    private static PostInterface REST_CLIENT;
    private Context mContext;

    static {    }

    public APIClient() {
    }
    public static Retrofit.Builder getBuilder() {
        return builder;
    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }

    public static void setRetrofit(Retrofit retrofit) {
        APIClient.retrofit = retrofit;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public PostInterface get(Context mContext) {

            this.mContext = mContext;

            return setupRestClient();

    }

    public static void setBuilder(Retrofit.Builder builder){
        APIClient.builder = builder;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public PostInterface setupRestClient() {

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        /**
         * Configured OkHttp Client To Authenticatell
         */
        OkHttpClient client = null;

        try {

            CertificatePinner certPinner = new CertificatePinner.Builder()
                    .add("appmattus.com",
                            "sha256/4hw5tz+scE+TW+mlai5YipDfFWn1dqvfLG+nU7tq1V8=")
                    .build();


            client = new OkHttpClient.Builder().addNetworkInterceptor(httpLoggingInterceptor)
                    .sslSocketFactory(getSSLConfig(mContext))
                    .certificatePinner(certPinner)
                    .hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    boolean value = true;
                    //TODO:Some logic to verify your host and set value
                    return value;
                }
            }).build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        builder = null;

        builder = new Retrofit.Builder().baseUrl(Constants.BASE_URL)
                .client(client).addConverterFactory(ScalarsConverterFactory.create());
        retrofit = builder.build();
        REST_CLIENT = retrofit.create(PostInterface.class);

        return REST_CLIENT;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static SSLSocketFactory getSSLConfig(Context context) throws CertificateException, IOException,
            KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

        // Loading CAs from an InputStream
        CertificateFactory cf = null;
        cf = CertificateFactory.getInstance("X.509");

        Certificate ca;
        // using Java7. If you used Java6 close it manually with finally.
        try (InputStream cert = context.getResources().openRawResource(R.raw.sslcertificate)) {
            ca = cf.generateCertificate(cert);
        }

        // Creating a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // Creating a TrustManager that trusts the CAs in our KeyStore.
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Creating an SSLSocketFactory that uses our TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);
        return sslContext.getSocketFactory();
    }
}