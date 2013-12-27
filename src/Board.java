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

	/**
	 * Create a new Board
	 */
	public Board() {
		board = new byte[28][31];
		try {
			texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("pics/Textures.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Construct board from a .pac file in the levels folder.
	 */
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

	/**
	 * Display the board with its current contents.
	 */
	public void display() {
		for (int why = 0; why < board[0].length; why++) {
			for (int ex = 0; ex < board.length; ex++) {
				//2 blocks surrounding
				if (getSurroundingBlockNumber(ex, why) == 2) {
					if (board[ex][why] == 4)
						displayTexture(3, 2, ex, why, getOpposite(getClosedCorner(ex, why)));
					else
						displayTexture(board[ex][why], 2, ex, why, getOpposite(getClosedCorner(ex, why)));
				}
				//3 blocks surrounding
				else if (getSurroundingBlockNumber(ex, why) == 3) {
					displayTexture(board[ex][why], 1, ex, why, getOpenFace(ex, why));
				}

				//4 blocks surrounding
				else if (getSurroundingBlockNumber(ex, why) == 4) {
					if (getOpenCorner(ex, why) != 4) {
						if (board[ex][why] == 3) //because this one is weird
							displayTexture(board[ex][why], 3, ex, why, getOpposite(getOpenCorner(ex, why)));
						else
							displayTexture(board[ex][why], 2, ex, why, getOpposite(getOpenCorner(ex, why)));
					}
					//if its in the middle, and therefore should not be rendered
					else
						displayTexture(0, 1, ex, why, 0);
				}

				//doesn't meet any of the other cases
				else
					displayTexture(board[ex][why], 1, ex, why, 0);
			}
		}
	}

	/**
	 * Remove coin at the given coordinates.
	 * This sets the slot the coin was in to value 0
	 * @param ex The x coordinate of the coin
	 * @param why The y coordinate of the coin
	 */
	public void removeCoinAt(int ex, int why) {
		board[ex][why] = 0;
	}

	/**
	 * Display a texture onto the board.
	 * This displays a portion of the texture specified by the user onto a specified location of the board, also specified by the user.
	 * @param textureX The x coordinate of the image on the texture
	 * @param textureY The y coordinate of the image on the texture
	 * @param boardX The x coordinate to display the image on the board at
	 * @param boardY The y coordinate to display the image on the board at
	 * @param rotation The amount of rotation to apply to the image. Either 0, 1, 2, or 3
	 */
	private void displayTexture(int textureX, int textureY, double boardX, double boardY, int rotation) {
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
				GL11.glRotated(rotation * 90, 0, 0, 1);
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

	/**
	 * Returns the face directly opposite to the one given.
	 * @param face The face to be evaluated
	 * @return The face opposite to the one given
	 */
	private int getOpposite(int face) {
		if(face <= 1)
			return face + 2;
		return face - 2;
	}

	/**
	 * Gives the number of blocks surrounding the block at the given coordinates.
	 * @param ex The x coordinate of the block to be evaluated
	 * @param why The y coordinate of the block to be evaluated
	 * @return The number of blocks surrounding the block at the given coordinates
	 */
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

	/**
	 * Gives the face of the given block that is not a wall, out of bounds, or data value 7.
	 * @param ex The x coordinate of the block to be evaluated
	 * @param why The y coordinate of the block to be evaluated
	 * @return The face of the given block that is open
	 */
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

	/**
	 * Gives the corner of the given block that is not a wall, out of bounds, or data value 7.
	 * @param ex The x coordinate of the block to be evaluated
	 * @param why The y coordinate of the block to be evaluated
	 * @return The corner of the given block that is open
	 */
	private int getOpenCorner(int ex, int why) {

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

	/**
	 * Gives the corner of the given block that is a wall, out of bounds, or data value 7.
	 * @param ex The x coordinate of the block to be evaluated
	 * @param why The y coordinate of the block to be evaluated
	 * @return The corner of the block that is not open
	 */
	private int getClosedCorner(int ex,int why) {
		try {
			if (board[ex - 1][why - 1] >= 3)
				return 0;
		} catch (ArrayIndexOutOfBoundsException e) {
			//do nothing
		}

		try {
			if (board[ex + 1][why - 1] >= 3)
				return 1;
		} catch (ArrayIndexOutOfBoundsException e) {
			//do nothing
		}

		try {
			if (board[ex + 1][why + 1] >= 3)
				return 2;
		} catch (ArrayIndexOutOfBoundsException e) {
			//do nothing
		}

		try {
			if (board[ex - 1][why + 1] >= 3)
				return 3;
		} catch (ArrayIndexOutOfBoundsException e) {
			//do nothing
		}
		return 4;
	}

	/**
	 * Checks if a block of a certain type is next to the block at the given coordinates
	 * @param ex The x coordinate of the block to be evaluated
	 * @param why The y coordinate of the block to be evaluated
	 * @param type The type of the block that is being checked for
	 * @return If the given block is next to a block with the given type
	 */
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