package edu.xmu.inroomlocation.map;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Keyan1_4Floor_2 extends AbstractRoomMap_2 {
    public static final String MAP_NAME = "map_keyan.png";
    public static final float MAP_ROTATION = 150.f;
    private static final float STEP_LENGTH = 12f;

    private final List<String> anchorNames;
    private final Map<String, PointF> anchorLocs;

    private final List<PointF> referencePoints;
    private final List<PointF> referencePointConnections;


    public Keyan1_4Floor_2() {

        anchorNames = List.of(
                "科研一卫生间",
                "科研一紧急出口标志",
                "科研一楼梯口",
                "科研一409室",
                "科研一消防栓",
                "科研一二走廊",
                "科研二北门",
                "科研二卫生间",
                "科研二消防栓",
                "科研二东窗"
        );
        HashMap<String, PointF> anchorLocsInit = new HashMap<>();
        anchorLocsInit.put("科研二东窗", new PointF(20f, 145f));
        anchorLocsInit.put("科研二消防栓", new PointF(410f, 145f));
        anchorLocsInit.put("科研二卫生间", new PointF(740f, 185f));
        anchorLocsInit.put("科研二北门", new PointF(970f, 270f));
        anchorLocsInit.put("科研一二走廊", new PointF(970f, 430f));
        anchorLocsInit.put("科研一消防栓", new PointF(970f, 640f));
        anchorLocsInit.put("科研一409室", new PointF(970f, 880f));
        anchorLocsInit.put("科研一楼梯口", new PointF(1050f, 960f));
        anchorLocsInit.put("科研一紧急出口标志", new PointF(970f, 1150));
        anchorLocsInit.put("科研一卫生间", new PointF(1050f, 1250f));
        anchorLocs = Collections.unmodifiableMap(anchorLocsInit);

        referencePoints = List.of(
                new PointF(20f, 145f), // 科研二东窗
                new PointF(410f, 145f), // 科研二消防栓
                new PointF(670f, 145f), // 科研二卫生间 左上
                new PointF(670f, 185f), // 科研二卫生间 左
                new PointF(740f, 185f), // 科研二卫生间
                new PointF(970f, 185f), // 科研二卫生间 右
                new PointF(970f, 270f), // 科研二北门
                new PointF(970f, 430f), // 科研一二走廊
                new PointF(970f, 640f), // 科研一消防栓
                new PointF(970f, 880f), // 科研一409室
                new PointF(970f, 960f), // 科研一楼梯口 左
                new PointF(1050f, 960f), // 科研一楼梯口
                new PointF(970f, 1150), // 科研一紧急出口标志
                new PointF(970f, 1250f), // 科研一卫生间 左
                new PointF(1050f, 1250f) // 科研一卫生间
        );

        referencePointConnections = List.of(
                new PointF(0, 1),
                new PointF(1, 2),
                new PointF(2, 3),
                new PointF(3, 4),
                new PointF(4, 5),
                new PointF(5, 6),
                new PointF(6, 7),
                new PointF(7, 8),
                new PointF(8, 9),
                new PointF(9, 10),
                new PointF(10, 11),
                new PointF(10, 12),
                new PointF(12, 13),
                new PointF(13, 14)
        );

    }

    @Override
    public String getMapFileName() {
        return MAP_NAME;
    }

    @Override
    public List<String> getAnchorNames() {
        return anchorNames;
    }

    @Override
    public List<PointF> getAnchorPoints() {
        ArrayList<PointF> locs = new ArrayList<>(anchorNames.size());
        for (String name : anchorNames) {
            locs.add(anchorLocs.get(name));
        }
        return locs;
    }

    @Override
    public float getMapRotation() {
        return MAP_ROTATION;
    }

    @Override
    public List<PointF> getNavReferenceNodes() {
        return this.referencePoints;
    }

    @Override
    public List<PointF> getNavReferenceNodesConnections() {
        return referencePointConnections;
    }

    @Override
    public float getStepLength() {
        return STEP_LENGTH;
    }
}
