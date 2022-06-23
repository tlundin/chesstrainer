package com.teraime.chesstrainer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Looper;

import androidx.core.content.ContextCompat;

public class Progressor {

    enum Difficulty {
        normal,
        hard,
        nightmare
    }

    Paint pB = new Paint();
    Paint pR = new Paint();
    Paint pL = new Paint();
    Paint pT = new Paint();
    //frame
    Paint pFrameGold = new Paint();
    Paint pFrameSilver = new Paint();
    Paint pBlackText = new Paint();
    Paint pWhiteText = new Paint();

    int offset,width,height,centre;
    int pStartOffsetX, ballRadius, skullSize;
    int boardMargin=5;
    int currentLevel=1;
    int scrollFactor=0;int textSize;
    float pointRectBorderW;
    final Handler animationHandler = new Handler(Looper.myLooper());
    final SurfaceHolderCallback surfer;
    final Rect diffRect,goalRect,starRect;
    RectF rectouter, rectinner;
    final Bitmap skull,grimreaper,goalflag,knight,golden_knight;
    private Paint gradientP;


    public Progressor(Context ctx, int _offset, int _width, int _height, SurfaceHolderCallback _surfer) {
        surfer=_surfer;
        offset=_offset;
        width=_width; height = _height;
        ballRadius = height/4;
        int pointRectW = ballRadius * 2 + ballRadius / 2, pointRectH = ballRadius;
        skullSize = ballRadius*2;
        centre=height/2-boardMargin;
        pStartOffsetX = width/4-pointRectW/2;
        pR.setStyle(Paint.Style.STROKE);
        pR.setColor(Color.WHITE);
        pFrameGold.setStyle(Paint.Style.FILL);
        pFrameGold.setColor(Color.parseColor("#C0C0C0"));
        pFrameGold.setStrokeWidth(5);
        pB.setStyle(Paint.Style.FILL);
        pB.setColor(Color.WHITE);
        pT.setStyle(Paint.Style.FILL);
        pT.setColor(Color.GREEN);
        pL.setStyle(Paint.Style.STROKE);
        gradientP = drawGradient();
        //PathEffect effects = new DashPathEffect(new float[]{15,8,15,8},0);
        //pL.setPathEffect(effects);
        pL.setColor(Color.WHITE);
        pL.setStrokeWidth(5);
        textSize = ballRadius/2;
        pBlackText.setTextAlign(Paint.Align.CENTER);
        pBlackText.setTextSize(textSize);
        pBlackText.setFakeBoldText(true);
        pBlackText.setAntiAlias(true);
        pBlackText.setFilterBitmap(true);
        pBlackText.setDither(true);

        pointRectBorderW = ballRadius * .10f;
        rectouter = new RectF(0, 0, pointRectW, pointRectH);
        rectinner = new RectF(pointRectBorderW, pointRectBorderW, pointRectW - pointRectBorderW, pointRectH - pointRectBorderW*(pointRectW/pointRectH));

        Shader goldShade = new LinearGradient(0,
                0,
                0,
                rectouter.height(),
                new int[] {
                        ContextCompat.getColor(ctx,R.color.lt_gold),
                        ContextCompat.getColor(ctx,R.color.dk_gold),
                         },
                null,
                Shader.TileMode.REPEAT);
        pFrameGold.setShader(goldShade);
        Shader silverShade = new LinearGradient(0,
                0,
                0,
                rectouter.height(),
                new int[] {
                        ContextCompat.getColor(ctx,R.color.dk_silver),
                        ContextCompat.getColor(ctx,R.color.lt_silver),
                },
                null,
                Shader.TileMode.REPEAT);
        pFrameGold.setShader(goldShade);
        pFrameSilver.setShader(silverShade);

        pWhiteText.setColor(Color.WHITE);
        pWhiteText.setTextAlign(Paint.Align.CENTER);
        pWhiteText.setTextSize(textSize);
        pWhiteText.setFakeBoldText(true);
        pWhiteText.setAntiAlias(true);
        pWhiteText.setFilterBitmap(true);
        pWhiteText.setDither(true);
        diffRect = new Rect(0,0,skullSize,skullSize);
        goalRect = new Rect(0,0,skullSize,skullSize);
        starRect = new Rect(0,0,skullSize/3,skullSize/3);
        skull = BitmapFactory.decodeResource(ctx.getResources(),R.drawable.skull);
        grimreaper = BitmapFactory.decodeResource(ctx.getResources(),R.drawable.grimreaper);
        goalflag = BitmapFactory.decodeResource(ctx.getResources(),R.drawable.goalflag);
        knight = BitmapFactory.decodeResource(ctx.getResources(),R.drawable.fancy_knight);
        golden_knight = BitmapFactory.decodeResource(ctx.getResources(),R.drawable.golden_knight);



    }

    boolean scrollAnimate = false;
    int mStoplevel = 1;

    public void scrollAnimate(final int stepsTot, AnimationDoneListener animationDoneListener) {

        mStoplevel = stepsTot+currentLevel;

        animationHandler.post(new Runnable() {
            int levelAcc = Math.max(stepsTot/10,1);
            boolean slow = true;
            int sleepTime = 60;
            @Override
            public void run() {
                int scrollDistance = width / 4 ;
                while (currentLevel<mStoplevel) {
                    int scrollSteps = 5;
                    int scrollPerIteration = scrollDistance / scrollSteps;
                    scrollAnimate = true;
                    sleepTime = Math.max(sleepTime - 15,0);
                    while (scrollSteps > 0) {
                        scrollFactor += scrollPerIteration;
                        invalidateView();
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        scrollSteps--;

                    }
                    scrollFactor=0;
                    currentLevel= Math.min(currentLevel+levelAcc,mStoplevel);
                    int lvlDiff = mStoplevel - currentLevel;
                    if (lvlDiff <= scrollSteps ) {
                        scrollSteps = lvlDiff;
                        slow = true;
                    } else
                        slow=false;

                }
                scrollAnimate=false;
                scrollFactor=0;
                pStartOffsetX=0;
                animationDoneListener.onAnimationDone();
            }
        });
    }

    private void invalidateView() {
        surfer.surfaceChanged();
    }

    private void drawLevel(Canvas c, int level,int stars, int left, int top) {

        rectouter.offsetTo(left,top);
        rectinner.offsetTo(left+boardMargin,top+boardMargin);
        c.drawDoubleRoundRect(rectouter, 10, 10, rectinner, 10, 10, pFrameGold);
        switch (stars) {
            case 1:
                starRect.offsetTo(left+starRect.width() / 2, top-starRect.height()/2 );
                c.drawBitmap(knight,null,starRect,pR);
                break;
            case 2:

                break;

            case 3:
                break;

        }
        //starRect[1].offsetTo(rW / 2 - starRect[0].width() / 2, 20);
        //starRect[0].offsetTo(rW / 2 - rW / 4 - starRect[0].width() / 2, 20);
        //starRect[2].offsetTo(rW / 2 + rW / 4 - starRect[0].width() / 2, 20);

        c.drawText(level+"", left+rectouter.width()/2, top+rectouter.height()-textSize/2-pointRectBorderW, pWhiteText);
    }
    public void onDraw(Canvas c) {

        c.save();
        c.translate(0,offset);
        c.drawRect(0,0,width,height,gradientP);


        //c.drawLine(0,centre,width,centre,pL);
        int lvl = Math.max(currentLevel-1,1);
        for (int i = 0; i<5;i++) {
            int curr = i+lvl;
            LevelDescriptor ld = getLevel(curr);
            Difficulty difficulty = surfer.getDifficulty(curr);
            int x = pStartOffsetX+width/4*(i+1)-scrollFactor;
            drawLevel(c,curr,1,x,centre-(int)rectouter.height()/2);
            //c.drawCircle(x,centre,ballRadius,(curr == mStoplevel)?pT:pB);
            //c.drawText(curr+"",pStartOffsetX+width/4*(i+1)-scrollFactor,centre+textSize/2, pBlackText);
            if(difficulty!=Difficulty.normal) {
                diffRect.offsetTo(x-skullSize/2,centre-3*ballRadius-10);
                c.drawBitmap(difficulty==Difficulty.nightmare?skull:grimreaper, null, diffRect, pR);
            }
        }
        //c.drawRect(width/2-ballRadius,centre-ballRadius,width/2+ballRadius,centre+ballRadius,pL);
        c.restore();
    }

    private LevelDescriptor getLevel(int curr) {
        return null;
    }


    private Paint drawGradient() {

        Paint paint = new Paint();
        Shader mShader = new LinearGradient(0, 0, 0, height, new int[] {
                Color.BLACK,Color.GRAY, Color.BLACK },
                null, Shader.TileMode.REPEAT);  // CLAMP MIRROR REPEAT

        paint.setShader(mShader);

        return paint;
    }
}
