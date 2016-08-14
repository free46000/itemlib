package free.com.itemlib.item;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import free.com.itemlib.R;


/**
 * Created by free46000 on 2015/6/6 0006.
 */
public class ItemInputFragment extends Fragment {
    private ListItemInputAdapter adapter;
    private ItemEntity inputEntity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ListView listView = new ListView(getActivity());
        listView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        adapter = new ListItemInputAdapter(getActivity().getApplicationContext());
        listView.setAdapter(adapter);
        return listView;

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void setInputEntity(ItemEntity inputEntity) {
        this.inputEntity = inputEntity;
        if (inputEntity != null) {
            adapter.setDataItemEntity(inputEntity);
        }
    }

    public ItemEntity fillInputEntity() {
        inputEntity.fillSelf(adapter.getInputValueMap());
        return inputEntity;
    }

    public boolean isValueChanged() {
        return adapter.isValueChanged();
    }

    public boolean isValueValidate() {
        return adapter.isValueValidate();
    }

    public ListItemInputAdapter getAdapter() {
        return adapter;
    }

}
