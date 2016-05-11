package duy.phuong.handnote.DTO;

/**
 * Created by Phuong on 10/05/2016.
 */
public class SideMenuItem {
    public int mIcon;
    public int mIconFocused;
    public String mTitle;
    public boolean mFocused;

    public SideMenuItem(int Icon, int IconFocused, String Title, boolean State) {
        this.mIcon = Icon;
        this.mIconFocused = IconFocused;
        this.mTitle = Title;
        this.mFocused = State;
    }
}
