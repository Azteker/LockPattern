package com.example.yxzeng.lockpattern;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: document your custom view class.
 */
public class GestureLock extends View {

    private Point[][] points = new Point[3][3];
    private boolean inited = false;

    private boolean isDraw = false;
    private ArrayList<Point> pointlist = new ArrayList<Point>();
    private ArrayList<Integer> passlist = new ArrayList<Integer>();

    private Bitmap bitmapPointError;
    private Bitmap bitmapPointNormal;
    private Bitmap bitmapPointPress;

    private OnDrawFinishedListener Listener;

    float mouseX, mouseY;

    private float bitmapR;

    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint pressPaint = new Paint();
    Paint errorPaint = new Paint();

    public GestureLock(Context context) {
        super(context);
    }

    public GestureLock(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GestureLock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mouseX = event.getX();
        mouseY = event.getY();
        int[] ij;

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                resetPoints();
                ij = getSelectedPoint();
                if(ij!=null){
                    isDraw = true;
                    int i = ij[0];
                    int j = ij[1];
                    points[i][j].state = Point.STATE_PRESS;
                    pointlist.add(points[i][j]);
                    passlist.add(i*3+j);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(isDraw){
                    ij = getSelectedPoint();
                    if(ij!=null){
                        int i = ij[0];
                        int j = ij[1];
                        if(!pointlist.contains(points[i][j])) {
                            points[i][j].state = Point.STATE_PRESS;
                            pointlist.add(points[i][j]);
                            passlist.add(i*3+j);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                boolean valid = false;
                if(Listener!=null && isDraw){
                    valid = Listener.OnDrawFinished(passlist);
                }
                if(!valid){
                    for(Point p:pointlist)
                        p.state = Point.STATE_ERROR;
                }
                isDraw = false;
        }
        this.postInvalidate();
        return true;
    }

    private  int[] getSelectedPoint(){
        Point pMouse = new Point(mouseX, mouseY);
        for(int i=0;i<3;i++)
            for(int j=0;j<3;j++){
                if(points[i][j].distance(pMouse)<bitmapR){
                    int[] result = new int[2];
                    result[0] = i;
                    result[1] = j;
                    return result;
                }
            }
        return null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!inited){
            init();
        }

        drawPoint(canvas);
        if(!pointlist.isEmpty()){
            Point a = pointlist.get(0);
            for(int i=1;i<pointlist.size();i++){
                Point b = pointlist.get(i);
                drawLine(canvas, a, b);
                a = b;
            }
            if(isDraw){
                drawLine(canvas, a, new Point(mouseX, mouseY));
            }
        }

    }

    private void drawLine(Canvas canvas, Point a, Point b){
            if(a.state == Point.STATE_PRESS){
                canvas.drawLine(a.x, a.y, b.x, b.y, pressPaint);
            }else if(a.state == Point.STATE_ERROR){
                canvas.drawLine(a.x, a.y, b.x, b.y, errorPaint);
            }
    }

    private void drawPoint(Canvas canvas){
        for(int i=0;i<3;i++)
            for(int j=0;j<3;j++){
                switch (points[i][j].state){
                    case 0:
                        canvas.drawBitmap(bitmapPointNormal, points[i][j].x-bitmapR, points[i][j].y-bitmapR, paint);
                        break;
                    case 1:
                        canvas.drawBitmap(bitmapPointPress, points[i][j].x-bitmapR, points[i][j].y-bitmapR, paint);
                        break;
                    case 2:
                        canvas.drawBitmap(bitmapPointError, points[i][j].x-bitmapR, points[i][j].y-bitmapR, paint);
                }
            }
    }

    private void init(){
        pressPaint.setColor(Color.YELLOW);
        pressPaint.setStrokeWidth(5);

        errorPaint.setColor(Color.RED);
        errorPaint.setStrokeWidth(5);

        bitmapPointError = BitmapFactory.decodeResource(getResources(), R.drawable.error);
        bitmapPointPress = BitmapFactory.decodeResource(getResources(), R.drawable.press);
        bitmapPointNormal = BitmapFactory.decodeResource(getResources(), R.drawable.normal);

        bitmapR = bitmapPointError.getHeight()/2;
        int width = getWidth();
        int height = getHeight();
        int offset = Math.abs(width-height)/2;
        int offsetX, offsetY;
        int space;
        if(width>height){
            space = height/4;
            offsetX = offset;
            offsetY = 0;
        }else{
            space = width/4;
            offsetX = 0;
            offsetY = offset;
        }

        points[0][0] = new Point(offsetX+space,offsetY+space);
        points[0][1] = new Point(offsetX+2*space, offsetY+space);
        points[0][2] = new Point(offsetX+3*space, offsetY+space);
        points[1][0] = new Point(offsetX+space,offsetY+2*space);
        points[1][1] = new Point(offsetX+2*space,offsetY+2*space);
        points[1][2] = new Point(offsetX+3*space,offsetY+2*space);
        points[2][0] = new Point(offsetX+space,offsetY+3*space);
        points[2][1] = new Point(offsetX+2*space,offsetY+3*space);
        points[2][2] = new Point(offsetX+3*space,offsetY+3*space);

        inited = true;
    }

    public void resetPoints(){
        passlist.clear();
        pointlist.clear();
        for(int i=0;i<3;i++)
            for(int j=0;j<3;j++){
                points[i][j].state = Point.STATE_NORMAL;
            }
        this.postInvalidate();
    }

    public interface OnDrawFinishedListener{
        boolean OnDrawFinished(List<Integer> passList);
    }

    public void setOnDrawFinishedListener(OnDrawFinishedListener listener){
        this.Listener = listener;
    }
}
