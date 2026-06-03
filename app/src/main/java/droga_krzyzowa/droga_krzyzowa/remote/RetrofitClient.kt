package droga_krzyzowa.droga_krzyzowa.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Twoja nowa domena z Cloudflare
    private const val BASE_URL = "https://api-parafia.mivs.dev/"

    val apiService: ParishApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ParishApiService::class.java)
    }
}