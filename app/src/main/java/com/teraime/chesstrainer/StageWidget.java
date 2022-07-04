package com.teraime.chesstrainer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

public class StageWidget implements GameWidget, View.OnTouchListener {

    private final int width,height;
    private final Rect box;
    private final int offset,squareSize;
    private final int top,left,right,bottom;
    private final Paint p,p2,p4,pT,pf,pO,p6;
    private Paint p3,p7;
    private final Bitmap stageBmp,oneBmp,twoBmp,treBmp,fyrBmp,femBmp,sexBmp,sjuBmp,eitBmp,nioBmp,zeroBmp;
    private final Rect txtRect,numRectOne,numRectTwo,numRectThree;
    private final RectF stageRectO1,stageRectO2,stageRectI2;
    private final BitmapShader dk_shade,lt_shade;
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
        pf = new Paint();
        pO = new Paint();
        p6 = new Paint();

        //outer outer rect
        pO.setColor(Color.BLACK);
        pO.setAlpha(50);

        //button outline
        p.setStyle(Paint.Style.STROKE);
        p.setColor(ContextCompat.getColor(ctx, R.color.lt_silver));
        p.setStrokeWidth(15);
        p.setAntiAlias(true);

        //shadow fill
        pf.setStyle(Paint.Style.FILL);
        //pf.setColor( ContextCompat.getColor(ctx, R.color.dk_green));
        pf.setColor(Color.BLACK);
        pf.setAntiAlias(true);

        //fill rect
        p3.setStyle(Paint.Style.FILL);
        p6.setStyle(Paint.Style.FILL);

        //rect shadow
        p2.setColor(ContextCompat.getColor(ctx, R.color.dk_gray));
        p2.setStyle(Paint.Style.FILL);
        p2.setMaskFilter(new BlurMaskFilter(
                5 /* shadowRadius */,
                BlurMaskFilter.Blur.NORMAL));

        //text
        pT = new Paint();
        pT.setColor(Color.BLACK);
        pT.setStyle(Paint.Style.FILL);
        pT.setTextAlign(Paint.Align.CENTER);
        pT.setTextSize(80);
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

        Bitmap bitmapl = BitmapFactory.decodeResource(ctx.getResources(),R.drawable.glassball3);
        dk_shade = new BitmapShader(bitmapl, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        p3.setShader(dk_shade);
        Bitmap bitmapd = BitmapFactory.decodeResource(ctx.getResources(),R.drawable.white_square_wood);
        lt_shade = new BitmapShader(bitmapl, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);


        stageRectO1 = new RectF(0,0,width,height);
        stageRectO2 = new RectF(left,top,right+10,bottom+5);
        stageRectI2 = new RectF(left,top,right-10,bottom-20);
        //stageRectI2 = new RectF(left,top,right-10,bottom-20);

        txtRect = new Rect((int)(left+left*.04f),(int)(top+squareSize/3f),(int)(right-right*.25f),(int)(bottom-squareSize/3f));
        numRectOne = new Rect((int)(int)(right-right*.22f),(int)(top+squareSize/3f),(int)(right-right*.15f),(int)(bottom-squareSize/3f));
        numRectTwo = new Rect((int)(int)(right-right*.15f),(int)(top+squareSize/3f),(int)(right-right*.08f),(int)(bottom-squareSize/3f));
        numRectThree = new Rect((int)(int)(right-right*.08f),(int)(top+squareSize/3f),(int)(right-right*.01f),(int)(bottom-squareSize/3f));

        GameContext.gc.registerClickListener(this);

    }

    boolean scaleB = false;
    boolean save = false;
    @Override
    public void draw(Canvas c) {
        c.drawDoubleRoundRect(stageRectO1,0,0,stageRectO2,25,25,pO);
        if (scaleB) {
            c.save();
            c.scale(0.9f, 0.9f);
            c.translate(0.055f*width,.055f*height);
            save=true;
        }
        c.drawDoubleRoundRect(stageRectO2,25,25,stageRectI2,25,25,p2);
        c.drawRoundRect(stageRectI2,25,25,p);
        c.drawRoundRect(stageRectI2,25,25,p3);
        //c.drawRect(left + offsetX, top + offsetY, right + offsetX, bottom + offsetY, p2);
        c.drawBitmap(stageBmp,null,txtRect,p4);
        //c.drawText("STAGE 1",width/2,height/2,pT);
        int std=stage;
        int stdH = std/100;
        c.drawBitmap(getNumberBmp(stdH), null, numRectOne, p4);
        if (stdH>0)
            std = (std - stdH*100);
        int stdT = std/10;
        c.drawBitmap(getNumberBmp(stdT), null, numRectTwo, p4);
        if (stdT>0)
            std = (std - 10*stdT);
        c.drawBitmap(getNumberBmp(std),null,numRectThree,p4);
        if (save) {
            save = false;
            c.restore();
        }
    }

    @Override
    public String getName() {
        return "stage";
    }

    Bitmap getNumberBmp(int number) {
        Bitmap[] bmpA = new Bitmap[]{zeroBmp,oneBmp,twoBmp,treBmp,fyrBmp,femBmp,sexBmp,sjuBmp,eitBmp,nioBmp};
        return bmpA[number];
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d("v","event "+event.getAction());
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                if (stageRectO2.contains(event.getX(),event.getY()-offset)) {
                    Log.d("v","down!");
                    p3.setShader(lt_shade);
                    scaleB=true;
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d("v","up!");
                p3.setShader(dk_shade);
                scaleB=false;
                if (stageRectO2.contains(event.getX(),event.getY()-offset)) {
                    Log.d("v","clock!");

                }
                break;

        }

        return true;
    }
}
