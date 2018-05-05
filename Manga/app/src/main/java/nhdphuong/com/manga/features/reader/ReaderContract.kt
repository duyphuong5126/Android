package nhdphuong.com.manga.features.reader

import nhdphuong.com.manga.Base

/*
 * Created by nhdphuong on 5/5/18.
 */
interface ReaderContract {
    interface View : Base.View<Presenter>  {
        fun showBookTitle(bookTitle: String)
        fun showBookPages(pageList: List<String>)
        fun jumpToPage(pageNumber: Int)
    }

    interface Presenter : Base.Presenter {

    }
}