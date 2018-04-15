package nhdphuong.com.manga.views.adapters

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.util.Log
import nhdphuong.com.manga.R
import nhdphuong.com.manga.Constants
import nhdphuong.com.manga.data.entity.book.Book
import nhdphuong.com.manga.supports.GlideUtils
import nhdphuong.com.manga.supports.SupportUtils

/*
 * Created by nhdphuong on 3/18/18.
 */
class MainListAdapter(private val mItemList: List<Book>, private val mMainListClickCallback: OnMainListClick)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private val TAG = MainListAdapter::class.java.simpleName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_main_list, parent, false)
        return MainListViewHolder(view, mMainListClickCallback)
    }

    override fun getItemCount(): Int = mItemList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val mainLisViewHolder = holder as MainListViewHolder
        mainLisViewHolder.setData(mItemList[position])
    }

    inner class MainListViewHolder(itemView: View, private val mMainListClickCallback: OnMainListClick) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private lateinit var mBookPreview: Book
        private val mIvItemThumbnail: ImageView = itemView.findViewById(R.id.ivItemThumbnail)
        private val mTv1stTitle: TextView = itemView.findViewById(R.id.tvItemTitle)
        private val mTv2ndTitle: TextView = itemView.findViewById(R.id.tv2ndTitlePart)
        private val mIvLanguage: ImageView = itemView.findViewById(R.id.ivLanguage)
        private var mIsTitleModifiable = true

        init {
            mIvItemThumbnail.setOnClickListener(this)
            mTv1stTitle.setOnClickListener(this)
            mTv2ndTitle.setOnClickListener(this)
            mIvLanguage.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            mMainListClickCallback.onItemClick(mBookPreview)
        }

        @SuppressLint("SetTextI18n")
        fun setData(item: Book) {
            mIsTitleModifiable = true
            mBookPreview = item
            val languageIconResId = when (item.language) {
                Constants.CHINESE_LANG -> R.drawable.ic_lang_cn
                Constants.ENGLISH_LANG -> R.drawable.ic_lang_gb
                else -> R.drawable.ic_lang_jp
            }
            mIvLanguage.setImageResource(languageIconResId)

            Log.d(TAG, "Thumbnail: ${item.thumbnail}")
            GlideUtils.loadImage(item.thumbnail, R.drawable.ic_404_not_found, mIvItemThumbnail)

            mTv1stTitle.text = item.previewTitle
            mTv1stTitle.viewTreeObserver.addOnGlobalLayoutListener({
                if (mIsTitleModifiable) {
                    val fullText = item.previewTitle
                    val ellipsizedText = SupportUtils.getEllipsizedText(mTv1stTitle)
                    val remainText = fullText.replace(ellipsizedText, "")
                    mTv1stTitle.text = ellipsizedText
                    mTv2ndTitle.text = remainText
                    mIsTitleModifiable = false
                }
            })
        }
    }

    interface OnMainListClick {
        fun onItemClick(item: Book)
    }
}