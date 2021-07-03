package edu.xmu.inroomlocation.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import edu.xmu.inroomlocation.R;
import edu.xmu.inroomlocation.map.AbstractInRoomMap;

public class StepView extends View {
    private Paint mPaint;
    private Paint mStrokePaint;
    private Path mArrowPath; // 箭头路径

    private int cR = 10; // 圆点半径
    private int arrowR = 20; // 箭头半径

    private float mCurX = 200;
    private float mCurY = 200;
    private int mOrient;
    private Bitmap mBitmap;
    private List<PointF> mPointList = new ArrayList<>();

    private int mWidth = 0;
    private int mHeight = 0;
    private AbstractInRoomMap mMap;

    int mNearestNavPoint = -1;
    private int mNavTarget = -1;

    private Handler mMainHandler;


    public StepView(Context context) {
        this(context, null);
    }

    public StepView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 初始化画笔
        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mStrokePaint = new Paint(mPaint);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(5);

        // 初始化箭头路径
        mArrowPath = new Path();
        mArrowPath.arcTo(new RectF(-arrowR, -arrowR, arrowR, arrowR), 0, -180);
        mArrowPath.lineTo(0, -3 * arrowR);
        mArrowPath.close();

        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);

        setKeepScreenOn(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (canvas == null) return;
//        canvas.drawBitmap(mBitmap, new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight()), new Rect(0, 0, getWidth(), getHeight()), null); // 将mBitmap绘到canLock

        if (mMap != null) {
            mMap.drawMap(canvas, mWidth, mHeight);
        }

        for (PointF p : mPointList) {
            canvas.drawCircle(p.x, p.y, cR, mPaint);
        }
        canvas.save(); // 保存画布
        canvas.translate(mCurX, mCurY); // 平移画布
        canvas.rotate(mOrient + mMap.getMapRotation()); // 转动画布
        canvas.drawPath(mArrowPath, mPaint);
        canvas.drawArc(new RectF(-arrowR * 0.8f, -arrowR * 0.8f, arrowR * 0.8f, arrowR * 0.8f),
                0, 360, false, mStrokePaint);
        canvas.restore(); // 恢复画布
    }

    /**
     * 当屏幕被触摸时调用
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

//        this.setCurLoc(mCurX, mCurY);

        Message message = new Message();
        message.what = 3;
        message.obj = new float[]{x, y};
        mMainHandler.sendMessage(message);

        return false;
    }

    /**
     * 自动增加点
     */
    public void autoAddPoint(float stepLen) {
//        mCurX += (float) (stepLen * Math.sin(Math.toRadians(mOrient)));
//        mCurY += -(float) (stepLen * Math.cos(Math.toRadians(mOrient)));
        mCurX += (float) (stepLen * Math.sin(Math.toRadians(mOrient + mMap.getMapRotation())));
        mCurY += -(float) (stepLen * Math.cos(Math.toRadians(mOrient + mMap.getMapRotation())));
        mPointList.add(new PointF(mCurX, mCurY));
        invalidate();
    }
    /**
     * 自动增加点
     */
    public void autoAddPoint() {
//        mCurX += (float) (stepLen * Math.sin(Math.toRadians(mOrient)));
//        mCurY += -(float) (stepLen * Math.cos(Math.toRadians(mOrient)));
        mCurX += (float) (mMap.getStepLength() * Math.sin(Math.toRadians(mOrient + mMap.getMapRotation())));
        mCurY += -(float) (mMap.getStepLength() * Math.cos(Math.toRadians(mOrient + mMap.getMapRotation())));
        mPointList.add(new PointF(mCurX, mCurY));
        invalidate();
    }

    public void autoDrawArrow(int orient) {
        mOrient = orient;
        invalidate();
    }


    public void loadMap(AbstractInRoomMap map) {
        if (map != this.mMap) {
            this.mMap = map;

            this.invalidate();
        }
    }

    public AbstractInRoomMap getLoadedMap() {
        return mMap;
    }

    public void setCurLoc(float x, float y) {
        mCurX = x;
        mCurY = y;
        invalidate();
    }
    public void setCurLoc(PointF xy) {
        mCurX = xy.x;
        mCurY = xy.y;
        invalidate();
    }


    public int findNearestNavPoint() {
        return mMap.getNearestNavPoint(mCurX, mCurY);
    }

    public PointF getWifiLocByIdx(int idx) {
        return mMap.getWifiLocByIdx(idx);
    }

    public void navTo(int to) {
        this.mNavTarget = to;
        mMap.setNavPath(mNearestNavPoint, mNavTarget);
    }

    public void navTo(float[] xy) {
        this.mNavTarget = mMap.getNearestNavPoint(xy[0], xy[1]);
        mMap.setNavPath(mNearestNavPoint, mNavTarget);
    }

    public void refreshCurrentNearestLoc() {
        mNearestNavPoint = findNearestNavPoint();

        mMap.setCurrentNearestPoint(mNearestNavPoint);

        mMap.setNavPath(mNearestNavPoint, mNavTarget);
    }

    public void setMainHandler(Handler mHandler) {
        this.mMainHandler = mHandler;
    }
}