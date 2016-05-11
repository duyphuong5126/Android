package duy.phuong.handnote.Support;

import android.content.Context;
import android.content.res.Resources;

import duy.phuong.handnote.Fragment.BaseFragment;
import duy.phuong.handnote.R;

/**
 * Created by Phuong on 13/03/2016.
 */
public abstract class LanguageUtils {
    public static String getAppTitle(Context context) {
        return context.getResources().getString(R.string.app_name);
    }

    public static String getFragmentTitle(String fragmentName, Context context) {
        Resources resources = context.getResources();
        switch (fragmentName) {
            case BaseFragment.DRAWING_FRAGMENT:
                return resources.getString(R.string.drawing_fragment_en);
            case BaseFragment.LEARNING_FRAGMENT:
                return resources.getString(R.string.learning_fragment_en);
            case BaseFragment.CREATE_NOTE_FRAGMENT:
                return resources.getString(R.string.create_fragment_en);
            case BaseFragment.TEMPLATES_FRAGMENT:
                return resources.getString(R.string.templates_fragment_en);
            default:
                return resources.getString(R.string.app_name);
        }
    }
}
