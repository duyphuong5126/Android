package nhdphuong.com.manga.data.entity.book

import com.google.gson.annotations.SerializedName

/*
 * Created by nhdphuong on 3/24/18.
 */
class ImageMeasurements(@field:SerializedName("t") val type: String,
                        @field:SerializedName("w") val width: Int,
                        @field:SerializedName("h") val height: Int) {

}