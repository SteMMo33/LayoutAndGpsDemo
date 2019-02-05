package com.example.stefanomora.layoutandgpsdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.view.View;

/**
 * Created by stefano.mora on 09/01/2018.
 */

class DemoMapOverlay extends View {

    //public ArrayList<GeoPoint> points;
    Context mContext;

    public DemoMapOverlay(Context context){
        super(context);
        mContext = context;
        //points  = new ArrayList<GeoPoint>();
    }

    public void addPoint(Point point){
        //points.add(point);
    }

    private static void makePath(Path p) {
        p.moveTo(10, 0);
        p.cubicTo(100, -50, 200, 150, 300, 0);
    }


    //public void draw(Canvas canvas, MapView mapv, boolean shadow){
    public void draw(Canvas canvas){
        // super.draw(canvas, mapv, shadow);
        super.draw(canvas);

        Paint mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setColor(Color.BLUE);
        mPaint.setAlpha(200);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(2);

        /*
        Path path = new Path();
        Projection projection = mapv.getProjection();

        for (int i=0; i<points.size()-1; i++){
            Point p1 = new Point();
            Point p2 = new Point();

            projection.toPixels((GeoPoint)points.get(i),p1);
            projection.toPixels((GeoPoint)points.get(i+1),p2);

            path.moveTo(p2.x, p2.y);
            path.lineTo(p1.x,p1.y);
        }

        canvas.drawPath(path, mPaint);
        */

        canvas.drawColor(Color.CYAN);
        Paint p = new Paint();
        // smooths
        p.setAntiAlias(true);
        p.setColor(Color.RED);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(5);
        // opacity
        //p.setAlpha(0x80);

        p.setColor(Color.BLACK);
        Path mPath = new Path();
        makePath(mPath);
        canvas.drawPath(mPath, p); //(rectF, 90, 45, true, p);
    }

}
