package nhdphuong.com.manga.features.preview

import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.util.Log
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import nhdphuong.com.manga.Constants
import nhdphuong.com.manga.DownloadManager
import nhdphuong.com.manga.NHentaiApp
import nhdphuong.com.manga.R
import nhdphuong.com.manga.api.ApiConstants
import nhdphuong.com.manga.data.entity.book.Book
import nhdphuong.com.manga.data.entity.book.ImageMeasurements
import nhdphuong.com.manga.data.entity.book.Tag
import nhdphuong.com.manga.data.repository.BookRepository
import nhdphuong.com.manga.features.reader.ReaderActivity
import nhdphuong.com.manga.supports.SupportUtils
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.concurrent.CountDownLatch
import javax.inject.Inject

/*
 * Created by nhdphuong on 4/14/18.
 */
class BookPreviewPresenter @Inject constructor(private val mView: BookPreviewContract.View,
                                               private val mBook: Book,
                                               private val mContext: Context,
                                               private val mBookRepository: BookRepository) : BookPreviewContract.Presenter, DownloadManager.DownloadCallback {

    companion object {
        private val TAG = BookPreviewPresenter::class.java.simpleName
        private const val MILLISECOND: Long = 1000

        private const val BATCH_COUNT = 5
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

    private val uploadedTimeStamp: String = SupportUtils.getTimeElapsed(System.currentTimeMillis() - mBook.updateAt * MILLISECOND)

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
        mView.showUploadedTime(String.format(mContext.getString(R.string.uploaded), uploadedTimeStamp))
        mView.showPageCount(String.format(mContext.getString(R.string.page_count), mBook.numOfPages))
        mTagList = LinkedList()
        mCategoryList = LinkedList()
        mArtistList = LinkedList()
        mCharacterList = LinkedList()
        mLanguageList = LinkedList()
        mParodyList = LinkedList()
        mGroupList = LinkedList()
        if (DownloadManager.isDownloading && DownloadManager.bookId == mBook.bookId) {
            DownloadManager.setDownloadCallback(this)
            mView.updateDownloadProgress(DownloadManager.progress, DownloadManager.total)
        }
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

            if (!DownloadManager.isDownloading) {
                val bookPages = LinkedList<String>()
                for (pageId in 0 until mBook.bookImages.pages.size) {
                    val page = mBook.bookImages.pages[pageId]
                    bookPages.add(ApiConstants.getPictureUrl(mBook.mediaId, pageId + 1, page.imageType))
                }
                bookPages.size.let { total ->
                    if (total > 0) {
                        DownloadManager.setDownloadCallback(this)
                        DownloadManager.startDownloading(mBook.bookId, total)
                        launch {
                            var progress = 0
                            val resultList = LinkedList<String>()
                            var currentPage = 0
                            while (currentPage < total) {
                                val lastPage = if (currentPage + BATCH_COUNT <= total) currentPage + BATCH_COUNT else total
                                runBlocking {
                                    val countDownLatch = CountDownLatch(lastPage - currentPage)
                                    for (downloadPage in currentPage until lastPage) {
                                        async {
                                            mBook.bookImages.pages[downloadPage].let { page ->
                                                val result = SupportUtils.getImageBitmap(bookPages[downloadPage])!!

                                                val resultFilePath = nHentaiApp.getImageDirectory(mBook.mediaId)

                                                val format = if (page.imageType == Constants.PNG_TYPE) {
                                                    Bitmap.CompressFormat.PNG
                                                } else {
                                                    Bitmap.CompressFormat.JPEG
                                                }
                                                val fileName = String.format("%0${mPrefixNumber}d", downloadPage + 1)
                                                val resultPath = SupportUtils.compressBitmap(result, resultFilePath, fileName, format)
                                                resultList.add(resultPath)
                                                Log.d(TAG, "$fileName is saved successfully")
                                                countDownLatch.countDown()
                                            }
                                            launch(UI) {
                                                progress++
                                                DownloadManager.updateProgress(mBook.bookId, progress)
                                                if (resultList.size == total) {
                                                    delay(1000)
                                                    nHentaiApp.refreshGallery(*resultList.toTypedArray())
                                                    DownloadManager.endDownloading()
                                                }
                                            }
                                            Log.d(TAG, "Download page ${downloadPage + 1} completed")
                                        }
                                    }
                                    countDownLatch.await()
                                }
                                currentPage += BATCH_COUNT
                            }
                        }
                    }
                }
            } else {
                if (DownloadManager.bookId == mBook.bookId) {
                    mView.showThisBookBeingDownloaded()
                } else {
                    mView.showBookBeingDownloaded(DownloadManager.bookId)
                }
            }
        }
    }

    override fun restartBookPreview(bookId: String) {
        launch {
            val bookDetails = mBookRepository.getBookDetails(bookId)
            bookDetails?.let {
                launch(UI) {
                    BookPreviewActivity.restart(bookDetails)
                }
            }
        }
    }

    override fun stop() {

    }

    override fun onDownloadingStarted(bookId: String, total: Int) {
        if (mView.isActive()) {
            mView.initDownloading(total)
        }
    }

    override fun onProgressUpdated(bookId: String, progress: Int, total: Int) {
        if (mView.isActive()) {
            mView.updateDownloadProgress(progress, total)
        }
    }

    override fun onDownloadingEnded() {
        if (mView.isActive()) {
            mView.finishDownloading()
        }
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