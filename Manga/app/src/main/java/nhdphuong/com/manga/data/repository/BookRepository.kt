package nhdphuong.com.manga.data.repository

import nhdphuong.com.manga.data.BookDataSource
import nhdphuong.com.manga.scope.Remote
import nhdphuong.com.manga.data.entity.book.RemoteBook
import javax.inject.Inject
import javax.inject.Singleton

/*
 * Created by nhdphuong on 3/24/18.
 */
@Singleton
class BookRepository @Inject constructor(@Remote private val mBookRemoteDataSource: BookDataSource) : BookDataSource {
    override suspend fun getBookByPage(page: Int): RemoteBook? {
        return mBookRemoteDataSource.getBookByPage(page)
    }
}