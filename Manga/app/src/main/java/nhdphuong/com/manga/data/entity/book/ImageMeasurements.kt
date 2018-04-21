package nhdphuong.com.manga.data.entity.book

import com.google.gson.annotations.SerializedName
import nhdphuong.com.manga.Constants
import java.io.Serializable

/*
 * Created by nhdphuong on 3/24/18.
 */
class ImageMeasurements(@field:SerializedName(Constants.IMAGE_TYPE) val type: String,
                        @field:SerializedName(Constants.IMAGE_WIDTH) val width: Int,
                        @field:SerializedName(Constants.IMAGE_HEIGHT) val height: Int): Serializable