package com.example.sampleads.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sampleads.R;

public class ProductListActivity extends AppCompatActivity {

    String[] product;
    ListView listView_product;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        product = getResources().getStringArray(R.array.products);

        listView_product = (ListView) findViewById(R.id.listview_product);

        listView_product.setAdapter(new ArrayAdapter<String>(ProductListActivity.this,R.layout.single_item_list,R.id.textview,product));

        listView_product.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                String item = (String) adapterView.getItemAtPosition(i);


                Intent intent = new Intent(ProductListActivity.this,PostAdActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("item",item);
                startActivity(intent);
            }
        });
    }
}
