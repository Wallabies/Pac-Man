import org.lwjgl.input.Mouse;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import java.io.IOException;

/**
 * Created by Peter on 1/2/14.
 */
public class Ghost {

	private int bodyState; //0 or 1
	private int eyeState; //0, 1, 2, or 3
	private int ghostNumber; //0, 1, 2, or 3
	private double y;
	private double x;

	private static Texture texture;

	public Ghost(int gn, double ex, double why) {
		ghostNumber = gn;
		x = ex;
		y = why;
		eyeState = 0;
		bodyState = 0;
		try {
			texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("pics/Character_Models.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void update() {

	}

	public void display() {
		//double ex = ((double)Mouse.getX() / 8) / Game.SCALE_FACTOR;
		//double why = Board.HEIGHT - (((double)Mouse.getY() / 8) / Game.SCALE_FACTOR);

		//draw face
		//Game.displayTexture(2 + ghostNumber, 1 + bodyState, ex, why, 0, texture);

		//draw eyes
		//Game.displayTexture(2 + eyeState, 3, ex, why, 0, texture);
	}
}
