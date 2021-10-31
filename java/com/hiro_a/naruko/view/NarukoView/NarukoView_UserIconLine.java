package com.hiro_a.naruko.view.NarukoView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.hiro_a.naruko.task.ConvertDp2Px;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class NarukoView_UserIconLine extends View {
    int count = 0;
    float radius = 400;
    int textSize = 30;
    int animFps = 90;
    long startTime, elapsedTime;
    long sleepTime = 1000 / animFps;
    float iconOffset = 0;
    float lineStartX, lineStartY;
    double deg = 90;
    double lineEndX, lineEndY;
    boolean isRunning = false;
    boolean touching = false;
    Point userIconGrid = new Point(0, 0);
    Point userIconMovingGrid = new Point(0, 0);

    Paint linePaint;
    Paint iconOuterCirclePaint;

    Path iconOuterCirclePath;

    public NarukoView_UserIconLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        iconOuterCirclePath = new Path();

        //UI白線Path設定
        iconOuterCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        iconOuterCirclePaint.setStyle(Paint.Style.STROKE);
        iconOuterCirclePaint.setColor(Color.WHITE);
        iconOuterCirclePaint.setStrokeWidth(2);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStrokeWidth(5);
        linePaint.setColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.rotate(90);

        if (isRunning){
            startTime = System.currentTimeMillis();
            lineEndX = cos((Math.PI/180)*deg)*radius+(textSize/2)+45;
            lineEndY = sin((Math.PI/180)*deg)*radius-(textSize/2);

            canvas.drawLine(lineStartX+iconOffset, lineStartY-iconOffset, -(float)lineEndX, -(float)lineEndY, linePaint);

            elapsedTime = System.currentTimeMillis();
            if (elapsedTime-startTime < sleepTime){
                try{
                    Thread.sleep(sleepTime-(elapsedTime-startTime));
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }

            if (deg < 175){
                deg = deg + 270/animFps;
                invalidate();
            }else {
                isRunning = false;
            }
        }else if (touching) {
            canvas.drawLine((float)(userIconMovingGrid.y + iconOffset + 45), -(float)userIconMovingGrid.x - iconOffset, -(float)lineEndX, -(float)lineEndY, linePaint);
            touching = false;
        }else {
            lineEndX = cos((Math.PI/180)*deg)*radius+(textSize/2)+45;
            lineEndY = sin((Math.PI/180)*deg)*radius-(textSize/2);

            canvas.drawLine(lineStartX+iconOffset, lineStartY-iconOffset, -(float)lineEndX, -(float)lineEndY, linePaint);
        }
    }

    public void getUserGrid(Point grid, boolean largeText){
        userIconGrid = new Point(grid.y, -grid.x);

        //1回目の文字列は既定の半径、2回目以降はTextSize分ずらす
        if (count < 8){
            count++;

            //メッセージが2段の場合はさらに下にずらす
            if (largeText){
                count++;
            }
        }
        //回転半径
        radius = ((count - 1) * (textSize + convertDp2Px(5, getContext()))) + convertDp2Px(200, getContext());
        //radius = (count * textSize) + 400;

        ConvertDp2Px convertDp2Px = new ConvertDp2Px();
        iconOffset = convertDp2Px.convert(50, getContext());
        lineStartX = userIconGrid.x;
        lineStartY = userIconGrid.y;

        isRunning = true;
        deg = 89.15f;
        invalidate();
    }

    public void getUserGridMoving(Point grid){
        userIconMovingGrid = grid;


        touching = true;
        invalidate();
    }

    //dp→px変換
    public static float convertDp2Px(float dp, Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return dp * metrics.density;
    }
}
