//package free.com.itemlib;
//
//import android.app.Activity;
//import android.content.Context;
//import android.graphics.PixelFormat;
//import android.os.Bundle;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.helper.ItemTouchHelper;
//import android.view.Gravity;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.view.WindowManager;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import free.com.itemlib.item.BaseItemAdapter;
//import free.com.itemlib.item.listener.OnItemLongClickListener;
//import free.com.itemlib.item.view.ItemViewHolder;
//import free.com.itemlib.item.view.content.Item;
//import free.com.itemlib.item.view.content.ItemBase;
//
///**
// * Created by free46000 on 2016/8/15 0015.
// */
//public class RecyclerActivity extends Activity {
//    public static final int NONE = -1;
//
//
//    private RecyclerView recyclerView;
//    private BaseItemAdapter baseItemAdapter;
//    private PanelTouchHelper touchHelper;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_recycler_muti);
//
//        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
//        baseItemAdapter = new BaseItemAdapter(this);
//        baseItemAdapter.addDataItem(new ItemRecycler(), new ItemRecycler(), new ItemRecycler());
//        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        recyclerView.setAdapter(baseItemAdapter);
//        baseItemAdapter.notifyDataSetChanged();
//        recyclerView.setClipChildren(false);
//        recyclerView.setClipToPadding(false);
//
//        touchHelper = new PanelTouchHelper(recyclerView);
//    }
//
//
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        int contentTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
//        float currX = ev.getX();
//        float currY = ev.getY() - contentTop;
//        if (touchHelper.onTouch(ev, currX, currY)) {
//            return true;
//        }
//        return super.dispatchTouchEvent(ev);
//    }
//
//    class OnBaseDragListener implements PanelTouchHelper.OnDragListener {
//        private Item currItem;
//        private RecyclerView lastRecyclerView;
//
//        public OnBaseDragListener(Item currItem) {
//            this.currItem = currItem;
//        }
//
//        public void onRecyclerSelected(RecyclerView recyclerView, int selectedPos) {
//            lastRecyclerView = recyclerView;
//        }
//
//        public void onRecyclerChanged(RecyclerView fromView, RecyclerView toView, int itemFromPos, int itemToPos) {
//            BaseItemAdapter adapter = (BaseItemAdapter) fromView.getAdapter();
//            adapter.removeDataTest(itemFromPos);
//            adapter = (BaseItemAdapter) toView.getAdapter();
//            adapter.addDataTest(itemToPos, currItem);
//
//            lastRecyclerView = toView;
//
//        }
//
//        public void onItemSelected(View selectedView, int selectedPos) {
//        }
//
//        public void onItemChanged(RecyclerView recyclerView, int fromPos, int toPos) {
//            BaseItemAdapter adapter = (BaseItemAdapter) recyclerView.getAdapter();
//            adapter.moveDataTest(fromPos, toPos);
//        }
//
//        public void onDragFinish(int itemPos) {
//            ((MainActivity.ItemText) currItem).setGravity(View.VISIBLE);
//            lastRecyclerView.getAdapter().notifyDataSetChanged();
////            for (int i = 0; i < parentRecycler.getChildCount(); i++) {
////                View childView = parentRecycler.getChildAt(i);
////                if (childView instanceof RecyclerView) {
////                    ((RecyclerView) childView).getAdapter().notifyDataSetChanged();
////                }
////            }
//        }
//
//        public void onDragStart() {
////            if (currItem instanceof MainActivity.ItemText) {
////                ((MainActivity.ItemText) currItem).setGravity(View.INVISIBLE);
////                itemViewHolder.refreshView();
////            }
//        }
//
//
//    }
//
//
//    class ItemRecycler extends ItemBase {
//
//        @Override
//        public View initItemView(Context context, final ViewGroup viewGroup) {
//            final RecyclerView recyclerView = new RecyclerView(context);
//            recyclerView.setClipChildren(false);
//            recyclerView.setClipToPadding(false);
//            recyclerView.setMinimumWidth(400);
//
//            recyclerView.setLayoutManager(new LinearLayoutManager(context));
//            final BaseItemAdapter baseItemAdapter = new BaseItemAdapter(context);
//            baseItemAdapter.setDataItemList(getItemList());
//            recyclerView.setAdapter(baseItemAdapter);
//
//            baseItemAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
//                @Override
//                public void onItemLongClick(Item item, int location) {
//                }
//
//
//                @Override
//                public void onItemLongClick(Item item, ItemViewHolder itemViewHolder, int location, int columnLoc) {
//                    if (item instanceof MainActivity.ItemText) {
//                        ((MainActivity.ItemText) item).setGravity(View.INVISIBLE);
//                        itemViewHolder.refreshView();
//                    }
//                    View floatView = item.newItemView2Show(RecyclerActivity.this, null);
//                    View itemView = itemViewHolder.getItemView();
//                    touchHelper.setOnDragListener(new OnBaseDragListener(item));
//                    touchHelper.startDrag(recyclerView.getChildViewHolder(itemView), floatView);
//                }
//
//
//            });
//
//
//            return recyclerView;
//        }
//
//        @Override
//        public void fillData(View itemView) {
//
//        }
//
//        private List<Item> getItemList() {
//            List<Item> list = new ArrayList<>();
//            for (int i = 0; i < 5; i++) {
//                list.add(new MainActivity.ItemText(i + "fsadfsafdsafdsafdsafdsa\nfdsafdasfdsafdsafdsfdasf" + i));
//            }
//            return list;
//        }
//    }
//
//}
