package nhdphuong.com.manga.features.header

import javax.inject.Inject

/*
 * Created by nhdphuong on 4/10/18.
 */
class HeaderPresenter @Inject constructor(private val mView: HeaderContract.View) : HeaderContract.Presenter {
    init {
        mView.setPresenter(this)
    }

    override fun start() {

    }

    override fun stop() {

    }

}