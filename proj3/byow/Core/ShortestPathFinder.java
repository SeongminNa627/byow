package byow.Core;
import edu.princeton.cs.algs4.*;
import edu.princeton.cs.algs4.DijkstraUndirectedSP;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;

import java.awt.*;
import java.util.*;

public class ShortestPathFinder {
    public static ArrayList<Point> path(Point from, Point to, EdgeWeightedGraph graph, ArrayList<Point> indexMap){
        int source = indexMap.indexOf(from);
        int destination = indexMap.indexOf(to);
        DijkstraUndirectedSP dj = new DijkstraUndirectedSP(graph, source);
        ArrayList<Point> shortestPath = new ArrayList<Point>(10);
        shortestPath.add(from);
        int curr = source;
        for (Edge e: dj.pathTo(destination)){
            int next = e.other(curr);
            Point v = indexMap.get(next);
            shortestPath.add(v);
            curr = next;
        }
        return shortestPath;
    }
    public static Point nearestPoint(Point pos, WorldInfo wi){
        Point possible = null;
        double minDist = 10000000;
        for (Point p: wi.roomDimension.keySet()){
            double d = dist(pos, p);
            int[] dim = wi.roomDimension.get(p);
            if (pos.x <= p.x + dim[0] && pos.x >= p.x - dim[1] && pos.y >= p.y - dim[2] && pos.y <= p.y + dim[3]){
                return p;
            }
            if (d < minDist){
                possible = p;
            }
        }
        return possible;
    }
    public static double dist(Point from, Point to){
        return Math.pow((Math.pow((from.x - to.x),2) + Math.pow((from.y - to.y),2)),1/2);
    }
}
