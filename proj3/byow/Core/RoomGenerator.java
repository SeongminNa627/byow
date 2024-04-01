package byow.Core;

import byow.Core.HallWay;
import byow.Core.HallWayGenerator;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.*;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Random;

public class RoomGenerator{
    TETile[][] tiles;
    Random ranSour;
    HashMap<Point, int[]> roomDimension;
    HashMap<Point, int[]> skeleton;
    HashSet<HallWay> hallways;
    int WIDTH;
    int HEIGHT;
    public RoomGenerator(TETile[][] tiles, Random ranSour, HashMap<Point, int[]> skeleton, HashSet<HallWay> hallways){
        this.tiles = tiles;
        this.ranSour = ranSour;
        this.roomDimension = new HashMap<Point, int[]>();
        this.skeleton = skeleton;
        this.hallways = hallways;
        this.WIDTH = tiles.length;
        this.HEIGHT = tiles[0].length;
    }
    public HashMap<Point, int[]> getRoomDimension(){
        return this.roomDimension;
    }
    public void putRoomDimension(Point p, int[] dim){ getRoomDimension().put(p, dim);}
    public HashSet<HallWay> getHallways() {
        return this.hallways;
    }
    public HashMap<Point, int[]> getSkeleton(){ return this.skeleton;}
    public TETile[][] getTiles(){
        return this.tiles;
    }
    public void thicken(){
        tiles = getTiles();
        for (Point center: getRoomDimension().keySet()) {
            tiles[center.x][center.y] = Tileset.FLOOR;
        }
        int numOfIteration = ranSour.nextInt(getHallways().size());
        for (HallWay hw: getHallways()){
            if (numOfIteration == 0){
                break;
            }
            addHallWay(hw);
            numOfIteration --;
        }
    }
    public void addHallWay(HallWay hw){
        tiles = getTiles();
        boolean availablity = true;
        Point from = hw.from;
        Point to = hw.to;
        int[] coefficient;
        if (hw.dir == "EAST" || hw.dir == "WEST") {
            if (hw.from.y < 3){
                coefficient = new int[]{1};
            }
            else if (hw.from.y > WIDTH - 4){
                coefficient = new int[]{-1};
            }
            else{
                coefficient = new int[]{1, -1};
            }
            if (hw.dir == "WEST"){
                from = hw.to;
                to = hw.from;
            }
            int start = from.x + getRoomDimension().get(from)[0] + 1;
            int end = to.x - getRoomDimension().get(to)[1] - 1;
            int num = 0;
            do {
                int c = coefficient[num];
                if (c == 1 && getRoomDimension().get(from)[3]==0 || c == -1 && getRoomDimension().get(from)[2]==0){
                    start = from.x - 2;
                }
                if (c == 1 && getRoomDimension().get(to)[3] == 0 || c == -1 && getRoomDimension().get(to)[2] == 0)
                    end = to.x + 2;
                for (int i = start; i <= end; i ++){
                    if (tiles[i][from.y + 3*(c)].description() == "floor"){
                        availablity = false;
                    }
                }
                if (availablity){
                    for (int index = from.x; index <= to.x; index ++){
                        tiles[index][from.y + c] = Tileset.FLOOR;
                    }
                    return;
                }
                num ++;
            } while (num < coefficient.length);
        }
        else if (hw.dir == "NORTH" || hw.dir == "SOUTH") {
            if (hw.from.x < 3) {
                coefficient = new int[]{1};
            } else if (hw.from.x > HEIGHT - 4) {
                coefficient = new int[]{-1};
            } else {
                coefficient = new int[]{1, -1};
            }
            if (hw.dir == "SOUTH") {
                from = hw.to;
                to = hw.from;
            }
            int start = from.y + getRoomDimension().get(from)[3] + 1;
            int end = to.y - getRoomDimension().get(to)[2] - 1;
            int num = 0;
            do {
                int c = coefficient[num];
                if (c == 1 && getRoomDimension().get(from)[0] == 0 || c == -1 && getRoomDimension().get(from)[1] == 0) {
                    start = from.y - 2;
                }
                if (c == 1 && getRoomDimension().get(to)[0] == 0 || c == -1 && getRoomDimension().get(to)[1] == 0)
                    end = to.y + 2;

                for (int j = start; j <= end; j++) {
                    if (tiles[from.x + 3 * (c)][j].description() == "floor") {
                        availablity = false;
                    }
                }
                if (availablity) {
                    for (int index = from.y; index <= to.y; index++) {
                        tiles[from.x + c][index] = Tileset.FLOOR;
                    }
                    return;
                }
                num++;
            } while (num < coefficient.length);
        }
    }
    public void roomGenerator(){
        tiles = getTiles();
        for (Point center: getSkeleton().keySet()){
            tiles[center.x][center.y] = Tileset.FLOOR;
            int[] roomDim = new int[4];
            if (!validLhallway(center)){
                int[] hallDim = getSkeleton().get(center);
                String[] dirs = {"EAST", "WEST","SOUTH","NORTH"};
                for (int i = 0; i < 4; i ++){
                    if (hallDim[i] != 0){
                        roomDim[i] = maxAlongHallway(center, dirs[i]);
                    }
                }
                if (hallDim[0] != 0 && hallDim[1] != 0 && hallDim[2] != 0 && hallDim[3] != 0){
                    int up = 0;
                    while(extendable(center, roomDim[0], roomDim[1], up, "NORTH")){
                        up ++;
                    }
                    int down = 0;
                    while(extendable(center, roomDim[0], roomDim[1], down, "SOUTH")){
                        down ++;
                    }
                    roomDim[3] = up;
                    roomDim[2] = down;
                }
                else if ((hallDim[2] == 0 || hallDim[3] == 0) && (hallDim[0] != 0 && hallDim[1] != 0)){
                    //horizontal line stack
                    if (roomDim[0] == 0){
                        int right = HallWayGenerator.lenDeterminator(getTiles(), ranSour, center, "EAST");
                        roomDim[0] = right;
                    }
                    if (roomDim[1] == 0){
                        int left = HallWayGenerator.lenDeterminator(getTiles(), ranSour,center, "WEST");
                        roomDim[1] = left;
                    }
                    int up = 0;
                    while(extendable(center, roomDim[0], roomDim[1], up, "NORTH")){
                        up ++;
                    }
                    int down = 0;
                    while(extendable(center, roomDim[0], roomDim[1], down, "SOUTH")){
                        down ++;
                    }
                    roomDim[3] = up;
                    roomDim[2] = down;
                }
                else if ((hallDim[1] == 0 || hallDim[0] == 0) && (hallDim[2] != 0 && hallDim[3] != 0)){
                    if (roomDim[2] == 0){
                        int down = HallWayGenerator.lenDeterminator(getTiles(), ranSour, center, "SOUTH");
                        roomDim[2] = down;
                    }
                    if (roomDim[3] == 0){
                        int up = HallWayGenerator.lenDeterminator(getTiles(), ranSour, center, "NORTH");
                        roomDim[3] = up;
                    }
                    int right = 0;
                    while (extendable(center, roomDim[3], roomDim[2], right, "EAST")){
                        right ++;
                    }
                    int left = 0;
                    while (extendable(center, roomDim[3], roomDim[2], left, "WEST")){
                        left ++;
                    }
                    roomDim[0] = right;
                    roomDim[1] = left;
                }else{
                    if (roomDim[0] == 0 && roomDim[2] == 0 || roomDim[0] == 0 && roomDim[3] == 0){
                        int down = 0;
                        int right = HallWayGenerator.maximumLen(getTiles(), center, "EAST");
                        int up = 0;
                        int left = roomDim[1];
                        while(extendable(center, right, left, up, "NORTH")){
                            up ++;
                        }
                        while(extendable(center, right, left, down, "SOUTH")){
                            down ++;
                        }
                        roomDim = new int[]{right, left, down, up};
                    }
                    else if (roomDim[1] == 0 && roomDim[3] == 0 || roomDim[1] == 0 && roomDim[2] == 0){
                        int up = 0;
                        int left = HallWayGenerator.maximumLen(getTiles(),center, "WEST");
                        int down = 0;
                        int right = roomDim[0];
                        while (extendable(center,  right, left, up, "NORTH")){
                            up ++;
                        }
                        while (extendable(center, right, left, down, "SOUTH")){
                            down ++;
                        }
                        roomDim = new int[]{right, left, down, up};

                    }
                }

                for (int index = 0; index < 4; index ++){
                    if (roomDim[index] >= 1){
                        roomDim[index] = ranSour.nextInt(roomDim[index]) + 1;
                    }
                }
                putRoomDimension(center, roomDim);
                for (int j = center.y - roomDim[2]; j <=  center.y + roomDim[3]; j ++){
                    for (int i = center.x - roomDim[1]; i <= center.x + roomDim[0]; i++){
                        tiles[i][j] = Tileset.FLOOR;
                    }
                }
            }

        }

    }
    public boolean extendable(Point center, int plus, int minus, int epsilon ,String dir){
        HashSet<Point> ignore = new HashSet<>();
        if (dir == "EAST"){
            if (center.x + epsilon < WIDTH - 4){
                for (int i = 1; i <= getSkeleton().get(center)[0]; i++){
                    ignore.add(new Point(center.x + i, center.y));
                }
                int ceil = 2;
                int floor = 2;
                if (center.y + plus == HEIGHT - 2){
                    ceil = 1;
                }
                if (center.y - minus == 1){
                    floor = 1;
                }
                for (int j = center.y - minus - floor; j <= center.y + plus + ceil ;j ++){
                    if (((tiles[center.x + epsilon + 3][j].description() == "floor")
//                                ||(tiles[center.x + epsilon + 3][j].description() == "wall"))
                    )
                            && !ignore.contains(new Point(center.x + epsilon + 3, j))){
                        return false;
                    }
                }
                return true;
                //right check
            }
            else {
                if (center.x + epsilon + 1 >= WIDTH - 2){
                    return false;
                }
                return false;
            }
        }
        else if (dir == "WEST"){
            if (center.x - epsilon - 3 > 0)  {
                for (int i = 1; i <= getSkeleton().get(center)[1]; i++){
                    ignore.add(new Point(center.x - i, center.y));
                }
                int ceil = 2;
                int floor = 2;
                if (center.y + plus == HEIGHT - 2){
                    ceil = 1;
                }
                if (center.y - minus == 1){
                    floor = 1;
                }
                for (int j = center.y - minus - floor; j <= center.y + plus + ceil; j ++){
                    if (((tiles[center.x - epsilon - 3][j].description() == "floor")
//                                || (tiles[center.x - epsilon - 3][j].description() == "wall"))
                    )
                            && !ignore.contains(new Point(center.x - epsilon - 3, j))){
                        return false;
                    }
                }
                return true;
            }
            else {
                if (center.x - epsilon - 1 <= 0){
                    return false;
                }
                return false;
            }
        }
        else if (dir == "SOUTH"){
            if (center.y - epsilon - 3 > 0){
                for (int j = 1; j <= getSkeleton().get(center)[2]; j++){
                    ignore.add(new Point(center.x, center.y - j));
                }
                int leftWall = 2;
                int rightWall = 2;
                if (center.x + plus == WIDTH - 2){
                    rightWall = 1;
                }
                if (center.x - minus == 1){
                    leftWall = 1;
                }
                for (int i = center.x - minus - leftWall; i <= center.x + plus + rightWall; i ++){
                    if (((tiles[i][center.y - epsilon - 3].description() == "floor")
//                                ||(tiles[i][center.y - epsilon - 3].description() == "wall"))
                    )
                            && !ignore.contains(new Point(i, center.y - epsilon - 3))){
                        return false;
                    }
                }
                return true;
            }
            else{
                if (center.y - epsilon - 1 <= 0){
                    return false;
                }
                return true;
            }
            //down check
        }
        else{
            //up check
            if (center.y + epsilon < HEIGHT - 4){
                for (int j = 1; j <= getSkeleton().get(center)[3]; j++){
                    ignore.add(new Point(center.x, center.y + j));
                }
                int leftWall = 2;
                int rightWall = 2;
                if (center.x + plus == WIDTH - 2){
                    rightWall = 1;
                }
                if (center.x - minus == 1){
                    leftWall = 1;
                }
                for (int i = center.x - minus - leftWall ; i <= center.x + plus + rightWall; i ++){
                    if (((tiles[i][center.y + epsilon + 3].description() == "floor")
//                                ||(tiles[i][center.y + epsilon + 3].description() == "wall"))
                    )
                            && !ignore.contains(new Point(i, center.y + epsilon + 3))){
                        return false;
                    }
                }
                return true;
            }
            else {
                if (center.y + epsilon + 1 >= HEIGHT - 2) {
                    return false;
                }
                return true;
            }
        }
    }
    public int maxAlongHallway(Point c, String dir){
        int max = 0;
        if (dir == "WEST"){
            // c hallway index: 0
            while(!( tiles[c.x - max - 2][c.y + 1].description() == "floor"
                    || max + 3 == getSkeleton().get(c)[1]
                    || tiles[c.x - max - 2][c.y - 1].description() == "floor")){
                max ++;
            }
        }
        else if (dir == "EAST"){
            // c hallway index: 1
            while(!(tiles[c.x + max + 2][c.y + 1].description() == "floor"
                    || max + 3 == getSkeleton().get(c)[0]
                    || tiles[c.x + max + 2][c.y - 1].description() == "floor")){
                max ++;
            }
        }
        else if (dir == "SOUTH"){
            // c hallway index: 2
            while(!(tiles[c.x + 1][c.y - max - 2].description() == "floor"
                    || max + 3 == getSkeleton().get(c)[2]
                    || tiles[c.x - 1][c.y - max - 2].description() == "floor")){
                max ++;
            }
        }
        else{
            // c hallway index: 3
            while(!(tiles[c.x + 1][c.y + max + 2].description() == "floor"
                    || max + 3 == getSkeleton().get(c)[3]
                    || tiles[c.x - 1][c.y + max + 2].description() == "floor")){
                max ++;
            }
        }
        if (max == 0){
            return max;
        }
        return max - 1;
    }
    public boolean validLhallway(Point center){
        int[] dim = getSkeleton().get(center);
        if (dim[0] != 0 && dim[2] !=0 && dim[1] == 0 || dim[3] == 0){
            if (ranSour.nextInt(1) == 1){
                return true;
            }
            return false;
        }
        else if (dim[1] != 0 && dim[2] !=0 && dim[0] == 0 || dim[3] == 0){
            if (ranSour.nextInt(1) == 1){
                return true;
            }
            return false;
        }
        else if (dim[1] != 0 && dim[3] !=0 && dim[2] == 0 || dim[0] == 0){
            if (ranSour.nextInt(1) == 1){
                return true;
            }
            return false;
        }
        else if (dim[0] != 0 && dim[3] !=0 && dim[1] == 0 || dim[2] == 0){
            if (ranSour.nextInt(1) == 1){
                return true;
            }
            return false;
        }
        return false;
    }

}