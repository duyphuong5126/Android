package nhdphuong.com.manga.features.home

import nhdphuong.com.manga.Base
import nhdphuong.com.manga.data.entity.book.Book

/*
 * Created by nhdphuong on 3/18/18.
 */
interface HomeContract {
    interface View : Base.View<Presenter> {
        fun setUpHomeBookList(homeBookList: List<Book>)
        fun refreshHomeBookList()
        fun refreshHomePagination(pageCount: Long)
        fun showNothingView(isEmpty: Boolean)
    }

    interface Presenter : Base.Presenter {
        fun jumpToPage(pageNumber: Int)
        fun jumToFirstPage()
        fun jumToLastPage()
    }
}