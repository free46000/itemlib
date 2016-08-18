package free.com.itemlib;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import free.com.itemlib.item.BaseItemAdapter;
import free.com.itemlib.item.listener.OnItemLongClickListener;
import free.com.itemlib.item.view.ItemViewHolder;
import free.com.itemlib.item.view.content.Item;
import free.com.itemlib.item.view.content.ItemBase;

/**
 * Created by free46000 on 2016/8/15 0015.
 */
public class RecyclerActivity extends Activity {
    public static final int NONE = -1;


    private RecyclerView recyclerView;
    private BaseItemAdapter baseItemAdapter;
    private Item currSelectedItem;
    private View currTouchedView;
    private float currX, currY;
    WindowManager wManager;
    WindowManager.LayoutParams mParams;
    private int contentTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_muti);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        baseItemAdapter = new BaseItemAdapter(this);
        baseItemAdapter.addDataItem(new ItemRecycler(), new ItemRecycler(), new ItemRecycler());
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(baseItemAdapter);
        baseItemAdapter.notifyDataSetChanged();
        recyclerView.setClipChildren(false);
        recyclerView.setClipToPadding(false);
    }


    int lastChildPos = -1;
    int lastParentPos = -1;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        contentTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();

        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            lastChildPos = NONE;
            lastParentPos = NONE;
        }
        currX = ev.getX();
        currY = ev.getY() - contentTop;
        updateView();
        View view = recyclerView.findChildViewUnder(currX, currY);
        // TODO: 2016/8/17 0017 和上次移动距离大于一定值才去判断
        int parentPos = getPositionByItemView(view);
        int childPos = NONE;
        if (parentPos != NONE && view instanceof RecyclerView) {
            float childX = ev.getX() - view.getLeft();
            float childY = ev.getY() - contentTop;
            View itemView = ((RecyclerView) view).findChildViewUnder(childX, childY);

            childPos = getPositionByItemView(itemView);
            System.out.println("find_parent_out:" + lastParentPos + "-" + parentPos + "==" + "childX:" + childX + "childY:" + childY + "===" + "parentX:" + currX);
            if (isSelectedRecyclerView(lastParentPos, parentPos)) {
                lastParentPos = parentPos;
            } else if (isRealChangeRecyclerView(lastParentPos, parentPos, childPos)) {
                BaseItemAdapter adapter = (BaseItemAdapter) ((RecyclerView) recyclerView.getChildAt(lastParentPos)).getAdapter();
                adapter.removeDataTest(lastChildPos);
                adapter = (BaseItemAdapter) ((RecyclerView) view).getAdapter();
                adapter.addDataTest(childPos, new MainActivity.ItemText("afsdfsafsdgsgsagQQQQQQQQQQQQQQQQ"));
                System.out.println("find_parent:" + lastParentPos + "-" + parentPos);

                //因为切换父控件，所以childpos需要重置为相等，不然上个的最后位置有可能超过当前的大小抛出错误
                lastChildPos = childPos;
                //在切换recycleview并且触摸到子recycleview的item的时候才真正去改变值
                lastParentPos = parentPos;
            }


            if (childPos != NONE) {
                if (lastChildPos != childPos && lastChildPos != NONE) {
                    System.out.println("find:" + lastParentPos + "-" + parentPos + "======" + lastChildPos + "-" + childPos);
                    ((RecyclerView) view).getAdapter().notifyItemMoved(childPos, lastChildPos);
                }
                lastChildPos = childPos;
            }

        }
        if (ev.getActionMasked() == MotionEvent.ACTION_UP) {
            if (currTouchedView != null)
                wManager.removeView(currTouchedView);
            currTouchedView = null;
            currSelectedItem = null;
        }
        if (currTouchedView != null) {
            return true;
        }


        return super.dispatchTouchEvent(ev);


    }

    /**
     * 查找当前view在RecyclerView中的位置 没有返回NONE
     *
     * @param itemView View
     * @return int
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
        }
        return NONE;
    }

    /**
     * 是否真正切换了RecyclerView
     * 需要注意这里把没有切换后没有touch到Item当成不是真正切换
     *
     * @param lastParentPos
     * @param currParentPos
     * @param currChildPos
     * @return
     */
    private boolean isRealChangeRecyclerView(int lastParentPos, int currParentPos, int currChildPos) {
        return lastParentPos != currParentPos && currChildPos != NONE && lastParentPos != NONE && currParentPos != NONE;
    }

    /**
     * 是否为初次选中RecycleView
     *
     * @param lastParentPos
     * @param currParentPos
     * @return
     */
    private boolean isSelectedRecyclerView(int lastParentPos, int currParentPos) {
        return lastParentPos == NONE && currParentPos != NONE;
    }

    private void updateView() {
        if (currTouchedView != null) {
            mParams.x = (int) currX;
            mParams.y = (int) currY;
            wManager.updateViewLayout(currTouchedView, mParams);
        }

    }

    private ItemTouchHelper getTouchHelper(RecyclerView view) {
        ItemViewHolder viewHolder = (ItemViewHolder) view.getTag();
        ItemRecycler itemRecycler = (ItemRecycler) viewHolder.getCurrItem();
        return itemRecycler.touchHelper;
    }


    class ItemRecycler extends ItemBase {
        public ItemTouchHelper touchHelper;

        @Override
        public View initItemView(Context context, final ViewGroup viewGroup) {
            final RecyclerView recyclerView = new RecyclerView(context);
            recyclerView.setClipChildren(false);
            recyclerView.setClipToPadding(false);
            recyclerView.setMinimumWidth(400);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            final BaseItemAdapter baseItemAdapter = new BaseItemAdapter(context);
            baseItemAdapter.setDataItemList(getItemList());
            recyclerView.setAdapter(baseItemAdapter);

            baseItemAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
                @Override
                public void onItemLongClick(Item item, int location) {
                }


                @Override
                public void onItemLongClick(Item item, ItemViewHolder itemViewHolder, int location, int columnLoc) {
                    currSelectedItem = item;
                    currTouchedView = currSelectedItem.newItemView2Show(RecyclerActivity.this, null);
//                    itemViewHolder.getItemView().setVisibility(View.INVISIBLE);
                    itemViewHolder.getItemView().setBackgroundColor(0xFF999999);
                    createView(currTouchedView, itemViewHolder.getItemView());
                }

                private void createView(View view, View bottomView) {
                    wManager = (WindowManager) getSystemService(
                            Context.WINDOW_SERVICE);
                    mParams = new WindowManager.LayoutParams();
//                    mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;// 系统提示window
                    mParams.format = PixelFormat.TRANSLUCENT;// 支持透明
                    //mParams.format = PixelFormat.RGBA_8888;
                    mParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// 焦点
                    mParams.gravity = Gravity.LEFT | Gravity.TOP;
                    mParams.width = bottomView.getWidth();//窗口的宽和高
                    mParams.height = bottomView.getHeight();
                    mParams.x = (int) bottomView.getX();//窗口位置的偏移量
                    mParams.y = (int) bottomView.getY();
                    mParams.alpha = 0.6f;
                    wManager.addView(view, mParams);
                }

            });


            return recyclerView;
        }

        @Override
        public void fillData(View itemView) {

        }

        private List<Item> getItemList() {
            List<Item> list = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                if (i % 5 == 0) {
                    list.add(new MainActivity.ItemText(i + "fdsagsaadfdasfasfdasfdasfdasfdgagsaggasg" + i) {
                        @Override
                        public boolean isFullSpan() {
                            return true;
                        }
                    });
                }
                list.add(new MainActivity.ItemText(i + "fsadfsafdsafdsafdsafdsafdsafdasfdsafdsafdsfdasf" + i));
            }
            return list;
        }
    }

}
