package edu.xmu.inroomlocation.map;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Keyan1_4Floor extends AbstractInRoomMap {

    Paint wallPaint = new Paint();
    Paint navEdgeTestPaint = new Paint();
    Paint navEdgePaint = new Paint();
    Paint wifiHotpotPainter = new Paint();
    Paint navPointPainter = new Paint();
    Paint textPaint = new Paint();
    private Paint currentNearestPointPainter = new Paint();


    float mapRotation = 150;
    float mapWidth = 1000, mapHeight = 1150;
    float stepLength = 25;

    private float mScreenWidth = mapWidth, mScreenHeight = mapHeight;

    boolean showWifiLocs = true;
    boolean showNavLocs = true;
    private boolean showNavEdge = true;
    boolean isShowNavEdge = false;

    boolean showCurrentNearestPoint = true;
    int currentNearestPoint = -1;

    Map<String, PointF> wifiLocs;
    Map<String, PointF> navLocs;
    ArrayList<PointF[]> walls;

    List<String> navLocNames;
    List<String> wifiLocNames;

    int[][] adj;
    int[][] dis;
    int[][] next;

    private List<Integer> mCurrentNavPath;

    public Keyan1_4Floor() {
        // init painter
        wallPaint.setColor(Color.BLACK);
        wallPaint.setAntiAlias(true);
        wallPaint.setStyle(Paint.Style.FILL);
//        wallPaint.setStyle(Paint.Style.STROKE);
        wallPaint.setStrokeWidth(3);

        navEdgeTestPaint.setColor(Color.GRAY);
        navEdgeTestPaint.setAntiAlias(true);
        navEdgeTestPaint.setStyle(Paint.Style.FILL);
        navEdgeTestPaint.setStrokeWidth(2);

        navEdgePaint.setColor(Color.GREEN);
        navEdgePaint.setAntiAlias(true);
        navEdgePaint.setStyle(Paint.Style.FILL);
        navEdgePaint.setStrokeWidth(10);

        wifiHotpotPainter.setColor(Color.YELLOW);
        wifiHotpotPainter.setStrokeJoin(Paint.Join.ROUND);
        wifiHotpotPainter.setStrokeCap(Paint.Cap.ROUND);
        wifiHotpotPainter.setStrokeWidth(3);


        navPointPainter.setColor(Color.GREEN);
        navPointPainter.setStrokeJoin(Paint.Join.ROUND);
        navPointPainter.setStrokeCap(Paint.Cap.ROUND);
        navPointPainter.setStrokeWidth(3);

        currentNearestPointPainter.setColor(Color.RED);
        currentNearestPointPainter.setStrokeJoin(Paint.Join.ROUND);
        currentNearestPointPainter.setStrokeCap(Paint.Cap.ROUND);
        currentNearestPointPainter.setStrokeWidth(3);


        textPaint.setColor(Color.BLACK);
        textPaint.setStrokeJoin(Paint.Join.ROUND);
        textPaint.setStrokeCap(Paint.Cap.ROUND);
        textPaint.setStrokeWidth(3);


        // init points
//        initPoints();

    }

    private void initPoints() {
        wifiLocs = new HashMap<>();
        navLocs = new HashMap<>();
        walls = new ArrayList<>();

        walls.add(new PointF[]{new PointF(3, 0), new PointF(670 + 27 * 3, 0)});
        walls.add(new PointF[]{new PointF(3, 45 * 3), new PointF(670 - 27 * 3, 45 * 3)});

        walls.add(new PointF[]{new PointF(670 - 27 * 3, 45 * 3), new PointF(670 - 27 * 3, 45 * 3 + 35 * 3)});
        walls.add(new PointF[]{new PointF(670 + 27 * 3, 0), new PointF(670 + 27 * 3, 45 * 3)});

        walls.add(new PointF[]{new PointF(670 - 27 * 3, 45 * 3 + 35 * 3), new PointF(997 - 45 * 3, 45 * 3 + 35 * 3)});
        walls.add(new PointF[]{new PointF(670 + 27 * 3, 45 * 3), new PointF(997, 45 * 3)});

        walls.add(new PointF[]{new PointF(997 - 45 * 3, 45 * 3 + 35 * 3), new PointF(997 - 45 * 3, 1150)});
        walls.add(new PointF[]{new PointF(997, 45 * 3), new PointF(997, 1150)});
//        walls.add(new PointF[]{new PointF(0, 2000), new PointF(1000, 2000)});
//        walls.add(new PointF[]{new PointF(0, 3500), new PointF(1000, 3500)});
//
//        walls.add(new PointF[]{new PointF(1350, 0), new PointF(1350, 5000)});


/*        wifiLocs.put("northstair", new PointF(1175, 4800));
        wifiLocs.put("401north", new PointF(1175, 4600));
        wifiLocs.put("402north", new PointF(1175, 4050));
//        wifiLocs.put("403", new PointF(1175, 4050));
//        wifiLocs.put("404", new PointF(1175, 4050));
        wifiLocs.put("409", new PointF(1175, 1900));
        wifiLocs.put("409mid", new PointF(1175, 1200));
//        wifiLocs.put("410", new PointF(1175, 4050));
//        wifiLocs.put("411", new PointF(1175, 4050));
//        wifiLocs.put("mid", new PointF(1175, 2500));
        wifiLocs.put("409inroom1", new PointF(575, 1700));
        wifiLocs.put("409inroom2", new PointF(575, 1000));*/

        wifiLocs.put("科研二东窗", new PointF(3, 67.5f));
        wifiLocs.put("科研二消防栓", new PointF(223, 67.5f));
        wifiLocs.put("科研二卫生间", new PointF(757, 167.5f));
        wifiLocs.put("科研二北门", new PointF(997 - 35 * 1.5f, 175f)); // 175?
        wifiLocs.put("科研一二走廊", new PointF(997 - 35 * 1.5f, 310f));
        wifiLocs.put("科研一消防栓", new PointF(997 - 35 * 1.5f, 510f));
        wifiLocs.put("科研一409室", new PointF(997 - 35 * 1.5f, 760f));
        wifiLocs.put("科研一楼梯口", new PointF(997 - 35 * 1.5f, 845f));
        wifiLocs.put("科研一紧急出口", new PointF(997 - 35 * 1.5f, 1025f));
        wifiLocs.put("科研一卫生间", new PointF(997 - 35 * 1.5f, 1150f));
        wifiLocNames = List.of(
                "科研一卫生间",
                "科研一紧急出口",
                "科研一楼梯口",
                "科研一409室",
                "科研一消防栓",
                "科研一二走廊",
                "科研二北门",
                "科研二卫生间",
                "科研二消防栓",
                "科研二东窗"
        );



//        navLocs.put("northstair", new PointF(1175, 4800));
//        navLocs.put("401north", new PointF(1175, 4600));
//        navLocs.put("402north", new PointF(1175, 4050));
//        navLocs.put("409", new PointF(1175, 1900));
//        navLocs.put("409mid", new PointF(1175, 1200));
//        navLocs.put("409inroom1", new PointF(575, 1700));
//        navLocs.put("409inroom2", new PointF(575, 1000));
//        navLocs.put("409inroom3", new PointF(575, 1900));
//        navLocs.put("409inroom4", new PointF(575, 1200));
//        navLocNames = List.of(
//                "northstair",
//                "401north",
//                "402north",
//                "409",
//                "409mid",
//                "409inroom1",
//                "409inroom2",
//                "409inroom3",
//                "409inroom4"
//        );
        navLocs.put("0", new PointF(3, 67.5f));
        navLocs.put("1", new PointF(223, 67.5f));
        navLocs.put("1-0", new PointF(453, 67.5f));
        navLocs.put("1-1", new PointF(680, 67.5f));
        navLocs.put("1-2", new PointF(680, 167.5f));
        navLocs.put("2", new PointF(757, 167.5f));
        navLocs.put("3", new PointF(997 - 35 * 1.5f, 175f)); // 175?
        navLocs.put("4", new PointF(997 - 35 * 1.5f, 310f));
        navLocs.put("5", new PointF(997 - 35 * 1.5f, 510f));
        navLocs.put("6", new PointF(997 - 35 * 1.5f, 760f));
        navLocs.put("7", new PointF(997 - 35 * 1.5f, 845f));
        navLocs.put("8", new PointF(997 - 35 * 1.5f, 1025f));
        navLocs.put("9", new PointF(997 - 35 * 1.5f, 1150f));
        navLocNames = List.of(
                "0",
                "1",
                "1-0",
                "1-1",
                "1-2",
                "2",
                "3",
                "4",
                "5",
                "6",
                "7",
                "8",
                "9"
        );

        adj = new int[navLocNames.size()][navLocNames.size()];
        dis = new int[navLocNames.size()][navLocNames.size()]; //  for floyd
        next = new int[navLocNames.size()][navLocNames.size()];//  for floyd

        for (int i = 0; i < adj.length; i++) {
            for (int j = 0; j < adj.length; j++) {
                adj[i][j] = INF;
            }
        }

//        adj[0][1] = adj[1][0] = 1;
//        adj[1][2] = adj[2][1] = 1;
//        adj[2][3] = adj[3][2] = 1;
//        adj[3][4] = adj[4][3] = 1;
//        adj[3][7] = adj[7][3] = 1;
//        adj[7][5] = adj[5][7] = 1;
//        adj[5][8] = adj[8][5] = 1;
//        adj[8][6] = adj[6][8] = 1;
        for (int i = 0; i < adj.length - 1; i++) {
            adj[i][i + 1] = adj[i + 1][i] = 1;
        }


        initialise(adj.length, adj);
        floydWarshall(adj.length);
    }

    @Override
    public void drawMap(Canvas canvas, int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }

        if (mScreenWidth != width || mScreenHeight != height) {
            this.mScreenWidth = width;
            this.mScreenHeight = height;
            reScaleSelf();
        }

        for (PointF[] points : walls) {
            PointF pointF0 = points[0];
            PointF pointF1 = points[1];

            canvas.drawLine(pointF0.x, pointF0.y, pointF1.x, pointF1.y, wallPaint);
        }

        if (showWifiLocs) {
             for (Map.Entry<String, PointF> wifi : wifiLocs.entrySet()) {
                PointF p = wifi.getValue();

                canvas.drawCircle(p.x, p.y, 30, wifiHotpotPainter);
                canvas.drawText(wifi.getKey(), p.x, p.y, textPaint);
            }
        }

        if (showNavLocs) {
            for (Map.Entry<String, PointF> wifi : navLocs.entrySet()) {
                PointF p = wifi.getValue();

                canvas.drawCircle(p.x, p.y, 10, navPointPainter);
            }
        }

        if (showNavEdge) {
//            PointF pointF1 = navLocs.get(navLocNames.get(0));
//            PointF pointF2 = navLocs.get(navLocNames.get(1));
//            canvas.drawLine(pointF1.x, pointF1.y, pointF2.x, pointF2.y, navEdgeTestPaint);

            for (int i = 0; i < adj.length; i++) {
                for (int j = 0; j < i; j++) {
                    if (adj[i][j] == 1) {
                        PointF pointF1 = navLocs.get(navLocNames.get(i));
                        PointF pointF2 = navLocs.get(navLocNames.get(j));
                        canvas.drawLine(pointF1.x, pointF1.y, pointF2.x, pointF2.y, navEdgeTestPaint);
                    }
                }
            }
        }

        if (isShowNavEdge) {
            if (mCurrentNavPath == null) {
                return;
            }

            for (int i = 0; i < mCurrentNavPath.size() - 1; i++) {
                PointF pointF1 = navLocs.get(navLocNames.get(mCurrentNavPath.get(i)));
                PointF pointF2 = navLocs.get(navLocNames.get(mCurrentNavPath.get(i + 1)));
                canvas.drawLine(pointF1.x, pointF1.y, pointF2.x, pointF2.y, navEdgePaint);
            }
        }

        if (showCurrentNearestPoint) {
            if (currentNearestPoint == -1) {
                return;
            }

            PointF p = navLocs.get(navLocNames.get(currentNearestPoint));
            canvas.drawCircle(p.x, p.y, 10, currentNearestPointPainter);

        }
    }

    @Override
    public void reScaleSelf(float width, float height) {
        if (width <= 0 || height <= 0) {
            return;
        }

        if (mScreenWidth != width || mScreenHeight != height) {
            this.mScreenWidth = width;
            this.mScreenHeight = height;
            reScaleSelf();
        }
    }


    public void reScaleSelf() {
        this.initPoints();

        CanvasScaler canvasScaler = getCanvasScaler(mScreenWidth, mScreenHeight, mapWidth, mapHeight);

        for (int i = 0; i < walls.size(); i++) {
            PointF[] pointFS = walls.get(i);
            pointFS[0] = canvasScaler.toCanvasXY(pointFS[0]);
            pointFS[1] = canvasScaler.toCanvasXY(pointFS[1]);
        }

        for (String k : wifiLocs.keySet()) {
            wifiLocs.put(k, canvasScaler.toCanvasXY(wifiLocs.get(k)));
        }

        for (String k : navLocs.keySet()) {
            navLocs.put(k, canvasScaler.toCanvasXY(navLocs.get(k)));
        }
    }


    private final static int INF = 999999;
    private void initialise(int length, int[][] graph) {
        for(int i = 0; i < length; i++)
        {
            for(int j = 0; j < length; j++)
            {
                dis[i][j] = graph[i][j];

                // No edge between node
                // i and j
                if (graph[i][j] == INF)
                    next[i][j] = -1;
                else
                    next[i][j] = j;
            }
        }
    }
    private void floydWarshall(int length)
    {
        for(int k = 0; k < length; k++)
        {
            for(int i = 0; i < length; i++)
            {
                for(int j = 0; j < length; j++)
                {

                    // We cannot travel through
                    // edge that doesn't exist
                    if (dis[i][k] == INF ||
                            dis[k][j] == INF)
                        continue;

                    if (dis[i][j] > dis[i][k] +
                            dis[k][j])
                    {
                        dis[i][j] = dis[i][k] +
                                dis[k][j];
                        next[i][j] = next[i][k];
                    }
                }
            }
        }
    }


    public List<Integer> getPath(int u, int v) {
        // If there's no path between
        // node u and v, simply return
        // an empty array
        if (next[u][v] == -1)
            return null;

        // Storing the path in a vector
        List<Integer> path = new ArrayList<>();
        path.add(u);

        while (u != v)
        {
            u = next[u][v];
            path.add(u);
        }
        return path;
    }

    @Override
    public void setNavPath(int from, int to) {
        if (from < 0 || to < 0 || from >= next.length || to >= next.length) {
            this.mCurrentNavPath = null;
            isShowNavEdge = false;
            return;
        }
        this.mCurrentNavPath = getPath(from, to);
        isShowNavEdge = true;
    }

    @Override
    public PointF getWifiLocByIdx(int idx) {
        return wifiLocs.get(wifiLocNames.get(idx));
    }

    @Override
    public int getNearestNavPoint(float mCurX, float mCurY) {
        float minDis = 99999999;
        int minIdx = 0;
        for (int i = 0; i < navLocNames.size(); i++) {

            PointF p = navLocs.get(navLocNames.get(i));
            float dis = (p.x - mCurX) * (p.x - mCurX) + (p.y - mCurY) * (p.y - mCurY);
            if (dis < minDis) {
                minIdx = i;
                minDis = dis;
            }
        }

        return minIdx;
    }

    @Override
    public String getWifiNameByIdx(int idx) {
        return wifiLocNames.get(idx);
    }

    /**
     * get a copy of list
     * @return
     */
    @Override
    public List<String> getWifiNames() {
        return new ArrayList<>(wifiLocNames);
    }

    @Override
    public PointF getNavLocByIdx(int navTargetIdx) {
        return navLocs.get(navLocNames.get(navTargetIdx));
    }

    @Override
    public void setCurrentNearestPoint(int currentNearestPoint) {
        this.currentNearestPoint = currentNearestPoint;
    }


    @Override
    public float getMapRotation() {
        return this.mapRotation;
    }
    @Override
    public float getStepLength() {
        return this.stepLength;
    }
}
