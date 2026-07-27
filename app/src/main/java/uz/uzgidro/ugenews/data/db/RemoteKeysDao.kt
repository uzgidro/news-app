package uz.uzgidro.ugenews.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(keys: List<RemoteKeys>)

    @Query("SELECT * FROM remote_keys WHERE newsId = :newsId")
    suspend fun keysByNewsId(newsId: Int): RemoteKeys?

    @Query("DELETE FROM remote_keys")
    suspend fun clearAll()
}
