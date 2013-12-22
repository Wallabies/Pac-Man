/**
 * Created by Peter on 12/20/13.
 */
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.File;
import org.lwjgl.opengl.GL11;

public class Board {

	private byte[][] board;
	private int apothem = 4;

	public Board() {
		board = new byte[28][31];
	}

	public void readBoardFromFile() {

		Scanner s = null;
		try {
			s = new Scanner(new File("levels/level.pac"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		int line = 0;
		while (s.hasNextLine()) {
			String str = s.nextLine();
			for (int i = 0; i < str.length(); i++) {
				board[i][line] = Byte.parseByte(String.valueOf(str.charAt(i)));
			}
			line++;
		}
	}

	public void display() {
		double scaleFactor = (double)Start.WINDOW_HEIGHT / (board[0].length * 8);
		double newApothem = apothem * scaleFactor;
		double newEx;
		double newWhy;

		for (int why = 0; why < board[0].length; why++)
			for (int ex = 0; ex < board.length; ex++) {
				GL11.glColor3d((board[ex][why] * 16.0) / 100, (board[ex][why] * 16.0) / 100, (board[ex][why] * 16.0) / 100);

				newEx = ex * 8 * scaleFactor + newApothem;
				newWhy = why * 8 * scaleFactor + newApothem;

				GL11.glBegin(GL11.GL_QUADS);
					GL11.glVertex2d(newEx - newApothem, newWhy - newApothem);
					GL11.glVertex2d(newEx + newApothem, newWhy - newApothem);
					GL11.glVertex2d(newEx + newApothem, newWhy + newApothem);
					GL11.glVertex2d(newEx - newApothem, newWhy + newApothem);
				GL11.glEnd();
			}
	}
}
