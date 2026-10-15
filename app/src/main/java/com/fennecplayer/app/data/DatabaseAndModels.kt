package com.fennecplayer.app.data

import android.content.Context
import androidx.paging.PagingSource
import androidx.room.*

enum class ContentType {
    LIVE,
    MOVIE,
    SERIES
}

@Entity(
    tableName = "playlist_items",
    indices = [
        Index(value = ["contentType", "categoryName"]),
        Index(value = ["contentType", "name"]),
        Index(value = ["channelNumber"])
    ]
)
data class PlaylistItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val channelNumber: Int = 0,
    val name: String,
    val streamUrl: String,
    val logoUrl: String = "",
    val categoryName: String = "General",
    val contentType: ContentType = ContentType.LIVE,
    val isFavorite: Boolean = false
)

@Dao
interface FennecDao {
    @Query("DELETE FROM playlist_items")
    suspend fun clearAllItems()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannelsBatch(items: List<PlaylistItemEntity>)

    @Query("SELECT DISTINCT categoryName FROM playlist_items WHERE contentType = :contentType ORDER BY categoryName ASC")
    suspend fun getCategories(contentType: ContentType): List<String>

    @Query("""
        SELECT * FROM playlist_items 
        WHERE contentType = :contentType 
        AND (:categoryName = 'All' OR categoryName = :categoryName)
        AND (:searchQuery = '' OR name LIKE '%' || :searchQuery || '%')
        ORDER BY channelNumber ASC
    """)
    fun getPagedItems(
        contentType: ContentType,
        categoryName: String,
        searchQuery: String
    ): PagingSource<Int, PlaylistItemEntity>

    @Query("UPDATE playlist_items SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean)

    @Query("SELECT * FROM playlist_items WHERE contentType = :contentType AND isFavorite = 1 ORDER BY channelNumber ASC")
    fun getFavoritePagedItems(contentType: ContentType): PagingSource<Int, PlaylistItemEntity>

    @Query("SELECT COUNT(*) FROM playlist_items WHERE contentType = :contentType")
    suspend fun getCountByType(contentType: ContentType): Int
}

class Converters {
    @TypeConverter
    fun fromContentType(value: ContentType?): String = value?.name ?: ContentType.LIVE.name

    @TypeConverter
    fun toContentType(value: String?): ContentType = try {
        if (value != null) ContentType.valueOf(value) else ContentType.LIVE
    } catch (e: Exception) {
        ContentType.LIVE
    }
}

@Database(entities = [PlaylistItemEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class FennecDatabase : RoomDatabase() {
    abstract fun dao(): FennecDao

    companion object {
        @Volatile
        private var INSTANCE: FennecDatabase? = null

        fun getInstance(context: Context): FennecDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FennecDatabase::class.java,
                    "fennec_player.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
