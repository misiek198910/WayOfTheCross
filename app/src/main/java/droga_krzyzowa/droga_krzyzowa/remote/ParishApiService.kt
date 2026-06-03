package droga_krzyzowa.droga_krzyzowa.remote

import retrofit2.http.GET


interface ParishApiService {

    @GET("news")
    suspend fun getNewsFeed(): retrofit2.Response<List<NewsResponse>> // Dodaj Response<>
}