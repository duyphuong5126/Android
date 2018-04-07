package nhdphuong.com.manga.data.entity.book

import com.google.gson.annotations.SerializedName

/*
 * Created by nhdphuong on 3/24/18.
 */
class BookTitle(@field:SerializedName("english") val englishName: String,
                @field:SerializedName("japanese") val japaneseName: String,
                @field:SerializedName("pretty") val pretty: String) {
}