/**
 * Created by Peter on 12/20/13.
 */
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.io.File;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Board {

	private byte[][] board;
	private int apothem = 4;
	private Texture texture;

	public Board() {
		board = new byte[28][31];
		try {
			texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("pics/Textures.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		for (int why = 0; why < board[0].length; why++) {
			for (int ex = 0; ex < board.length; ex++) {
				//2 blocks surrounding
				if (getSurroundingBlockNumber(ex, why) == 2) {
					if (board[ex][why] == 4)
						displayTexture(3, 2, ex, why, getOpenDirection(ex, why));
					else
						displayTexture(board[ex][why], 2, ex, why, getOpenDirection(ex, why));
				}
				//3 blocks surrounding
				else if (getSurroundingBlockNumber(ex, why) == 3) {
					displayTexture(board[ex][why], 1, ex, why, getOpenFace(ex, why));
				}

				//4 blocks surrounding
				else if (getSurroundingBlockNumber(ex, why) == 4) {
					if (getOpenCorner(ex, why) != 4) {
						if (board[ex][why] == 3) //because this one is weird
							displayTexture(board[ex][why], 3, ex, why, getOppositeFace(getOpenCorner(ex, why)));
						else
							displayTexture(board[ex][why], 2, ex, why, getOppositeFace(getOpenCorner(ex, why)));
					}
					//if its in the middle, and therefore should not be rendered
					else
						displayTexture(0, 1, ex, why, 0);
				}

				else
					displayTexture(board[ex][why], 1, ex, why, 0);
			}
		}
	}

	private void displayTexture(int textureX, int textureY, double boardX, double boardY, int face) {
		if(textureX != 0 && textureX != 7) {
			double scaleFactor = (double)Start.WINDOW_HEIGHT / (board[0].length * 8);
			double newApothem = apothem * scaleFactor;
			double numX = (double)(textureX - 1) / 8;
			double numY = (double)(textureY - 1) / 8;
			boardX = boardX * 8 * scaleFactor + newApothem;
			boardY = boardY * 8 * scaleFactor + newApothem;

			Color.white.bind();
			texture.bind();
			GL11.glPushMatrix();
				GL11.glTranslated(boardX, boardY, 0);
				GL11.glRotated(face * 90, 0, 0, 1);
				GL11.glTranslated(-boardX, -boardY, 0);
				GL11.glBegin(GL11.GL_QUADS);
					GL11.glTexCoord2d(0 + numX, 0 + numY);            GL11.glVertex2d(boardX - newApothem, boardY - newApothem);
					GL11.glTexCoord2d(0.125 + numX, 0 + numY);        GL11.glVertex2d(boardX + newApothem, boardY - newApothem);
					GL11.glTexCoord2d(0.125 + numX, 0.125 + numY);    GL11.glVertex2d(boardX + newApothem, boardY + newApothem);
					GL11.glTexCoord2d(0 + numX, 0.125 + numY);        GL11.glVertex2d(boardX - newApothem, boardY + newApothem);
				GL11.glEnd();
			GL11.glPopMatrix();
			getSurroundingBlockNumber(0, 0);
		}
	}

	private int getOppositeFace(int face) {
		if(face <= 1)
			return face + 2;
		return face - 2;
	}

	private int getSurroundingBlockNumber(int ex, int why) {
		if (board[ex][why] < 3 || board[ex][why] == 6)
			return 0;

		int count = 0;

		//direction 0
		if (why - 1 < 0)
			count++;
		else if (board[ex][why - 1] >= 3)
			count++;

		//direction 1
		if (ex + 1 >= board.length)
			count++;
		else if (board[ex + 1][why] >= 3)
			count++;

		//direction 2
		if (why + 1 >= board[0].length)
			count++;
		else if (board[ex][why + 1] >= 3)
			count++;

		//direction 3
		if (ex - 1 < 0)
			count++;
		else if (board[ex - 1][why] >= 3)
			count++;

		return count;
	}

	private int getOpenFace(int ex, int why) {
		int num = 0;

		try {
			if (board[ex][why - 1] < 3)
				num = 0;
		} catch (ArrayIndexOutOfBoundsException e) {
			//do nothing
		}

		try {
			if (board[ex + 1][why] < 3)
				num = 1;
		} catch (ArrayIndexOutOfBoundsException e) {
			//do nothing
		}

		try {
			if (board[ex][why + 1] < 3)
				num = 2;
		} catch (ArrayIndexOutOfBoundsException e) {
			//do nothing
		}

		try {
			if (board[ex - 1][why] < 3)
				num = 3;
		} catch (ArrayIndexOutOfBoundsException e) {
			//do nothing
		}

		return num;
	}

	private int getOpenDirection(int ex, int why) {
		if (board[ex - 1][why] < 3 && board[ex][why - 1] < 3)
			return 0;
		if (board[ex][why - 1] < 3 && board[ex + 1][why] < 3)
			return 1;
		if (board[ex + 1][why] < 3 && board[ex][why + 1] < 3)
			return 2;
		if (board[ex][why + 1] < 3 && board[ex - 1][why] < 3)
			return 3;
		return 0;
	}

	private int getOpenCorner(int ex, int why) {//it's a bit wonky, but it works

		try {
			if (board[ex - 1][why - 1] < 3)
				return 0;
		} catch (ArrayIndexOutOfBoundsException e) {
			//do nothing
		}

		try {
			if (board[ex + 1][why - 1] < 3)
				return 1;
		} catch (ArrayIndexOutOfBoundsException e) {
			//do nothing
		}

		try {
			if (board[ex + 1][why + 1] < 3)
				return 2;
		} catch (ArrayIndexOutOfBoundsException e) {
			//do nothing
		}

		try {
			if (board[ex - 1][why + 1] < 3)
				return 3;
		} catch (ArrayIndexOutOfBoundsException e) {
			//do nothing
		}
		return 4;
	}

	private boolean isNextTo(int ex, int why, int type) {
		try {
			if (board[ex][why - 1] == type)
				return true;
		} catch (ArrayIndexOutOfBoundsException e) {
			//do nothing
		}

		try {
			if (board[ex + 1][why] == type)
				return true;
		} catch (ArrayIndexOutOfBoundsException e) {
			//do nothing
		}

		try {
			if (board[ex][why + 1] == type)
				return true;
		} catch (ArrayIndexOutOfBoundsException e) {
			//do nothing
		}

		try {
			if (board[ex - 1][why] == type)
				return true; //3
		} catch (ArrayIndexOutOfBoundsException e) {
			//do nothing
		}
		return false;
	}
}