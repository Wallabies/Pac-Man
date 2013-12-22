/**
 * Created by Peter on 12/20/13.
 */

public class Game {

	Board board;

	public Game() {
		board = new Board();
		board.readBoardFromFile();
	}

	public void update() {

	}

	public void display() {
		board.display();
	}

}
