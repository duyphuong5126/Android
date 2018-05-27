package nhdphuong.com.manga.features.preview

import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.util.Log
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import nhdphuong.com.manga.Constants
import nhdphuong.com.manga.NHentaiApp
import nhdphuong.com.manga.R
import nhdphuong.com.manga.api.ApiConstants
import nhdphuong.com.manga.data.entity.book.Book
import nhdphuong.com.manga.data.entity.book.ImageMeasurements
import nhdphuong.com.manga.data.entity.book.Tag
import nhdphuong.com.manga.data.repository.BookRepository
import nhdphuong.com.manga.features.reader.ReaderActivity
import nhdphuong.com.manga.supports.GlideUtils
import nhdphuong.com.manga.supports.SupportUtils
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import javax.inject.Inject

/*
 * Created by nhdphuong on 4/14/18.
 */
class BookPreviewPresenter @Inject constructor(private val mView: BookPreviewContract.View,
                                               private val mBook: Book,
                                               private val mContext: Context,
                                               private val mBookRepository: BookRepository) : BookPreviewContract.Presenter {
    companion object {
        private val TAG = BookPreviewPresenter::class.java.simpleName
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
    private var isInfoLoaded = false
    private var isBookCoverReloaded = false
    private lateinit var mCacheCoverUrl: String

    private lateinit var mTagList: LinkedList<Tag>
    private lateinit var mArtistList: LinkedList<Tag>
    private lateinit var mCategoryList: LinkedList<Tag>
    private lateinit var mLanguageList: LinkedList<Tag>
    private lateinit var mParodyList: LinkedList<Tag>
    private lateinit var mCharacterList: LinkedList<Tag>
    private lateinit var mGroupList: LinkedList<Tag>

    private val mPrefixNumber: Int
        get() {
            var totalPages = mBook.numOfPages
            var prefixCount = 1
            while (totalPages / 10 > 0) {
                totalPages /= 10
                prefixCount++
            }
            return prefixCount
        }

    init {
        mView.setPresenter(this)
    }

    override fun start() {
        if (!this::mCacheCoverUrl.isInitialized) {
            mView.showBookCoverImage(ApiConstants.getBookCover(mBook.mediaId))
        } else {
            mView.showBookCoverImage(mCacheCoverUrl)
        }
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
        if (!isInfoLoaded) {
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

            loadBookThumbnails()

            loadRecommendBook()
        }
    }

    override fun reloadCoverImage() {
        if (!isBookCoverReloaded) {
            isBookCoverReloaded = true
            launch {
                val coverUrl = async {
                    getReachableBookCover()
                }.await()
                launch(UI) {
                    mView.showBookCoverImage(coverUrl)
                }
            }
        }
    }

    override fun saveCurrentAvailableCoverUrl(url: String) {
        Log.d(TAG, "Current available url: $url")
        mCacheCoverUrl = url
    }

    override fun startReadingFrom(startReadingPage: Int) {
        ReaderActivity.start(mContext, startReadingPage, mBook)
    }

    override fun downloadBook() {
        NHentaiApp.instance.let { nHentaiApp ->
            if (!nHentaiApp.isStoragePermissionAccepted) {
                mView.showRequestStoragePermission()
                return@let
            }

            val bookPages = LinkedList<String>()
            for (pageId in 0 until mBook.bookImages.pages.size) {
                val page = mBook.bookImages.pages[pageId]
                bookPages.add(ApiConstants.getPictureUrl(mBook.mediaId, pageId + 1, page.imageType))
            }
            bookPages.size.let { total ->
                if (total > 0) {
                    mView.initDownloading(total)
                    launch {
                        var progress = 0
                        for (downloadPage in 0 until total) {
                            async {
                                mBook.bookImages.pages[downloadPage].let { page ->
                                    val result = GlideUtils.downloadImage(mContext, bookPages[downloadPage], page.width, page.height)

                                    val resultFilePath = nHentaiApp.getImageDirectory(mBook.mediaId)

                                    val format = if (page.imageType == Constants.PNG_TYPE) {
                                        Bitmap.CompressFormat.PNG
                                    } else {
                                        Bitmap.CompressFormat.JPEG
                                    }
                                    val fileName = String.format("%0${mPrefixNumber}d", downloadPage + 1)
                                    SupportUtils.compressBitmap(result, resultFilePath, fileName, format)
                                    Log.d(TAG, "$fileName is saved successfully")
                                }
                                launch(UI) {
                                    progress++
                                    mView.updateDownloadProgress(progress, total)
                                }
                                Log.d(TAG, "Download page ${downloadPage + 1} completed")
                            }.await()
                        }
                        delay(1000)
                        launch(UI) {
                            mView.finishDownloading()
                        }
                    }
                }
            }
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

    private fun getReachableBookCover(): String {
        val connectivityManager = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        val mediaId = mBook.mediaId
        val coverUrl = ApiConstants.getBookCover(mediaId)
        if (networkInfo != null && networkInfo.isConnected) {
            var isReachable = false
            val bookPages = mBook.bookImages.pages
            var currentPage = 0
            var url = ApiConstants.getPictureUrl(mediaId, currentPage, bookPages[currentPage].imageType)
            while (!isReachable && currentPage < bookPages.size) {
                isReachable = try {
                    val urlServer = URL(url)
                    val urlConn = urlServer.openConnection() as HttpURLConnection
                    urlConn.connectTimeout = 3000
                    urlConn.connect()
                    urlConn.responseCode == 200
                } catch (e: Exception) {
                    false
                }
                if (isReachable) {
                    return url
                }
                currentPage++
                url = ApiConstants.getPictureUrl(mediaId, currentPage, bookPages[currentPage].imageType)
            }
        }
        return coverUrl
    }

    private fun loadBookThumbnails() {
        val thumbnails = LinkedList<String>()
        val mediaId = mBook.mediaId
        val bookPages: List<ImageMeasurements> = mBook.bookImages.pages
        for (pageId in 0 until bookPages.size) {
            val page = bookPages[pageId]
            val url = ApiConstants.getThumbnailByPage(mediaId, pageId + 1, page.imageType)
            thumbnails.add(url)
        }
        mView.showBookThumbnailList(thumbnails)
        isInfoLoaded = true
    }

    private fun loadRecommendBook() {
        launch {
            mBookRepository.getRecommendBook(mBook.bookId)?.bookList?.let { bookList ->
                Log.d(TAG, "Number of recommend book of book ${mBook.bookId}: ${bookList.size}")
                launch(UI) {
                    mView.showRecommendBook(bookList)
                }
            }
        }
    }
}