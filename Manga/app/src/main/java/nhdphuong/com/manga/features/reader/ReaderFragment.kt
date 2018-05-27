package nhdphuong.com.manga.features.reader

import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import nhdphuong.com.manga.R
import nhdphuong.com.manga.databinding.FragmentReaderBinding
import nhdphuong.com.manga.supports.AnimationHelper
import nhdphuong.com.manga.views.adapters.BookReaderAdapter

/*
 * Created by nhdphuong on 5/5/18.
 */
class ReaderFragment : Fragment(), ReaderContract.View {
    companion object {
        private val TAG = ReaderFragment::class.java.simpleName
        private const val REQUEST_STORAGE_PERMISSION = 1001
    }

    private lateinit var mPresenter: ReaderContract.Presenter
    private lateinit var mBinding: FragmentReaderBinding

    override fun setPresenter(presenter: ReaderContract.Presenter) {
        mPresenter = presenter
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater!!, R.layout.fragment_reader, container, false)
        return mBinding.root
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity.window.statusBarColor = ContextCompat.getColor(context, R.color.grey_1)
        mBinding.ibBack.setOnClickListener {
            navigateToGallery()
        }

        mBinding.ibDownload.setOnClickListener {

        }

        mBinding.mtvCurrentPage.setOnClickListener {
            mPresenter.backToGallery()
        }

        mBinding.ibDownload.setOnClickListener {
            mPresenter.downloadCurrentPage()
        }

        mBinding.ibDownloadPopupClose.setOnClickListener {
            hideDownloadPopup()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mPresenter.start()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            val result = if (grantResults[0] == PackageManager.PERMISSION_GRANTED) "granted" else "denied"
            Log.d(TAG, "Storage permission is $result")
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onStop() {
        super.onStop()
        activity.window.statusBarColor = ContextCompat.getColor(context, R.color.colorPrimary)
        mPresenter.stop()
    }

    override fun showBookTitle(bookTitle: String) {

        mBinding.mtvBookTitle.let { mtvBookTitle ->
            mtvBookTitle.text = bookTitle
            AnimationHelper.startTextRunning(mtvBookTitle)
        }
    }

    override fun showBookPages(pageList: List<String>) {
        val bookReaderAdapter = BookReaderAdapter(context, pageList, View.OnClickListener {
            if (mBinding.clReaderBottom.visibility == View.VISIBLE) {
                AnimationHelper.startSlideOutTop(activity, mBinding.clReaderTop, {
                    mBinding.clReaderTop.visibility = View.GONE
                })
                AnimationHelper.startSlideOutBottom(activity, mBinding.clReaderBottom, {
                    mBinding.clReaderBottom.visibility = View.GONE
                })
            } else {
                AnimationHelper.startSlideInTop(activity, mBinding.clReaderTop, {
                    mBinding.clReaderTop.visibility = View.VISIBLE
                })
                AnimationHelper.startSlideInBottom(activity, mBinding.clReaderBottom, {
                    mBinding.clReaderBottom.visibility = View.VISIBLE
                })
            }
        })
        mBinding.vpPages.adapter = bookReaderAdapter
        mBinding.vpPages.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                mPresenter.updatePageIndicator(position)
                if (position - 1 >= 0) {
                    bookReaderAdapter.resetPage(position - 1)
                }
                if (position + 1 < bookReaderAdapter.count) {
                    bookReaderAdapter.resetPage(position + 1)
                }
            }
        })
    }

    override fun showPageIndicator(pageString: String) {
        mBinding.mtvCurrentPage.text = pageString
    }

    override fun jumpToPage(pageNumber: Int) {
        mBinding.vpPages.setCurrentItem(pageNumber, true)
    }

    override fun navigateToGallery() {
        activity.onBackPressed()
    }

    override fun requestStoragePermission() {
        val storagePermission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ActivityCompat.requestPermissions(activity, storagePermission, REQUEST_STORAGE_PERMISSION)
    }

    override fun showDownloadPopup() {
        mBinding.clDownloadedPopup.visibility = View.VISIBLE
    }

    override fun hideDownloadPopup() {
        mBinding.clDownloadedPopup.visibility = View.GONE
    }

    override fun updateDownloadPopupTitle(downloadTitle: String) {
        mBinding.mtvDownloadTitle.text = downloadTitle
    }

    override fun showLoading() {
        mBinding.pbDownloading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        mBinding.pbDownloading.visibility = View.GONE
    }

}