package byow.lab13;

import byow.Core.RandomUtils;
import edu.princeton.cs.introcs.StdDraw;

import javax.xml.datatype.DatatypeConfigurationException;
import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    private boolean gameOver;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        //TODO: Initialize random number generator
        this.width = width;
        this.height = height;
        rand = new Random(seed);
    }

    public String generateRandomString(int n) {
        if (n <= 0){
            return "";
        }
        else{
            return generateRandomString(n - 1) + CHARACTERS[new Random().nextInt(CHARACTERS.length)];
        }
    }

    public void drawFrame(String s) {
        //TODO: Take the string and display it in the center of the screen
        //TODO: If game is not over, display relevant game information at the top of the screen
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        if (!gameOver){
            StdDraw.line(0.2, height - 4, width - 0.2, height - 4);
            StdDraw.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));
            StdDraw.text(4, height - 2,  "Round " + round);
            if (s == "Now...Type your words!"){
                StdDraw.text(width/2, height - 2, "Type!");
            } else{
                StdDraw.text(width/2, height - 2, "Watch!");
            }
        }

        StdDraw.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
        StdDraw.text(width/2, height/2, s);
        StdDraw.show();
    }

    public void flashSequence(String letters) {
        //TODO: Display each character in letters, making sure to blank the screen between letters
        for (int i = 0; i < letters.length(); i ++){
            drawFrame(Character.toString(letters.charAt(i)));
            StdDraw.pause(500);
            drawFrame("");
            StdDraw.pause(1000);

        }
    }

    public String solicitNCharsInput(int n) {
        //TODO: Read n letters of player input
        String s = "";
        while ((StdDraw.hasNextKeyTyped() && n > 0)){
            s += StdDraw.nextKeyTyped();
            n --;
        }
        return s;
    }

    public void startGame() {
        //TODO: Set any relevant variables before the game starts

        //TODO: Establish Engine loop
        round = 1;
        gameOver = false;
        do{
            drawFrame("Round: " + round);
            StdDraw.pause(1000);
            int targetStringLen = rand.nextInt(3 + round * 2);
            String target = generateRandomString(targetStringLen);
            flashSequence(target);
            drawFrame("Now...Type your words!");
            StdDraw.pause(10000);
            String userInput = solicitNCharsInput(targetStringLen);
            if (userInput.equals(target)){
                round ++;
            }
            else{
                gameOver = true;
            }
        }while (!gameOver);
        drawFrame("Game Over! You made it to round: " + (round - 1));


    }

}
