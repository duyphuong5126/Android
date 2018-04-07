package nhdphuong.com.manga.data.entity.book

import com.google.gson.annotations.SerializedName

/*
 * Created by nhdphuong on 3/24/18.
 */
class Tag(@field:SerializedName("id") val tagId: Long,
          @field:SerializedName("type") val type: String,
          @field:SerializedName("name") val name: String,
          @field:SerializedName("url") val url: String,
          @field:SerializedName("count") val count: Long) {

}