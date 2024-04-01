package byow.Core;
import java.awt.Point;
import java.util.HashSet;
import java.util.*;

import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.Edge;
public class GraphOperator {
    static private EdgeWeightedGraph GG;
    static private ArrayList<Point> ROOMINDEX;
     public GraphOperator(WorldInfo wi){
        WorldInfo map = wi;
        int numOfVertex = wi.roomDimension.size();
        ROOMINDEX = new ArrayList<>(numOfVertex);

        ROOMINDEX.addAll(wi.roomDimension.keySet());
        HashSet<HallWay> hallways = wi.hallways;
        EdgeWeightedGraph graph = new EdgeWeightedGraph(numOfVertex);
        for (HallWay hw: hallways){
            graph.addEdge(new Edge(ROOMINDEX.indexOf(hw.from), ROOMINDEX.indexOf(hw.to), hw.len));
        }
        GG = graph;

    }
    public static EdgeWeightedGraph getGraph(){
        return GG;
    }
    public static ArrayList<Point> getIndexList(){
        return ROOMINDEX;
    }
}
