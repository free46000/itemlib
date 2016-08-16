package free.com.itemlib;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import free.com.itemlib.item.BaseItemAdapter;
import free.com.itemlib.item.view.content.Item;
import free.com.itemlib.item.view.content.ItemBase;

/**
 * Created by free46000 on 2016/8/15 0015.
 */
public class RecyclerActivity extends Activity {
    private RecyclerView recyclerView;
    private BaseItemAdapter baseItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        baseItemAdapter = new BaseItemAdapter(this);
        baseItemAdapter.addDataItem(new ItemRecycler(), new ItemRecycler(), new ItemRecycler());
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setAdapter(baseItemAdapter);
        baseItemAdapter.notifyDataSetChanged();
        recyclerView.setClipChildren(false);
        recyclerView.setClipToPadding(false);
    }

    class ItemRecycler extends ItemBase {

        @Override
        public View initItemView(Context context, final ViewGroup viewGroup) {
            final RecyclerView recyclerView = new RecyclerView(context);
            recyclerView.setClipChildren(false);
            recyclerView.setClipToPadding(false);
            recyclerView.setMinimumWidth(600);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            final BaseItemAdapter baseItemAdapter = new BaseItemAdapter(context);
            baseItemAdapter.setDataItemList(getItemList());
            recyclerView.setAdapter(baseItemAdapter);
            //ItemTouchHelper 用于实现 RecyclerView Item 拖曳效果的类
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {

                @Override
                public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                    //actionState : action状态类型，有三类 ACTION_STATE_DRAG （拖曳），ACTION_STATE_SWIPE（滑动），ACTION_STATE_IDLE（静止）
//                int dragFlags = makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.UP | ItemTouchHelper.DOWN
//                        | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);//支持上下左右的拖曳
//                int swipeFlags = makeMovementFlags(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);//表示支持左右的滑动
                    int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN
                            | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;//支持上下左右的拖曳
                    int swipeFlags =  ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;//表示支持左右的滑动
                    return makeMovementFlags(dragFlags, swipeFlags);//直接返回0表示不支持拖曳和滑动
                }

                @Override
                public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
//                 viewHolder.itemView.bringToFront();
                    recyclerView.bringToFront();
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
            itemTouchHelper.attachToRecyclerView(recyclerView);

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
