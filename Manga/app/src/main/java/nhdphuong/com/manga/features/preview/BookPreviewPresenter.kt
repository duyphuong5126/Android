package nhdphuong.com.manga.features.preview

import nhdphuong.com.manga.Constants
import nhdphuong.com.manga.api.ApiConstants
import nhdphuong.com.manga.data.entity.book.Book
import nhdphuong.com.manga.data.entity.book.Tag
import java.util.*
import javax.inject.Inject

/*
 * Created by nhdphuong on 4/14/18.
 */
class BookPreviewPresenter @Inject constructor(private val mView: BookPreviewContract.View,
                                               private val mBook: Book) : BookPreviewContract.Presenter {
    private var isTagListInitialized = false
    private var isLanguageListInitialized = false
    private var isArtistListInitialized = false
    private var isCategoryListInitialized = false
    private var isCharacterListInitialized = false
    private var isParodyListInitialized = false
    private var isGroupListInitialized = false

    private lateinit var mTagList: LinkedList<Tag>
    private lateinit var mArtistList: LinkedList<Tag>
    private lateinit var mCategoryList: LinkedList<Tag>
    private lateinit var mLanguageList: LinkedList<Tag>
    private lateinit var mParodyList: LinkedList<Tag>
    private lateinit var mCharacterList: LinkedList<Tag>
    private lateinit var mGroupList: LinkedList<Tag>

    init {
        mView.setPresenter(this)
    }

    override fun start() {
        mView.showBookCoverImage(ApiConstants.getBookCover(mBook.mediaId))
        mView.show1stTitle(mBook.title.englishName)
        mView.show2ndTitle(mBook.title.japaneseName)
        mTagList = LinkedList()
        mCategoryList = LinkedList()
        mArtistList = LinkedList()
        mCharacterList = LinkedList()
        mLanguageList = LinkedList()
        mParodyList = LinkedList()
        mGroupList = LinkedList()
    }

    override fun loadInfoLists() {
        for (tag in mBook.tags) {
            when (tag.type) {
                Constants.TAG -> mTagList.add(tag)
                Constants.CATEGORY -> mCategoryList.add(tag)
                Constants.CHARACTER -> mCharacterList.add(tag)
                Constants.ARTIST -> mArtistList.add(tag)
                Constants.LANGUAGE -> mLanguageList.add(tag)
                Constants.PARODY -> mParodyList.add(tag)
                Constants.GROUP -> mGroupList.add(tag)
            }
        }

        if (!isTagListInitialized) {
            if (mTagList.isEmpty()) {
                mView.hideTagList()
            } else {
                mView.showTagList(mTagList)
            }
            isTagListInitialized = true
        }
        if (!isArtistListInitialized) {
            if (mArtistList.isEmpty()) {
                mView.hideArtistList()
            } else {
                mView.showArtistList(mArtistList)
            }
            isArtistListInitialized = true
        }
        if (!isLanguageListInitialized) {
            if (mLanguageList.isEmpty()) {
                mView.hideLanguageList()
            } else {
                mView.showLanguageList(mLanguageList)
            }
            isLanguageListInitialized = true
        }
        if (!isCategoryListInitialized) {
            if (mCategoryList.isEmpty()) {
                mView.hideCategoryList()
            } else {
                mView.showCategoryList(mCategoryList)
            }
            isCategoryListInitialized = true
        }
        if (!isCharacterListInitialized) {
            if (mCharacterList.isEmpty()) {
                mView.hideCharacterList()
            } else {
                mView.showCharacterList(mCharacterList)
            }
            isCharacterListInitialized = true
        }
        if (!isGroupListInitialized) {
            if (mGroupList.isEmpty()) {
                mView.hideGroupList()
            } else {
                mView.showGroupList(mGroupList)
            }
            isGroupListInitialized = true
        }
        if (!isParodyListInitialized) {
            if (mParodyList.isEmpty()) {
                mView.hideParodyList()
            } else {
                mView.showParodyList(mParodyList)
            }
            isParodyListInitialized = true
        }
    }

    override fun stop() {

    }
}