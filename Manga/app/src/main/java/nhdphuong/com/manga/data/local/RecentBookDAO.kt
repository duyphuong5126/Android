package nhdphuong.com.manga.data.local

import android.arch.persistence.room.*
import nhdphuong.com.manga.Constants
import nhdphuong.com.manga.data.entity.RecentBook

/*
 * Created by nhdphuong on 6/8/18.
 */
@Dao
interface RecentBookDAO {
    companion object {
        private const val RECENT_BOOK_TABLE: String = "RecentBook"
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecentBooks(vararg recentBookEntities: RecentBook)

    @Insert
    fun insertRecentBooks(recentBookEntities: List<RecentBook>)

    @Update
    fun updateRecentBook(recentBookEntity: RecentBook)

    @Query("select * from $RECENT_BOOK_TABLE limit :limit offset :offset")
    fun getRecentBooks(limit: Int, offset: Int): List<RecentBook>

    @Query("select * from $RECENT_BOOK_TABLE where ${Constants.IS_FAVORITE} = 1 limit :limit offset :offset")
    fun getFavoriteBooks(limit: Int, offset: Int): List<RecentBook>

    @Query("select ${Constants.IS_FAVORITE} from $RECENT_BOOK_TABLE where ${Constants.BOOK_ID} = :bookId")
    fun isFavoriteBook(bookId: String): Int

    @Query("select ${Constants.BOOK_ID} from $RECENT_BOOK_TABLE where ${Constants.BOOK_ID} = :bookId")
    fun getRecentBook(bookId: String): String

    @Query("select * from $RECENT_BOOK_TABLE")
    fun getRecentBooks(): List<RecentBook>
}