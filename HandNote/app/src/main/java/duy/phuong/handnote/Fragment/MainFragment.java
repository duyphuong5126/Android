package duy.phuong.handnote.Fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import duy.phuong.handnote.DAO.LocalStorage;
import duy.phuong.handnote.DTO.Note;
import duy.phuong.handnote.MyView.NotesAdapter;
import duy.phuong.handnote.R;
import duy.phuong.handnote.Support.SupportUtils;

/**
 * Created by Phuong on 26/01/2016.
 */
public class MainFragment extends BaseFragment implements NotesAdapter.AdapterListener {
    private ScrollView mMainScroll;
    private ListView mListNotes;
    private ArrayList<Note> mNotes;
    private LocalStorage mLocalStorage;
    private NotesAdapter mAdapter;
    private TextView mMainTextView;
    private ShowNoteListener mShowNoteListener;

    public interface ShowNoteListener {
        void showNote(Note note);
    }

    public void setShowNoteListener(ShowNoteListener ShowNoteListener) {
        this.mShowNoteListener = ShowNoteListener;
    }

    public MainFragment() {
        this.mLayoutRes = R.layout.fragment_main;
        mNotes = new ArrayList<>();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMainTextView = (TextView) mFragmentView.findViewById(R.id.mainTextView);
        mMainScroll = (ScrollView) mFragmentView.findViewById(R.id.mainScroll);
        mListNotes = (ListView) mFragmentView.findViewById(R.id.listNotes);
    }

    @Override
    public void onStart() {
        super.onStart();
        mLocalStorage = new LocalStorage(mActivity);
        mNotes = new ArrayList<>();
        mNotes.addAll(mLocalStorage.getListNote());
        mAdapter = new NotesAdapter(mNotes, mActivity, R.layout.item_note);
        mAdapter.setListener(this);
        mListNotes.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mListNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mShowNoteListener.showNote(mNotes.get(position));
            }
        });
        setListViewHeight(mListNotes);
        checkEmptyList();
    }

    private void checkEmptyList() {
        if (mNotes.isEmpty()) {
            mMainTextView.setVisibility(View.VISIBLE);
        } else {
            mMainTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public String fragmentIdentify() {
        return BaseFragment.MAIN_FRAGMENT;
    }

    @Override
    public void deleteNote(Note note) {
        mLocalStorage.deleteNote(note);
        SupportUtils.deleteFile(note.mBitmapPath);
        SupportUtils.deleteFile(note.mContentPath);
        Toast.makeText(mActivity, "Delete done", Toast.LENGTH_SHORT).show();
        checkEmptyList();
    }

    @Override
    public void showNote(Note note) {
        mShowNoteListener.showNote(note);
    }
}
