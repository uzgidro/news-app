package uz.uzgidro.ugenews.data.net.api

import retrofit2.http.GET
import retrofit2.http.Query
import uz.uzgidro.ugenews.data.net.dto.NewsResponseDto

interface NewsApi {
    @GET("news")
    suspend fun getNews(@Query("page") page: Int): NewsResponseDto
}
