package byow.lab12;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    public static void addHexagon(TETile[][] tiles, int xpos, int ypos) {
        if (tiles[0][0] == null){
            for (int x = 0; x < 30; x += 1) {
                for (int y = 0; y < 30; y += 1) {
                    tiles[x][y] = Tileset.NOTHING;
                }
            }
        }
        for (int r = 0; r < 3; r ++){
            for (int w = 0; w < 3 + 2 * r; w ++){
                tiles[xpos - r + w][ypos - r] = Tileset.FLOWER;
                tiles[xpos - r + w][ypos - (6 - r - 1)] =  Tileset.FLOWER;
            }
        }

    }
    public static void  tesselate(TETile[][] tiles, int size){
//        for (int x = 0; x < 2; x ++){
//            columnDraw(tiles, 2 + 5 * x, 23 + 3 * x, 3 + x);
//            columnDraw(tiles, 26 - (5 * x), 23 + 3 * x, 3 + x);
//        }
//        columnDraw(tiles, 12, 29, 5);
        columnDraw(tiles, 2 , 23 , 3);
        columnDraw(tiles, 7, 26, 4);
        columnDraw(tiles, 12, 29, 5);
        columnDraw(tiles,17, 26, 4);
        columnDraw(tiles, 22,23,3);

    }
    public static void columnDraw(TETile[][] tiles, int xpos, int ypos, int numOfTiles){
        for (int i = 0; i < numOfTiles; i ++){
            addHexagon(tiles, xpos, ypos - i * 6);
        }
    }
    public static void main(String[] args){
        TERenderer ter = new TERenderer();
        ter.initialize(30, 30);

        TETile[][] hexaTiles = new TETile[30][30];
        tesselate(hexaTiles,3);

        ter.renderFrame(hexaTiles);
    }
}
