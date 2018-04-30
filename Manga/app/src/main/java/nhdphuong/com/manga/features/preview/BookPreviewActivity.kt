package nhdphuong.com.manga.features.preview

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import nhdphuong.com.manga.Constants
import nhdphuong.com.manga.NHentaiApp
import nhdphuong.com.manga.R
import nhdphuong.com.manga.data.entity.book.Book
import javax.inject.Inject


class BookPreviewActivity : AppCompatActivity() {
    @Suppress("unused")
    @Inject
    lateinit var mPresenter: BookPreviewPresenter

    companion object {
        fun start(context: Context, book: Book) {
            val intent = Intent(context, BookPreviewActivity::class.java)
            intent.putExtra(Constants.BOOK, book)
            context.startActivity(intent)
        }

        private var mInstance: BookPreviewActivity? = null

        fun restart(book: Book) {
            mInstance?.let { bookPreviewActivity ->
                bookPreviewActivity.intent.putExtra(Constants.BOOK, book)
                bookPreviewActivity.recreate()
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_preview)
        mInstance = this

        val book = intent.extras.getSerializable(Constants.BOOK) as Book

        var bookPreviewFragment = supportFragmentManager.findFragmentById(R.id.clBookPreview) as BookPreviewFragment?
        if (bookPreviewFragment == null) {
            bookPreviewFragment = BookPreviewFragment()
            supportFragmentManager.beginTransaction()
                    .replace(R.id.clBookPreview, bookPreviewFragment)
                    .commitAllowingStateLoss()
        }

        NHentaiApp.instance.applicationComponent.plus(BookPreviewModule(bookPreviewFragment, book)).inject(this)
    }
}
