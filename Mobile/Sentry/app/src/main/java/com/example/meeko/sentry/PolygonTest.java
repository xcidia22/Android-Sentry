package com.example.meeko.sentry;

import com.google.android.gms.maps.model.LatLng;

public class PolygonTest {

    boolean PointIsInRegion(double x, double y, LatLng[] thePath) {
        int crossings = 0;
        LatLng a,b;
        LatLng point = new LatLng(x, y);
        int count = thePath.length;
        // for each edge
        for (int i=0; i < count; i++)
        {
            a = thePath [i];
            int j = i + 1;
            if (j >= count)
            {
                j = 0;
            }
            b = thePath [j];
            if (RayCrossesSegment(point, a, b))
            {
                crossings++;
            }
        }
        // odd number of crossings?
        return (crossings % 2 == 1);
    }

    boolean RayCrossesSegment(LatLng point, LatLng a, LatLng b) {
        double px = point.longitude;
        double py = point.latitude;
        double ax = a.longitude;
        double ay = a.latitude;
        double bx = b.longitude;
        double by = b.latitude;
        if (ay > by)
        {
            ax = b.longitude;
            ay = b.latitude;
            bx = a.longitude;
            by = a.latitude;
        }
        // alter longitude to cater for 180 degree crossings
        if (px < 0) { px += 360; };
        if (ax < 0) { ax += 360; };
        if (bx < 0) { bx += 360; };

        if (py == ay || py == by) py += 0.00000001;
        if ((py > by || py < ay) || (px > Math.max(ax, bx))) return false;
        if (px < Math.min(ax, bx)) return true;

        double red = (ax != bx) ? ((by - ay) / (bx - ax)) : Float.MAX_VALUE;
        double blue = (ax != px) ? ((py - ay) / (px - ax)) : Float.MAX_VALUE;
        return (blue >= red);
    }
}