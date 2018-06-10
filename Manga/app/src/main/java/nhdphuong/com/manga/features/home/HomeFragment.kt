package nhdphuong.com.manga.features.home

import `in`.srain.cube.views.ptr.PtrDefaultHandler
import `in`.srain.cube.views.ptr.PtrFrameLayout
import `in`.srain.cube.views.ptr.PtrHandler
import `in`.srain.cube.views.ptr.PtrUIHandler
import `in`.srain.cube.views.ptr.indicator.PtrIndicator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import nhdphuong.com.manga.Constants
import nhdphuong.com.manga.R
import nhdphuong.com.manga.views.adapters.BookAdapter
import nhdphuong.com.manga.data.entity.book.Book
import nhdphuong.com.manga.databinding.FragmentHomeBinding
import nhdphuong.com.manga.features.preview.BookPreviewActivity
import nhdphuong.com.manga.views.DialogHelper
import nhdphuong.com.manga.views.adapters.HomePaginationAdapter

/*
 * Created by nhdphuong on 3/16/18.
 */
class HomeFragment : Fragment(), HomeContract.View, PtrUIHandler {

    companion object {
        private val TAG = HomeFragment::class.java.simpleName
        private const val GRID_COLUMNS = 2
        private const val LANDSCAPE_GRID_COLUMNS = 3
    }

    private lateinit var mBinding: FragmentHomeBinding
    private lateinit var mHomeListAdapter: BookAdapter
    private lateinit var mHomePaginationAdapter: HomePaginationAdapter
    private lateinit var mHomePresenter: HomeContract.Presenter
    private lateinit var mLoadingDialog: Dialog

    private lateinit var mUpdateDotsHandler: Handler

    override fun setPresenter(presenter: HomeContract.Presenter) {
        mHomePresenter = presenter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Log.d(TAG, "onAttach")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView")
        mBinding = DataBindingUtil.inflate(inflater!!, R.layout.fragment_home, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

        mBinding.btnFirst.setOnClickListener {
            mHomePresenter.jumToFirstPage()
            mHomePaginationAdapter.selectFirstPage()
            jumpTo(0)
        }
        mBinding.btnLast.setOnClickListener {
            mHomePresenter.jumToLastPage()
            mHomePaginationAdapter.selectLastPage()
            jumpTo(mHomePaginationAdapter.itemCount - 1)
        }
        mLoadingDialog = DialogHelper.showLoadingDialog(activity)
        mBinding.srlPullToReload.addPtrUIHandler(this)
        mBinding.srlPullToReload.setPtrHandler(object : PtrHandler {
            override fun onRefreshBegin(frame: PtrFrameLayout?) {
                mHomePresenter.reloadCurrentPage {
                    frame?.postDelayed({
                        mBinding.srlPullToReload.refreshComplete()
                    }, 1000)
                }
            }

            override fun checkCanDoRefresh(frame: PtrFrameLayout?, content: View?, header: View?): Boolean {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header)
            }
        })
        mBinding.mtvReload.setOnClickListener {
            mHomePresenter.reloadCurrentPage {

            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, "onActivityCreated")
        mHomePresenter.start()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState")
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Log.d(TAG, "onViewStateRestored")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        mHomePresenter.stop()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG, "onConfigurationChanged")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.BOOK_PREVIEW_RESULT && resultCode == Activity.RESULT_OK) {
            mHomePresenter.reloadRecentBooks()
        }
    }

    @SuppressLint("PrivateResource")
    override fun setUpHomeBookList(homeBookList: List<Book>) {
        val homeFragment = this
        mHomeListAdapter = BookAdapter(homeBookList, BookAdapter.HOME_PREVIEW_BOOK, object : BookAdapter.OnBookClick {
            override fun onItemClick(item: Book) {
                BookPreviewActivity.start(homeFragment, item)
            }
        })
        val mainList: RecyclerView = mBinding.rvMainList
        val isLandscape = resources.getBoolean(R.bool.is_landscape)
        val mainListLayoutManager = GridLayoutManager(context, if (isLandscape) LANDSCAPE_GRID_COLUMNS else GRID_COLUMNS)
        mainListLayoutManager.isAutoMeasureEnabled = true
        mainList.layoutManager = mainListLayoutManager
        mainList.adapter = mHomeListAdapter
    }

    override fun refreshHomeBookList() {
        mHomeListAdapter.notifyDataSetChanged()
        mHomePresenter.saveLastBookListRefreshTime()
        mBinding.rvMainList.post {
            mBinding.rvMainList.smoothScrollBy(0, 0)
        }
        mHomePresenter.reloadRecentBooks()
    }

    override fun refreshHomePagination(pageCount: Long) {
        mHomePaginationAdapter = HomePaginationAdapter(context, pageCount.toInt(), object : HomePaginationAdapter.OnPageSelectCallback {
            override fun onPageSelected(page: Int) {
                Log.d(TAG, "Page $page is selected")
                mHomePresenter.jumpToPage(page)
            }
        })
        val mainPagination = mBinding.rvPagination
        mainPagination.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        mainPagination.adapter = mHomePaginationAdapter
    }

    override fun showLastBookListRefreshTime(lastRefreshTimeStamp: String) {
        val lastRefresh = String.format(getString(R.string.last_update), lastRefreshTimeStamp)
        mBinding.refreshHeader?.mtvLastUpdate?.text = lastRefresh
    }

    override fun showNothingView(isEmpty: Boolean) {
        mBinding.clNothing.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    override fun showRefreshingDialog() {
        DialogHelper.showBookListRefreshingDialog(activity, onOk = {

        })
    }

    override fun showFavoriteBooks(favoriteList: List<Int>) {
        mHomeListAdapter.setFavoriteList(favoriteList)
    }

    override fun showRecentBooks(recentList: List<Int>) {
        mHomeListAdapter.setRecentList(recentList)
    }

    override fun showLoading() {
        mLoadingDialog.show()
        mBinding.clNavigation.visibility = View.GONE
    }

    override fun hideLoading() {
        mLoadingDialog.dismiss()
        mBinding.clNavigation.visibility = View.VISIBLE
    }

    override fun isActive(): Boolean = isAdded

    override fun onUIRefreshComplete(frame: PtrFrameLayout?) {
        Log.d(TAG, "onUIRefreshComplete")
        endUpdateDotsTask()
        mBinding.refreshHeader?.mtvRefresh?.text = getString(R.string.updated)
        mHomePresenter.saveLastBookListRefreshTime()
        mHomePresenter.reloadLastBookListRefreshTime()
        mBinding.refreshHeader?.ivRefresh?.rotation = 0F
        mBinding.refreshHeader?.ivRefresh?.visibility = View.VISIBLE
        mBinding.refreshHeader?.pbRefresh?.visibility = View.GONE
    }

    override fun onUIPositionChange(frame: PtrFrameLayout?, isUnderTouch: Boolean, status: Byte, ptrIndicator: PtrIndicator?) {
        Log.d(TAG, "onUIPositionChange isUnderTouch: $isUnderTouch, status: $status, " +
                "over keep header: ${ptrIndicator?.isOverOffsetToKeepHeaderWhileLoading}, " +
                "over refresh: ${ptrIndicator?.isOverOffsetToRefresh}")
        if (ptrIndicator?.isOverOffsetToKeepHeaderWhileLoading == true) {
            mBinding.refreshHeader?.mtvRefresh?.text = getString(R.string.release_to_refresh)
            mBinding.refreshHeader?.ivRefresh?.rotation = 180F
        }
    }

    override fun onUIRefreshBegin(frame: PtrFrameLayout?) {
        Log.d(TAG, "onUIRefreshBegin")
        mBinding.refreshHeader?.ivRefresh?.visibility = View.GONE
        mBinding.refreshHeader?.pbRefresh?.visibility = View.VISIBLE
        mBinding.refreshHeader?.mtvRefresh?.text = String.format(getString(R.string.updating), "")
        runUpdateDotsTask()

    }

    override fun onUIRefreshPrepare(frame: PtrFrameLayout?) {
        Log.d(TAG, "onUIRefreshPrepare")
        mHomePresenter.reloadLastBookListRefreshTime()
    }

    override fun onUIReset(frame: PtrFrameLayout?) {
        Log.d(TAG, "onUIReset")
        mBinding.refreshHeader?.mtvRefresh?.text = getString(R.string.pull_down)
    }

    private fun jumpTo(pageNumber: Int) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            mBinding.rvPagination.scrollToPosition(pageNumber)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun runUpdateDotsTask() {
        mUpdateDotsHandler = Handler()
        var currentPos = 0
        val updateDotsTask = {
            val dotsArray = resources.getStringArray(R.array.dots)
            val loadingString = getString(R.string.updating)
            Log.d("Dialog", "Current pos: $currentPos")
            mBinding.refreshHeader?.mtvRefresh?.text = String.format(loadingString, dotsArray[currentPos])
            if (currentPos < dotsArray.size - 1) currentPos++ else currentPos = 0
        }
        val runnable = object : Runnable {
            override fun run() {
                updateDotsTask()
                mUpdateDotsHandler.postDelayed(this, 500)
            }
        }
        runnable.run()
    }

    private fun endUpdateDotsTask() {
        if (this::mUpdateDotsHandler.isInitialized) {
            mUpdateDotsHandler.removeCallbacksAndMessages(null)
        }
    }
}