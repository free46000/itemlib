package free.com.itemlib;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * view 的拖拽功能
 * Created by zhangsiqi on 2016/8/16.
 */
public class DragHelper {

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowParams;
    private ImageView mDragImageView;
    private RecyclerView mCurrentVerticalView;
    private PagerRecyclerView mHorizontalRecyclerView;

    private boolean isDragging = false;
    private float mBornLocationX, mBornLocationY;//抓起时 view 的坐标
    private int offsetX, offsetY;//抓起时 view 坐标和点击点的距离
    private boolean confirmOffset = false;//是否确定了 offset

    private Timer mHorizontalScrollTimer = new Timer();
    private TimerTask mHorizontalScrollTimerTask;
    private static final int HORIZONTAL_STEP = 40;// 横向滑动步伐.
    private static final int HORIZONTAL_SCROLL_PERIOD = 30;
    private int leftScrollBounce;// 拖动的时候，开始向左滚动的边界
    private int rightScrollBounce;// 拖动的时候，开始向右滚动的边界

    private Timer mVerticalScrollTimer = new Timer();
    private TimerTask mVerticalScrollTimerTask;
    private static final int VERTICAL_STEP = 10;// 纵向滑动步伐.
    private static final int VERTICAL_SCROLL_PERIOD = 20;
    private int upScrollBounce;// 拖动的时候，开始向上滚动的边界
    private int downScrollBounce;// 拖动的时候，开始向下滚动的边界


    public DragHelper(Activity activity) {
        mWindowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        mWindowParams.alpha = 1.0f;
        mWindowParams.format = PixelFormat.TRANSLUCENT;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.gravity = Gravity.TOP | Gravity.LEFT;
        mWindowParams.x = 0;
        mWindowParams.y = 0;

        mDragImageView = new ImageView(activity);
        mDragImageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mDragImageView.setOnTouchListener(new View.OnTouchListener() {// 如果没执行 move 丢失了点击事件，碰一下屏幕 window 消失
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE || event.getAction() == MotionEvent.ACTION_DOWN) {
                    drop();
                }
                return false;
            }
        });
    }


    public void bindRecyclerView(@NonNull RecyclerView view) {
        mCurrentVerticalView = view;
        RecyclerView.LayoutManager layoutManager = view.getLayoutManager();
        if (!(layoutManager instanceof LinearLayoutManager))
            throw new RuntimeException("LayoutManager must be LinearLayoutManager");
    }

    public void bindHorizontalRecyclerView(@NonNull PagerRecyclerView view) {
        mHorizontalRecyclerView = view;
        RecyclerView.LayoutManager layoutManager = view.getLayoutManager();
        if (!(layoutManager instanceof LinearLayoutManager))
            throw new RuntimeException("LayoutManager must be LinearLayoutManager");
    }

    public boolean isDragging() {
        return isDragging;
    }

    public void drag(View drager, int position) {
        isDragging = true;
        getTargetHorizontalRecyclerViewScrollBoundaries();
        getTargetVerticalRecyclerViewScrollBoundaries();
        drager.destroyDrawingCache();
        drager.setDrawingCacheEnabled(true);
        Bitmap bitmap = drager.getDrawingCache();
        if (bitmap != null) {
            mDragImageView.setImageBitmap(bitmap);
            mDragImageView.setRotation(1.5f);
            int[] location = new int[2];
            drager.getLocationOnScreen(location);
            mWindowParams.x = location[0];
            mWindowParams.y = location[1];
            mBornLocationX = location[0];
            mBornLocationY = location[1];
            confirmOffset = false;
            mWindowManager.addView(mDragImageView, mWindowParams);
        }
    }

    public void drop() {
        if (isDragging) {
            mWindowManager.removeView(mDragImageView);
            isDragging = false;
            if (mVerticalScrollTimerTask != null)
                mVerticalScrollTimerTask.cancel();

            if (mHorizontalScrollTimerTask != null)
                mHorizontalScrollTimerTask.cancel();

            if (mHorizontalRecyclerView != null)
                mHorizontalRecyclerView.backToCurrentPage();
        }
    }

    public void updateDragViewPosition(float x, float y) {
        if (mWindowManager == null || mWindowParams == null)
            return;
        if (!confirmOffset) {
            calculateOffset(x, y);
        }
        if (isDragging) {
            mWindowParams.x = (int) (x - offsetX);
            mWindowParams.y = (int) (y - offsetY);
            mWindowManager.updateViewLayout(mDragImageView, mWindowParams);
            updateSelectedVerticalRecyclerView();
            recyclerViewScrollHorizontal((int) x);
            recyclerViewScrollVertical((int) y);
        }
    }

    private void calculateOffset(float x, float y) {
        offsetX = (int) Math.abs(x - mBornLocationX);
        offsetY = (int) Math.abs(y - mBornLocationY);
        confirmOffset = true;
    }

    private void getTargetVerticalRecyclerViewScrollBoundaries() {
        int[] location = new int[2];
        mCurrentVerticalView.getLocationOnScreen(location);
        upScrollBounce = location[1] + 150;
        downScrollBounce = location[1] + mCurrentVerticalView.getHeight() - 150;
    }

    private void getTargetHorizontalRecyclerViewScrollBoundaries() {
        leftScrollBounce = 300;
        rightScrollBounce = 1000;
    }

    private void recyclerViewScrollHorizontal(final int x) {
        if (mHorizontalScrollTimerTask != null)
            mHorizontalScrollTimerTask.cancel();

        if (x > rightScrollBounce) {
            mHorizontalScrollTimerTask = new TimerTask() {
                @Override
                public void run() {
                    mHorizontalRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            mHorizontalRecyclerView.scrollBy(HORIZONTAL_STEP, 0);
                        }
                    });
                }
            };
            mHorizontalScrollTimer.schedule(mHorizontalScrollTimerTask, 0, HORIZONTAL_SCROLL_PERIOD);
        } else if (x < leftScrollBounce) {
            mHorizontalScrollTimerTask = new TimerTask() {
                @Override
                public void run() {
                    mHorizontalRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            mHorizontalRecyclerView.scrollBy(-HORIZONTAL_STEP, 0);
                        }
                    });
                }
            };
            mHorizontalScrollTimer.schedule(mHorizontalScrollTimerTask, 0, HORIZONTAL_SCROLL_PERIOD);
        }
    }

    private void recyclerViewScrollVertical(final int y) {
        if (mVerticalScrollTimerTask != null)
            mVerticalScrollTimerTask.cancel();

        if (y > downScrollBounce) {
            mVerticalScrollTimerTask = new TimerTask() {
                @Override
                public void run() {
                    mCurrentVerticalView.post(new Runnable() {
                        @Override
                        public void run() {
                            mCurrentVerticalView.scrollBy(0, VERTICAL_STEP);
                        }
                    });
                }
            };
            mVerticalScrollTimer.schedule(mVerticalScrollTimerTask, 0, VERTICAL_SCROLL_PERIOD);
        } else if (y < upScrollBounce) {
            mVerticalScrollTimerTask = new TimerTask() {
                @Override
                public void run() {
                    mCurrentVerticalView.post(new Runnable() {
                        @Override
                        public void run() {
                            mCurrentVerticalView.scrollBy(0, -VERTICAL_STEP);
                        }
                    });
                }
            };
            mVerticalScrollTimer.schedule(mVerticalScrollTimerTask, 0, VERTICAL_SCROLL_PERIOD);
        }
    }

    private int mPagerPosition;
    private void updateSelectedVerticalRecyclerView() {
//        int newPosition = mHorizontalRecyclerView.getCurrentPosition();
//        if (mPagerPosition != newPosition) {
//            RecyclerViewListsAdapter.ViewHolder holder = (RecyclerViewListsAdapter.ViewHolder) mHorizontalRecyclerView.findViewHolderForAdapterPosition(newPosition);
//            mCurrentVerticalView = (RecyclerView) holder.itemView.findViewById(R.id.task_list_rv);
//            mPagerPosition = newPosition;
//        }
    }

}
