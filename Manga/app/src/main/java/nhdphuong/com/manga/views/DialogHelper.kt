package nhdphuong.com.manga.views

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import nhdphuong.com.manga.R
import nhdphuong.com.manga.supports.GlideUtils

class DialogHelper {
    companion object {
        @SuppressLint("InflateParams", "SetTextI18n")
        fun showLoadingDialog(activity: Activity): Dialog {
            val dotsArray = listOf(".", "..", "...")
            var currentPos = 0
            val loadingString = activity.getString(R.string.loading)
            val dialog = Dialog(activity, android.R.style.Theme_Black_NoTitleBar)
            val layoutInflater = activity.layoutInflater
            val contentView = layoutInflater.inflate(R.layout.layout_loading_dialog, null, false)
            val tvLoading: TextView = contentView.findViewById(R.id.tvLoading)
            val ivLoading: ImageView = contentView.findViewById(R.id.ivLoading)
            dialog.setContentView(contentView)
            dialog.setCancelable(false)
            dialog.window.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
            GlideUtils.loadGifImage(R.raw.ic_loading_cat_transparent, ivLoading)
            val onFinishTask = runScheduledTaskOnMainThread({
                Log.d("Dialog", "Current pos: $currentPos")
                tvLoading.text = loadingString + dotsArray[currentPos]
                if (currentPos < dotsArray.size - 1) currentPos++ else currentPos = 0
            }, 700)

            dialog.setOnDismissListener {
                onFinishTask()
            }

            return dialog
        }

        private fun runScheduledTaskOnMainThread(task: () -> Unit, timeInterval: Long): () -> Unit {
            val handler = Handler(Looper.getMainLooper())
            val updateDotsTask = object : Runnable {
                override fun run() {
                    task()
                    handler.postDelayed(this, timeInterval)
                }
            }
            handler.post {
                updateDotsTask.run()
            }
            return {
                handler.removeCallbacksAndMessages(null)
            }
        }
    }
}