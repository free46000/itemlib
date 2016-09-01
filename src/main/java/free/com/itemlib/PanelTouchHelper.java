package free.com.itemlib;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Interpolator;

/**
 * Created by free46000 on 2016/8/19.
 */
public class PanelTouchHelper {
    public static final float SCROLL_BASE_STEP = 0.5F;
    public static final int SCROLL_MAX_SPEED = 30;
    public static final int NONE = -1;

    private View currItemView;
    private RecyclerView.ViewHolder currViewHolder;
    private RecyclerView parentRecycler;
    private OnDragListener onDragListener;
    private DragFloatViewHelper floatViewHelper;

    private int lastParentPos = NONE;
    private int lastChildPos = NONE;

    private float initTouchX, initTouchY;
    private float lastTouchX, lastTouchY;
    private RecyclerView lastRecyclerView;

    public PanelTouchHelper(RecyclerView parentRecycler) {
        this.parentRecycler = parentRecycler;
        floatViewHelper = new DragFloatViewHelper();

    }

    /**
     * 开始拖拽
     *
     * @param viewHolder 选中的Item的ViewHolder
     * @param floatView  需要跟随手势浮动的 View
     */
    public void startDrag(RecyclerView.ViewHolder viewHolder, View floatView) {
        currItemView = viewHolder.itemView;
        this.currViewHolder = viewHolder;
        onDragListener.onDragStart();
        floatViewHelper.createView(floatView, currItemView);
    }

    /**
     * 设置拖拽回调Listener
     *
     * @param onDragListener 回调Listener
     */
    public void setOnDragListener(OnDragListener onDragListener) {
        this.onDragListener = onDragListener;
    }

    /**
     * touch事件处理
     *
     * @param event  touch的event
     * @param touchX touchX
     * @param touchY touchY
     * @return true表示消耗掉事件
     */
    public boolean onTouch(MotionEvent event, float touchX, float touchY) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            lastChildPos = NONE;
            lastParentPos = NONE;
            initTouchX = touchX;
            initTouchY = touchY;
        }

        lastTouchX = touchX;
        lastTouchY = touchY;

        if (currItemView != null) {
            floatViewHelper.updateView((int) touchX, (int) touchY);
            moveIfNecessary(touchX, touchY);
            if (lastRecyclerView != null) {
                lastRecyclerView.removeCallbacks(mScrollRunnable);
                mScrollRunnable.run();
                lastRecyclerView.invalidate();
            }

            if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                if (currItemView != null) {
                    onDragListener.onDragFinish(lastChildPos);
                    floatViewHelper.removeView();
                }


                currItemView = null;
                currViewHolder = null;
            }

            return true;
        }
        return false;
    }


    private boolean moveIfNecessary(float touchX, float touchY) {
        View view = parentRecycler.findChildViewUnder(touchX, touchY);
        int parentPos = getPositionByItemView(view);
        if (parentPos != NONE && view instanceof RecyclerView) {
            lastRecyclerView = (RecyclerView) view;
            float childX = touchX - view.getLeft();
            float childY = touchY;
            View itemView = ((RecyclerView) view).findChildViewUnder(childX, childY);

            int childPos = getPositionByItemView(itemView);
            System.out.println("find_parent_out:" + lastParentPos + "-" + parentPos + "==" + "childX:" + childX + "childY:" + childY);
            if (isSelectedRecyclerView(lastParentPos, parentPos)) {
                lastParentPos = parentPos;
                onDragListener.onRecyclerSelected((RecyclerView) view, parentPos);
            } else if (isRealChangeRecyclerView(lastParentPos, parentPos, childPos)) {
                onDragListener.onRecyclerChanged((RecyclerView) parentRecycler.getChildAt(lastParentPos),
                        (RecyclerView) view, lastChildPos, childPos);
                System.out.println("find_parent:" + lastParentPos + "-" + parentPos);
                //因为切换父控件，所以需要重置为NONE，不然上个的最后位置有可能超过当前的大小抛出错误
                lastChildPos = NONE;
                //在切换recycle view并且触摸到子recycle view的item的时候才真正去改变值
                lastParentPos = parentPos;
            }


            if (childPos != NONE) {
                if (isSelectedChildView(lastChildPos, childPos)) {
                    currItemView = itemView;
                    lastChildPos = childPos;
                    onDragListener.onItemSelected(itemView, childPos);
                }

                int targetPos = getTargetChildPos(currItemView, ((RecyclerView) view), childX, childY);
                if (isNeedMove(currItemView, lastChildPos, targetPos, childY)) {
                    onDragListener.onItemChanged((RecyclerView) view, lastChildPos, targetPos);

                    System.out.println("find:" + lastParentPos + "-" + parentPos + "======" + lastChildPos + "-" + targetPos);
                    lastChildPos = targetPos;
                }
            }
        }
        return true;
    }

    private long mDragScrollStartTimeInMs;

    /**
     * When user drags a view to the edge, we start scrolling the LayoutManager as long as View
     * is partially out of bounds.
     */
    private final Runnable mScrollRunnable = new Runnable() {


        @Override
        public void run() {
            boolean isParentScroll = scrollIfNecessary(parentRecycler, (int) lastTouchX, (int) lastTouchY);
            boolean isChildScroll = scrollIfNecessary(lastRecyclerView, (int) lastTouchX, (int) lastTouchY);
            if (currItemView != null && (isParentScroll || isChildScroll)) {
                //it might be lost during scrolling
                moveIfNecessary(lastTouchX, lastTouchY);
                lastRecyclerView.removeCallbacks(mScrollRunnable);
                ViewCompat.postOnAnimation(lastRecyclerView, this);
            }
        }
    };

    /**
     * If user drags the view to the edge, trigger a scroll if necessary.
     */
    private boolean scrollIfNecessary(RecyclerView recyclerView, int curX, int curY) {
        if (currViewHolder == null) {
            mDragScrollStartTimeInMs = Long.MIN_VALUE;
            return false;
        }
        final long now = System.currentTimeMillis();
        final long scrollDuration = mDragScrollStartTimeInMs
                == Long.MIN_VALUE ? 0 : now - mDragScrollStartTimeInMs;
        RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
        int scrollX = 0;
        int scrollY = 0;
        if (lm.canScrollHorizontally()) {
            int lrLimit = onDragListener.getLeftRightScrollLimit();
            if (curX < lrLimit) {
                int level = SCROLL_MAX_SPEED * (lrLimit - curX) / lrLimit;
                scrollX = -calcScrollDistance(level, scrollDuration);
            } else if (curX > recyclerView.getWidth() - lrLimit) {
                int level = SCROLL_MAX_SPEED * (curX - recyclerView.getWidth() + lrLimit) / lrLimit;
                scrollX = calcScrollDistance(level, scrollDuration);
            }
        }

        if (lm.canScrollVertically()) {
            int udLimit = onDragListener.getUpDownScrollLimit();
            if (curY < udLimit) {
                int level = SCROLL_MAX_SPEED * (udLimit - curY) / udLimit;
                scrollY = -calcScrollDistance(level, scrollDuration);
            } else if (curY > recyclerView.getHeight() - udLimit) {
                int level = SCROLL_MAX_SPEED * (curY - recyclerView.getHeight() + udLimit) / udLimit;
                scrollY = calcScrollDistance(level, scrollDuration);
            }
        }

        System.out.println("scroll:::::" + scrollY + "=" + recyclerView.getScrollY() + "curY::" + curY);
        System.out.println("scroll:::::" + scrollX + "=" + recyclerView.getScrollX() + "curX::" + curX);
        if (scrollX != 0 || scrollY != 0) {
            if (mDragScrollStartTimeInMs == Long.MIN_VALUE) {
                mDragScrollStartTimeInMs = now;
            }
            recyclerView.scrollBy(scrollX, scrollY);
            return true;
        }
        mDragScrollStartTimeInMs = Long.MIN_VALUE;
        return false;
    }

    private int calcScrollDistance(int touchLevel, long scrollDuration) {
        return (int) (touchLevel * SCROLL_BASE_STEP + (scrollDuration / 200));

    }

    /**
     * Called by the ItemTouchHelper when user is dragging a view out of bounds.
     * <p/>
     * You can override this method to decide how much RecyclerView should scroll in response
     * to this action. Default implementation calculates a value based on the amount of View
     * out of bounds and the time it spent there. The longer user keeps the View out of bounds,
     * the faster the list will scroll. Similarly, the larger portion of the View is out of
     * bounds, the faster the RecyclerView will scroll.
     *
     * @param recyclerView        The RecyclerView instance to which ItemTouchHelper is attached
     *                            to.
     * @param viewSize            The total size of the View in scroll direction, excluding
     *                            item decorations.
     * @param viewSizeOutOfBounds The total size of the View that is out of bounds. This value
     *                            is negative if the View is dragged towards left or top edge.
     * @param totalSize           The total size of RecyclerView in the scroll direction.
     * @param msSinceStartScroll  The time passed since View is kept out of bounds.
     * @return The amount that RecyclerView should scroll. Keep in mind that this value will
     * be passed to {@link RecyclerView#scrollBy(int, int)} method.
     */
    public int interpolateOutOfBoundsScroll(RecyclerView recyclerView,
                                            int viewSize, int viewSizeOutOfBounds,
                                            int totalSize, long msSinceStartScroll) {
//        final int maxScroll = getMaxDragScroll(recyclerView);
        final int maxScroll = 50;//应该是20dp
        final int absOutOfBounds = Math.abs(viewSizeOutOfBounds);
        final int direction = (int) Math.signum(viewSizeOutOfBounds);
        // might be negative if other direction
        float outOfBoundsRatio = Math.min(1f, 1f * absOutOfBounds / viewSize);
        final int cappedScroll = (int) (direction * maxScroll *
                sDragViewScrollCapInterpolator.getInterpolation(outOfBoundsRatio));
        final float timeRatio;
        if (msSinceStartScroll > DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS) {
            timeRatio = 1f;
        } else {
            timeRatio = (float) msSinceStartScroll / DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS;
        }
        final int value = (int) (cappedScroll * sDragScrollInterpolator
                .getInterpolation(timeRatio));
        if (value == 0) {
            return viewSizeOutOfBounds > 0 ? 1 : -1;
        }
        return value;
    }

    private static final Interpolator sDragScrollInterpolator = new Interpolator() {
        public float getInterpolation(float t) {
            return t * t * t * t * t;
        }
    };
    /**
     * Drag scroll speed keeps accelerating until this many milliseconds before being capped.
     */
    private static final long DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS = 2000;
    private static final Interpolator sDragViewScrollCapInterpolator = new Interpolator() {
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };

    /**
     * 获取需要move的目标view，即toPosition
     *
     * @return NONE为找不到
     */
    private int getTargetChildPos(View selectedView, RecyclerView recyclerView, float touchX,
                                  float touchY) {

        View itemView = recyclerView.findChildViewUnder(touchX, touchY);
        int childPos = getPositionByItemView(itemView);
        if (childPos != NONE && childPos != lastChildPos && isCurrPosition(touchY, itemView)) {
            return childPos;
        }
        float bottomY = touchY + selectedView.getHeight();
        itemView = recyclerView.findChildViewUnder(touchX, bottomY);
        childPos = getPositionByItemView(itemView);
        if (childPos != NONE && childPos != lastChildPos && isCurrPosition(touchY, itemView)) {
            return childPos;
        }
        return NONE;
    }


    /**
     * 两个Item是否需要move
     */
    private boolean isNeedMove(View selectedView, int lastChildPos, int targetPos, float touchY) {
        if (selectedView == null || lastChildPos == NONE || targetPos == NONE) {
            return false;
        }
        System.out.println("isNeedRemove-top:" + selectedView.getTop() + "======height:" + selectedView.getHeight() + "======touchY:" + touchY);
        int top = selectedView.getTop() < 0 ? 0 : selectedView.getTop();
        return Math.abs(touchY - top) > (selectedView.getHeight() / 2);
    }

    /**
     * touch的位置是否为当前view，防止两个item切换时的抖动问题
     */
    private boolean isCurrPosition(float childY, View itemView) {
        if (childY > itemView.getTop() && childY < itemView.getBottom()) {
            return true;
        }
        System.out.println("isCurrPosition:" + (childY > itemView.getTop() && childY < itemView.getBottom()));

        return false;
    }


    /**
     * 查找当前view在RecyclerView中的位置 没有返回NONE
     */
    private int getPositionByItemView(View itemView) {
        if (itemView == null) {
            return NONE;
        }
        try {
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            System.out.println("paramsgetViewAdapterPosition:" + params.getViewAdapterPosition()
                    + "==getViewLayoutPosition:" + params.getViewLayoutPosition());
            return ((RecyclerView.LayoutParams) itemView.getLayoutParams()).getViewAdapterPosition();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return NONE;
    }

    /**
     * 是否真正切换了RecyclerView
     * 需要注意这里把没有切换后没有touch到Item当成不是真正切换
     */
    private boolean isRealChangeRecyclerView(int lastParentPos, int currParentPos,
                                             int currChildPos) {
        return lastParentPos != currParentPos && currChildPos != NONE && lastParentPos != NONE && currParentPos != NONE;
    }

    /**
     * 是否为初次选中RecycleView
     */
    private boolean isSelectedRecyclerView(int lastParentPos, int currParentPos) {
        return lastParentPos == NONE && currParentPos != NONE;
    }

    /**
     * 是否为第一次选中子ItemView
     */
    private boolean isSelectedChildView(int lastChildPos, int currChildPos) {
        return lastChildPos == NONE && currChildPos != NONE;
    }

    class DragFloatViewHelper {
        private View currTouchedView;
        private WindowManager wManager;
        private WindowManager.LayoutParams mParams;

        /**
         * 创建浮层view
         *
         * @param floatView 浮层view
         * @param coverView 被覆盖的view，用于计算浮层位置
         */
        public void createView(View floatView, View coverView) {
            currTouchedView = floatView;
            wManager = (WindowManager) floatView.getContext().getSystemService(
                    Context.WINDOW_SERVICE);
            mParams = new WindowManager.LayoutParams();
//                    mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;// 系统提示window
            mParams.format = PixelFormat.TRANSLUCENT;// 支持透明
            //mParams.format = PixelFormat.RGBA_8888;
            mParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// 焦点
            mParams.gravity = Gravity.START | Gravity.TOP;
            mParams.width = coverView.getWidth();//窗口的宽和高
            mParams.height = coverView.getHeight();
            mParams.x = (int) coverView.getX();//窗口位置的偏移量
            mParams.y = (int) coverView.getY();
            mParams.alpha = 0.6f;
            wManager.addView(floatView, mParams);
        }

        /**
         * 更新浮层View
         *
         * @param x X
         * @param y Y
         */
        public void updateView(int x, int y) {
            if (currTouchedView != null) {
                mParams.x = x;
                mParams.y = y;
                wManager.updateViewLayout(currTouchedView, mParams);
            }
        }

        /**
         * 移除浮层view
         */
        public void removeView() {
            if (currTouchedView != null && wManager != null) {
                wManager.removeView(currTouchedView);
            }
        }

    }


    public static abstract class OnDragListener {
        private int upDownLimit = 200;
        private int leftRightLimit = 200;

        public int getLeftRightScrollLimit() {
            return leftRightLimit;
        }

        public int getUpDownScrollLimit() {
            return upDownLimit;
        }

        /**
         * 第一次被选中的时候回调
         *
         * @param selectedView 选中的RecyclerView
         * @param selectedPos  选中的位置
         */
        public abstract void onRecyclerSelected(RecyclerView selectedView, int selectedPos);

        /**
         * 触摸时切换RecyclerView的时候回调
         *
         * @param fromView    上个RecyclerView
         * @param toView      当前RecyclerView
         * @param itemFromPos 上个item选中的位置
         * @param itemToPos   当前item选中的位置
         */
        public abstract void onRecyclerChanged(RecyclerView fromView, RecyclerView toView, int itemFromPos, int itemToPos);

        /**
         * 第一次被选中的时候回调
         *
         * @param selectedView 选中的RecyclerView
         * @param selectedPos  选中的位置
         */
        public abstract void onItemSelected(View selectedView, int selectedPos);

        /**
         * 触摸时切换Item的时候回调
         *
         * @param recyclerView 包含Item的RecyclerView
         * @param fromPos      上个选中的位置
         * @param toPos        当前选中的位置
         */
        public abstract void onItemChanged(RecyclerView recyclerView, int fromPos, int toPos);

        /**
         * 拖拽结束时回调
         */
        public abstract void onDragFinish(int itemPos);


        /**
         * 拖拽开始时回调
         */
        public abstract void onDragStart();
    }


}
