package free.com.itemlib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by zhangsiqi on 2016/8/18.
 */
public class DragLayout extends RelativeLayout {

    private DragHelper mDragHelper;
    private PanelTouchHelper mph;

    public DragLayout(Context context) {
        super(context);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDragHelper(DragHelper dragHelper) {
        mDragHelper = dragHelper;
    }

    public void setPanelTouchHelper(PanelTouchHelper helper) {
        this.mph = helper;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mDragHelper != null) {
            if (mDragHelper.isDragging())
                return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                mDragHelper.updateDragViewPosition(event.getRawX(), event.getRawY());
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mDragHelper.drop();
                break;
        }
        return super.onTouchEvent(event);
    }
}
