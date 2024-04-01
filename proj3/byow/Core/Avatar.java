package byow.Core;

import java.awt.*;
import java.io.Serializable;

public class Avatar implements Serializable {
    Point currPos;
    public Avatar(int x, int y){
        currPos = new Point(x, y);
    }
    public Point getPos(){
        return currPos;
    }
    public void setPos(int x, int y){
        currPos = new Point(x, y);
    }
}