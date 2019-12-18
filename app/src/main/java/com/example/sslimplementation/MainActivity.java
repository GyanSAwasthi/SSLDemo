package com.example.sslimplementation;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sslimplementation.client.APIClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    Button btnClick;
    TextView tvResponse;
    Context ctx;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // ctx = this;
        setContentView(R.layout.activity_login);
        btnClick = (Button) findViewById(R.id.click);
        tvResponse = (TextView) findViewById(R.id.tvResponse);

        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvResponse.setText("");
               /* progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Please wait..loading....");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();*/
                APIClient client = new APIClient();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    PostInterface postInterface = client.get(ctx);
                    Call<String> call = postInterface.getLogin();

                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            progressDialog.dismiss();
                            tvResponse.setVisibility(View.VISIBLE);
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
}