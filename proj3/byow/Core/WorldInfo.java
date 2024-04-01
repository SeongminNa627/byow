package byow.Core;

import byow.Core.Engine;
import byow.Core.HallWay;
import byow.TileEngine.TETile;

import java.awt.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

public class WorldInfo implements Serializable {
    Avatar main;
    HashMap<Point, int[]> roomDimension;
    HashMap<Point, int[]> skeleton;
    HashSet<HallWay> hallways;
    TETile[][] tiles;

    public WorldInfo(TETile[][] tiles, HashMap<Point, int[]> roomDimension, HashMap<Point, int[]> skeleton, HashSet<HallWay> hallways, Avatar main){
        this.main = main;
        this.roomDimension = roomDimension;
        this.skeleton = skeleton;
        this.hallways = hallways;
        this.tiles = tiles;
    }
}