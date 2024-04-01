package byow.Core;

import byow.Core.Engine;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.*;
import java.util.*;

public class HallWayGenerator{
    private Queue<Point> fringe = new LinkedList<>();
    private Point root;
    private TETile[][] tiles;
    private Random ranSour;
    private HashMap<Point, int[]> skeleton = new HashMap<Point, int[]>();
    private HashSet<HallWay> HallWays = new HashSet<>();
    int WIDTH;
    int HEIGHT;

    public HallWayGenerator(TETile[][] tiles, Random ranSour){
        WIDTH = tiles.length;
        HEIGHT = tiles[0].length;
        this.tiles = tiles;
        this.ranSour = ranSour;
        root =new Point(WIDTH/2, HEIGHT/2);
    }
    public HashSet<HallWay> getHallways(){
        return HallWays;
    }
    public void addHallWay(HallWay hw){
        getHallways().add(hw);
    }
    public HashMap<Point, int[]> getSkeleton(){return skeleton;}
    public void putSkeleton(Point p, int[] dim){ getSkeleton().put(p, dim);}
    public void setSkeleton(Point p, int dir, int len){
        int[] modified = getSkeleton().get(p);
        modified[dir] = len;
        putSkeleton(p, modified);
    }
    public TETile[][] getTiles(){
        return tiles;
    }
    public static int lenDeterminator(TETile[][] tiles, Random ranSour, Point p, String dir){
        int maxLen = maximumLen(tiles, p, dir);
        int finLen;
        if (maxLen < 5){
            finLen = 0;
        }
        else{
            finLen = ranSour.nextInt(maxLen - 4) + 3;
            if (finLen > 10){
                finLen = 10;
            }
            if (finLen < 4){
                finLen = 0;
            }
        }
        return finLen;
    }
    public void branchAll(){
        int north = branchNorth(root);
        int south = branchSouth(root);
        int east = branchEast(root);
        int west = branchWest(root);
        int[] dim = {east, west, south, north};
        getSkeleton().put(root, dim);

        while (!fringe.isEmpty()){
            Point p = fringe.poll();
            int numOfBranches = ranSour.nextInt(4);
            int E = 0;
            int W = 0;
            int S = 0;
            int N = 0;
            for (int i = 0; i < numOfBranches; i ++) {
                int dir = ranSour.nextInt(4);
                switch (dir){
                    case 0:
                        E = branchEast(p);
                    case 1:
                        W = branchWest(p);
                    case 2:
                        S = branchSouth(p);
                    case 3:
                        N = branchNorth(p);
                }
            }
            int[] dirs = {E, W, S, N};
            putSkeleton(p, dirs);
        }
        updateDimensions();
    }
    public void updateDimensions(){
        for (HallWay hallway: getHallways()){
            if (hallway.dir == "EAST"){
                setSkeleton(hallway.from, 0, hallway.len);
                setSkeleton(hallway.to,1,hallway.len);
            }
            else if (hallway.dir == "WEST"){
                setSkeleton(hallway.from,1, hallway.len);
                setSkeleton(hallway.to, 0, hallway.len);
            }
            else if (hallway.dir == "NORTH"){
                setSkeleton(hallway.from, 3, hallway.len);
                setSkeleton(hallway.to, 2, hallway.len);
            }
            else {
                setSkeleton(hallway.from, 2, hallway.len);
                setSkeleton(hallway.to, 3, hallway.len);
            }
        }
    }

    public int branchNorth(Point p){
        tiles = getTiles();
        int north = lenDeterminator(tiles, ranSour, p , "NORTH");
        if (north > 0){
            for (int j = p.y; j < p.y + north + 1; j ++){
                tiles[p.x][j] = Tileset.FLOOR;
            }
            Point nextPoint = new Point(p.x, p.y + north);
            fringe.add(nextPoint);
            addHallWay(new HallWay(p, nextPoint, "NORTH", north - 1));
            return north;
        }
        return 0;
    }
    public int branchSouth(Point p){
        tiles = getTiles();
        int south = lenDeterminator(tiles, ranSour, p, "SOUTH");
        if (south > 0){
            for (int j = p.y; j > p.y - south - 1; j --){
                tiles[p.x][j] = Tileset.FLOOR;
            }
            Point nextPoint = new Point(p.x, p.y - south);
            fringe.add(nextPoint);
            addHallWay(new HallWay(p, nextPoint, "SOUTH", south - 1));
            return south;
        }
        return 0;
    }
    public int branchWest(Point p){
        tiles = getTiles();
        int west = lenDeterminator(tiles, ranSour, p, "WEST");
        if (west > 0){
            for (int i = p.x; i > p.x - west - 1; i --){
                tiles[i][p.y] = Tileset.FLOOR;
            }
            Point nextPoint = new Point(p.x - west, p.y);
            fringe.add(nextPoint);
            addHallWay(new HallWay(p, nextPoint, "WEST", west - 1));
            return west;
        }
        return 0;
    }
    public int branchEast(Point p){
        int east = lenDeterminator(tiles, ranSour, p, "EAST");
        if (east > 0){
            for (int i = p.x; i < p.x + east + 1; i ++){
                tiles[i][p.y] = Tileset.FLOOR;
            }
            Point nextPoint = new Point(p.x + east, p.y);
            fringe.add(nextPoint);
            addHallWay(new HallWay(p, nextPoint, "EAST", east - 1));
            return east;
        }
        return 0;
    }

    public static int maximumLen(TETile[][] tiles,Point p, String dir){
        int len = 0;
        if (dir =="NORTH" ){
            len = 0;
            while (northCheck(tiles, p.x, p.y + len)){
                len ++;
            }
        }
        else if (dir == "SOUTH"){
            len = 0;
            while (southCheck(tiles,p.x, p.y - len)){
                len ++;
            }
        }
        else if (dir == "WEST"){
            len = 0;
            while (westCheck(tiles,p.x - len, p.y)){
                len ++;
            }
        }
        else if (dir == "EAST"){
            len = 0;
            while (eastCheck(tiles,p.x + len, p.y)){
                len ++;
            }
        }
        if (len != 0){
            return len - 1;
        }
        return len;
    }
    public static boolean northCheck(TETile[][] tiles, int x, int y){
        int HEIGHT = tiles[0].length;
        int WIDTH = tiles.length;
        if (y < HEIGHT - 4) {
            // obstacle check
            if (x - 2 == 0) {
                if (tiles[x - 1][y + 3].description() == "floor"
                        || tiles[x][y + 3].description() == "floor"
                        || tiles[x + 1][y + 3].description() == "floor"
                        || tiles[x + 2][y + 3].description() == "floor") {
                    return false;
                }
            }
            else if (x - 1 == 0){
                if (tiles[x][y + 3].description() == "floor"
                        || tiles[x + 1][y + 3].description() == "floor"
                        || tiles[x + 2][y + 3].description() == "floor") {
                    return false;
                }
            }
            else if (x + 1 == WIDTH - 1){
                if (tiles[x - 2][y + 3].description() == "floor"
                        || tiles[x - 1][y + 3].description() == "floor"
                        || tiles[x][y + 3].description() == "floor") {
                    return false;
                }
            }
            else if (x + 2 == WIDTH - 1) {
                if (tiles[x - 2][y + 3].description() == "floor"
                        || tiles[x - 1][y + 3].description() == "floor"
                        || tiles[x][y + 3].description() == "floor"
                        || tiles[x + 1][y + 3].description() == "floor") {
                    return false;
                }
            } else if (tiles[x - 2][y + 3].description() == "floor"
                    || tiles[x - 1][y + 3].description() == "floor"
                    || tiles[x][y + 3].description() == "floor"
                    || tiles[x + 1][y + 3].description() == "floor"
                    || tiles[x + 2][y + 3].description() == "floor") {
                return false;
            }
        }
        else if (x + 3 >= WIDTH || x - 3 <= 0 || y + 3 >= HEIGHT){
            //border check
            return false;
        }
        return true;
    }
    public static boolean southCheck(TETile[][] tiles, int x, int y){
        int HEIGHT = tiles[0].length;
        int WIDTH = tiles.length;
        if (y > 3) {
            // obstacle check
            if (x - 2 == 0) {
                if (tiles[x - 1][y - 3].description() == "floor"
                        || tiles[x][y - 3].description() == "floor"
                        || tiles[x + 1][y - 3].description() == "floor"
                        || tiles[x + 2][y - 3].description() == "floor") {
                    return false;
                }
            }
            else if (x - 1 == 0){
                if (tiles[x - 1][y - 3].description() == "floor"
                        || tiles[x][y - 3].description() == "floor"
                        || tiles[x + 1][y - 3].description() == "floor") {
                    return false;
                }
            }
            else if (x + 1 == WIDTH - 1) {
                if (tiles[x - 2][y - 3].description() == "floor"
                        || tiles[x - 1][y - 3].description() == "floor"
                        || tiles[x][y - 3].description() == "floor") {
                    return false;
                }
            }
            else if (x + 2 == WIDTH - 1) {
                if (tiles[x - 2][y - 3].description() == "floor"
                        || tiles[x - 1][y - 3].description() == "floor"
                        || tiles[x][y - 3].description() == "floor"
                        || tiles[x + 1][y - 3].description() == "floor") {
                    return false;
                }
            }
            else if (tiles[x - 2][y - 3].description() == "floor"
                    || tiles[x - 1][y - 3].description() == "floor"
                    || tiles[x][y - 3].description() == "floor"
                    || tiles[x + 1][y - 3].description() == "floor"
                    || tiles[x + 2][y - 3].description() == "floor") {
                return false;
            }
        }
        else if ( x + 3 >= WIDTH || x - 3 <= 0 || y - 3 <= 0){
            return false;
        }
        return true;
    }
    public static boolean eastCheck(TETile[][] tiles, int x, int y){
        int HEIGHT = tiles[0].length;
        int WIDTH = tiles.length;
        if (x < WIDTH - 4) {
            // obstacle check
            if (y - 2 == 0) {
                if (tiles[x + 3][y - 1].description() == "floor"
                        || tiles[x + 3][y].description() == "floor"
                        || tiles[x + 3][y + 1].description() == "floor"
                        || tiles[x + 3][y + 2].description() == "floor") {
                    return false;
                }
            }
            else if (y - 1 == 0) {
                if ( tiles[x + 3][y].description() == "floor"
                        || tiles[x + 3][y + 1].description() == "floor"
                        || tiles[x + 3][y + 2].description() == "floor") {
                    return false;
                }
            }
            else if (y + 1 == HEIGHT - 1) {
                if (tiles[x + 3][y - 2].description() == "floor" // 밑에 쪽
                        || tiles[x + 3][y - 1].description() == "floor" //밑에 쪽
                        || tiles[x + 3][y].description() == "floor" // 중간
                ){
                    return false;
                }
            }
            else if (y + 2 == HEIGHT - 1) {
                if (tiles[x + 3][y - 2].description() == "floor" // 밑에 쪽
                        || tiles[x + 3][y - 1].description() == "floor" //밑에 쪽
                        || tiles[x + 3][y].description() == "floor" // 중간
                        || tiles[x + 3][y + 1].description() == "floor") // 위쪽
                {
                    return false;
                }
            } else if (tiles[x + 3][y - 2].description() == "floor"
                    || tiles[x + 3][y - 1].description() == "floor"
                    || tiles[x + 3][y].description() == "floor"
                    || tiles[x + 3][y + 1].description() == "floor"
                    || tiles[x + 3][y + 2].description() == "floor") {
                return false;
            }
        }
        else if ( x + 3 >= WIDTH || y - 3 <= 0 || y + 3 >= HEIGHT){
            return false;
        }
        return true;
    }
    public static boolean westCheck(TETile[][] tiles, int x, int y){
        int HEIGHT = tiles[0].length;
        int WIDTH = tiles.length;
        if (x > 3) {
            // obstacle check
            if (y - 2 == 0) {
                if (tiles[x - 3][y - 1].description() == "floor"
                        || tiles[x - 3][y].description() == "floor"
                        || tiles[x - 3][y + 1].description() == "floor"
                        || tiles[x - 3][y + 2].description() == "floor") {
                    return false;
                }
            }
            else if (y - 1 == 0) {
                if (tiles[x - 3][y].description() == "floor"
                        || tiles[x - 3][y + 1].description() == "floor"
                        || tiles[x - 3][y + 2].description() == "floor") {
                    return false;
                }
            }
            else if (y + 1 == HEIGHT - 1) {
                if (tiles[x - 3][y].description() == "floor"
                        || tiles[x - 3][y - 1].description() == "floor"
                        || tiles[x - 3][y - 2].description() == "floor") {
                    return false;
                }
            }
            else if (y + 2 == HEIGHT - 1) {
                if (tiles[x - 3][y + 1].description() == "floor"
                        || tiles[x - 3][y].description() == "floor"
                        || tiles[x - 3][y - 1].description() == "floor"
                        || tiles[x - 3][y - 2].description() == "floor")
                {
                    return false;
                }
            } else if (tiles[x - 3][y + 2].description() == "floor"
                    || tiles[x - 3][y + 1].description() == "floor"
                    || tiles[x - 3][y].description() == "floor"
                    || tiles[x - 3][y - 1].description() == "floor"
                    || tiles[x - 3][y - 2].description() == "floor" ) {
                return false;
            }
        }
        else if ( y - 3 <= 0 || x - 3 <= 0 || y + 3 >= HEIGHT){
            return false;
        }
        return true;
    }
}