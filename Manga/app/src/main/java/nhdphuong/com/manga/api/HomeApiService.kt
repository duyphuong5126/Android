package nhdphuong.com.manga.api

import nhdphuong.com.manga.data.entity.book.RemoteBook
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/*
 * Created by nhdphuong on 3/24/18.
 */
interface HomeApiService {
    @GET("/api/galleries/all")
    fun getGalleriesByPage(@Query("page") pageNumber: Int): Call<RemoteBook>
}