package com.teraime.chesstrainer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

public class ScoreKeeper implements GameWidget {

    final RectF box;
    final int offset,radius;
    Bitmap glassBall;
    Paint p = new Paint();
    Paint pR = new Paint();
    private int value=0;
    private PointF center = new PointF();
    private Path segment = new Path();

    int width,height;
    public ScoreKeeper(Context ctx, int _offset, int _width, int _height) {
        width = _width;
        height = _height;
        box = new RectF(0,0,_height,_height);
        center.x = _width/ 2;
        center.y = _height / 2;
        radius = _height / 2;
        offset=_offset;
        glassBall = BitmapFactory.decodeResource(ctx.getResources(),R.drawable.glassball4);
        Log.d("box ",box.width()+" off "+offset);

        pR.setColor(Color.GREEN);
    }

    private void setPaths()
    {
        float y = center.y + radius - (2 * radius * value / 100 - 1);
        float x = center.x - (float) Math.sqrt(Math.pow(radius, 2) - Math.pow(y - center.y, 2));

        float angle = (float) Math.toDegrees(Math.atan((center.y - y) / (x - center.x)));
        float startAngle = 180 - angle;
        float sweepAngle = 2 * angle - 180;

        segment.rewind();
        segment.addArc(box, startAngle, sweepAngle);
        segment.close();
    }

    public void draw(Canvas c) {
        box.offsetTo(width/3,0);
        c.drawPath(segment, pR);
        c.drawBitmap(glassBall,null,box,p);

    }

    @Override
    public String getName() {
        return "orb";
    }

    public void setFillValue(int newValueAsPercent) {
        this.value = newValueAsPercent;
        setPaths();
    }
}
