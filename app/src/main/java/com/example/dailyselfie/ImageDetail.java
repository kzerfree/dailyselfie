package com.example.dailyselfie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


public class ImageDetail extends Activity {
    private ImageData item;
    private ImageView view;
    private EditText edit_des;
    private Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_detail);

        Intent intent = getIntent();
        item = (ImageData) intent.getExtras().getSerializable("item");
//        --------------------------------------------------------
        view = (ImageView) findViewById(R.id.imageView);
        edit_des = (EditText) findViewById(R.id.description_detail);
        save = (Button) findViewById(R.id.save_infor);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // lưu data
                item.setDescription(edit_des.getText().toString());
                Log.i("test3", item.getPath() + " - " + item.getDescription());
                Intent intent = getIntent();
                intent.putExtra("data",item);
                setResult(MainActivity.RESULT_CODE_SAVE, intent);
                finish();
            }
        });
        //        --------------------------------------------------------


        edit_des.setText(item.getDescription());
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ImageAdapter.setImageFromFilePath(item.getPath(), view, view.getWidth(), view.getHeight());
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

}
