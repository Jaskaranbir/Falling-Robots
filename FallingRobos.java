import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import becker.robots.City;
import becker.robots.Direction;
import becker.robots.Robot;
import becker.robots.Wall;
import becker.robots.icons.RobotIcon;

public class FallingRobots {
	public static int i; // A counter to iterate through an array

	public volatile static int score = 0;

	public static void main(String args[]) {
		// The width of area.
		// Keep this limited or game might go out of control due to much larger width
		int size = 15;
		int height = 15; // Enemies start from this height
		City ct = new City((height + 1), size);
		
		for (int i = 0; i < size; i++)
			new Wall(ct, height, i, Direction.SOUTH);
		
		// To generate random positions for enemy robots
		Random rnd = new Random();
		
		// Assign key controls to player
		Robot player = new Robot(ct, (height - 1), 0, Direction.EAST) {
			protected void keyTyped(char e) {
				if ((e == 'a' || e == 'A' || e == 'w' || e == 'W') && super.getDirection().equals(Direction.EAST)) {
						super.setSpeed(super.getSpeed() + 4);
						super.turnLeft();
						super.turnLeft();
						super.setSpeed(super.getSpeed() - 4);
				} else if ((e == 'd' || e == 'D' || e == 's' || e == 'S') && super.getDirection().equals(Direction.WEST)) {
						super.setSpeed(super.getSpeed() + 4);
						super.turnLeft();
						super.turnLeft();
						super.setSpeed(super.getSpeed() - 4);
				}
				// A rough attempt to keep player into specified grid limit
				if(!(super.getAvenue() == (size - 1) && super.getDirection().equals(Direction.EAST)) && !(super.getAvenue() == 0 && super.getDirection().equals(Direction.WEST)))
					super.move();
			}
		};

		player.setSpeed(player.getSpeed() + 4);

		// Array list that holds enemy robots
		ArrayList<Robot> robos = new ArrayList<Robot>();

		// A thread to create enemies
		new Thread() {
			public void run() {
				for (;;) {
					Robot enemy = new Robot(ct, 0, rnd.nextInt(size), Direction.SOUTH);
					robos.add(enemy);
					// Enable and set min and max speed to enable enemy robot's
					// speed randomization. Default speed = 2.0
					//--------------------------------------------
					// double minSpeed = 1.0;
					// double maxSpeed = 3.0;
					// enemy.setSpeed((rnd.nextDouble() * (maxSpeed - minSpeed)) + minSpeed);
					enemy.setColor(Color.black);
					RobotIcon ri = (RobotIcon) enemy.getIcon();
					enemy.setIcon(null);

					// Second thread to move the enemies
					new Thread() {
						public void run() {
							try {
								Robot x = robos.get(i);
								x.setIcon(ri);
								for (int y = 0; y < (size + 1); y++) {
									x.move();
									if (x.getIntersection() == player.getIntersection()) {
										score++;
										player.setLabel("" + score);
										x.setIcon(null);
										break;
									}
								}
								robos.remove(i);
								i++;
							} catch (Exception e) {
								// Let the bad stuff happen. We don't care since
								// most of these are must-occur robot-crash exceptions.
							}
						}
					}.start();
					try {
						// This is what controls the number of enemies appearing at once. Increase value to decrease enemy count.
						Thread.sleep(1000);
						// Of course there are better ways, like TaskSchedular.
						// But my motive here is to keep this class simple
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

}
