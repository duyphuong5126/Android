package duy.phuong.handnote.MyView.DrawingView;

/**
 * Created by Phuong on 05/12/2015.
 */
public class MyEdge {
    private MyPoint mStartPoint, mEndPoint;

    public MyEdge() {
        mStartPoint = new MyPoint(-1, -1);
        mEndPoint = new MyPoint(-1, -1);
    }

    public MyEdge(MyPoint StartPoint, MyPoint EndPoint) {
        this.mStartPoint = StartPoint;
        this.mEndPoint = EndPoint;
    }

    public MyPoint getEndPoint() {
        return mEndPoint;
    }

    public MyPoint getStartPoint() {
        return mStartPoint;
    }

    public boolean checkSameEdge(MyEdge myEdge) {
        return ((mStartPoint.samePoint(myEdge.getStartPoint()) && mEndPoint.samePoint(myEdge.getEndPoint())) ||
                (mStartPoint.samePoint(myEdge.getEndPoint()) && mEndPoint.samePoint(myEdge.getStartPoint()))
        );
    }
}
