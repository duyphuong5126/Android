package nhdphuong.com.manga.data.entity.book

import com.google.gson.annotations.SerializedName

/*
 * Created by nhdphuong on 3/24/18.
 */
class BookImages(@field:SerializedName("pages") val pages: List<ImageMeasurements>,
                 @field:SerializedName("cover") val cover: ImageMeasurements,
                 @field:SerializedName("thumbnail") val thumbnail: ImageMeasurements) {

}