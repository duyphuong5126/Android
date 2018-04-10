package nhdphuong.com.manga.features.header

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import nhdphuong.com.manga.R
import nhdphuong.com.manga.data.Tab
import nhdphuong.com.manga.databinding.FragmentHeaderBinding
import nhdphuong.com.manga.supports.SpaceItemDecoration
import nhdphuong.com.manga.views.adapters.TabAdapter

/*
 * Created by nhdphuong on 4/10/18.
 */
class HeaderFragment : Fragment(), HeaderContract.View {
    private lateinit var mPresenter: HeaderContract.Presenter
    private lateinit var mBinding: FragmentHeaderBinding
    private lateinit var mTabAdapter: TabAdapter
    override fun setPresenter(presenter: HeaderContract.Presenter) {
        mPresenter = presenter
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_header, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mTabAdapter = TabAdapter(context, object : TabAdapter.OnMainTabClick {
            override fun onTabClick(tab: Tab) {

            }
        })

        val tabSelector: RecyclerView = mBinding.rvMainTabs
        tabSelector.adapter = mTabAdapter
        tabSelector.addItemDecoration(SpaceItemDecoration(context, R.dimen.dp20, true, true))
        tabSelector.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        mBinding.ibHamburger.setOnClickListener {
            val isTabHidden = tabSelector.visibility == View.GONE
            tabSelector.visibility = if (!isTabHidden) View.GONE else View.VISIBLE
        }
    }

    override fun showLoading() {
    }

    override fun hideLoading() {

    }
}