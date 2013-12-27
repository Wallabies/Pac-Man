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

	}

	/**
	 * Display the game to the screen
	 */
	public void display() {
		board.display();
	}

}
