import org.lwjgl.input.Mouse;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import java.io.IOException;

/**
 * Created by Mason on 1/14/14.
 */
public class PacMan {

    private double x;
    private double y;
	private int state; // 0, 1, or 2
	private int direction; // 0, 1, 2, or 3
	private Texture texture;

	public PacMan(double ex, double why) {
        x = ex;
        y = why;
		state = 0;
		direction = 0;
		try {
			texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("pics/Character_Models.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

	public void update() {

	}

	public void display() {
		double ex = ((double) Mouse.getX() / 8) / Game.SCALE_FACTOR;
		double why = Board.HEIGHT - (((double)Mouse.getY() / 8) / Game.SCALE_FACTOR);
		Game.displayTexture(1, 1 + state, ex, why, direction, texture);
	}
}
