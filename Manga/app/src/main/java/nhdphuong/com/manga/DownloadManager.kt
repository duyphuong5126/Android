package nhdphuong.com.manga

/*
 * Created by nhdphuong on 6/2/18.
 */
class DownloadManager {
    companion object {
        private var mMediaId: String = ""
            set(value) {
                if (!isDownloading) {
                    field = value
                }
            }
        val mediaId: String
            get() = mMediaId


        private var mTotal: Int = 0
            set(value) {
                if (!isDownloading) {
                    field = value
                }
            }
        val total: Int
            get() = mTotal

        private var mProgress: Int = 0
            set(value) {
                if (isDownloading) {
                    field = value
                }
            }
        val progress: Int
            get() = mProgress

        val isDownloading: Boolean
            get() = mTotal > 0 && ((mProgress * 1f) / (mTotal * 1f)) < 1.0

        private var mDownloadCallback: DownloadCallback? = null

        fun startDownloading(mediaId: String, total: Int) {
            if (mMediaId != mediaId && !isDownloading) {
                mMediaId = mediaId
                mTotal = total
                mDownloadCallback?.onDownloadingStarted(mediaId, total)
            }
        }

        fun updateProgress(mediaId: String, progress: Int) {
            if (mMediaId == mediaId && isDownloading) {
                mProgress = progress
                mDownloadCallback?.onProgressUpdated(mediaId, progress, total)
            }
        }

        fun endDownloading() {
            mMediaId = ""
            mTotal = 0
            mProgress = 0
            mDownloadCallback?.onDownloadingEnded()
            mDownloadCallback = null
        }

        fun setDownloadCallback(downloadCallback: DownloadCallback) {
            mDownloadCallback = downloadCallback
        }
    }

    interface DownloadCallback {
        fun onDownloadingStarted(mediaId: String, total: Int)
        fun onProgressUpdated(mediaId: String, progress: Int, total: Int)
        fun onDownloadingEnded()
    }
}