package uz.uzgidro.ugenews.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [NewsEntity::class, RemoteKeys::class],
    version = 1,
    exportSchema = false,
)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}
