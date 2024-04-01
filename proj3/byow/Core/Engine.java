package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.introcs.StdDraw;


import java.awt.*;

import java.io.File;

import java.io.IOException;
import java.io.Serializable;

import java.util.*;

public class Engine implements Serializable {
    TERenderer ter = new TERenderer();
    public static final int WIDTH = 80;
    public static final int HEIGHT = 50;
    public static int SEED;

    private static Boolean goodGame = false;

    private static WorldInfo WI;
    private static GraphOperator GO;
    private static Point START;
    private static Point END;
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */

    public TETile[][] interactWithInputString(String input) throws IOException, ClassNotFoundException {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        TETile[][] newWorld = new TETile[WIDTH][HEIGHT];
        TETile[][] finalWorldFrame = null;
        if (input.charAt(0) == 'N'){
            SEED = (seedExtract(input));
            Random seedRan = new Random(SEED);
            WI = createWorld(newWorld, seedRan);
            GraphOperator GO = new GraphOperator(WI);
            finalWorldFrame = newWorld;
        }
        else if (input.charAt(0) == 'L'){
            WI = Utils.read();
        }
        else if (input.charAt(0) == 'Q'){

        }
        return finalWorldFrame;
    }
    // default world 만들기
    public WorldInfo createWorld(TETile[][] tiles, Random seedRan){
        WorldInfo wi;
        TETile[][] tempTiles = tiles;
        HashMap<Point, int[]> skeleton;
        HashMap<Point, int[]> roomDimension;
        HashSet<HallWay> hallways;
        pave(tempTiles);
        HallWayGenerator hg = new HallWayGenerator(tempTiles, seedRan);
        // Branch out from each point
        hg.branchAll();
        skeleton = hg.getSkeleton();


        tempTiles = hg.getTiles();
        hallways = hg.getHallways();

        RoomGenerator rg = new RoomGenerator(tempTiles, seedRan, skeleton, hallways);
        rg.roomGenerator();
        roomDimension = rg.getRoomDimension();
        rg.thicken();
        tempTiles = rg.getTiles();
        enclose(tempTiles);


        Point[] pointList = skeleton.keySet().toArray(new Point[0]);
        Random ran = new Random(SEED);
        int start = ran.nextInt(pointList.length);
        int end = ran.nextInt(pointList.length);
        Avatar main = new Avatar(pointList[start].x,pointList[start].y);
        START = pointList[start];
        END = pointList[end];

        tempTiles[pointList[start].x][pointList[start].y] = Tileset.AVATAR;
        tempTiles[pointList[end].x][pointList[end].y] = Tileset.UNLOCKED_DOOR;

        wi = new WorldInfo(tempTiles, roomDimension, skeleton, hallways, main);
        return wi;
    }
    public void pave(TETile[][] tiles){
        for (int i = 0; i < WIDTH; i ++){
            for (int j = 0; j < HEIGHT; j ++){
                tiles[i][j] = Tileset.NOTHING;
            }
        }
    }
    public void interactWithKeyboard() throws IOException, ClassNotFoundException {
        entryBoardSet(WIDTH, HEIGHT);
        mainMenu();
        String input = "";
        Boolean mapInit = true;
        while (mapInit){
            char next = getNextKey();
            if (next == 'L'){
                input = input + Character.toString(next);
                break;
            }
            if (next == 'S'){
                input = input + Character.toString('S');
                mapInit = false;
            }
            input = input + Character.toString(next);
            drawFrame(input, WIDTH/2, HEIGHT/2);
        }

        Avatar main;
        interactWithInputString(input);
        main = WI.main;

        EdgeWeightedGraph graph = GO.getGraph();
        ArrayList<Point> indexList = GO.getIndexList();
        Iterable<Point> path = ShortestPathFinder.path(START, END, graph ,indexList);
//        for (Point p: path){
//            WI.tiles[p.x][p.y] = Tileset.SAND;
//        }
//        WI.tiles[START.x][START.y] = Tileset.AVATAR;
        WI.tiles[END.x][END.y] = Tileset.LOCKED_DOOR;

        ter.initialize(80, 52);
        ter.renderFrame(WI.tiles);
        while (!goodGame){
            while (StdDraw.hasNextKeyTyped() && !goodGame){
                char next = getNextKey();
                if (next == 'W' && movable(WI.tiles, main, 'w') ){
                    move(WI.tiles, main, 'w');
                }
                else if (next == 'S' && movable(WI.tiles, main,'s')){
                    move(WI.tiles, main, 's');
                }
                else if (next == 'D' && movable(WI.tiles, main,'d')){
                    move(WI.tiles, main, 'd');
                }
                else if (next == 'A' && movable(WI.tiles, main, 'a')){
                    move(WI.tiles, main, 'a');
                }
                else if (next == ':'){
                    char followed = getNextKey();
                    if (followed == 'Q'){
                        goodGame = true;
                        Utils.save(WI);
                    }
                }
                if (next == 'R'){
                    for (Point p: path){
                        WI.tiles[p.x][p.y] = Tileset.FLOOR;
                    }
                    Point currStart = ShortestPathFinder.nearestPoint(main.getPos(), WI);
                    ArrayList<Point> newPath = ShortestPathFinder.path(currStart, END, graph, indexList);
                    while (newPath.size() > 1){
                        Point former = newPath.get(0);
                        Point later = newPath.get(1);
                        if (former.x == later.x ){
                            if (former.y > later.y){
                                for (int j = later.y; j <= former.y; j++){
                                    WI.tiles[former.x][j] = Tileset.SAND;
                                }
                            }else{
                                for (int j = former.y; j <= later.y; j ++){
                                    WI.tiles[former.x][j] = Tileset.SAND;
                                }
                            }
                        }
                        else if(former.y == later.y){
                            if (former.x > later.x){
                                for (int i = later.x; i <= former.x; i ++){
                                    WI.tiles[i][former.y] = Tileset.SAND;
                                }
                            }else{
                                for (int i = former.x; i <= later.x; i ++){
                                    WI.tiles[i][former.y] = Tileset.SAND;
                                }
                            }
                        }
                        newPath.remove(0);
                    }
                    if (next == 'E'){

                    }

                    WI.tiles[END.x][END.y] = Tileset.LOCKED_DOOR;
                }
                // WI frame 정보를 실시간으로 업데이트
                ter.renderFrame(WI.tiles);
            }
            String tile = getMouseDesc();
            if (tile != null){
                ter.renderFrameWStatus(WI.tiles, tile);
            }
        }

        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(WIDTH/2, HEIGHT/2, "You Made It!");
        StdDraw.show();
    }
    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @return the 2D TETile[][] representing the state of the world
     */
    public String getMouseDesc(){

        int mouseX = (int) Math.round(StdDraw.mouseX());
        int mouseY = (int) Math.round(StdDraw.mouseY());
        if (WI.tiles.length - 1 < mouseX || mouseX <0){
            return null;
        }
        if (WI.tiles[0].length - 1 < mouseY || mouseY < 0){
            return null;
        }
        else {
//            String tile = WI.tiles[mouseX][mouseY].description();
            String tile = "(" + mouseX + ", " + mouseY + ")";
            return tile;
        }
    }
    public boolean movable(TETile[][] world, Avatar it, char dir){
        if (dir == 'w' && world[it.currPos.x][it.currPos.y + 1].description() == ("wall")){
            return false;
        }
        if (dir == 'a' && world[it.currPos.x-1][it.currPos.y].description() == "wall"){
            return false;
        }
        if (dir == 'd' && world[it.currPos.x+1][it.currPos.y].description() == "wall"){
            return false;
        }
        if (dir == 's' && world[it.currPos.x][it.currPos.y - 1].description() == "wall"){
            return false;
        }
        if (dir == 'w' && world[it.currPos.x][it.currPos.y + 1].description() == ("locked door")){
            goodGame = true;
            return false;
        }
        if (dir == 'a' && world[it.currPos.x-1][it.currPos.y].description() == ("locked door")){
            goodGame = true;
            return false;
        }
        if (dir == 'd' && world[it.currPos.x+1][it.currPos.y].description() == ("locked door")){
            goodGame = true;
            return false;
        }
        if (dir == 's' && world[it.currPos.x][it.currPos.y - 1].description() == ("locked door")){
            goodGame = true;
            return false;
        }

        return true;

    }
    public void move(TETile[][] world, Avatar it, char dir){
        world[it.getPos().x][it.getPos().y] = Tileset.FLOOR;
        if (dir == 'w'){
            it.setPos(it.getPos().x, it.getPos().y + 1);
        }
        else if (dir == 's'){
            it.setPos(it.getPos().x, it.getPos().y - 1);
        }
        else if (dir == 'a'){
            it.setPos(it.getPos().x - 1, it.getPos().y);
        }
        else {
            it.setPos(it.getPos().x + 1, it.getPos().y);
        }
        world[it.getPos().x][it.getPos().y] = Tileset.AVATAR;
    }
    public char getNextKey() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                return c;
            }
        }
    }
    public void drawFrame(String s, int x, int y) {
        //TODO: Take the string and display it in the center of the screen
        //TODO: If game is not over, display relevant game information at the top of the screen
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
        StdDraw.text(x, y, s);
        StdDraw.show();
    }
    public void entryBoardSet(int width, int height) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        StdDraw.setCanvasSize(width * 16, height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }
    public void mainMenu(){
        StdDraw.setPenColor(Color.WHITE);
        Font title = new Font("Monaco", Font.BOLD, 45);
        StdDraw.setFont(title);
        StdDraw.text(WIDTH/2, 2*HEIGHT/3, "Navigator");
        Font text = new Font("Monaco", 2, 30);
        StdDraw.setFont(text);
        StdDraw.text(WIDTH/2, HEIGHT/3, "New Game (N)");
        StdDraw.text(WIDTH/2, HEIGHT/3 - 2, "Load Game (L)");
        StdDraw.text(WIDTH/2, HEIGHT/3 - 4, "Quit (Q)");
        StdDraw.show();
    }


    public static int seedExtract(String key){
        int start = 1;
        int end = 0;
        int pos = start;
        while (key.charAt(pos) != 'S') {
            pos ++;
        }
        end = pos;
        return Integer.parseInt(key.substring(start, end));
    }
    public void enclose(TETile[][] tiles){
        for (int x = 0; x < WIDTH - 1; x ++){
            for (int y = 0; y < HEIGHT - 1; y ++){
                if (tiles[x][y].description() == "nothing" && tiles[x + 1][y].description() == "floor"){
                    tiles[x][y] = Tileset.WALL;
                }
            }
        }
        for (int x = WIDTH - 1; x > 0; x --){
            for (int y = 0; y < HEIGHT - 1; y ++){
                if (tiles[x][y].description() == "nothing" && tiles[x - 1][y].description() == "floor"){
                    tiles[x][y] = Tileset.WALL;
                }
            }
        }
        for (int y = 0; y < HEIGHT - 1; y ++){
            for (int x = 0; x< WIDTH - 1; x ++){
                if (tiles[x][y].description() == "nothing" && tiles[x][y + 1].description() == "floor"){
                    tiles[x][y] = Tileset.WALL;
                }
            }
        }
        for (int y = HEIGHT - 1; y > 0; y --){
            for (int x = 0; x< WIDTH - 1; x ++){
                if (tiles[x][y].description() == "nothing" && tiles[x][y - 1].description() == "floor"){
                    tiles[x][y] = Tileset.WALL;
                }
            }
        }
        for (int x = 0; x< WIDTH; x ++){
            for (int y = 0; y < HEIGHT; y ++){
                if (tiles[x][y].description() == "nothing"){
                    if(x > 0 && y < HEIGHT - 1){
                        if(tiles[x-1][y].description() == "wall" && tiles[x][y+1].description() == "wall" && tiles[x-1][y+1].description() == "floor"){
                            tiles[x][y] = Tileset.WALL;
                        }
                    }
                    if (x > 0 && y > 0){
                        if(tiles[x-1][y].description() == "wall" && tiles[x][y-1].description() == "wall" && tiles[x-1][y-1].description() == "floor"){
                            tiles[x][y] = Tileset.WALL;
                        }
                    }
                    if (x < WIDTH - 1 && y < HEIGHT - 1){
                        if(tiles[x+1][y].description() == "wall" && tiles[x][y+1].description() == "wall" && tiles[x+1][y+1].description() == "floor"){
                            tiles[x][y] = Tileset.WALL;
                        }
                    }
                    if (x < WIDTH - 1 && y > 0) {
                        if(tiles[x+1][y].description() == "wall" && tiles[x][y-1].description() == "wall" && tiles[x+1][y-1].description() == "floor"){
                            tiles[x][y] = Tileset.WALL;
                        }
                    }
                }
            }
        }
    }

}
//TODO: Branching out the skeleton hallways.