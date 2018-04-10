package nhdphuong.com.manga.features.home

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
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
import nhdphuong.com.manga.R
import nhdphuong.com.manga.views.adapters.MainListAdapter
import nhdphuong.com.manga.data.entity.book.Book
import nhdphuong.com.manga.databinding.FragmentHomeBinding
import nhdphuong.com.manga.views.DialogHelper
import nhdphuong.com.manga.views.adapters.HomePaginationAdapter

/*
 * Created by nhdphuong on 3/16/18.
 */
class HomeFragment : Fragment(), HomeContract.View {

    companion object {
        private val TAG = HomeFragment::class.java.simpleName
        private const val GRID_COLUMNS = 2
    }

    private lateinit var mBinding: FragmentHomeBinding
    private lateinit var mHomeListAdapter: MainListAdapter
    private lateinit var mHomePaginationAdapter: HomePaginationAdapter
    private lateinit var mHomePresenter: HomeContract.Presenter
    private lateinit var mLoadingDialog: Dialog

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
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
        mHomePresenter.stop()
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
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG, "onConfigurationChanged")
    }

    @SuppressLint("PrivateResource")
    override fun setUpHomeBookList(homeBookList: List<Book>) {
        mHomeListAdapter = MainListAdapter(homeBookList, object : MainListAdapter.OnMainListClick {
            override fun onItemClick(item: Book) {

            }
        })
        val mainList: RecyclerView = mBinding.rvMainList
        val mainListLayoutManager = GridLayoutManager(context, GRID_COLUMNS)
        mainListLayoutManager.isAutoMeasureEnabled = true
        mainList.layoutManager = mainListLayoutManager
        mainList.adapter = mHomeListAdapter
    }

    override fun refreshHomeBookList() {
        mHomeListAdapter.notifyDataSetChanged()
        mBinding.rvMainList.post {
            mBinding.rvMainList.smoothScrollBy(0, 0)
        }
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

    override fun showNothingView(isEmpty: Boolean) {
        mBinding.clNothing.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    override fun showLoading() {
        mLoadingDialog.show()
        mBinding.clNavigation.visibility = View.GONE
    }

    override fun hideLoading() {
        mLoadingDialog.dismiss()
        mBinding.clNavigation.visibility = View.VISIBLE
    }

    private fun jumpTo(pageNumber: Int) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            mBinding.rvPagination.scrollToPosition(pageNumber)
        }
    }
}