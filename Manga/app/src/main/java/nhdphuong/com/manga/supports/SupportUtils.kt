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
import nhdphuong.com.manga.NHentaiApp
import nhdphuong.com.manga.R
import java.io.BufferedInputStream
import java.io.IOException
import java.net.URL


class SupportUtils {
    companion object {
        private const val MILLISECOND: Long = 1000
        private const val MINUTE: Long = MILLISECOND * 60
        private const val HOUR: Long = MINUTE * 60
        private const val DAY: Long = HOUR * 24
        private const val WEEK: Long = DAY * 7
        private const val MONTH: Long = DAY * 30
        private const val YEAR: Long = DAY * 365

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

        fun getTimeElapsed(timeElapsed: Long): String {
            NHentaiApp.instance.applicationContext.let { context ->

                val yearsElapsed = timeElapsed / YEAR
                val monthsElapsed = timeElapsed / MONTH
                val weeksElapsed = timeElapsed / WEEK
                val daysElapsed = timeElapsed / DAY
                val hoursElapsed = timeElapsed / HOUR
                val minutesElapsed = timeElapsed / MINUTE
                if (yearsElapsed > 0) {
                    return if (yearsElapsed > 1) {
                        String.format(context.getString(R.string.years_elapsed), yearsElapsed)
                    } else {
                        context.getString(R.string.year_elapsed)
                    }
                }
                if (monthsElapsed > 0) {
                    return if (monthsElapsed > 1) {
                        String.format(context.getString(R.string.months_elapsed), monthsElapsed)
                    } else {
                        context.getString(R.string.month_elapsed)
                    }
                }
                if (weeksElapsed > 0) {
                    return if (weeksElapsed > 1) {
                        String.format(context.getString(R.string.weeks_elapsed), weeksElapsed)
                    } else {
                        context.getString(R.string.week_elapsed)
                    }
                }
                if (daysElapsed > 0) {
                    return if (daysElapsed > 1) {
                        String.format(context.getString(R.string.days_elapsed), daysElapsed)
                    } else {
                        context.getString(R.string.day_elapsed)
                    }
                }
                if (hoursElapsed > 0) {
                    return if (hoursElapsed > 1) {
                        String.format(context.getString(R.string.hours_elapsed), hoursElapsed)
                    } else {
                        context.getString(R.string.hour_elapsed)
                    }
                }
                if (minutesElapsed > 0) {
                    return if (minutesElapsed > 1) {
                        String.format(context.getString(R.string.minutes_elapsed), minutesElapsed)
                    } else {
                        context.getString(R.string.minute_elapsed)
                    }
                }
                return context.getString(R.string.just_now)
            }
        }
    }
}