package com.hcl.kandy.cpaas.data.remote;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hcl.kandy.cpaas.utils.RetrofitUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final int TIMEOUT_PERIOD = 120;
    private static ApiEndPoint apiEndpoint = null;
    /**
     * Create {@link Retrofit} instance using {@link OkHttpClient} and
     * IAM api base url for using {@link ApiEndPoint} webservice.
     * The retrofit object then create implementation  of API endpoint defined by {@link ApiEndPoint}
     * @return {@link ApiEndPoint}
     */
    public static ApiEndPoint getAuthApi(String baseUrl) {
        if (apiEndpoint == null) {
            Retrofit retrofit = getHeaderRetrofitInstance(false, baseUrl);
            apiEndpoint = retrofit.create(ApiEndPoint.class);
        }
        return apiEndpoint;
    }


    @NonNull
    private static Retrofit getHeaderRetrofitInstance(boolean hasSSLPinning, String baseUrl) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getNormalOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }


    public static OkHttpClient getNormalOkHttpClient() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                .readTimeout(TIMEOUT_PERIOD, TimeUnit.SECONDS) //Sets the default read timeout for new connections
                .writeTimeout(TIMEOUT_PERIOD, TimeUnit.SECONDS) //set the default write timeout for new connections
                .connectTimeout(TIMEOUT_PERIOD, TimeUnit.SECONDS); //Sets the default connect timeout for new connections.

        /*log network call*/
        okHttpClientBuilder.addInterceptor(RetrofitUtils.getLoggingLevel());



        /*setting authorization token to call*/
        okHttpClientBuilder.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
                    .addHeader("Content-Type", "application/x-www-form-urlencoded");
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });
        OkHttpClient test = okHttpClientBuilder.build();

        return test;
    }

}
