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
class BookAdapter(private val mItemList: List<Book>, private val mAdapterType: Int, private val mBookClickCallback: OnBookClick)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private val TAG = BookAdapter::class.java.simpleName
        const val HOME_PREVIEW_BOOK = 1
        const val RECOMMEND_BOOK = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutResId = when (viewType) {
            HOME_PREVIEW_BOOK -> R.layout.item_home_list
            RECOMMEND_BOOK -> R.layout.item_recommend_list
            else -> R.layout.item_home_list
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        return MainListViewHolder(view, mBookClickCallback)
    }

    override fun getItemCount(): Int = mItemList.size

    override fun getItemViewType(position: Int): Int = mAdapterType

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val mainLisViewHolder = holder as MainListViewHolder
        mainLisViewHolder.setData(mItemList[position])
    }

    inner class MainListViewHolder(itemView: View, private val mBookClickCallback: OnBookClick) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
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
            mBookClickCallback.onItemClick(mBookPreview)
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

    interface OnBookClick {
        fun onItemClick(item: Book)
    }
}