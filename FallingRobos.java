import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import becker.robots.City;
import becker.robots.Direction;
import becker.robots.Robot;
import becker.robots.Wall;
import becker.robots.icons.RobotIcon;

public class FallingRobos {
	public static int i;

	public volatile static int score = 0;

	public static void main(String args[]) {
		int size = 20; // The width of area
		int height = 19; // Enemies start from this height
		City ct = new City((height + 1), size);
		for (int i = 0; i < size; i++)
			new Wall(ct, height, i, Direction.SOUTH);
		Random rnd = new Random();
		Robot player = new Robot(ct, (height - 1), 0, Direction.EAST) {
			protected void keyTyped(char e) {
				if (e == 'a' || e == 'A') {
					if (super.getDirection().equals(Direction.EAST)) {
						super.setSpeed(super.getSpeed() + 4);
						super.turnLeft();
						super.turnLeft();
						super.setSpeed(super.getSpeed() - 4);
					}
				} else if (e == 'd' || e == 'D') {
					if (super.getDirection().equals(Direction.WEST)) {
						super.setSpeed(super.getSpeed() + 4);
						super.turnLeft();
						super.turnLeft();
						super.setSpeed(super.getSpeed() - 4);
					}
				}
				super.move();
				// A rough attempt to send back player into specified grid limit
				// if he tries to go out. This is not foolproof.
				if (super.getAvenue() > (size - 1) && super.getDirection().equals(Direction.EAST)) {
					super.setSpeed(super.getSpeed() + 4);
					super.turnLeft();
					super.turnLeft();
					while (super.getAvenue() > (size - 1))
						super.move();
					super.setSpeed(super.getSpeed() - 4);
				} else if (super.getAvenue() < 0 && super.getDirection().equals(Direction.WEST)) {
					super.setSpeed(super.getSpeed() + 4);
					super.turnLeft();
					super.turnLeft();
					while (super.getAvenue() < 0)
						super.move();
					super.setSpeed(super.getSpeed() - 4);
				}
			}
		};

		final double defSpeed = player.getSpeed();
		player.setSpeed(defSpeed + 2);

		// Array list that holds enemy robots
		List<Robot> robos = new ArrayList<Robot>();

		i = 0; // Counter to iterate through the robot array created above
		Thread createRobo = new Thread() {
			public void run() {
				for (;;) {
					// First thread to create enemies
					Robot enemy = new Robot(ct, 0, rnd.nextInt(size), Direction.SOUTH);
					robos.add(enemy);
					// Enable and set min and max speed to enable enemy robot's
					// speed randomization. Default speed = 2.0
					// double minSpeed = 1.0;
					// double maxSpeed = 2.0;
					// enemy.setSpeed((rnd.nextDouble() * (maxSpeed - minSpeed))
					// + minSpeed);
					enemy.setColor(Color.black);
					RobotIcon ri = (RobotIcon) enemy.getIcon();
					enemy.setIcon(null);
					Thread moveRobo = new Thread() {
						public void run() {
							try {
								// Second thread to move the robot
								Robot x = robos.get(i);
								x.setIcon(ri);
								int y = 0;
								while (y < (size + 1)) {
									x.move();
									if (x.getIntersection() == player.getIntersection()) {
										score++;
										player.setLabel("" + score);
										x.setIcon(null);
										break;
									}
									y++;
								}
								robos.remove(i);
								i++;
							} catch (Exception e) {
								// Let the bad stuff happen. We don't care since
								// most of these are must-occur exceptions.
							}
						}
					};
					moveRobo.start();
					try {
						Thread.sleep(100); // This is what controls the number
											// of enemies appearing at once.
											// Increase value to decrease enemy
											// count.
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		createRobo.start();
	}

}
