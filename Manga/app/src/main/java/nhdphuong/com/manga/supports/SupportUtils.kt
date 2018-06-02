package nhdphuong.com.manga.supports

import android.content.Context
import android.graphics.Bitmap
import android.text.TextUtils
import android.util.TypedValue
import android.widget.TextView
import nhdphuong.com.manga.Constants
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.util.*
import android.graphics.BitmapFactory
import java.io.BufferedInputStream
import java.io.IOException
import java.net.URL


class SupportUtils {
    companion object {
        fun dp2Pixel(context: Context, dp: Int): Int =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp * 1F, context.resources.displayMetrics).toInt()

        fun formatBigNumber(number: Long): String {
            return NumberFormat.getNumberInstance(Locale.US).format(number)
        }

        fun getEllipsizedText(textView: TextView): String {
            val text = textView.text.toString()
            val lines = textView.lineCount
            val width = textView.width
            val len = text.length
            val where = TextUtils.TruncateAt.END
            val paint = textView.paint

            val result = StringBuffer()

            var startPosition = 0
            var cnt: Int
            var tmp: Int
            var hasLines = 0

            while (hasLines < lines - 1) {
                cnt = paint.breakText(text, startPosition, len, true, width.toFloat(), null)
                if (cnt >= len - startPosition) {
                    result.append(text.substring(startPosition))
                    break
                }

                tmp = text.lastIndexOf('\n', startPosition + cnt - 1)

                if (tmp >= 0 && tmp < startPosition + cnt) {
                    result.append(text.substring(startPosition, tmp + 1))
                    startPosition += tmp + 1
                } else {
                    tmp = text.lastIndexOf(' ', startPosition + cnt - 1)
                    startPosition += if (tmp >= startPosition) {
                        result.append(text.substring(startPosition, tmp + 1))
                        tmp + 1
                    } else {
                        result.append(text.substring(startPosition, cnt))
                        cnt
                    }
                }

                hasLines++
            }

            if (startPosition < len) {
                result.append(TextUtils.ellipsize(text.subSequence(startPosition, len), paint, width.toFloat(), where))
            }

            val ellipsizedText = result.toString()
            return ellipsizedText.substring(0, ellipsizedText.length - 1)
        }

        fun compressBitmap(bitmap: Bitmap, filePath: String, filename: String, format: Bitmap.CompressFormat): String {
            val dirs = File(filePath)
            if (!dirs.exists()) {
                dirs.mkdirs()
            }

            val fileType = if (format == Bitmap.CompressFormat.PNG) {
                Constants.PNG
            } else {
                Constants.JPG
            }
            val resultPath = "$filePath/$filename.$fileType"
            val output = File(resultPath)
            if (!output.exists()) {
                output.createNewFile()
            }

            val outputStream = FileOutputStream(output)
            bitmap.compress(format, 100, outputStream)
            bitmap.recycle()
            return resultPath
        }

        fun getImageBitmap(urlString: String): Bitmap? {
            var bitmap: Bitmap? = null
            try {
                val url = URL(urlString)
                val conn = url.openConnection()
                conn.connectTimeout = 10000
                conn.readTimeout = 20000
                conn.connect()
                val inputStream = conn.getInputStream()
                val bufferedInputStream = BufferedInputStream(inputStream)
                bitmap = BitmapFactory.decodeStream(bufferedInputStream)
                bufferedInputStream.close()
                inputStream.close()
            } catch (e: IOException) {

            }

            return bitmap
        }
    }
}