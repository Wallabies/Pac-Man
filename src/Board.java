/**
 * Created by Peter on 12/20/13.
 */
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.io.File;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Board {

	public static final int WIDTH = 28;
	public static final int HEIGHT = 31;

	public static final int FACE_UP = 0;
	public static final int FACE_RIGHT = 1;
	public static final int FACE_DOWN = 2;
	public static final int FACE_LEFT = 3;

	private byte[][] board;
	private int apothem;
	private Texture texture;
	private int coinBlinkCount;
	private int blinkRate;

	/**
	 * Create a new Board
	 */
	public Board() {
		board = new byte[WIDTH][HEIGHT];
		apothem = 4;
		coinBlinkCount = 0;
		blinkRate = 8;

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
				try {
					board[i][line] = Byte.parseByte(String.valueOf(str.charAt(i)));
				} catch (NumberFormatException e) {
					board[i][line] = (byte)str.charAt(i);
				}
			}
			line++;
		}
	}

	/**
	 * Update the board.
	 */
	public void update() {
		coinBlinkCount++;
		if (coinBlinkCount >= blinkRate * 2)
			coinBlinkCount = 0;
	}

	/**
	 * Display the board with its current contents.
	 */
	public void display() {
		for (int why = 0; why < HEIGHT; why++) {
			for (int ex = 0; ex < WIDTH; ex++) {
				//one of the letters or special characters
				if (board[ex][why] > 20)
					displayTexture(1, board[ex][why], ex, why, 0);
				//2 tiles surrounding
				else if (getSurroundingTileNumber(ex, why) == 2) {
					if (board[ex][why] == 4)
						displayTexture(3, 2, ex, why, getOpposite(getClosedCorner(ex, why)));
					else
						displayTexture(board[ex][why], 2, ex, why, getOpposite(getClosedCorner(ex, why)));
				}
				//3 tiles surrounding
				else if (getSurroundingTileNumber(ex, why) == 3)
					displayTexture(board[ex][why], 1, ex, why, getOpenFace(ex, why));

				//4 tiles surrounding
				else if (getSurroundingTileNumber(ex, why) == 4) {
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
				else {
					if (board[ex][why] == 2) {
						if (coinBlinkCount <= blinkRate)
							displayTexture(board[ex][why], 1, ex, why, 0);
					}
					else
						displayTexture(board[ex][why], 1, ex, why, 0);
				}
			}
		}
	}

	/**
	 * Set the type of tile at the given coordinates.
	 * @param ex The x coordinate of the tile to be changed
	 * @param why The y coordinate of the tile to be changed
	 * @param type The type to set the tile at the given coordinates
	 */
	public void setTypeAt(int ex, int why, byte type) {
		try {
			board[ex][why] = type;
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Error: Unable to set type");
		}
	}

	/**
	 * Give the x coordinate as if the point was located on the board.
	 * @param ex The point to be evaluated
	 * @return The parameter as if it was on the board
	 */
	public static int snapScreenPointToBoardX(double ex) {
		return (int)(ex / Game.SCALE_FACTOR / 8);
	}

	/**
	 * Give the y coordinate as if the point was located on the board.
	 * @param why The point to be evaluated
	 * @return The parameter as if it was on the board
	 */
	public static int snapScreenPointToBoardY(double why) {
		return HEIGHT - 1 - snapScreenPointToBoardX(why);
	}

	/**
	 * Gives whether or not the tile at the given coordinates is solid (wall, door, or out of bounds).
	 * @param ex The x coordinate of the tile to be evaluated
	 * @param why The y coordinate of the tile to be evaluated
	 * @return True if the tile is solid, false if it is not
	 */
	public boolean isSolid(int ex, int why) {
		try {
			return board[ex][why] >= 3 && board[ex][why] < 20 && board[ex][why] != 8;
		} catch (ArrayIndexOutOfBoundsException e) {
			return true;
		}
	}

	/**
	 * Gives the coordinates of a point a distance away from the given coordinates at a given face.
	 * @param ex The initial x coordinate
	 * @param why The initial y coordinate
	 * @param distance The distance from the initial point
	 * @param face The face to be used
	 * @return The coordinates of the point distance away from the initial point at face
	 */
	public int[] getCoordinatesAt(int ex, int why, int distance, int face) {
		switch (face) {
			case FACE_UP: return new int[] {ex, why - distance};
			case FACE_RIGHT: return new int[] {ex + distance, why};
			case FACE_DOWN: return new int[] {ex, why + distance};
			case FACE_LEFT: return new int[] {ex - distance, why};
		}
		return new int[0];
	}

	/**
	 * For code testing purposes! Draws a box at the given coordinates.
	 * @param ex The x coordinate of the given point
	 * @param why The y coordinate of the given point
	 */
	public void drawBoxAt(double ex, double why) {
		double newApothem = apothem * Game.SCALE_FACTOR;
		ex = ex * 8 * Game.SCALE_FACTOR + newApothem;
		why = why * 8 * Game.SCALE_FACTOR + newApothem;

		try {
			GL11.glColor3d(1, 0, 0);
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glVertex2d(ex - newApothem, why - newApothem);
				GL11.glVertex2d(ex + newApothem, why - newApothem);
				GL11.glVertex2d(ex + newApothem, why + newApothem);
				GL11.glVertex2d(ex - newApothem, why + newApothem );
			GL11.glEnd();
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Unable to draw box.");
		}
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
	private void displayTexture(double textureX, double textureY, double boardX, double boardY, int rotation) {
		if(textureX != 0 && textureX != 7 && textureX != 8) {
			switch ((int)textureY) {
				case 82: textureY = 2; break;
				case 69: textureY = 3; break;
				case 65: textureY = 4; break;
				case 68: textureY = 5; break;
				case 89: textureY = 6; break;
				case 33: textureY = 7; break;
			}
			Game.displayTexture(textureX, textureY, boardX, boardY, rotation, texture);
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
	 * Gives the number of tiles surrounding the tile at the given coordinates.
	 * @param ex The x coordinate of the tile to be evaluated
	 * @param why The y coordinate of the tile to be evaluated
	 * @return The number of tiles surrounding the tile at the given coordinates
	 */
	private int getSurroundingTileNumber(int ex, int why) {
		if (board[ex][why] < 3 || board[ex][why] == 6)
			return 0;

		int count = 0;

		//direction 0
		if (isSolid(ex, why - 1))
			count++;

		//direction 1
		if (isSolid(ex + 1, why))
			count++;

		//direction 2
		if (isSolid(ex, why + 1))
			count++;

		//direction 3
		if (isSolid(ex - 1, why))
			count++;

		return count;
	}

	/**
	 * Gives the face of the given tile that is not a solid tile.
	 * @param ex The x coordinate of the tile to be evaluated
	 * @param why The y coordinate of the tile to be evaluated
	 * @return The face of the given tile that is open
	 */
	private int getOpenFace(int ex, int why) {
		int num = 0;

		//direction 0
		if (!isSolid(ex, why - 1))
			num = 0;

		//direction 1
		if (!isSolid(ex + 1, why))
			num = 1;

		//direction 2
		if (!isSolid(ex, why + 1))
			num = 2;

		//direction 3
		if (!isSolid(ex - 1, why))
			num = 3;

		return num;
	}

	/**
	 * Gives the corner of the given tile that is not a solid tile.
	 * @param ex The x coordinate of the tile to be evaluated
	 * @param why The y coordinate of the tile to be evaluated
	 * @return The corner of the given tile that is open
	 */
	private int getOpenCorner(int ex, int why) {

		//direction 0
		if (!isSolid(ex - 1, why - 1))
			return 0;

		//direction 1
		if (!isSolid(ex + 1, why - 1))
			return 1;

		//direction 2
		if (!isSolid(ex + 1, why + 1))
			return 2;

		//direction 3
		if (!isSolid(ex - 1, why + 1))
			return 3;

		return 4;
	}

	/**
	 * Gives the corner of the given tile that is a solid tile.
	 * @param ex The x coordinate of the tile to be evaluated
	 * @param why The y coordinate of the tile to be evaluated
	 * @return The corner of the tile that is not open
	 */
	private int getClosedCorner(int ex,int why) {
		//direction 0
		if (isSolid(ex - 1, why - 1))
			return 0;

		//direction 1
		if (isSolid(ex + 1, why - 1))
			return 1;

		//direction 2
		if (isSolid(ex + 1, why + 1))
			return 2;

		//direction 3
		if (isSolid(ex - 1, why + 1))
			return 3;

		return 4;
	}

	/**
	 * Checks if a tile of a certain type is next to the tile at the given coordinates.
	 * @param ex The x coordinate of the tile to be evaluated
	 * @param why The y coordinate of the tile to be evaluated
	 * @param type The type of the tile that is being checked for
	 * @return If the given tile is next to a tile with the given type
	 */
	private boolean isNextTo(int ex, int why, int type) {

		//direction 0
		if (board[ex][why - 1] == type)
			return true;

		//direction 1
		if (board[ex + 1][why] == type)
			return true;

		//direction 2
		if (board[ex][why + 1] == type)
			return true;

		//direction 3
		if (board[ex - 1][why] == type)
			return true;

		return false;
	}
}