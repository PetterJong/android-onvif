package com.balckhao.blackonvif.udv.ptzControl;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.balckhao.blackonvif.R;


/**
 * Created by ZhangHao on 2017/6/7.
 * 云台控制 View
 */

public class PtzControlView extends View implements View.OnTouchListener {

    //常态下边框颜色
    private int strokeColor;
    //背景色
    private int bgViewColor;
    //边框宽度
    private int strokeWidth;
    //当前按下的方向
    private int direction = -1;
    //画笔
    private Paint paint;
    //用来绘制三角形按钮
    private Path path;
    //点击回调
    private PtzClickCallBack callBack;

    /**
     * 可选方向
     */
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int TOP = 3;
    public static final int BOTTOM = 4;

    public PtzControlView(Context context) {
        this(context, null);
    }

    public PtzControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PtzControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PtzControlView, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.PtzControlView_bgViewColor) {
                //背景色
                bgViewColor = a.getColor(attr, Color.BLUE);
            } else if (attr == R.styleable.PtzControlView_strokeColor) {
                // 常态下边框颜色
                strokeColor = a.getColor(attr, Color.BLUE);
            } else if (attr == R.styleable.PtzControlView_strokeWidth) {
                //边框宽度
                strokeWidth = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
            }
        }
        a.recycle();
        //初始化画笔
        paint = new Paint();
        paint.setStrokeWidth(strokeWidth);
        paint.setAntiAlias(true);

        //初始化path
        path = new Path();

        setOnTouchListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制背景圆
        int radius = getWidth() > getHeight() ? getHeight() / 2 : getWidth() / 2;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(bgViewColor);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, paint);
        //绘制内部小空心圆
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(strokeColor);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius / 3, paint);
        //绘制最外层空心圆
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius - strokeWidth / 2, paint);
        //绘制四个三角按钮
        //三角形高,除掉内部圆的半径后的三分之一
        int triangle = (radius - radius / 3) / 3;
        //左边
        if (direction == LEFT) {
            paint.setStyle(Paint.Style.FILL);
        } else {
            paint.setStyle(Paint.Style.STROKE);
        }
        path.reset();
        path.moveTo(triangle, getHeight() / 2);
        path.lineTo(triangle + triangle, getHeight() / 2 - triangle / 2);
        path.lineTo(triangle + triangle, getHeight() / 2 + triangle / 2);
        path.close();
        canvas.drawPath(path, paint);
        //右边
        if (direction == RIGHT) {
            paint.setStyle(Paint.Style.FILL);
        } else {
            paint.setStyle(Paint.Style.STROKE);
        }
        path.reset();
        path.moveTo(getWidth() - triangle, getHeight() / 2);
        path.lineTo(getWidth() - triangle - triangle, getHeight() / 2 - triangle / 2);
        path.lineTo(getWidth() - triangle - triangle, getHeight() / 2 + triangle / 2);
        path.close();
        canvas.drawPath(path, paint);
        //上边
        if (direction == TOP) {
            paint.setStyle(Paint.Style.FILL);
        } else {
            paint.setStyle(Paint.Style.STROKE);
        }
        path.reset();
        path.moveTo(getWidth() / 2, triangle);
        path.lineTo(getWidth() / 2 - triangle / 2, triangle + triangle);
        path.lineTo(getWidth() / 2 + triangle / 2, triangle + triangle);
        path.close();
        canvas.drawPath(path, paint);

        //下边
        if (direction == BOTTOM) {
            paint.setStyle(Paint.Style.FILL);
        } else {
            paint.setStyle(Paint.Style.STROKE);
        }
        path.reset();
        path.moveTo(getWidth() / 2, getHeight() - triangle);
        path.lineTo(getWidth() / 2 - triangle / 2, getHeight() - triangle - triangle);
        path.lineTo(getWidth() / 2 + triangle / 2, getHeight() - triangle - triangle);
        path.close();
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //按下时
                int x = (int) event.getX();
                int y = (int) event.getY();
                //背景圆的半径
                int radius = getWidth() > getHeight() ? getHeight() / 2 : getWidth() / 2;
                //判断按下坐标所在的区域
                if (x > getWidth() / 2 - radius && x < getWidth() / 2 - radius / 3
                        && y > getHeight() / 2 - radius / 3 && y < getHeight() / 2 + radius / 3) {
                    //左
                    direction = LEFT;
                } else if (x < getWidth() / 2 + radius && x > getWidth() / 2 + radius / 3
                        && y > getHeight() / 2 - radius / 3 && y < getHeight() / 2 + radius / 3) {
                    //右
                    direction = RIGHT;
                } else if (x < getWidth() / 2 + radius / 3 && x > getWidth() / 2 - radius / 3
                        && y < getHeight() / 2 - radius / 3 && y > getHeight() / 2 - radius) {
                    //上
                    direction = TOP;
                } else if (x < getWidth() / 2 + radius / 3 && x > getWidth() / 2 - radius / 3
                        && y > getHeight() / 2 + radius / 3 && y < getHeight() / 2 + radius) {
                    //下
                    direction = BOTTOM;
                }
                if (callBack != null) {
                    callBack.itemClick(v, true, direction);
                }
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (callBack != null) {
                    callBack.itemClick(v, false, direction);
                }
                direction = -1;
                postInvalidate();
                break;
        }
        return true;
    }

    public void setCallBack(PtzClickCallBack callBack) {
        this.callBack = callBack;
    }

    /**
     * Author ： BlackHao
     * Time : 2017/6/7 14:24
     * Description : 用于 PtzControlView 点击之后回调
     */
    public interface PtzClickCallBack {
        /**
         * 回调
         * @param view 点击的 View
         * @param isDown 是否是按下状态（按下/抬起两种）
         * @param direction 方向
         */
        void itemClick(View view, boolean isDown, int direction);
    }
}
