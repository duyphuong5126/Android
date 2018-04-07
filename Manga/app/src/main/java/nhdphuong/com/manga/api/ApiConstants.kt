package nhdphuong.com.manga.api

/*
 * Created by nhdphuong on 3/24/18.
 */
object ApiConstants {
    const val NHENTAI_HOME = "https://nhentai.net"
    const val NHENTAI_I = "https://i.nhentai.net"
    const val NHENTAI_T = "https://t.nhentai.net"

    fun getBookThumbnailBytId(mediaId: String, imageType: String): String = "$NHENTAI_T/galleries/$mediaId/thumb$imageType"
}