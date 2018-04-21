package nhdphuong.com.manga.features.preview

import android.content.Context
import nhdphuong.com.manga.Constants
import nhdphuong.com.manga.R
import nhdphuong.com.manga.api.ApiConstants
import nhdphuong.com.manga.data.entity.book.Book
import nhdphuong.com.manga.data.entity.book.Tag
import java.util.*
import javax.inject.Inject

/*
 * Created by nhdphuong on 4/14/18.
 */
class BookPreviewPresenter @Inject constructor(private val mView: BookPreviewContract.View,
                                               private val mBook: Book,
                                               private val mContext: Context) : BookPreviewContract.Presenter {
    companion object {
        private const val MILLISECOND: Long = 1000
        private const val MINUTE: Long = MILLISECOND * 60
        private const val HOUR: Long = MINUTE * 60
        private const val DAY: Long = HOUR * 24
        private const val WEEK: Long = DAY * 7
        private const val MONTH: Long = DAY * 30
        private const val YEAR: Long = DAY * 365
    }

    private var isTagListInitialized = false
    private var isLanguageListInitialized = false
    private var isArtistListInitialized = false
    private var isCategoryListInitialized = false
    private var isCharacterListInitialized = false
    private var isParodyListInitialized = false
    private var isGroupListInitialized = false

    private lateinit var mTagList: LinkedList<Tag>
    private lateinit var mArtistList: LinkedList<Tag>
    private lateinit var mCategoryList: LinkedList<Tag>
    private lateinit var mLanguageList: LinkedList<Tag>
    private lateinit var mParodyList: LinkedList<Tag>
    private lateinit var mCharacterList: LinkedList<Tag>
    private lateinit var mGroupList: LinkedList<Tag>

    init {
        mView.setPresenter(this)
    }

    override fun start() {
        mView.showBookCoverImage(ApiConstants.getBookCover(mBook.mediaId))
        mView.show1stTitle(mBook.title.englishName)
        mView.show2ndTitle(mBook.title.japaneseName)
        mView.showUploadedTime(String.format(mContext.getString(R.string.uploaded), getUploadedTimeString()))
        mView.showPageCount(String.format(mContext.getString(R.string.page_count), mBook.numOfPages))
        mTagList = LinkedList()
        mCategoryList = LinkedList()
        mArtistList = LinkedList()
        mCharacterList = LinkedList()
        mLanguageList = LinkedList()
        mParodyList = LinkedList()
        mGroupList = LinkedList()
    }

    override fun loadInfoLists() {
        for (tag in mBook.tags) {
            when (tag.type) {
                Constants.TAG -> mTagList.add(tag)
                Constants.CATEGORY -> mCategoryList.add(tag)
                Constants.CHARACTER -> mCharacterList.add(tag)
                Constants.ARTIST -> mArtistList.add(tag)
                Constants.LANGUAGE -> mLanguageList.add(tag)
                Constants.PARODY -> mParodyList.add(tag)
                Constants.GROUP -> mGroupList.add(tag)
            }
        }

        if (!isTagListInitialized) {
            if (mTagList.isEmpty()) {
                mView.hideTagList()
            } else {
                mView.showTagList(mTagList)
            }
            isTagListInitialized = true
        }
        if (!isArtistListInitialized) {
            if (mArtistList.isEmpty()) {
                mView.hideArtistList()
            } else {
                mView.showArtistList(mArtistList)
            }
            isArtistListInitialized = true
        }
        if (!isLanguageListInitialized) {
            if (mLanguageList.isEmpty()) {
                mView.hideLanguageList()
            } else {
                mView.showLanguageList(mLanguageList)
            }
            isLanguageListInitialized = true
        }
        if (!isCategoryListInitialized) {
            if (mCategoryList.isEmpty()) {
                mView.hideCategoryList()
            } else {
                mView.showCategoryList(mCategoryList)
            }
            isCategoryListInitialized = true
        }
        if (!isCharacterListInitialized) {
            if (mCharacterList.isEmpty()) {
                mView.hideCharacterList()
            } else {
                mView.showCharacterList(mCharacterList)
            }
            isCharacterListInitialized = true
        }
        if (!isGroupListInitialized) {
            if (mGroupList.isEmpty()) {
                mView.hideGroupList()
            } else {
                mView.showGroupList(mGroupList)
            }
            isGroupListInitialized = true
        }
        if (!isParodyListInitialized) {
            if (mParodyList.isEmpty()) {
                mView.hideParodyList()
            } else {
                mView.showParodyList(mParodyList)
            }
            isParodyListInitialized = true
        }
    }

    override fun stop() {

    }

    private fun getUploadedTimeString(): String {
        val uploadedTimeElapsed = System.currentTimeMillis() - mBook.updateAt * MILLISECOND
        val yearsElapsed = uploadedTimeElapsed / YEAR
        val monthsElapsed = uploadedTimeElapsed / MONTH
        val weeksElapsed = uploadedTimeElapsed / WEEK
        val daysElapsed = uploadedTimeElapsed / DAY
        val hoursElapsed = uploadedTimeElapsed / HOUR
        val minutesElapsed = uploadedTimeElapsed / MINUTE
        if (yearsElapsed > 0) {
            return if (yearsElapsed > 1) {
                String.format(mContext.getString(R.string.years_elapsed), yearsElapsed)
            } else {
                mContext.getString(R.string.year_elapsed)
            }
        }
        if (monthsElapsed > 0) {
            return if (monthsElapsed > 1) {
                String.format(mContext.getString(R.string.months_elapsed), monthsElapsed)
            } else {
                mContext.getString(R.string.month_elapsed)
            }
        }
        if (weeksElapsed > 0) {
            return if (weeksElapsed > 1) {
                String.format(mContext.getString(R.string.weeks_elapsed), weeksElapsed)
            } else {
                mContext.getString(R.string.week_elapsed)
            }
        }
        if (daysElapsed > 0) {
            return if (daysElapsed > 1) {
                String.format(mContext.getString(R.string.days_elapsed), daysElapsed)
            } else {
                mContext.getString(R.string.day_elapsed)
            }
        }
        if (hoursElapsed > 0) {
            return if (hoursElapsed > 1) {
                String.format(mContext.getString(R.string.hours_elapsed), hoursElapsed)
            } else {
                mContext.getString(R.string.hour_elapsed)
            }
        }
        if (minutesElapsed > 0) {
            return if (minutesElapsed > 1) {
                String.format(mContext.getString(R.string.minutes_elapsed), minutesElapsed)
            } else {
                mContext.getString(R.string.minute_elapsed)
            }
        }
        return mContext.getString(R.string.just_now)
    }
}