package com.hw.autotest.storage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hw.autotest.R;

public class StorageTestList extends Activity  implements OnItemClickListener{
    
    private ListView myListView = null;
    private TextView titleTv = null;
    private String[] mStrings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storagetestlist);
        myListView = (ListView) findViewById(R.id.list);
        titleTv = (TextView) findViewById(R.id.title_textview);
        titleTv.setText(getString(R.string.title_main));
        mStrings = getResources().getStringArray(R.array.storage_test_array);
        myListView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mStrings));
        myListView.setOnItemClickListener(this);
    }
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        //StartActivity here
        switch(arg2) {
        case 0:
            doActivity(FlashUserData.class);
            break;
        case 1:
            break;
        case 2:
            break;
        }
    }
    
    private void doActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }
}
