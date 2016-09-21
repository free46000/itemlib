package free.com.itemlib;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * Created by free46000 on 2016/8/19.
 * 面板拖动辅助类 -跨RecyclerView拖动
 * <p>
 * todo 通过#OnDragListener的切换回调boolean值处理是否可以替换的场景（有的Item是不允许被拖动的）
 */
public class PanelTouchHelper {
    private static final int SCROLL_MAX_SPEED = 30;
    private static final int NONE = -1;
    private static final int MOVE_LIMIT = 2;

    private View currItemView;
    private RecyclerView horizontalRecycler;
    private OnDragListener onDragListener;
    private DragFloatViewHelper floatViewHelper;

    private int lastParentPos = NONE;
    private int lastChildPos = NONE;

    private float lastTouchRawX, lastTouchRawY;
    private int offsetX, offsetY;
    private RecyclerView lastRecyclerView;

    public PanelTouchHelper(RecyclerView horizontalRecycler) {
        this.horizontalRecycler = horizontalRecycler;
        floatViewHelper = new DragFloatViewHelper();
    }

    /**
     * 开始拖拽
     *
     * @param viewHolder 选中的Item的ViewHolder
     * @param floatView  需要跟随手势浮动的 View
     */
    public void startDrag(RecyclerView.ViewHolder viewHolder, View floatView) {
        startDrag(viewHolder.itemView, viewHolder.getAdapterPosition(), floatView);
    }

    /**
     * 开始拖拽
     *
     * @param itemView     选中的Item的View
     * @param itemPosition 选中的Item的position
     * @param floatView    需要跟随手势浮动的 View
     */
    public void startDrag(View itemView, int itemPosition, View floatView) {
        if (!onDragListener.onItemSelected(itemView, itemPosition)) {
            return;
        }
        currItemView = itemView;
        lastChildPos = itemPosition;
        onDragListener.onDragStart();
        floatViewHelper.createView(floatView, currItemView);
        lastParentPos = NONE;
    }


    /**
     * 设置拖拽回调Listener
     *
     * @param onDragListener 回调Listener
     */
    public void setOnDragListener(OnDragListener onDragListener) {
        this.onDragListener = onDragListener;
    }

    public RecyclerView getHorizontalRecycler() {
        return horizontalRecycler;
    }

    /**
     * touch事件处理
     *
     * @param event touch的event
     * @return true表示消耗掉事件
     */
    public boolean onTouch(MotionEvent event) {
        lastTouchRawX = event.getRawX();
        lastTouchRawY = event.getRawY();

        if (currItemView != null) {
            floatViewHelper.updateView((int) lastTouchRawX, (int) lastTouchRawY);

            boolean isCanMove = moveIfNecessary(lastTouchRawX, lastTouchRawY);
            if (lastRecyclerView != null) {

                lastRecyclerView.removeCallbacks(mScrollRunnable);
                mScrollRunnable.run();
                lastRecyclerView.invalidate();
            }

            if (event.getActionMasked() == MotionEvent.ACTION_UP || event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
                stopDrag();
            }

            return true;
        }
        return false;
    }

    private void stopDrag() {
        if (currItemView != null) {
            onDragListener.onDragFinish(lastRecyclerView, lastChildPos, lastParentPos);
            floatViewHelper.removeView();
        }

        currItemView = null;
    }

    /**
     * 当用户拖动的时候，计算是否需要移动Item
     *
     * @param touchRawX float event.getRawX()
     * @param touchRawY float event.getRawY()
     */
    private boolean moveIfNecessary(float touchRawX, float touchRawY) {
        boolean result = true;

        float[] location = getRecyclerViewInsideLocation(horizontalRecycler, touchRawX, touchRawY);

        View view = horizontalRecycler.findChildViewUnder(location[0], location[1]);
        int verticalPos = getPositionByItemView(view);
        System.out.println("find_parent_out:" + lastParentPos + "-" + verticalPos + "==" + location[0] + "====" + location[1]);

        RecyclerView verticalRecycler = findRecyclerView(view);
        if (verticalPos == NONE || verticalRecycler == null) {
            return false;
        }

        location = getRecyclerViewInsideLocation(verticalRecycler, touchRawX, touchRawY);
        float childX = location[0];
        float childY = location[1];
        System.out.println("find_parent_out:" + lastParentPos + "-" + verticalPos + "==" + "childX:" + childX + "childY:" + childY);

        View itemTargetView = verticalRecycler.findChildViewUnder(childX, childY);
        int childPos = getTargetChildPos(itemTargetView, childY, lastParentPos, verticalPos);

        if (isSelectedRecyclerView(lastParentPos, verticalPos)) {
            onDragListener.onRecyclerSelected(verticalRecycler, verticalPos);
            lastParentPos = verticalPos;
            lastRecyclerView = verticalRecycler;
        } else if (isChangeRecyclerView(lastParentPos, verticalPos)) {
            if (childPos == NONE) {
                childPos = calcChildPositionOnChangedRecyclerView(verticalRecycler, childX, childY);
            }
            if (childPos != NONE) {
                boolean isChanged = onDragListener.onRecyclerChanged(lastRecyclerView, verticalRecycler,
                        lastChildPos, childPos, lastParentPos, verticalPos);
                if (!isChanged) {
                    return result;
                }
                System.out.println("find_parent:" + lastParentPos + "-" + verticalPos);
                //在切换recycle view并且触摸到子recycle view的item的时候才真正去改变值
                lastParentPos = verticalPos;
                lastRecyclerView = verticalRecycler;
                //因为切换父控件，所以需要重置为当前ChildPos，不然上个的最后位置有可能超过当前的大小抛出错误
                lastChildPos = childPos;
            }
        }

        if (childPos == NONE) {
            return result;
        }

        if (isNeedMove(itemTargetView, lastChildPos, childPos, childY)) {
            boolean isChanged = onDragListener.onItemChanged(verticalRecycler, lastChildPos, childPos, lastParentPos);
            if (!isChanged) {
                return result;
            }
            final RecyclerView.LayoutManager layoutManager = verticalRecycler.getLayoutManager();
            if (layoutManager instanceof ItemTouchHelper.ViewDropHandler) {
                ((ItemTouchHelper.ViewDropHandler) layoutManager).prepareForDrop(currItemView,
                        itemTargetView, (int) childX, (int) childY);
            }

            System.out.println("find:" + lastParentPos + "-" + verticalPos + "======" + lastChildPos + "-" + childPos);
            lastChildPos = childPos;
        }
        return result;
    }

    /**
     * 获取当前点击的位置在RecyclerView内部的坐标 Y坐标范围0+padding到height-padding
     */
    private float[] getRecyclerViewInsideLocation(RecyclerView verticalRecycler, float touchRawX, float touchRawY) {
        float[] result = new float[2];
        int[] location = new int[2];
        verticalRecycler.getLocationOnScreen(location);
        result[0] = touchRawX - location[0];
        result[1] = touchRawY - location[1];
        System.out.println("getRecyclerViewInsideLocation:" + result[0] + "-" + result[1] + "==" + "X:" + touchRawX + "Y:" + touchRawY);

        int minX = verticalRecycler.getPaddingTop();
        int maxY = verticalRecycler.getHeight() - verticalRecycler.getPaddingBottom();
        result[1] = result[1] < minX ? minX : result[1];
        result[1] = result[1] > maxY ? maxY : result[1];
        return result;
    }

    /**
     * 当在两个RecyclerView中切换时，计算目标RecyclerView中Child位置 (若目标RecyclerView为空返回0)
     */
    private int calcChildPositionOnChangedRecyclerView(RecyclerView verticalRecycler, float childX, float childY) {
        if (verticalRecycler.getAdapter().getItemCount() == 0) {
            return 0;
        }
        return NONE;
    }

    /**
     * 从view中获取需要操作的RecyclerView
     *
     * @param view View
     */
    protected RecyclerView findRecyclerView(View view) {
        if (view == null) {
            return null;
        }

        if (view instanceof RecyclerView) {
            return (RecyclerView) view;
        } else if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                if (((ViewGroup) view).getChildAt(i) instanceof RecyclerView) {
                    return (RecyclerView) ((ViewGroup) view).getChildAt(i);
                }
            }
        }
        return null;
    }


    /**
     * 滚动Runnable，为了可持续滚动
     */
    private final Runnable mScrollRunnable = new Runnable() {

        @Override
        public void run() {
            float[] horLocation = getRecyclerViewInsideLocation(horizontalRecycler, lastTouchRawX, lastTouchRawY);
            float[] verLocation = getRecyclerViewInsideLocation(lastRecyclerView, lastTouchRawX, lastTouchRawY);
            boolean isHorizontalScroll = scrollIfNecessary(horizontalRecycler, (int) horLocation[0], (int) horLocation[1]);
            boolean isVerticalScroll = scrollIfNecessary(lastRecyclerView, (int) verLocation[0], (int) verLocation[1]);
            if (currItemView != null && (isHorizontalScroll || isVerticalScroll)) {
                //it might be lost during scrolling
                moveIfNecessary(lastTouchRawX, lastTouchRawY);
                lastRecyclerView.removeCallbacks(mScrollRunnable);
                ViewCompat.postOnAnimation(lastRecyclerView, this);
            }
        }
    };

    /**
     * 当用户滚动到边缘的时候,计算是否需要滚动
     */
    private boolean scrollIfNecessary(RecyclerView recyclerView, int curX, int curY) {
        if (currItemView == null) {
            return false;
        }
        RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
        int scrollX = 0;
        int scrollY = 0;
        if (lm.canScrollHorizontally()) {
            int direct = onDragListener.calcScrollHorizontalDirect(curX, recyclerView.getWidth());
            float lrLimit = onDragListener.getHorizontalScrollLimit();
            if (direct < 0) {
                float level = (lrLimit - curX) / lrLimit;
                scrollX = -calcScrollDistance(level);
            } else if (direct > 0) {
                float level = (curX - recyclerView.getWidth() + lrLimit) / lrLimit;
                scrollX = calcScrollDistance(level);
            }
        }

        if (lm.canScrollVertically()) {
            int direct = onDragListener.calcScrollVerticalDirect(curY, recyclerView.getHeight());
            float udLimit = onDragListener.getVerticalScrollLimit();
            if (direct < 0) {
                float level = (udLimit - curY) / udLimit;
                scrollY = -calcScrollDistance(level);
            } else if (direct > 0) {
                float level = (curY - recyclerView.getHeight() + udLimit) / udLimit;
                scrollY = calcScrollDistance(level);
            }
        }

        System.out.println("scroll:::::" + scrollY + "=" + recyclerView.getScrollY() + "curY::" + curY);
        System.out.println("scroll:::::" + scrollX + "=" + recyclerView.getScrollX() + "curX::" + curX);
        if (scrollX != 0 || scrollY != 0) {
            recyclerView.scrollBy(scrollX, scrollY);
        }
        return scrollX != 0 || scrollY != 0;
    }

    private int calcScrollDistance(float touchLevel) {
        touchLevel = touchLevel > 1 ? 1f : touchLevel;
        return (int) (touchLevel * SCROLL_MAX_SPEED);
    }

    /**
     * 获取需要move的目标view，即toPosition
     *
     * @return NONE为找不到
     */
    private int getTargetChildPos(View itemTargetView, float childY, int lastHorizontalPos, int currHorizontalPos) {
        int childPos = getPositionByItemView(itemTargetView);

        if (childPos != NONE && (childPos != lastChildPos || lastHorizontalPos != currHorizontalPos)
                && isCurrPosition(childY, itemTargetView)) {
            return childPos;
        }
        return NONE;
    }


    /**
     * 两个Item是否需要move
     */
    private boolean isNeedMove(View itemTargetView, int lastChildPos, int targetPos, float touchY) {
        if (itemTargetView == null || lastChildPos == NONE || targetPos == NONE || lastChildPos == targetPos) {
            return false;
        }
        int top = itemTargetView.getTop();

        int moveLimit = top + itemTargetView.getHeight() / MOVE_LIMIT;
        System.out.println("isNeedRemove-top:" + top + "======height:" + itemTargetView.getHeight()
                + "======touchY:" + touchY + "moveLimit====" + moveLimit);

        if (lastChildPos > targetPos) {
//            return touchY < moveLimit && top >= 0;
            return touchY < moveLimit;
        } else {
            return touchY > moveLimit;
        }
    }

    /**
     * touch的位置是否为当前view，防止两个item切换时的抖动问题
     */
    private boolean isCurrPosition(float childY, View itemView) {
        System.out.println("isCurrPosition:" + (childY > itemView.getTop() && childY < itemView.getBottom()));
        return childY > itemView.getTop() && childY < itemView.getBottom();
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
    private boolean isChangeRecyclerView(int lastParentPos, int currParentPos) {
        return lastParentPos != currParentPos && lastParentPos != NONE && currParentPos != NONE;
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
            mParams = initParams(floatView);
            mParams.width = coverView.getWidth();//窗口的宽和高
            mParams.height = coverView.getHeight();
            int[] location = getLocation(coverView);
            mParams.x = location[0];//窗口位置的偏移量
            mParams.y = location[1];
            modifyFloatView();
            wManager.addView(floatView, mParams);

            offsetX = (int) (lastTouchRawX - location[0]);
            offsetY = (int) (lastTouchRawY - location[1]);
        }

        protected void modifyFloatView() {
            //mParams.alpha = 0.6f;
            currTouchedView.setScaleX(0.95f);
            currTouchedView.setScaleY(0.95f);
            currTouchedView.setRotation(0.9f);
        }

        protected int[] getLocation(View coverView) {
            int[] result = new int[2];
            coverView.getLocationOnScreen(result);

            return result;
        }


        private WindowManager.LayoutParams initParams(View floatView) {
            WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
            mParams.token = floatView.getWindowToken();
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;// 系统提示window
            mParams.format = PixelFormat.TRANSLUCENT;// 支持透明
            //mParams.format = PixelFormat.RGBA_8888;
            mParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
            mParams.gravity = Gravity.LEFT | Gravity.TOP;
            return mParams;
        }

        /**
         * 更新浮层View
         *
         * @param x X
         * @param y Y
         */
        public void updateView(int x, int y) {
            if (currTouchedView != null) {
                mParams.x = x - offsetX;
                mParams.y = y - offsetY;
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
        private int horizontalLimit = 100;
        private int verticalLimit = 200;

        public float getHorizontalScrollLimit() {
            return verticalLimit;
        }

        public float getVerticalScrollLimit() {
            return horizontalLimit;
        }


        /**
         * 计算水平滚动指向
         * 可以对touchX的值进行过滤  例:滑动超出view(touchX<0||touchX>viewWidth)
         *
         * @return -1:像左滑 0:不滑动 1:像右滑
         */
        public int calcScrollHorizontalDirect(int touchX, int viewWidth) {
            float lrLimit = getHorizontalScrollLimit();
            if (touchX < lrLimit) {
                return -1;
            } else if (touchX > viewWidth - lrLimit) {
                return 1;
            }
            return 0;
        }

        /**
         * 计算垂直滚动指向
         * 可以对touchY的值进行过滤  例:滑动超出view(touchY<0||touchY>viewHeight)
         *
         * @return -1:像上滑 0:不滑动 1:像下滑
         */
        public int calcScrollVerticalDirect(int touchY, int viewHeight) {
            float udLimit = getVerticalScrollLimit();
            if (touchY < udLimit) {
                return -1;
            } else if (touchY > viewHeight - udLimit) {
                return 1;
            }
            return 0;
        }


        /**
         * 第一次被选中的时候回调
         *
         * @param selectedView 选中的RecyclerView
         * @param selectedPos  选中的位置
         */
        public abstract boolean onRecyclerSelected(RecyclerView selectedView, int selectedPos);

        /**
         * 触摸时切换RecyclerView的时候回调
         *
         * @param fromView            上个RecyclerView
         * @param toView              当前RecyclerView
         * @param itemFromPos         上个item选中的位置
         * @param itemToPos           当前item选中的位置
         * @param recyclerViewFromPos 上个RecyclerView选中的位置
         * @param recyclerViewToPos   当前RecyclerView选中的位置
         */
        public abstract boolean onRecyclerChanged(RecyclerView fromView, RecyclerView toView, int itemFromPos, int itemToPos,
                                                  int recyclerViewFromPos, int recyclerViewToPos);

        /**
         * 第一次被选中的时候回调
         *
         * @param selectedView 选中的RecyclerView
         * @param selectedPos  选中的位置
         */
        public abstract boolean onItemSelected(View selectedView, int selectedPos);

        /**
         * 触摸时切换Item的时候回调
         *
         * @param recyclerView    包含Item的RecyclerView
         * @param fromPos         上个选中的位置
         * @param toPos           当前选中的位置
         * @param recyclerViewPos 当前包含Item的RecyclerView选中的位置
         */
        public abstract boolean onItemChanged(RecyclerView recyclerView, int fromPos, int toPos, int recyclerViewPos);

        /**
         * 拖拽结束时回调
         */
        public abstract void onDragFinish(RecyclerView recyclerView, int itemPos, int itemHorizontalPos);


        /**
         * 拖拽开始时回调
         */
        public abstract void onDragStart();
    }
}
