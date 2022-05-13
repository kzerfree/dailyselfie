package com.example.dailyselfie;

import android.media.Image;

import java.io.Serializable;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageData implements Serializable {
    private String path;
    private String name;
    private String description;
    private boolean isSelected;

    public ImageData(String path, String name) {
        this.path = path;
        this.name = name;
        isSelected = false;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Date date = format.parse(name, new ParsePosition(0));
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(date);
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }



}
