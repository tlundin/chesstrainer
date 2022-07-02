package com.teraime.chesstrainer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

public class StageWidget implements GameWidget {

    private final int width,height;
    private final Rect box;
    private final int offset,squareSize;
    private final int top,left,right,bottom;
    private final Paint p,p2,p3,p4,pT;
    private final Bitmap stageBmp,oneBmp,twoBmp,treBmp,fyrBmp,femBmp,sexBmp,sjuBmp,eitBmp,nioBmp,zeroBmp;
    private final Rect stageRect, txtRect,numRectOne,numRectTwo,numRectThree;
    int offsetX = 10;
    int offsetY = 10;
    int stage;
    public StageWidget(Context ctx, int _offset, int _width, int _height, int squareSize, int stage) {
        this.stage=stage;
        width = _width;
        height = _height;
        box = new Rect(0,0,_width,_height);
        offset=_offset;
        this.squareSize = squareSize;
        left = squareSize*2;
        top = squareSize*3;
        right = left+squareSize*4;
        bottom = top+squareSize*2;

        p = new Paint();
        p2 = new Paint();
        p3 = new Paint();

        //background
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        p.setColor(Color.WHITE);
        p.setAlpha(200);

        //rect
        p3.setColor(Color.GRAY);
        p3.setStrokeWidth(5);
        p3.setStyle(Paint.Style.STROKE);

        //rect shadow
        p2.setColor(Color.BLACK);
        p2.setStyle(Paint.Style.STROKE);
        p2.setMaskFilter(new BlurMaskFilter(
                3 /* shadowRadius */,
                BlurMaskFilter.Blur.NORMAL));

        //text
        pT = new Paint();
        pT.setColor(Color.BLACK);
        pT.setStyle(Paint.Style.FILL);
        pT.setTextAlign(Paint.Align.CENTER);
        pT.setTextSize(60);
        pT.setFakeBoldText(true);

        //bmp
        p4 = new Paint();

        stageBmp = BitmapFactory.decodeResource(ctx.getResources(),R.drawable.stage);
        oneBmp = BitmapFactory.decodeResource(ctx.getResources(),R.drawable.one);
        twoBmp = BitmapFactory.decodeResource(ctx.getResources(),R.drawable.two);
        treBmp = BitmapFactory.decodeResource(ctx.getResources(),R.drawable.tre);
        fyrBmp = BitmapFactory.decodeResource(ctx.getResources(),R.drawable.fyr);
        femBmp = BitmapFactory.decodeResource(ctx.getResources(),R.drawable.fem);
        sexBmp = BitmapFactory.decodeResource(ctx.getResources(),R.drawable.sex);
        sjuBmp = BitmapFactory.decodeResource(ctx.getResources(),R.drawable.sju);
        eitBmp = BitmapFactory.decodeResource(ctx.getResources(),R.drawable.eit);
        nioBmp = BitmapFactory.decodeResource(ctx.getResources(),R.drawable.nio);
        zeroBmp = BitmapFactory.decodeResource(ctx.getResources(),R.drawable.zero);
        stageRect = new Rect(left,top,right,bottom);
        txtRect = new Rect((int)(left+left*.04f),(int)(top+squareSize/3f),(int)(right-right*.25f),(int)(bottom-squareSize/3f));
        numRectOne = new Rect((int)(int)(right-right*.22f),(int)(top+squareSize/3f),(int)(right-right*.15f),(int)(bottom-squareSize/3f));
        numRectTwo = new Rect((int)(int)(right-right*.15f),(int)(top+squareSize/3f),(int)(right-right*.08f),(int)(bottom-squareSize/3f));
        numRectThree = new Rect((int)(int)(right-right*.08f),(int)(top+squareSize/3f),(int)(right-right*.01f),(int)(bottom-squareSize/3f));

    }

    @Override
    public void draw(Canvas c) {
        c.drawRect(stageRect,p);
        c.drawRect(stageRect,p3);
        c.drawRect(left + offsetX, top + offsetY, right + offsetX, bottom + offsetY, p2);
        c.drawBitmap(stageBmp,null,txtRect,p4);
        int std=stage;
        int stdH = std/100;
        if (stdH>0) {
            c.drawBitmap(getNumberBmp(stdH), null, numRectOne, p4);
            std = (std - stdH*100);
        }
        int stdT = std/10;
        if (stdT>0) {
            c.drawBitmap(getNumberBmp(stdT), null, numRectTwo, p4);
            std = (std - 10*stdT);
        }
        c.drawBitmap(getNumberBmp(std),null,numRectThree,p4);
    }

    @Override
    public String getName() {
        return "stage";
    }

    Bitmap getNumberBmp(int number) {
        Bitmap[] bmpA = new Bitmap[]{zeroBmp,oneBmp,twoBmp,treBmp,fyrBmp,femBmp,sexBmp,sjuBmp,eitBmp,nioBmp};
        return bmpA[number];
    }
}
