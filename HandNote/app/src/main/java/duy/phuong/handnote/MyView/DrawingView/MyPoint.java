package duy.phuong.handnote.MyView.DrawingView;

/**
 * Created by Phuong on 01/12/2015.
 */
public class MyPoint {
    public int x, y;
    public static final int MAX_DEGREE = 8;
    private boolean Inspected = false;
    private int mCurrentNeighBor;
    private int mDegree = 0;

    private MyPoint[] mListNeighbor;

    public MyPoint() {
        x = y = -1;
        Inspected = false;
        mListNeighbor = new MyPoint[MAX_DEGREE];
    }

    public boolean isInspected() {
        return Inspected;
    }

    public void setInspected(boolean inspected) {
        Inspected = inspected;
    }

    public MyPoint(int x, int y) {
        this.x = x;
        this.y = y;
        Inspected = false;
        mListNeighbor = new MyPoint[MAX_DEGREE];
    }

    public MyPoint[] getListNeighbor() {
        return mListNeighbor;
    }

    public boolean newNeighbor(MyPoint point) {
        if (this.samePoint(point)) {
            return false;
        }
        if (mDegree < MAX_DEGREE) {
            int dx = Math.abs(this.x - point.x); int dy = Math.abs(this.y - point.y);
            if (dx <= 1 && dy <= 1) {
                mListNeighbor[mCurrentNeighBor] = point;
                mCurrentNeighBor++;
                mDegree++;
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public int getDegree() {
        return mDegree;
    }

    public boolean isFull() {
        return !(getDegree() < MAX_DEGREE);
    }

    public boolean samePoint(MyPoint myPoint) {
        return (this.x == myPoint.x && this.y == myPoint.y);
    }
}
