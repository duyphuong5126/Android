package nhdphuong.com.manga.supports

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

class GlideUtils {
    companion object {
        fun<IV : ImageView> loadImage(url: String, defaultResource: Int, imageView: IV) {
            val requestOptions = RequestOptions()
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL)
            requestOptions.error(defaultResource)
            Glide.with(imageView).load(url).apply(requestOptions).into(imageView)
        }
    }
}