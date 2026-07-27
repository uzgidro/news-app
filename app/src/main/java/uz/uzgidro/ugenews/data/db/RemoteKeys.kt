package uz.uzgidro.ugenews.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Ключи страниц для Paging RemoteMediator: для каждой новости — соседние страницы API. */
@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey val newsId: Int,
    val prevKey: Int?,
    val nextKey: Int?,
)
