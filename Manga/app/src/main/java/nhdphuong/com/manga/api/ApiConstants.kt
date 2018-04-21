package nhdphuong.com.manga.api

/*
 * Created by nhdphuong on 3/24/18.
 */
object ApiConstants {
    const val NHENTAI_HOME = "https://nhentai.net"
    const val NHENTAI_I = "https://i.nhentai.net"
    const val NHENTAI_T = "https://t.nhentai.net"

    private fun getThumbnailUrl(mediaId: String) = "$NHENTAI_T/galleries/$mediaId"

    fun getBookThumbnailById(mediaId: String, imageType: String): String = "$NHENTAI_T/galleries/$mediaId/thumb$imageType"

    fun getBookCover(mediaId: String): String = "${getThumbnailUrl(mediaId)}/cover.jpg"

    private fun getGalleryUrl(mediaId: String): String = "$NHENTAI_I/galleries/$mediaId"

    fun getPictureUrl(mediaId: String, pageNumber: Int, imageType: String) = "${getGalleryUrl(mediaId)}/$pageNumber.$imageType"
}