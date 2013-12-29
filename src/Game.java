import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * Created by Peter on 12/20/13.
 */

public class Game {

	Board board;

	/**
	 * Create a new Game
	 */
	public Game() {
		board = new Board();
		board.readBoardFromFile();
	}

	/**
	 * Update the Game
	 */
	public void update() {
		board.update();
		//board.setTypeAt(Board.snapToBoardX(Mouse.getX()), Board.snapToBoardY(Mouse.getY()), (byte)0);
	}

	/**
	 * Display the game to the screen
	 */
	public void display() {
		board.display();
	}

}
