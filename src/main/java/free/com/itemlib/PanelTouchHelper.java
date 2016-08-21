package free.com.itemlib;

import android.content.Context;
import android.graphics.PixelFormat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by free46000 on 2016/8/19.
 */
public class PanelTouchHelper {
    public static final int NONE = -1;

    private View currItemView;
    private RecyclerView.ViewHolder currViewHolder;
    private RecyclerView parentRecycler;
    private OnDragListener onDragListener;
    private DragFloatViewHelper floatViewHelper;

    private int lastParentPos = NONE;
    private int lastChildPos = NONE;

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
        }

        if (currItemView != null) {
            floatViewHelper.updateView((int) touchX, (int) touchY);
            View view = parentRecycler.findChildViewUnder(touchX, touchY);
            int parentPos = getPositionByItemView(view);
            if (parentPos != NONE && view instanceof RecyclerView) {
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
        return Math.abs(touchY - selectedView.getTop()) > (selectedView.getHeight() / 2);
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


    public interface OnDragListener {
        /**
         * 第一次被选中的时候回调
         *
         * @param selectedView 选中的RecyclerView
         * @param selectedPos  选中的位置
         */
        void onRecyclerSelected(RecyclerView selectedView, int selectedPos);

        /**
         * 触摸时切换RecyclerView的时候回调
         *
         * @param fromView    上个RecyclerView
         * @param toView      当前RecyclerView
         * @param itemFromPos 上个item选中的位置
         * @param itemToPos   当前item选中的位置
         */
        void onRecyclerChanged(RecyclerView fromView, RecyclerView toView, int itemFromPos, int itemToPos);

        /**
         * 第一次被选中的时候回调
         *
         * @param selectedView 选中的RecyclerView
         * @param selectedPos  选中的位置
         */
        void onItemSelected(View selectedView, int selectedPos);

        /**
         * 触摸时切换Item的时候回调
         *
         * @param recyclerView 包含Item的RecyclerView
         * @param fromPos      上个选中的位置
         * @param toPos        当前选中的位置
         */
        void onItemChanged(RecyclerView recyclerView, int fromPos, int toPos);

        /**
         * 拖拽结束时回调
         */
        void onDragFinish(int itemPos);


        /**
         * 拖拽开始时回调
         */
        void onDragStart();
    }


}
