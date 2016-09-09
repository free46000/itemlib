package itemlib.free.com.itemlib;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import free.com.itemlib.item.BaseItemAdapter;
import free.com.itemlib.item.view.ItemViewHolder;
import free.com.itemlib.item.view.content.ItemBase;
import free.com.itemlib.item.view.content.ItemImpl;
import free.com.itemlib.item.view.content.ItemInput;
import free.com.itemlib.item.view.content.ItemSimple;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        BaseItemAdapter adapter = new BaseItemAdapter(this);
        adapter.addDataItem(new ItemText(), new ItemText(), new ItemText());
        recyclerView.setAdapter(adapter);

//        startActivity(new Intent(this, free.com.itemlib.MainActivity.class));




    }

    class DisplayItem extends ItemImpl {
        private String name;

        public DisplayItem(String name) {
            this.name = name;
        }

        @Override
        public ItemViewHolder newItemViewHolder(Context context, ViewGroup parent) {
            return null;
        }
    }

    class ItemText extends ItemBase {

        @Override
        public View initItemView(Context context, ViewGroup viewGroup) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_text, viewGroup,false);
            return view;
        }

        @Override
        public void fillData(View itemView) {
            ((TextView) getView(itemView, R.id.textView)).setText("asfada" +
                    "sfdasfdsafadsfadsfdasfsafdasfadsfdasfdasfasdfdsafdsafdsafdsafdasfdsafdsafsdasfas"+
                    "sfdasfdsafadsfadsfdasfsafdasfadsfdasfdasfasdfdsafdsafdsafdsafdasfdsafdsafsdasfas"+
                    "sfdasfdsafadsfadsfdasfsafdasfadsfdasfdasfasdfdsafdsafdsafdsafdasfdsafdsafsdasfas"+
                    "sfdasfdsafadsfadsfdasfsafdasfadsfdasfdasfasdfdsafdsafdsafdsafdasfdsafdsafsdasfas");
        }
    }

}
