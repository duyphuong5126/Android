package duy.phuong.handnote.Listener;

import java.util.ArrayList;

import duy.phuong.handnote.DTO.ClusterLabel;
import duy.phuong.handnote.DTO.Note;
import duy.phuong.handnote.Recognizer.MachineLearning.SOM;

/**
 * Created by Phuong on 23/11/2015.
 */
public interface MainListener {
    void showFragment(String name);
    void toggleMainNavigator(boolean show);
    void toggleMainBottomTabs(boolean show);
    SOM getGlobalSOM();
    ArrayList<ClusterLabel> getMapNames();
    Note getCurrentNote();
    void initSOM();
    void screenOrientation(String fragmentName);
}
