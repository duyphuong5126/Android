package nhdphuong.com.manga.data.entity.book

import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import nhdphuong.com.manga.api.ApiConstants
import nhdphuong.com.manga.Constants

/*
 * Created by nhdphuong on 3/24/18.
 */
class Book(@field:SerializedName("id") val bookId: String,
           @field:SerializedName("media_id") private val mediaId: String,
           @field:SerializedName("title") private val title: BookTitle,
           @field:SerializedName("images") private val bookImages: BookImages,
           @field:SerializedName("scanlator") val scanlator: String,
           @field:SerializedName("upload_date") val updateAt: Long,
           @field:SerializedName("tags") val tags: List<Tag>,
           @field:SerializedName("num_pages") val numOfPages: Int,
           @field:SerializedName("num_favorites") val numOfFavorites: Int) {

    companion object {
        private const val ENG = "[English]"
        private const val CN = "[Chinese]"
        private const val NULL = "null"
    }

    val thumbnail: String
        get() {
            val imageType = if (Constants.PNG_TYPE.equals(bookImages.thumbnail.type, ignoreCase = true)) Constants.PNG else Constants.JPG
            return ApiConstants.getBookThumbnailBytId(mediaId, imageType)
        }

    val previewTitle: String
        get() {
            return if (!TextUtils.isEmpty(title.englishName) && !NULL.equals(title.englishName, ignoreCase = true)) {
                title.englishName + "\n"
            } else if (!TextUtils.isEmpty(title.japaneseName) && !NULL.equals(title.japaneseName, ignoreCase = true)) {
                title.japaneseName + "\n"
            } else if (!TextUtils.isEmpty(title.pretty) && !NULL.equals(title.pretty, ignoreCase = true)) {
                title.pretty
            } else ""
        }

    val language: String
        get() {
            return when {
                title.englishName.contains(ENG, ignoreCase = true) -> Constants.ENGLISH_LANG
                title.englishName.contains(CN, ignoreCase = true) -> Constants.CHINESE_LANG
                else -> Constants.JAPANESE_LANG
            }
        }

    val logString: String
        get() {
            return "Book title: eng=${title.englishName}\n" +
                    "            japanese=${title.japaneseName}\n" +
                    "            pretty=${title.pretty}\n"
        }
}