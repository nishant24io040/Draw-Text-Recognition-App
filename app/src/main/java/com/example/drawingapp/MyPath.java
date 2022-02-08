package com.example.drawingapp;

import android.graphics.Path;

public class MyPath extends Path {
    public int color;
    public int strokeWidth;
    public Path path;

    public MyPath(int color, int strokeWidth, Path path) {
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.path = path;
    }
}
