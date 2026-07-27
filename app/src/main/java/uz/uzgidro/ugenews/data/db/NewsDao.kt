package uz.uzgidro.ugenews.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<NewsEntity>)

    /** Лента в порядке прихода из API (page 1 сверху). */
    @Query("SELECT * FROM news ORDER BY ordinal ASC")
    fun pagingSource(): PagingSource<Int, NewsEntity>

    @Query("SELECT * FROM news WHERE id = :id")
    suspend fun getById(id: Int): NewsEntity?

    @Query("DELETE FROM news")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM news")
    suspend fun count(): Int
}
