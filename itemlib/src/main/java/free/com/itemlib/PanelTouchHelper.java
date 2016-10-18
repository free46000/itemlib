package free.com.itemlib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Created by free46000 on 2016/8/19.
 * 面板拖动辅助类 -跨RecyclerView拖动
 * <p>
 * todo 通过#OnDragListener的切换回调boolean值处理是否可以替换的场景（有的Item是不允许被拖动的）
 */
public class PanelTouchHelper {
    public static final int NONE = -1;
    public static final int MOVE_LIMIT = 2;

    private View currItemView;
    private RecyclerView horizontalRecycler;
    private OnDragListener onDragListener;
    private DragFloatViewHelper floatViewHelper;

    private int lastVerticalPos = NONE;
    private int lastChildPos = NONE;

    private float lastTouchRawX, lastTouchRawY;
    private RecyclerView lastRecyclerView;

    public PanelTouchHelper(RecyclerView horizontalRecycler) {
        this.horizontalRecycler = horizontalRecycler;
        floatViewHelper = new DragFloatViewHelper();
    }

    /**
     * 开始拖拽
     *
     * @param viewHolder 选中的Item的ViewHolder
     */
    public void startDrag(RecyclerView.ViewHolder viewHolder) {
        startDrag(viewHolder.itemView, viewHolder.getAdapterPosition());
    }

    /**
     * 开始拖拽
     *
     * @param itemView     选中的Item的View
     * @param itemPosition 选中的Item的position
     */
    public void startDrag(View itemView, int itemPosition) {
        if (!onDragListener.onItemSelected(itemView, itemPosition)) {
            return;
        }
        currItemView = itemView;
        lastChildPos = itemPosition;
        onDragListener.onDragStart();
        floatViewHelper.createView(currItemView, lastTouchRawX, lastTouchRawY, onDragListener.getScale());
        onDragListener.onDrawFloatView(floatViewHelper.getFloatView());
        lastVerticalPos = NONE;

        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) currItemView.getLayoutParams();
        itemViewHeight = currItemView.getHeight() + params.topMargin + params.bottomMargin;
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

        if (currItemView == null) {
            return false;
        }
        //如果drag过程没有MOVE事件，lastVerticalPos和lastRecyclerView是不能被正确初始化的，这样dragFinish回调就会有问题
//        if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
        floatViewHelper.updateView((int) lastTouchRawX, (int) lastTouchRawY);
        moveIfNecessary(lastTouchRawX, lastTouchRawY);
        scrollRunnableStart();
//        }

        if (event.getActionMasked() == MotionEvent.ACTION_UP ||
                event.getActionMasked() == MotionEvent.ACTION_CANCEL ||
                event.getActionMasked() == MotionEvent.ACTION_OUTSIDE) {
            stopDrag();
        }

        return true;
    }

    private void scrollRunnableStart() {
        if (lastRecyclerView != null) {
            lastRecyclerView.removeCallbacks(mScrollRunnable);
            mScrollRunnable.run();
            lastRecyclerView.invalidate();
        }
    }

    private void stopDrag() {
        if (currItemView != null) {
            onDragListener.onDragFinish(lastRecyclerView, lastChildPos, lastVerticalPos);
            floatViewHelper.removeView();
        }

        currItemView = null;
    }

    private int itemViewHeight;

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
//        System.out.println("find_parent_out:" + lastVerticalPos + "-" + verticalPos + "==" + location[0] + "====" + location[1]);

        RecyclerView verticalRecycler = findRecyclerView(view);
        if (verticalPos == NONE || verticalRecycler == null) {
            return false;
        }

        location = getRecyclerViewInsideLocation(verticalRecycler, touchRawX, touchRawY);
        float childX = location[0];
        float childY = location[1];

        View itemTargetView = verticalRecycler.findChildViewUnder(childX, childY);
        int childPos = getTargetChildPos(itemTargetView, childY, lastVerticalPos, verticalPos);
//        System.out.println("find_parent_out:" + childPos + "==" + lastVerticalPos + "-" + verticalPos + "==" + "childX:" + childX + "childY:" + childY);
        if (isSelectedRecyclerView(lastVerticalPos, verticalPos)) {
            onDragListener.onRecyclerSelected(verticalRecycler, verticalPos);
            lastVerticalPos = verticalPos;
            lastRecyclerView = verticalRecycler;
        } else if (isChangeRecyclerView(lastVerticalPos, verticalPos)) {
            childPos = calcChildPositionOnChangedRecyclerView(verticalRecycler, itemTargetView, childPos, childX, childY);
//            System.out.println("find_parent_inside:" + childPos + "==" + lastVerticalPos + "-" + verticalPos + "==" + "childX:" + childX + "childY:" + childY);
            if (childPos != NONE) {
                int[] toPos = onDragListener.onRecyclerChangedPosition(lastRecyclerView, verticalRecycler,
                        lastChildPos, childPos, lastVerticalPos, verticalPos);
                if (toPos != null) {
                    verticalPos = toPos[0];
                    childPos = toPos[1];
                }
                boolean isChanged = onDragListener.onRecyclerChanged(lastRecyclerView, verticalRecycler,
                        lastChildPos, childPos, lastVerticalPos, verticalPos);
                if (!isChanged) {
                    return result;
                }
//                System.out.println("find_parent:" + lastVerticalPos + "-" + verticalPos);
                //在切换recycle view并且触摸到子recycle view的item的时候才真正去改变值
                lastVerticalPos = verticalPos;
                lastRecyclerView = verticalRecycler;
                //因为切换父控件，所以需要重置为当前ChildPos，不然上个的最后位置有可能超过当前的大小抛出错误
                lastChildPos = childPos;
            }

        }

        if (childPos == NONE) {
            return result;
        }

        if (isNeedMove(itemTargetView, lastChildPos, childPos, childY)) {
            childPos = onDragListener.onItemChangedPosition(verticalRecycler, lastChildPos, childPos, lastVerticalPos);

            boolean isChanged = onDragListener.onItemChanged(verticalRecycler, lastChildPos, childPos, lastVerticalPos);
            if (!isChanged) {
                return result;
            }
            final RecyclerView.LayoutManager layoutManager = verticalRecycler.getLayoutManager();
            if (layoutManager instanceof ItemTouchHelper.ViewDropHandler) {
                OrientationHelper helper = OrientationHelper.createVerticalHelper(layoutManager);
                int start = helper.getDecoratedStart(itemTargetView);
                int end = helper.getDecoratedEnd(itemTargetView);


                if (lastChildPos > childPos) {
                    ((LinearLayoutManager) verticalRecycler.getLayoutManager())
                            .scrollToPositionWithOffset(childPos, start);
                } else {

                    ((LinearLayoutManager) verticalRecycler.getLayoutManager())
                            .scrollToPositionWithOffset(childPos, end - itemViewHeight);
                }
//                System.out.println(lastChildPos + "-" + childPos + "OrientationHelperOrientationHelper:"
//                        + height + "==" + itemViewHeight + "=||=" + start + "===" + end + "||||||" + myStart + "===" + itemTargetView.getHeight() );
            }


            if (lastChildPos == 0 || childPos == 0) {
                verticalRecycler.scrollToPosition(0);
            }


//            System.out.println("find:" + lastVerticalPos + "-" + verticalPos + "======" + lastChildPos + "-" + childPos);
            lastChildPos = childPos;
        }
        return result;
    }

    /**
     * 获取当前点击的位置在RecyclerView内部的坐标 (Y坐标范围0+padding到height-padding)?
     */
    private float[] getRecyclerViewInsideLocation(RecyclerView verticalRecycler, float touchRawX, float touchRawY) {
        float[] result = new float[2];
        int[] location = new int[2];
        verticalRecycler.getLocationOnScreen(location);
        result[0] = touchRawX - location[0];
        result[1] = touchRawY - location[1];
//        System.out.println("getRecyclerViewInsideLocation:" + result[0] + "-" + result[1] + "==" + "X:" + touchRawX + "Y:" + touchRawY);

        result[0] = result[0] * onDragListener.getScale();
        result[1] = result[1] * onDragListener.getScale();

        int minY = verticalRecycler.getPaddingTop();
        int maxY = verticalRecycler.getHeight() - verticalRecycler.getPaddingBottom();
        result[1] = Math.min(Math.max(result[1], minY), maxY);


        return result;
    }

    /**
     * 当在两个RecyclerView中切换时，计算目标RecyclerView中Child位置 (若目标RecyclerView为空返回0)
     */
    private int calcChildPositionOnChangedRecyclerView(RecyclerView verticalRecycler, View itemTargetView, int childPos, float childX, float childY) {
        if (childPos == NONE && verticalRecycler.getAdapter().getItemCount() == 0) {
            childPos = 0;
        } else if (itemTargetView != null) {
            int top = itemTargetView.getTop();
            if ((top + itemTargetView.getHeight() / MOVE_LIMIT) < childY) {
                //证明滑动位置在targetView的下半部分，所以要插入到当前位置+1
                childPos++;
            }
        }

        return childPos;
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
            scrollX = onDragListener.calcHorizontalScrollDistance(recyclerView, curX, curY);
        }
        if (lm.canScrollVertically()) {
            scrollY = onDragListener.calcVerticalScrollDistance(recyclerView, curX, curY);
        }
//        System.out.println("scroll:::::" + scrollY + "=" + recyclerView.getScrollY() + "curY::" + curY);
//        System.out.println("scroll:::::" + scrollX + "=" + recyclerView.getScrollX() + "curX::" + curX);

        if (scrollX != 0 || scrollY != 0) {
            recyclerView.scrollBy(scrollX, scrollY);
        }
        return scrollX != 0 || scrollY != 0;
    }


    /**
     * 获取需要move的目标view，即toPosition
     *
     * @return NONE为找不到
     */
    private int getTargetChildPos(View itemTargetView, float childY, int lastHorizontalPos, int currHorizontalPos) {
        int childPos = getPositionByItemView(itemTargetView);

        if (childPos != NONE && (childPos != lastChildPos || lastHorizontalPos != currHorizontalPos)
                && (isCurrPosition(childY, itemTargetView))) {
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
//        System.out.println("isCurrPosition:" + (childY > itemView.getTop() && childY < itemView.getBottom()));
        return childY >= itemView.getTop() && childY <= itemView.getBottom();
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
//            System.out.println("paramsgetViewAdapterPosition:" + params.getViewAdapterPosition()
//                    + "==getViewLayoutPosition:" + params.getViewLayoutPosition());
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


    public static class DragFloatViewHelper {
        private View currTouchedView;
        private WindowManager wManager;
        private WindowManager.LayoutParams mParams;
        private int offsetX, offsetY;

        /**
         * 创建浮层view
         *
         * @param coverView 被覆盖的view，用于计算浮层位置
         * @param scale
         */
        public void createView(View coverView, float touchRawX, float touchRawY, float scale) {
            currTouchedView = createFloatView(coverView);

            wManager = (WindowManager) currTouchedView.getContext().getSystemService(
                    Context.WINDOW_SERVICE);
            mParams = initParams(currTouchedView);
            mParams.width = (int) (coverView.getWidth() / scale);//窗口的宽和高
            mParams.height = (int) (coverView.getHeight() / scale);
            int[] location = getLocation(coverView);
            mParams.x = location[0];//窗口位置的偏移量
            mParams.y = location[1];
            wManager.addView(currTouchedView, mParams);

            offsetX = (int) (touchRawX - location[0]);
            offsetY = (int) (touchRawY - location[1]);
        }

        /**
         * @param coverView 被覆盖的view，用于生产浮层View
         * @return 需要跟随手势浮动的 View
         */
        protected View createFloatView(View coverView) {
            ImageView floatView = new ImageView(coverView.getContext());
            coverView.destroyDrawingCache();
            coverView.setDrawingCacheEnabled(true);
            Bitmap bitmap = coverView.getDrawingCache();
            if (bitmap != null && !bitmap.isRecycled()) {
                floatView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                floatView.setImageBitmap(bitmap);
            }
            return floatView;
        }

        /**
         * @return 需要跟随手势浮动的 View
         */
        public View getFloatView() {
            return currTouchedView;
        }


        protected int[] getLocation(View coverView) {
            int[] result = new int[2];
            coverView.getLocationOnScreen(result);

            return result;
        }


        private WindowManager.LayoutParams initParams(View floatView) {
            WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
            mParams.token = floatView.getWindowToken();
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;// 程序提示window
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
        protected int horizontalScrollMaxSpeed = 15;
        protected int verticalScrollMaxSpeed = 10;
        protected float horizontalLimit = 100;
        protected float verticalLimit = 200;


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

        public float getScale() {
            return 1;
        }


        /**
         * Item切换前回调此方法获取替换的目标Item
         * 默认返回 toPos
         *
         * @param recyclerViewToPos 目标RecyclerViewPosition
         * @param itemToPos         目标ItemPosition
         * @return 默认null[目标RecyclerViewPosition, 目标ItemPosition]
         */
        public int[] onRecyclerChangedPosition(RecyclerView fromView, RecyclerView toView, int itemFromPos, int itemToPos,
                                               int recyclerViewFromPos, int recyclerViewToPos) {
            return null;
        }

        /**
         * Item切换前回调此方法获取替换的目标Item
         * 默认返回 toPos
         *
         * @param toPos 目标ItemPosition
         * @return 目标ItemPosition
         */
        public int onItemChangedPosition(RecyclerView recyclerView, int fromPos, int toPos, int recyclerViewPos) {
            return toPos;
        }

        public int getHorizontalScrollMaxSpeed() {
            return (int) (horizontalScrollMaxSpeed * getScale());
        }

        public int getVerticalScrollMaxSpeed() {
            return (int) (verticalScrollMaxSpeed * getScale());
        }

        public float getHorizontalLimit() {
            return horizontalLimit * getScale();
        }

        public float getVerticalLimit() {
            return verticalLimit * getScale();
        }


        public int calcHorizontalScrollDistance(View view, int touchX, int touchY) {
            int direct = calcScrollHorizontalDirect(touchX, view.getWidth());
            int scrollDistance = 0;
            if (direct < 0) {
                float level = (getHorizontalLimit() - touchX) / getHorizontalLimit();
                scrollDistance = -calcScrollDistance(level, getHorizontalScrollMaxSpeed());
            } else if (direct > 0) {
                float level = (touchX - view.getWidth() + getHorizontalLimit()) / getHorizontalLimit();
                scrollDistance = calcScrollDistance(level, getHorizontalScrollMaxSpeed());
            }
            return scrollDistance;
        }

        public int calcVerticalScrollDistance(View view, int touchX, int touchY) {
            int direct = calcScrollVerticalDirect(touchY, view.getHeight());
            int scrollDistance = 0;
            if (direct < 0) {
                float level = (getVerticalLimit() - touchY) / getVerticalLimit();
                scrollDistance = -calcScrollDistance(level, getVerticalScrollMaxSpeed());
            } else if (direct > 0) {
                float level = (touchY - view.getHeight() + getVerticalLimit()) / getVerticalLimit();
                scrollDistance = calcScrollDistance(level, getVerticalScrollMaxSpeed());
            }
            return scrollDistance;
        }


        protected int calcScrollDistance(float touchLevel, int maxSpeed) {
            touchLevel = touchLevel > 1 ? 1f : touchLevel;
            return (int) (touchLevel * maxSpeed);
        }

        /**
         * 计算水平滚动指向
         * 可以对touchX的值进行过滤  例:滑动超出view(touchX<0||touchX>viewWidth)
         *
         * @return -1:像左滑 0:不滑动 1:像右滑
         */
        public int calcScrollHorizontalDirect(int touchX, int viewWidth) {
            if (touchX < getHorizontalLimit()) {
                return -1;
            } else if (touchX > viewWidth - getHorizontalLimit()) {
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
            if (touchY < getVerticalLimit()) {
                return -1;
            } else if (touchY > viewHeight - getVerticalLimit()) {
                return 1;
            }
            return 0;
        }

        /**
         * 对浮动view进行处理 如：动画的处理
         *
         * @param floatView 浮动的view
         */
        public void onDrawFloatView(View floatView) {
            floatView.setScaleX(0.95f);
            floatView.setScaleY(0.95f);
            floatView.setRotation(0.9f);
            floatView.setAlpha(0.8f);
        }
    }
}
