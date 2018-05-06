package nhdphuong.com.manga.supports

import android.annotation.SuppressLint
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.DrawableImageViewTarget

class GlideUtils {
    companion object {
        @SuppressLint("CheckResult")
        fun <IV : ImageView> loadImage(url: String, defaultResource: Int, imageView: IV) {
            val requestOptions = RequestOptions()
            requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE)
            requestOptions.error(defaultResource)
            Glide.with(imageView).load(url).apply(requestOptions).into(imageView)
        }

        fun <IV : ImageView> loadGifImage(gifResource: Int, imageView: IV) {
            val ivLoadingTarget = DrawableImageViewTarget(imageView)
            Glide.with(imageView).load(gifResource).into(ivLoadingTarget)
        }
    }
}