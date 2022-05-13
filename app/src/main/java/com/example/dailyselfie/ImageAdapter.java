package com.example.dailyselfie;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class ImageAdapter extends ArrayAdapter<ImageData> {

    private ArrayList<ImageData> items;
    private Context context;
    private LayoutInflater inflater;
    public ImageAdapter(Context context) {
        super(context,R.layout.image_item);
        this.context = context;
        inflater = LayoutInflater.from(this.context);
        items = new ArrayList<>();
        /// hàm hiển thị tất cả ảnh hiện  có khi mở ứng dụng
        loadImageSystem();
    }

    private void loadImageSystem() {
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir != null) {
            File[] selfieFiles = storageDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File file, String name) {
                    return name.endsWith(".jpg");
                }
            });
            for (File file : selfieFiles) {
                ImageData item = new ImageData(file.getAbsolutePath(), file.getName());
                items.add(item);
            }
        }
    }

    public int getCount() {
        return items.size();
    }

    public ImageData getItem(int position) {
        return items.get(position);
    }

    public int getItemPosition(ImageData item) {
        return items.indexOf(item);
    }

    public void replace(ImageData new_item) {
        for(ImageData item : items){
            if(item.getPath().equals(new_item.getPath())){
                items.set(items.indexOf(item), new_item);
                return;
            }
        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View row = inflater.inflate(R.layout.image_item, null);
        CheckBox selectedImage = (CheckBox)  row.findViewById(R.id.selectedImage);
        ImageView thumbnail_view = (ImageView)  row.findViewById(R.id.thumbnail);
        TextView date = (TextView)  row.findViewById(R.id.selfie_date);
        TextView description = (TextView)  row.findViewById(R.id.selfie_description);

        // lấy item hien tai
        ImageData currentItem = items.get(position);
        selectedImage.setChecked(currentItem.isSelected());
        selectedImage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                currentItem.setSelected(isChecked);
            }
        });
        ImageAdapter.setImageFromFilePath(currentItem.getPath(), thumbnail_view);
        date.setText(currentItem.getName());
        description.setText(currentItem.getDescription());
        return  row;
    }


    public void add(ImageData selfieRecord) {
        items.add(selfieRecord);
        notifyDataSetChanged();
    }

    public ArrayList<ImageData> getAll() {
        return items;
    }

    public ArrayList<ImageData> getSelectedItem() {
        ArrayList<ImageData> mSelectedRecordList = new ArrayList<>();
        for (ImageData item : items) {
            if (item.isSelected()) {
                mSelectedRecordList.add(item);
            }
        }
        return mSelectedRecordList;
    }

    public void clearAll() {
        items.clear();
        notifyDataSetChanged();
    }

    public void clearSelected() {
        items.removeAll(getSelectedItem());
        notifyDataSetChanged();
    }

    public static void setImageFromFilePath(String imagePath, ImageView imageView, int targetW, int targetH) {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmpOptions = new BitmapFactory.Options();


        int photoW = bmpOptions.outWidth;
        int photoH = bmpOptions.outHeight;

        // determine scale factor
        int scaleFactor = Math.max(photoW / targetW, photoH / targetH);

        // decode the image file into a Bitmap
        bmpOptions.inJustDecodeBounds = false;
        bmpOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmpOptions);
        imageView.setImageBitmap(bitmap);
    }

    public static void setImageFromFilePath(String imagePath, ImageView imageView) {
        setImageFromFilePath(imagePath, imageView, 120, 160);
    }
}
