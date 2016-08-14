package free.com.itemlib;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import free.com.itemlib.item.BaseItemAdapter;
import free.com.itemlib.item.decoration.HorizontalItemDecoration;
import free.com.itemlib.item.listener.OnLoadMoreListener;
import free.com.itemlib.item.view.ItemViewHolder;
import free.com.itemlib.item.view.content.Item;
import free.com.itemlib.item.view.content.ItemImpl;

/**
just for test  two 
*/
public class MainActivity extends Activity {
    private RecyclerView recyclerView;
    private BaseItemAdapter baseItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        baseItemAdapter = new BaseItemAdapter(this);
        TextView headView = new TextView(this);
        headView.setText("11\n11111\n11111\n11111\n111\n1fasjfjdkasfj");
        headView.setHeight(100);
        headView.setBackgroundColor(0xFF777777);


        TextView headView1 = new TextView(this);
        headView1.setText("22\n22\n22222\n22222\n222");
        headView1.setHeight(100);
        headView1.setBackgroundColor(0xFF777777);

        TextView headView2 = new TextView(this);
        headView2.setText("333\n33333\n33333\n333\n33333");
        headView2.setHeight(100);
        headView2.setBackgroundColor(0xFF777777);

        TextView headView3 = new TextView(this);
        headView3.setText("4444444444444444444444");
        headView3.setHeight(100);
        headView3.setBackgroundColor(0xFF777777);

        TextView footView = new TextView(this);
        footView.setText("111111111111111111111");
        footView.setHeight(100);
        footView.setBackgroundColor(0xFF777777);


        baseItemAdapter.setDataItemList(getItemList());
//        baseItemAdapter.addHeadView(headView, headView1, headView2, headView3);
//        baseItemAdapter.addFootView(footView);
        baseItemAdapter.addLoadMoreView(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                baseItemAdapter.addDataItem(new ItemText
                        ("QQQQQ"));
                baseItemAdapter.addDataItem(new ItemText
                        ("WWWWW"));
                baseItemAdapter.addDataItem(new ItemText
                        ("EEEEE"));
                baseItemAdapter.setLoadComplete(false);
//                new Handler().post(new Runnable() {
//                    @Override
//                    public void run() {
//                        baseItemAdapter.notifyDataSetChanged();
//
//                    }
//                });

            }
        }, false);
//        baseItemAdapter.openLoadAnimation(new SlideInBottomAnimation(), false);
//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(baseItemAdapter);
        recyclerView.addItemDecoration(new HorizontalItemDecoration(this, R.drawable.list_divider));
    }

    private List<Item> getItemList() {
        List<Item> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            if (i % 5 == 0) {
                list.add(new ItemText(i + "fdsagsaadfdasfasfdasfdasfdasfdgagsaggasg" + i) {
                    @Override
                    public boolean isFullSpan() {
                        return true;
                    }
                });
            }
            list.add(new ItemText(i + "fsadfsafdsafdsafdsafdsafdsafdasfdsafdsafdsfdasf" + i));
        }
        return list;
    }

    static int i;

    class ItemText extends ItemImpl {
        private String value;

        public ItemText(String value) {
            this.value = value;
            i++;
        }

//        @Override
//        public boolean isFullSpan() {
//            return i % 5 == 0;
//        }

        @Override
        public ItemViewHolder newItemViewHolder(Context context, ViewGroup parent) {
            return new ItemTextView(context, this, parent);
        }

        class ItemTextView extends ItemViewHolder<ItemText> {
            TextView textView;


            public ItemTextView(Context context, ItemText item, ViewGroup viewGroup) {
                super(context, item, viewGroup);
            }

            @Override
            protected View initItemView() {
                View view = LayoutInflater.from(context).inflate(R.layout.item_text, parentViewGroup, false);
                textView = (TextView) view.findViewById(R.id.text);
                return view;
            }

            @Override
            public void setData(ItemText itemContent) {
                textView.setText(itemContent.value);
                if (isFullSpan()) {
//                    itemView.setBackgroundColor(0xFFDD66CC);
                    textView.setHeight(60);
                }
            }
        }

    }

}
