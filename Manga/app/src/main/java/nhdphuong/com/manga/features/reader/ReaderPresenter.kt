package nhdphuong.com.manga.features.reader

import android.util.Log
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import nhdphuong.com.manga.api.ApiConstants
import nhdphuong.com.manga.data.entity.book.Book
import java.util.*
import javax.inject.Inject

/*
 * Created by nhdphuong on 5/5/18.
 */
class ReaderPresenter @Inject constructor(private val mView: ReaderContract.View,
                                          private val mBook: Book,
                                          private val mStartReadingPage: Int) : ReaderContract.Presenter {
    companion object {
        private val TAG = ReaderPresenter::class.java.simpleName
    }

    init {
        mView.setPresenter(this)
    }

    override fun start() {
        Log.d(TAG, "Start reading: ${mBook.previewTitle}")
        mView.showBookTitle(mBook.previewTitle)

        val bookPages = LinkedList<String>()
        for (pageId in 0 until mBook.bookImages.pages.size) {
            val page = mBook.bookImages.pages[pageId]
            bookPages.add(ApiConstants.getPictureUrl(mBook.mediaId, pageId + 1, page.imageType))
        }
        if (!bookPages.isEmpty()) {
            mView.showBookPages(bookPages)
        }

        if (mStartReadingPage != 0) {
            launch {
                delay(1000)
                launch(UI) {
                    mView.jumpToPage(mStartReadingPage)
                }
            }
        }
    }

    override fun stop() {

    }
}