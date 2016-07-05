package free.com.itemlib;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import free.com.itemlib.item.BaseItemAdapter;
import free.com.itemlib.item.animation.SlideInBottomAnimation;
import free.com.itemlib.item.view.ItemViewHolder;
import free.com.itemlib.item.view.content.Item;
import free.com.itemlib.item.view.content.ItemBase;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BaseItemAdapter baseItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        baseItemAdapter = new BaseItemAdapter(this);

        TextView headView = new TextView(this);
        headView.setText("111111111111111111111");
        headView.setHeight(100);
        headView.setBackgroundColor(0xFF777777);

        TextView headView1 = new TextView(this);
        headView1.setText("22222222222222222");
        headView1.setHeight(100);
        headView1.setBackgroundColor(0xFF777777);

        TextView headView2 = new TextView(this);
        headView2.setText("333333333333333333333");
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
        baseItemAdapter.addFootView(footView);
        baseItemAdapter.openLoadAnimation(new SlideInBottomAnimation(), false);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(baseItemAdapter);
//        baseItemAdapter.notifyDataSetChanged();
    }

    private List<Item> getItemList() {
        List<Item> list = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            if (i % 5 == 0) {
                list.add(new ItemText(i + "fdsagsagagsaggasg" + i) {
                    @Override
                    public boolean isFullSpan() {
                        return true;
                    }
                });
            }
            list.add(new ItemText(i + "fdsagsagagsaggasg" + i));
        }
        return list;
    }

    static int i;

    class ItemText extends ItemBase {
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
            return new ItemTextView(context, this);
        }

        class ItemTextView extends ItemViewHolder<ItemText> {

            public ItemTextView(Context context, ItemText item) {
                super(context, item);
            }

            @Override
            protected View initItemView() {
                TextView textView = new TextView(context);
                textView.setText("111111111111111111111");
                textView.setHeight(100);
                textView.setBackgroundColor(0xFFBBBBBB);
                return textView;
            }

            @Override
            public void setData(ItemText itemContent) {
                ((TextView) itemView).setText(itemContent.value);
                if (isFullSpan()) {
                    itemView.setBackgroundColor(0xFFDD66CC);
                    ((TextView) itemView).setHeight(60);
                }
            }
        }

    }

}
