package byow.Core;

import java.awt.*;
import java.io.Serializable;

public class HallWay implements Serializable {
    Point from;
    Point to;
    String dir;
    int len;
    public HallWay(Point from, Point to, String dir, int len){
        this.from = from;
        this.to = to;
        this.dir = dir;
        this.len = len;
    }
}