package nhdphuong.com.manga.views

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import nhdphuong.com.manga.R
import nhdphuong.com.manga.supports.GlideUtils
import nhdphuong.com.manga.views.customs.MyButton
import nhdphuong.com.manga.views.customs.MyTextView

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

        @SuppressLint("InflateParams")
        fun showStoragePermissionDialog(activity: Activity, onOk: () -> Unit, onDismiss: () -> Unit) {
            val contentView = activity.layoutInflater.inflate(R.layout.dialog_permission, null, false)
            val dialog = Dialog(activity)
            val mtvDescription: MyTextView = contentView.findViewById(R.id.mtvPermissionDescription)
            mtvDescription.text = activity.getString(R.string.storage_permission_require)
            contentView.findViewById<MyButton>(R.id.mbOkButton).setOnClickListener {
                dialog.dismiss()
                onOk()
            }
            contentView.findViewById<MyButton>(R.id.mbDismissButton).setOnClickListener {
                dialog.dismiss()
                onDismiss()
            }
            dialog.setContentView(contentView)
            dialog.show()
            dialog.window.let { window ->
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                window.setGravity(Gravity.CENTER)
                window.decorView.setBackgroundResource(android.R.color.transparent)
            }
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