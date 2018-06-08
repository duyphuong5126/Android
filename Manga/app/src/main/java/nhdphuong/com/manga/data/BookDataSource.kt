package nhdphuong.com.manga.data

import nhdphuong.com.manga.data.entity.book.Book
import nhdphuong.com.manga.data.entity.book.RecommendBook
import nhdphuong.com.manga.data.entity.book.RemoteBook

/*
 * Created by nhdphuong on 3/24/18.
 */
interface BookDataSource {
    suspend fun getBookByPage(page: Int): RemoteBook?
    suspend fun getBookDetails(bookId: String): Book?
    suspend fun getRecommendBook(bookId: String): RecommendBook?
}