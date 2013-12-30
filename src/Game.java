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
	}

	/**
	 * Display the game to the screen
	 */
	public void display() {
		board.display();
		int[] nums = board.getCoordinatesAt(Board.snapScreenPointToBoardX(Mouse.getX()), Board.snapScreenPointToBoardY(Mouse.getY()), 0, Board.FACE_UP);
		board.drawBoxAt(nums[0], nums[1]);
	}

}
