package nhdphuong.com.manga.data.remote

import android.util.Log
import nhdphuong.com.manga.api.HomeApiService
import nhdphuong.com.manga.data.BookDataSource
import nhdphuong.com.manga.data.entity.book.RemoteBook
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.CountDownLatch

/*
 * Created by nhdphuong on 3/24/18.
 */
class BookRemoteDataSource(private val mHomeApiService: HomeApiService) : BookDataSource{
    companion object {
        private val TAG = BookRemoteDataSource::class.java.simpleName
    }
    override suspend fun getBookByPage(page: Int): RemoteBook? {
        val countDownLatch = CountDownLatch(1)
        var remoteBook: RemoteBook? = null
        mHomeApiService.getGalleriesByPage(page).enqueue(object : Callback<RemoteBook> {
            override fun onResponse(call: Call<RemoteBook>?, response: Response<RemoteBook>?) {
                Log.d(TAG, "getBookByPage get remote successfully")
                remoteBook = response?.body()
                countDownLatch.countDown()
            }

            override fun onFailure(call: Call<RemoteBook>?, t: Throwable?) {
                Log.d(TAG, "getBookByPage get remote fail")
                countDownLatch.countDown()
            }
        })
        countDownLatch.await()
        return remoteBook
    }
}