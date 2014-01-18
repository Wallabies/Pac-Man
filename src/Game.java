import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

/**
 * Created by Peter on 12/20/13.
 */

public class Game {

	public static final double SCALE_FACTOR = (double)Start.WINDOW_HEIGHT / (Board.HEIGHT * 8);

	Board board;
	Ghost ghost;

	/**
	 * Create a new Game
	 */
	public Game() {
		board = new Board();
		board.readBoardFromFile();
		ghost = new Ghost(0, 0, 0);
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
		//for testing
		int[] nums = board.getCoordinatesAt(Board.snapScreenPointToBoardX(Mouse.getX()), Board.snapScreenPointToBoardY(Mouse.getY()), 0, Board.FACE_UP);
		//board.drawBoxAt(nums[0], nums[1]);
		board.display();
		ghost.display();
	}

	public static void displayTexture(double textureX, double textureY, double boardX, double boardY, int rotation, Texture texture) {

		double blockSize = (double) texture.getHeight() / Math.sqrt(texture.getImageHeight());
		double apothem = Math.sqrt(texture.getImageHeight()) / 2;
		apothem  *= SCALE_FACTOR;

		boardX = boardX * 8 * Game.SCALE_FACTOR + apothem;
		boardY = boardY * 8 * Game.SCALE_FACTOR + apothem;

		textureX--;
		textureY--;

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		Color.white.bind();
		texture.bind();
		GL11.glPushMatrix(); {
			GL11.glTranslated(boardX, boardY, 0);
			GL11.glRotated(rotation * 90, 0, 0, 1);
			GL11.glTranslated(-boardX, -boardY, 0);
			GL11.glBegin(GL11.GL_QUADS); {
				GL11.glTexCoord2d(textureX * blockSize, textureY * blockSize);              GL11.glVertex2d(boardX - apothem, boardY - apothem);
				GL11.glTexCoord2d(blockSize * (textureX + 1), blockSize * textureY);        GL11.glVertex2d(boardX + apothem, boardY - apothem);
                GL11.glTexCoord2d(blockSize * (textureX + 1), blockSize * (textureY + 1));  GL11.glVertex2d(boardX + apothem, boardY + apothem);
                GL11.glTexCoord2d(blockSize * textureX, blockSize * (textureY + 1));        GL11.glVertex2d(boardX - apothem, boardY + apothem);
			} GL11.glEnd();
		} GL11.glPopMatrix();
	}
}
