package duy.phuong.handnote.Listener;

import java.util.ArrayList;

import duy.phuong.handnote.RecognitionAPI.MachineLearning.SOM;

/**
 * Created by Phuong on 23/11/2015.
 */
public interface MainListener {
    void showFragment(String name);
    void toggleMainNavigator(boolean show);
    void toggleMainBottomTabs(boolean show);
    SOM getGlobalSOM();
    ArrayList<String> getMapNames();
}
