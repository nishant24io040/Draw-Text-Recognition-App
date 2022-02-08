package com.example.drawingapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
import java.util.ArrayList;

public class Mycanvas extends View {
    private float mX, mY;
    private Path mPath;
    private Paint mPaint;
    private ArrayList<MyPath> paths = new ArrayList<>();
    private ArrayList<MyPath> undoPaths = new ArrayList<>();
    private int brushColor;
    private int backgroundColor;
    private int brushSize;
    private float touchTolerance;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    private Changer changer;

    public Mycanvas(Context context) {
        this(context, null);
    }

    public Mycanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.MITER);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xff);
        brushColor = Color.BLACK;
        backgroundColor = Color.WHITE ;
        brushSize = 40;
        touchTolerance = 4;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    public void setColor(int color){
        brushColor = color;
    }

    public void clearAll(){
        paths.clear();
        invalidate();
    }

    public Bitmap NeedBitmap(){
        return mBitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        mCanvas.drawColor(backgroundColor);

        for(MyPath drawingPath:paths){
            mPaint.setColor(drawingPath.color);
            mPaint.setStrokeWidth(drawingPath.strokeWidth);
            mCanvas.drawPath(drawingPath.path,mPaint);
        }

        canvas.drawBitmap(mBitmap,0,0,mBitmapPaint);
        canvas.restore();
    }

    public void startTouch(float x, float y){
        mPath = new Path();
        MyPath drawingPath = new MyPath(brushColor,brushSize,mPath);
        paths.add(drawingPath);
        mPath.reset();
        mPath.moveTo(x,y);
        mX = x;
        mY = y;
        invalidate();
    }

    private void touchMove(float x, float y){
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if(dx >= touchTolerance || dy >= touchTolerance){
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
    }

    private void touchUp(){
        mPath.lineTo(mX, mY);
    }


    public void undo(){
        if(paths.size() > 0){
            undoPaths.add(paths.remove(paths.size()-1));
            invalidate();
        }
    }

    public void redo(){
        if(undoPaths.size() > 0){
            paths.add(undoPaths.remove(undoPaths.size()-1));
            invalidate();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                startTouch(x,y);
                if(changer != null){
                    changer.onTouchStart(x,y);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x,y);
                if(changer != null){
                    changer.onDrawingChange(x,y);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                if(changer != null){
                    changer.onDrawingChange(x,y);
                }
                invalidate();
                break;
        }
        return true;
    }
}