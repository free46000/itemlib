package free.com.itemlib;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

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
    private PanelTouchHelper touchHelper;

    private float lastTouchX;
    private float lastTouchY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_muti);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        baseItemAdapter = new BaseItemAdapter(this);
        baseItemAdapter.addDataItem(new ItemRecycler(5), new ItemRecycler(5), new ItemRecycler(5), new ItemRecycler(5), new ItemRecycler(5), new ItemRecycler(5), new ItemRecycler(5));
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(baseItemAdapter);
        baseItemAdapter.notifyDataSetChanged();
        recyclerView.setClipToPadding(false);

        touchHelper = new PanelTouchHelper(this, recyclerView);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int contentTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        lastTouchX = ev.getX();
        lastTouchY = ev.getY() - contentTop;
        if (touchHelper.onTouch(ev)) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    class OnBaseDragListener extends PanelTouchHelper.OnDragListener {
        private Item currItem;
        private RecyclerView lastRecyclerView;

        public OnBaseDragListener(Item currItem) {
            this.currItem = currItem;
        }

        public void onRecyclerSelected(RecyclerView recyclerView, int selectedPos) {
            lastRecyclerView = recyclerView;
        }

        public void onRecyclerChanged(RecyclerView fromView, RecyclerView toView, int itemFromPos, int itemToPos) {
            BaseItemAdapter adapter = (BaseItemAdapter) fromView.getAdapter();
            adapter.removeDataItem(itemFromPos);
            adapter = (BaseItemAdapter) toView.getAdapter();
            adapter.addDataItem(itemToPos, currItem);

            lastRecyclerView = toView;

        }

        public void onItemSelected(View selectedView, int selectedPos) {
        }

        public void onItemChanged(RecyclerView recyclerView, int fromPos, int toPos) {
            BaseItemAdapter adapter = (BaseItemAdapter) recyclerView.getAdapter();
            adapter.moveDataItem(fromPos, toPos);
        }

        public void onDragFinish(int itemPos) {
            ((MainActivity.ItemText) currItem).setGravity(View.VISIBLE);
            if (lastRecyclerView != null)
                lastRecyclerView.getAdapter().notifyDataSetChanged();
//            for (int i = 0; i < parentRecycler.getChildCount(); i++) {
//                View childView = parentRecycler.getChildAt(i);
//                if (childView instanceof RecyclerView) {
//                    ((RecyclerView) childView).getAdapter().notifyDataSetChanged();
//                }
//            }
        }

        public void onDragStart() {
//            if (currItem instanceof MainActivity.ItemText) {
//                ((MainActivity.ItemText) currItem).setGravity(View.INVISIBLE);
//                itemViewHolder.refreshView();
//            }
        }


    }


    class ItemRecycler extends ItemBase {

        private int length = 25;

        @Override
        public String getItemViewType() {
            return length + "";
        }

        public ItemRecycler(int length) {
            this.length = length;
        }

        public ItemRecycler() {
        }

        @Override
        public View initItemView(Context context, final ViewGroup viewGroup) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_recycler_group, viewGroup, false);


            final RecyclerView recyclerView = getView(view, R.id.item_group_recycler);
            recyclerView.setClipToPadding(false);

            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            final BaseItemAdapter baseItemAdapter = new BaseItemAdapter(context);
            baseItemAdapter.addDataItemList(getItemList(length));
            recyclerView.setAdapter(baseItemAdapter);

            baseItemAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
                @Override
                public void onItemLongClick(Item item, int location) {
                }


                @Override
                public void onItemLongClick(Item item, ItemViewHolder itemViewHolder, int location, int columnLoc) {
                    View floatView = item.newItemView2Show(RecyclerActivity.this, null);
                    View itemView = itemViewHolder.getItemView();
                    int[] locArr = new int[2];
                    itemView.getLocationOnScreen(locArr);
                    // TODO: 2016/9/3 0003 offset 在PanelTouchHelper中计算
                    touchHelper.setOnDragListener(new OnBaseDragListener(item));
                    touchHelper.startDrag(recyclerView.getChildViewHolder(itemView), floatView);
                    if (item instanceof MainActivity.ItemText) {
                        ((MainActivity.ItemText) item).setGravity(View.INVISIBLE);
                        itemViewHolder.refreshView();
                    }
                }


            });

            return view;
        }

        @Override
        public void fillData(View itemView) {
            // TODO: 2016/9/13 filllData 接口用holder
            // TODO: 2016/9/13 打印是不是复用了 
            System.out.println("" + itemView);
        }

        private List<Item> getItemList(int length) {
            List<Item> list = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                list.add(new MainActivity.ItemText(i + "fsadfsafdsafdsafdsafdsa\nfdsafdasfdsafdsafdsfdasf" + i));
            }
            return list;
        }
    }

}
