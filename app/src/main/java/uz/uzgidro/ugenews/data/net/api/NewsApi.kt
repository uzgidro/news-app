package uz.uzgidro.ugenews.data.net.api

import retrofit2.http.GET
import retrofit2.http.Path
import uz.uzgidro.ugenews.data.net.dto.NewsResponseDto

interface NewsApi {
    // Пагинация через PATH-параметр: query ?page=N сервером игнорируется
    // (всегда отдаёт page 1), а /news/page/{page} возвращает реальные страницы.
    @GET("news/page/{page}")
    suspend fun getNews(@Path("page") page: Int): NewsResponseDto
}
