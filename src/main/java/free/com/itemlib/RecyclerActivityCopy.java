package free.com.itemlib;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import java.util.ArrayList;
import java.util.List;

import free.com.itemlib.item.BaseItemAdapter;
import free.com.itemlib.item.view.ItemViewHolder;
import free.com.itemlib.item.view.content.Item;
import free.com.itemlib.item.view.content.ItemBase;

/**
 * Created by free46000 on 2016/8/15 0015.
 */
public class RecyclerActivityCopy extends Activity {
    private RecyclerView recyclerView;
    private BaseItemAdapter baseItemAdapter;

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
        int contentTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();

        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            lastChildPos = -1;
            lastParentPos = -1;
        }


        View view = recyclerView.findChildViewUnder(ev.getX(), ev.getY());
// TODO: 2016/8/16 记住上次的parentLoc位置判断是否需要removeItem然后在新的recyclerview中add
        // TODO: 2016/8/16 记住上次的child以便在去moveItem

        // TODO: 2016/8/17 0017 和上次移动距离大于一定值才去判断
        int parentLoc = -1;
        int childLoc = -1;
        if (view != null && view instanceof RecyclerView) {
            ItemViewHolder parentviewHolder = (ItemViewHolder) view.getTag();
            parentLoc = parentviewHolder.location;

            float childX = ev.getX() - view.getLeft();
            float childY = ev.getY() - contentTop;
            View itemView = ((RecyclerView) view).findChildViewUnder(childX, childY);

            if (itemView != null) {
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) itemView.getLayoutParams();
                childLoc = ((RecyclerView.LayoutParams) itemView.getLayoutParams()).getViewAdapterPosition();
                System.out.println("paramsgetViewAdapterPosition:" + params.getViewAdapterPosition()
                        + "==getViewLayoutPosition:" + params.getViewLayoutPosition() + "childY:" + childY
                        + "childX:" + childX);
            }
            if (lastChildPos != childLoc && childLoc != -1 && lastChildPos != -1) {
//                ((RecyclerView) view).getAdapter().notifyItemMoved(childLoc, lastChildPos);
                System.out.println("find:" + lastChildPos + "==" + childLoc);
            }
            if (lastParentPos != parentLoc && lastParentPos != -1 && parentLoc != -1 && childLoc != -1) {
                BaseItemAdapter adapter = (BaseItemAdapter) ((RecyclerView) recyclerView.getChildAt(lastParentPos)).getAdapter();
                ItemTouchHelper touchHelper = getTouchHelper((RecyclerView) recyclerView.getChildAt(lastParentPos));
//                ((RecyclerView) recyclerView.getChildAt(lastParentPos)).removeView(itemView);
                itemView.setVisibility(View.GONE);
//                touchHelper.startDrag(null);
//                adapter.removeDataTest(lastParentPos);
                adapter = (BaseItemAdapter) ((RecyclerView) view).getAdapter();
                adapter.addDataTest(childLoc, new MainActivity.ItemText("afsdfsafsdgsgsagQQQQQQQQQQQQQQQQ"));
                touchHelper = getTouchHelper((RecyclerView) view);
//                ((RecyclerView) view).addView(itemView);
//                touchHelper.startDrag(((RecyclerView) view).getC);
            }
            lastParentPos = parentLoc;
            if (childLoc != -1) {
                lastChildPos = childLoc;
            }


        }


        return super.dispatchTouchEvent(ev);


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
            //ItemTouchHelper 用于实现 RecyclerView Item 拖曳效果的类
            touchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {

                @Override
                public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                    //actionState : action状态类型，有三类 ACTION_STATE_DRAG （拖曳），ACTION_STATE_SWIPE（滑动），ACTION_STATE_IDLE（静止）
//                int dragFlags = makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.UP | ItemTouchHelper.DOWN
//                        | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);//支持上下左右的拖曳
//                int swipeFlags = makeMovementFlags(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);//表示支持左右的滑动
                    int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN
                            | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;//支持上下左右的拖曳
                    int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;//表示支持左右的滑动
                    return makeMovementFlags(dragFlags, swipeFlags);//直接返回0表示不支持拖曳和滑动
                }

                @Override
                public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
//                 viewHolder.itemView.bringToFront();
//                    recyclerView.bringToFront();
                    super.onSelectedChanged(viewHolder, actionState);
                }


                /**
                 * @param recyclerView attach的RecyclerView
                 * @param viewHolder 拖动的Item
                 * @param target 放置Item的目标位置
                 * @return
                 */
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    int fromPosition = viewHolder.getAdapterPosition();//要拖曳的位置
                    int toPosition = target.getAdapterPosition();//要放置的目标位置
//                Collections.swap(mData, fromPosition, toPosition);//做数据的交换
                    baseItemAdapter.notifyItemMoved(fromPosition, toPosition);
                    return true;
                }

                /**
                 * @param viewHolder 滑动移除的Item
                 * @param direction
                 */
                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    int position = viewHolder.getAdapterPosition();//获取要滑动删除的Item位置
//                mData.remove(position);//删除数据
                    baseItemAdapter.notifyItemRemoved(position);
                }

            });
            touchHelper.attachToRecyclerView(recyclerView);

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
