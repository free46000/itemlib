package free.com.itemlib;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import free.com.itemlib.item.ListItemAdapter;
import free.com.itemlib.item.ListItemInputAdapter;
import free.com.itemlib.item.ListItemInputAdapterBeta;
import free.com.itemlib.item.view.content.ItemInput;

public class InputActivity extends Activity {
    private ListView listView;
    private ListItemInputAdapter adapter;
    private ListItemInputAdapterBeta adapterBeta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        listView = (ListView) findViewById(R.id.listView);
        adapter = new ListItemInputAdapter(this);
        adapterBeta = new ListItemInputAdapterBeta(this);
        listView.setAdapter(adapter);

addItem(adapter);
addItem(adapterBeta);

        findViewById(R.id.saveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("adapter:" + adapter.getInputValueMap());
                System.out.println("adapter beta:" + adapterBeta.getInputValueMap());
                addItem(adapter);
                addItem(adapterBeta);
            }
        });
    }
    static int index;
    private void addItem(ListItemAdapter adapter) {
        for (int i = 0; i < 5; i++) {
            adapter.addDataItem(new ItemEdit(++index + ""));
        }
        adapter.notifyDataSetChanged();
    }

    class ItemEdit extends ItemInput {

        /**
         * @param key 本录入View的值对应的存储key
         */
        public ItemEdit(String key) {
            super(key);
        }

        @Override
        public Object getValue(View itemView) {
            return ((EditText) itemView).getText();
        }

        @Override
        public View initItemView(Context context, ViewGroup viewGroup) {
            return new EditText(context);
        }
    }


}
