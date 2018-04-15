package nhdphuong.com.manga.data.entity.book

import com.google.gson.annotations.SerializedName
import nhdphuong.com.manga.Constants
import java.io.Serializable

/*
 * Created by nhdphuong on 3/24/18.
 */
class BookTitle(@field:SerializedName(Constants.TITLE_ENG) val englishName: String,
                @field:SerializedName(Constants.TITLE_JAPANESE) val japaneseName: String,
                @field:SerializedName(Constants.TITLE_PRETTY) val pretty: String): Serializable {
}