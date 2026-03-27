package com.simats.saferoadschennaisrc.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static final String BASE_URL = "https://pathwayed-uglily-tenley.ngrok-free.dev/"; // New Ngrok URL
    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            okhttp3.OkHttpClient client = new okhttp3.OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        okhttp3.Request request = chain.request().newBuilder()
                                .addHeader("ngrok-skip-browser-warning", "true")
                                .build();
                        return chain.proceed(request);
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
