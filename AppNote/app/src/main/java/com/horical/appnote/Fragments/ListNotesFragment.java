package com.horical.appnote.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.horical.appnote.DTO.NoteDTO.NoteData;
import com.horical.appnote.DTO.NoteDTO.NoteImage;
import com.horical.appnote.DTO.NoteDTO.NoteVideoClip;
import com.horical.appnote.DTO.NoteDTO.NoteVoice;
import com.horical.appnote.LocalStorage.DAO.NoteDataDAO;
import com.horical.appnote.LocalStorage.DataConstant;
import com.horical.appnote.Interfaces.NoteDataLine;
import com.horical.appnote.MainActivity;
import com.horical.appnote.MyView.MyAdapter.ListNoteAdapter;
import com.horical.appnote.R;
import com.horical.appnote.Supports.CalendarUtils;
import com.horical.appnote.Supports.FileUtils;
import com.horical.appnote.Supports.LanguageUtils;

import org.w3c.dom.Text;

/**
 * Created by Phuong on 24/07/2015.
 */
public class ListNotesFragment extends BaseFragment implements ImageButton.OnClickListener, AdapterView.OnItemClickListener {

    private ArrayList<NoteData> mListNoteData, mSource;
    private ListNoteAdapter mListNoteAdapter;
    private ListView mListNote;
    private ImageButton mBtnCreateNote;
    private TextView mTvEmptyView;

    private ArrayList<String> mNotesContextMenu;
    private NoteDataDAO mNoteDataDAO;

    private FrameLayout mLayoutBanner;
    private ScrollView mScrollParent;

    private LinearLayout mLayoutSearch, mLayoutSearchHolder;
    private EditText mEdtSearchInput;

    private TextView mTvBannerMorning, mTvBannerMorningWork, mTvBannerMidday, mTvBannerAfternoon, mTvBannerEvening, mTvBannerNight
            , mTvBannerWeekend, mTvBannerDayOff;

    private boolean mShowSearchBar;

    private ArrayList<Integer> mListTips;

    public ListNotesFragment() {
        this.mLayout_xml_id = R.layout.fragment_list_notes;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNotesContextMenu = new ArrayList<>();
        for (String string : LanguageUtils.getListNoteMenu()) {
            mNotesContextMenu.add(string);
        }
        mListTips = new ArrayList<>();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTvBannerMorning = (TextView) mFragmentView.findViewById(R.id.tvBannerMorning);
        mTvBannerMorningWork = (TextView) mFragmentView.findViewById(R.id.tvBannerMorningWork);
        mTvBannerMidday = (TextView) mFragmentView.findViewById(R.id.tvBannerMidday);
        mTvBannerAfternoon = (TextView) mFragmentView.findViewById(R.id.tvBannerAfternoon);
        mTvBannerEvening = (TextView) mFragmentView.findViewById(R.id.tvBannerEvening);
        mTvBannerNight = (TextView) mFragmentView.findViewById(R.id.tvBannerNight);
        mTvBannerDayOff = (TextView) mFragmentView.findViewById(R.id.tvBannerDayOff);
        mTvBannerWeekend = (TextView) mFragmentView.findViewById(R.id.tvBannerWeekend);
        if (mListTips.isEmpty()) {
            mListTips.add(R.id.bannerNight);
            mListTips.add(R.id.bannerMorning);
            mListTips.add(R.id.bannerMorningWork);
            mListTips.add(R.id.bannerMidday);
            mListTips.add(R.id.bannerAfternoon);
            mListTips.add(R.id.bannerEvening);
            mListTips.add(R.id.bannerDayOff);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        mTvBannerMorning.setText(LanguageUtils.getBannerMorningString());
        mTvBannerMorningWork.setText(LanguageUtils.getBannerMorningWorkString());
        mTvBannerMidday.setText(LanguageUtils.getBannerMiddayString());
        mTvBannerAfternoon.setText(LanguageUtils.getBannerAfternoonString());
        mTvBannerEvening.setText(LanguageUtils.getBannerEveningString());
        mTvBannerNight.setText(LanguageUtils.getBannerNightString());
        mTvBannerDayOff.setText(LanguageUtils.getDayOffString());
        mTvBannerWeekend.setText(CalendarUtils.checkWeekend());

        mNoteDataDAO = new NoteDataDAO(mActivity);

        this.mBtnCreateNote = (ImageButton) mFragmentView.findViewById(R.id.buttonCreateNewNote);
        this.mBtnCreateNote.setOnClickListener(this);

        mLayoutSearch = (LinearLayout) mFragmentView.findViewById(R.id.SearchBar);
        mLayoutSearchHolder = (LinearLayout) mFragmentView.findViewById(R.id.layoutSearchHolder);
        mEdtSearchInput = (EditText) mFragmentView.findViewById(R.id.edtSearchNotes);
        mEdtSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchNotes(editable.toString());
            }
        });

        this.mTvEmptyView = (TextView) mFragmentView.findViewById(R.id.tvEmptyList);

        this.mListNote = (ListView) mFragmentView.findViewById(R.id.ListNotes);
        this.mListNote.setEmptyView(this.getEmptyListView());
        this.mListNote.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });
        this.reLoadListNote();
        registerForContextMenu(mListNote);

        mLayoutBanner = (FrameLayout) mFragmentView.findViewById(R.id.layoutBanner);
        mLayoutBanner.requestLayout();
        mScrollParent = (ScrollView) mFragmentView.findViewById(R.id.scrollParent);
        mScrollParent.smoothScrollTo(0, 0);

        int banner_id = CalendarUtils.checkTimeStamp();
        for (int i = 0; i < mListTips.size(); i++) {
            if (i == banner_id) {
                ((LinearLayout) mFragmentView.findViewById(mListTips.get(i))).setVisibility(View.VISIBLE);
            } else {
                ((LinearLayout) mFragmentView.findViewById(mListTips.get(i))).setVisibility(View.GONE);
            }
        }
    }

    private void reloadResource() {

    }

    @Override
    public void updateUI() {
        if (mListNoteData.isEmpty()) {
            mListNote.setVisibility(View.GONE);
            mTvEmptyView.setVisibility(View.VISIBLE);
        } else {
            mListNote.setVisibility(View.VISIBLE);
            mTvEmptyView.setVisibility(View.GONE);
        }

        if (mShowSearchBar) {
            mBtnCreateNote.setVisibility(View.GONE);
            mLayoutSearch.setVisibility(View.VISIBLE);
            mLayoutSearchHolder.setVisibility(View.VISIBLE);
            mEdtSearchInput.requestFocus();
        } else {
            mBtnCreateNote.setVisibility(View.VISIBLE);
            mLayoutSearch.setVisibility(View.GONE);
            mLayoutSearchHolder.setVisibility(View.GONE);
        }
    }

    public void reLoadListNote(){
        if (mListNoteData == null) {
            mListNoteData = new ArrayList<NoteData>();
        }
        if (mSource == null) {
            mSource = new ArrayList<NoteData>();
        }
        mSource.clear();
        mListNoteData.clear();
        mSource.addAll(mMainInterface.getAllNotes());
        mListNoteData.addAll(mMainInterface.getAllNotes());
        mListNoteAdapter = new ListNoteAdapter(mActivity, 0, mListNoteData);
        mListNote.setAdapter(mListNoteAdapter);
        this.mListNote.setOnItemClickListener(this);
        mListNoteAdapter.notifyDataSetChanged();
        setListViewHeight(this.mListNote);
        this.updateUI();
    }

    public void searchNotes(String infor) {
        mListNoteData.clear();
        for (NoteData data : mSource) {
            if (data.toString().contains(infor) && data.getNoteData() != null) {
                mListNoteData.add(data);
            }
        }
        setListViewHeight(this.mListNote);
        mListNoteAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(LanguageUtils.getSelectActionString());
        for (String string : mNotesContextMenu) {
            menu.add(Menu.NONE, v.getId(), Menu.NONE, string);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        NoteData noteData = mListNoteData.get(adapterContextMenuInfo.position);
        if (item.getTitle().toString().equals(LanguageUtils.getViewString())) {
            mMainInterface.PassNoteSummary(noteData.getNoteSummary());
        }
        if (item.getTitle().toString().equals(LanguageUtils.getUpdateString())) {
            mMainInterface.PassEditInfor(noteData);
        }
        if (item.getTitle().toString().equals(LanguageUtils.getDeleteString())) {
            String message = "";
            if (mNoteDataDAO.deleteNote(noteData.getNoteSummary().getID())){
                ArrayList<String> filePaths = new ArrayList<String>();
                for (NoteDataLine noteLine : noteData.getNoteData()) {
                    switch (noteLine.typeIdentify()) {
                        case DataConstant.TYPE_IMAGE:
                            filePaths.add(((NoteImage) noteLine).getFilePath());
                            break;
                        case DataConstant.TYPE_VIDEOCLIP:
                            filePaths.add(((NoteVideoClip) noteLine).getFilePath());
                            break;
                        case DataConstant.TYPE_VOICE:
                            filePaths.add(((NoteVoice) noteLine).getFilePath());
                            break;
                    }
                }
                if (filePaths.isEmpty()) {
                    message = LanguageUtils.getDeleteString() + " " + LanguageUtils.getNotifyCompletedString().toLowerCase();
                } else {
                    if (FileUtils.deleteFiles(filePaths)) {
                        message = LanguageUtils.getDeleteString() + " " + LanguageUtils.getNotifyCompletedString().toLowerCase();
                    } else {
                        message = LanguageUtils.getDeleteFileFailedString();
                    }
                }
            } else {
                message = LanguageUtils.getDeleteString() + " " + LanguageUtils.getNotifyFailedString().toLowerCase();
            }
            Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();

            mMainInterface.reloadListFile(DataConstant.TYPE_IMAGE);
            mMainInterface.reloadListFile(DataConstant.TYPE_VIDEOCLIP);
            mMainInterface.reloadListFile(DataConstant.TYPE_VOICE);

            mMainInterface.reloadListNote();
            this.reLoadListNote();
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonCreateNewNote:
                this.mMainInterface.ChangeFragment(BaseFragment.ListNotesFragment, BaseFragment.NewNoteFragment);
                return;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ((MainActivity) mActivity).PassNoteSummary(mListNoteData.get(i).getNoteSummary());
    }

    private View getEmptyListView(){
        View view = mActivity.getLayoutInflater().inflate(R.layout.list_empty, null);
        ((TextView) view.findViewById(R.id.tvEmptyList)).setText(LanguageUtils.getNothingListNoteString());
        return view;
    }

    public static void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        int desiredWidth, resultHeight = 0;
        ViewGroup.LayoutParams params;
        View view = null;
        if (listAdapter == null) {
            return;
        }
        desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) {
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            resultHeight += view.getMeasuredHeight();
        }
        params = listView.getLayoutParams();
        params.height = resultHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public void toggleSearchBar() {
        mShowSearchBar = !mShowSearchBar;
        if (!mShowSearchBar) {
            this.reLoadListNote();
            this.hideSoftKeyboard();
            mScrollParent.smoothScrollTo(0, 0);
        }
        this.updateUI();
    }
    private void hideSoftKeyboard() {
        View view = mActivity.getCurrentFocus();
        InputMethodManager input = (InputMethodManager) this.mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        input.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
