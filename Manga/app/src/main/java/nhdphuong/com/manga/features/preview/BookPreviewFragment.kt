package nhdphuong.com.manga.features.preview

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import nhdphuong.com.manga.R
import nhdphuong.com.manga.data.entity.book.Tag
import nhdphuong.com.manga.databinding.FragmentBookPreviewBinding
import nhdphuong.com.manga.views.InfoCardLayout

/*
 * Created by nhdphuong on 4/14/18.
 */
class BookPreviewFragment : Fragment(), BookPreviewContract.View {
    private lateinit var mPresenter: BookPreviewContract.Presenter
    private lateinit var mBinding: FragmentBookPreviewBinding
    private lateinit var mRequestOptions: RequestOptions
    private lateinit var mRequestManager: RequestManager

    override fun setPresenter(presenter: BookPreviewContract.Presenter) {
        mPresenter = presenter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRequestManager = Glide.with(this)
        mRequestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.ic_404_not_found)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_book_preview, container, false)
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        mPresenter.start()
        mBinding.root.viewTreeObserver.addOnGlobalLayoutListener {
            mPresenter.loadInfoLists()
        }
    }

    override fun showBookCoverImage(coverUrl: String) {
        mRequestManager.load(coverUrl).apply(mRequestOptions).into(mBinding.ivBookCover)
    }

    override fun show1stTitle(firstTitle: String) {
        if (!TextUtils.isEmpty(firstTitle)) {
            mBinding.tvTitle1.visibility = View.VISIBLE
            mBinding.tvTitle1.text = firstTitle
        } else {
            mBinding.tvTitle1.visibility = View.GONE
        }
    }

    override fun show2ndTitle(secondTitle: String) {
        if (!TextUtils.isEmpty(secondTitle)) {
            mBinding.tvTitle2.visibility = View.VISIBLE
            mBinding.tvTitle2.text = secondTitle
        } else {
            mBinding.tvTitle2.visibility = View.GONE
        }
    }

    override fun showTagList(tagList: List<Tag>) {
        mBinding.tvTagsLabel.visibility = View.VISIBLE
        mBinding.clTags.visibility = View.VISIBLE
        loadInfoList(mBinding.clTags, tagList)
    }

    override fun showArtistList(artistList: List<Tag>) {
        mBinding.tvArtistsLabel.visibility = View.VISIBLE
        mBinding.clArtists.visibility = View.VISIBLE
        loadInfoList(mBinding.clArtists, artistList)
    }

    override fun showLanguageList(languageList: List<Tag>) {
        mBinding.tvLanguagesLabel.visibility = View.VISIBLE
        mBinding.clLanguages.visibility = View.VISIBLE
        loadInfoList(mBinding.clLanguages, languageList)
    }

    override fun showCategoryList(categoryList: List<Tag>) {
        mBinding.tvCategoriesLabel.visibility = View.VISIBLE
        mBinding.clCategories.visibility = View.VISIBLE
        loadInfoList(mBinding.clCategories, categoryList)
    }

    override fun showCharacterList(characterList: List<Tag>) {
        mBinding.tvCharactersLabel.visibility = View.VISIBLE
        mBinding.clCharacters.visibility = View.VISIBLE
        loadInfoList(mBinding.clCharacters, characterList)
    }

    override fun showGroupList(groupList: List<Tag>) {
        mBinding.tvGroupsLabel.visibility = View.VISIBLE
        mBinding.clGroups.visibility = View.VISIBLE
        loadInfoList(mBinding.clGroups, groupList)
    }

    override fun showParodyList(parodyList: List<Tag>) {
        mBinding.tvParodiesLabel.visibility = View.VISIBLE
        mBinding.clParodies.visibility = View.VISIBLE
        loadInfoList(mBinding.clParodies, parodyList)
    }

    override fun hideTagList() {
        mBinding.tvTagsLabel.visibility = View.GONE
        mBinding.clTags.visibility = View.GONE
    }

    override fun hideArtistList() {
        mBinding.tvArtistsLabel.visibility = View.GONE
        mBinding.clArtists.visibility = View.GONE
    }

    override fun hideLanguageList() {
        mBinding.tvLanguagesLabel.visibility = View.GONE
        mBinding.clLanguages.visibility = View.GONE
    }

    override fun hideCategoryList() {
        mBinding.tvCategoriesLabel.visibility = View.GONE
        mBinding.clCategories.visibility = View.GONE
    }

    override fun hideCharacterList() {
        mBinding.tvCharactersLabel.visibility = View.GONE
        mBinding.clCharacters.visibility = View.GONE
    }

    override fun hideGroupList() {
        mBinding.tvGroupsLabel.visibility = View.GONE
        mBinding.clGroups.visibility = View.GONE
    }

    override fun hideParodyList() {
        mBinding.tvParodiesLabel.visibility = View.GONE
        mBinding.clParodies.visibility = View.GONE
    }

    override fun showPageCount(pageCount: String) {
        mBinding.tvPageCount.text = pageCount
    }

    override fun showUploadedTime(uploadedTime: String) {
        mBinding.tvUpdatedAt.text = uploadedTime
    }

    override fun showLoading() {

    }

    private fun loadInfoList(layout: ViewGroup, infoList: List<Tag>) {
        val infoCardLayout = InfoCardLayout(activity.layoutInflater, infoList, context)
        infoCardLayout.loadInfoList(layout)
    }

    override fun hideLoading() {

    }
}