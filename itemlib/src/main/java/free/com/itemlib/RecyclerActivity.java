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


    private RecyclerView mRecyclerView;
    private BaseItemAdapter baseItemAdapter;
    private PanelTouchHelper touchHelper;

    private float lastTouchX;
    private float lastTouchY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_muti);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        baseItemAdapter = new BaseItemAdapter(this);
        baseItemAdapter.addDataItem(new ItemRecycler(15), new ItemRecycler(15), new ItemRecycler(25), new ItemRecycler(15), new ItemRecycler(5), new ItemRecycler(5), new ItemRecycler(5));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setAdapter(baseItemAdapter);
        baseItemAdapter.notifyDataSetChanged();
        mRecyclerView.setClipToPadding(false);

        touchHelper = new PanelTouchHelper(mRecyclerView);
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

        public OnBaseDragListener(Item currItem) {
            this.currItem = currItem;
        }

        public boolean onRecyclerSelected(RecyclerView recyclerView, int selectedPos) {
            return true;
        }

        public boolean onRecyclerChanged(RecyclerView fromView, RecyclerView toView, int itemFromPos,
                                         int itemToPos, int i, int ii) {
            BaseItemAdapter adapter = (BaseItemAdapter) fromView.getAdapter();
            adapter.removeDataItem(itemFromPos);
            adapter = (BaseItemAdapter) toView.getAdapter();
            adapter.addDataItem(itemToPos, currItem);

            return true;
        }

        public boolean onItemSelected(View selectedView, int selectedPos) {
            return true;
        }

        public boolean onItemChanged(RecyclerView recyclerView, int fromPos, int toPos, int i) {
            BaseItemAdapter adapter = (BaseItemAdapter) recyclerView.getAdapter();
            adapter.moveDataItem(fromPos, toPos);
            return true;
        }

        public void onDragFinish(RecyclerView recyclerView, int itemPos, int itemHorizontalPos) {
            ((MainActivity.ItemText) currItem).setGravity(View.VISIBLE);
            if (recyclerView != null)
                recyclerView.getAdapter().notifyDataSetChanged();
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
            return length + "" + this.toString();
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
//            mRecyclerView.setClipToPadding(false);

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
                    touchHelper.setOnDragListener(new OnBaseDragListener(item));
                    touchHelper.startDrag(recyclerView.getChildViewHolder(itemView), floatView);
                    if (item instanceof MainActivity.ItemText) {
                        ((MainActivity.ItemText) item).setGravity(View.INVISIBLE);
                        itemViewHolder.refreshView();
                    }

//                    mRecyclerView.setScaleX(0.5f);
//                    mRecyclerView.setScaleY(0.5f);
//                    mRecyclerView.getLayoutParams().height = mRecyclerView.getLayoutParams().height * 2;

                }


            });

            return view;
        }

        @Override
        public void fillData(ItemViewHolder itemView) {
            System.out.println("" + itemView + "=================" + itemView.getItemView());
        }

        private List<Item> getItemList(int length) {
            List<Item> list = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                if (i == 1) {
                    list.add(new MainActivity.ItemText(i + "fsadfsa\nfdsafdsa\nfdsafdsa\nfdsafdasfd\nsafdsa\nfdsfdasf" + i));
                }
                list.add(new MainActivity.ItemText(i + "fsadfsafdsafdsafdsafdsa\nfdsafdasfdsafdsafdsfdasf" + i));
            }
            return list;
        }
    }

}
