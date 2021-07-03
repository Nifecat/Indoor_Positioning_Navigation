package edu.xmu.inroomlocation.map;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractInRoomMap {

    public void drawMap(Canvas canvas, int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }
    }

    public abstract float getMapRotation();

    public abstract float getStepLength();

    public CanvasScaler getCanvasScaler(float canvasWidth, float canvasHeight, float mapWidth, float mapHeight) {
        float canvasRatio = canvasWidth / canvasHeight;
        float mapRatio = mapWidth / mapHeight;
        if (canvasRatio < mapRatio) {
            return new CanvasScaler1(canvasWidth / mapWidth, (canvasHeight - mapHeight * canvasWidth / mapWidth) / 2);
        } else {
            return new CanvasScaler2(canvasHeight / mapHeight, (canvasWidth - mapWidth * canvasHeight / mapHeight) / 2);
        }
    }

    public abstract void setNavPath(int from, int to);

    public abstract PointF getWifiLocByIdx(int idx);

    public abstract int getNearestNavPoint(float mCurX, float mCurY);

    public abstract String getWifiNameByIdx(int idx);

    public abstract List<String> getWifiNames();

    public abstract PointF getNavLocByIdx(int navTargetIdx);

    interface CanvasScaler{
        PointF toCanvasXY(float mapX, float mapY);
        PointF toCanvasXY(PointF mapXY);
    }
    static class CanvasScaler1 implements CanvasScaler {
        private final float canvasMapRatio;
        private final float offset;

        CanvasScaler1(float canvasMapRatio, float offset) {
            this.canvasMapRatio = canvasMapRatio;
            this.offset = offset;
        }

        @Override
        public PointF toCanvasXY(float mapX, float mapY) {
            return new PointF(mapX * canvasMapRatio, mapY * canvasMapRatio + offset);
        }

        @Override
        public PointF toCanvasXY(PointF mapXY) {
            return new PointF(mapXY.x * canvasMapRatio, mapXY.y * canvasMapRatio + offset);
        }
    }
    static class CanvasScaler2 implements CanvasScaler {
        private final float canvasMapRatio;
        private final float offset;

        CanvasScaler2(float canvasMapRatio, float offset) {
            this.canvasMapRatio = canvasMapRatio;
            this.offset = offset;
        }

        @Override
        public PointF toCanvasXY(float mapX, float mapY) {
            return new PointF(mapX * canvasMapRatio+offset, mapY * canvasMapRatio);
        }
        @Override
        public PointF toCanvasXY(PointF mapXY) {
            return new PointF(mapXY.x * canvasMapRatio+offset, mapXY.y * canvasMapRatio);
        }
    }

    public abstract void setCurrentNearestPoint(int currentNearestPoint);
}
