package edu.xmu.inroomlocation.map;

import android.graphics.PointF;

import java.util.List;

public abstract class AbstractRoomMap_2 {

    public abstract String getMapFileName();


    public abstract float getMapRotation();

    public abstract float getStepLength();

    public abstract List<String> getAnchorNames();
    public abstract List<PointF> getAnchorPoints();

    public abstract List<PointF> getNavReferenceNodes();
    public abstract List<PointF> getNavReferenceNodesConnections();
}
