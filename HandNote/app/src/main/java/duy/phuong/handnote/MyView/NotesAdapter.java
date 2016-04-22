package duy.phuong.handnote.MyView;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import duy.phuong.handnote.DTO.Note;
import duy.phuong.handnote.R;

/**
 * Created by Phuong on 16/04/2016.
 */
public class NotesAdapter extends BaseAdapter {
    private Activity mActivity;
    private List<Note> mNotes;
    private int mLayout;
    private AdapterListener mListener;
    public interface AdapterListener {
        void deleteNote(Note note);
        void showNote(Note note);
    }

    public void setListener(AdapterListener Listener) {
        this.mListener = Listener;
    }

    public NotesAdapter(List<Note> notes, Activity activity, int layout) {
        super();
        mActivity = activity;
        mNotes = new ArrayList<>();
        mNotes.addAll(notes);
        mLayout = layout;
    }

    @Override
    public int getCount() {
        return mNotes.size();
    }

    @Override
    public Object getItem(int position) {
        return mNotes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Note note = mNotes.get(position);
        convertView = mActivity.getLayoutInflater().inflate(mLayout, null);
        ((ImageView) convertView.findViewById(R.id.imageView)).setImageBitmap(note.mImage);
        ((TextView) convertView.findViewById(R.id.textView)).setText(note.mContent);
        ((ImageButton) convertView.findViewById(R.id.buttonDelete)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Note note = mNotes.remove(position);
                notifyDataSetChanged();
                mListener.deleteNote(note);
            }
        });
        ((ImageButton) convertView.findViewById(R.id.buttonShow)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Note note = mNotes.get(position);
                mListener.showNote(note);
            }
        });
        return convertView;
    }
}
