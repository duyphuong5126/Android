package nhdphuong.com.manga.data.entity.book

import com.google.gson.annotations.SerializedName
import java.util.*

/*
 * Created by nhdphuong on 3/24/18.
 */
class RemoteBook(@field:SerializedName("result") val bookList: LinkedList<Book>,
                 @field:SerializedName("num_pages") val numOfPages: Long,
                 @field:SerializedName("per_page") val numOfBooksPerPage: Int)