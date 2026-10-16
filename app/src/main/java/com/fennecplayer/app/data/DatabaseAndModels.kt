package com.fennecplayer.app.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

enum class StreamType { LIVE, MOVIE, SERIES }

@Entity(
    tableName = "channels",
    indices = [Index(value = ["streamType", "groupName"]), Index(value = ["title"])]
)
data class ChannelEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val streamUrl: String,
    val logoUrl: String,
    val groupName: String,
    val streamType: StreamType,
    val isFavorite: Boolean = false
)

@Dao
interface ChannelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatch(channels: List<ChannelEntity>)

    @Query("SELECT DISTINCT groupName FROM channels WHERE streamType = :type ORDER BY groupName ASC")
    fun getCategoriesByType(type: StreamType): Flow<List<String>>

    @Query("SELECT * FROM channels WHERE streamType = :type AND (:group = 'All' OR groupName = :group) ORDER BY id ASC")
    fun getChannelsByGroup(type: StreamType, group: String): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE streamType = :type AND isFavorite = 1")
    fun getFavoritesByType(type: StreamType): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE streamType = :type AND title LIKE '%' || :query || '%' LIMIT 200")
    fun searchByType(type: StreamType, query: String): Flow<List<ChannelEntity>>

    @Query("UPDATE channels SET isFavorite = :isFav WHERE id = :channelId")
    suspend fun setFavorite(channelId: Long, isFav: Boolean)

    @Query("DELETE FROM channels")
    suspend fun clearAll()
}

@Database(entities = [ChannelEntity::class], version = 1, exportSchema = false)
abstract class FennecDatabase : RoomDatabase() {
    abstract fun channelDao(): ChannelDao

    companion object {
        @Volatile
        private var INSTANCE: FennecDatabase? = null

        fun getDatabase(context: Context): FennecDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    FennecDatabase::class.java,
                    "fennec_ibo_db"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
        }
    }
}
