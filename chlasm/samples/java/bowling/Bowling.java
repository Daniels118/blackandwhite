/**	An example of the Challenge system in action!
 *	In this document we describe a complete script... Bowling!
 */
package bowling;

import chl.lang.*;

public abstract class Bowling {
	private static final MobileStaticInfo BOWLING_BALL = MobileStaticInfo.TOY_BOWLING_BALL;
	private static final VillagerInfo MALE = VillagerInfo.NORSE_FARMER_MALE;

	private static boolean playingVoice;
	private static int score;
	private static boolean end;


	public static void myChallengeNotify(GameThingWithPos location, float radius) {
		try (Cinema cinema = Cinema.begin()) {
			EvilSpirit.eject();
			EvilSpirit.pointTo(location);
			cinema.say("Have a game of bowling!");
			while (!cinema.hasBeenRead()) {}
			cinema.say("Try to get a strike!", true);
			while (!cinema.hasBeenRead()) {}
		}
	}
	
	
	/**		Control the behaviour of the bowling ball.
	 *				Control the behaviour of the bowling ball, which is passed to this script as a parameter.
	 */
	public static void bowlingBall(LHObject ball) {
		Task.sleep(0.5);
		Marker initialPos = Marker.create(ball.getPosition());	//To update elevetion
		boolean moving = false;
		
		while (!end) {
			if (!ball.exists() && ball.isInHand() == 0 || ball.getPosition().getDistance(initialPos.getPosition()) > 20) {
				try (Dialogue dialog = Dialogue.begin()) {
					dialog.say("Ball lost");
					Task.sleep(1.0);
				}
				ball.delete();
				ball = Factory.createMobileStatic(BOWLING_BALL, initialPos.getPosition());
			} else if (!ball.getPosition().equals(initialPos.getPosition()) && !moving) {
				ball.setPosition(initialPos.getPosition());
			} else if (ball.isFlying() && !moving) {
				moving = true;
				try (Cinema cinema = Cinema.begin()) {
					cinema.say("Slow-mo");
					cinema.follow(ball, 10);
					cinema.setGameSpeed(0.5);
					Timer timer = Timer.create(5);
					while (ball.isFlying()
							&& ball.getPosition().getDistance(initialPos.getPosition()) <= 20
							&& timer.getTimeRemaining() > 0
							&& score != 6) {}
					cinema.setGameSpeed(1.0);
				}
			} else if (!ball.isFlying() && moving) {
				moving = false;
			}
		}
	}
	
	
	/**		Control the behaviour of a bowling pin.
	 */
	public static void bowlingPin(Marker manPos) {
		Living man = Factory.createVillager(MALE, manPos.getPosition());
		manPos = Marker.create(man.getPosition());	//To update elevetion
		boolean standing = true;
		boolean moving = false;
		
		while (!end) {
			if (!man.exists()) {
				man = Factory.createVillager(MALE, manPos.getPosition());
				standing = true;
				moving = false;
			} else if (man.getHealth() <= 0.1 && !man.isFlying()) {
				try (Dialogue dialog = Dialogue.begin()) {
					dialog.say("Man died");
					Task.sleep(1.0);
				}
				man.delete();
				man = Factory.createVillager(MALE, manPos.getPosition());
				if (!standing) {
					score--;
					standing = true;
				}
			} else if (man.isFlying() && standing) {
				score++;
				standing = false;
				if (!playingVoice) {
					playingVoice = true;
					try (Dialogue dialog = Dialogue.begin()) {
						dialog.say("Man got knocked over");
						Sound.startSaySound(Math.random(HelpText.THROW_BLOKE_06, HelpText.THROW_BLOKE_14), man.getPosition());
						Task.sleep(2.0);
					}
					playingVoice = false;
				}
			} else if (!man.isFlying() && !standing) {
				score--;
				standing = true;
			} else if (!man.getPosition().equals(manPos.getPosition()) && !moving) {
				man.moveTo(manPos.getPosition());
				moving = true;
			} else if (man.getPosition().equals(manPos.getPosition()) && moving) {
				moving = false;
			}
		}
	}
	
	
	/**		Start everything up, then monitor the progress of the game.
	 */
	@Export("Bowling")
	public static void bowling() {
		Marker pinPos = Marker.create(1920, 2240);
		Marker ballPos = Marker.create(pinPos.getPosition().add(10, 10));
		LHObject ball = Factory.createMobileStatic(BOWLING_BALL, ballPos.getPosition());
		float size = 0.7;
		Influence influence = Influence.create(2185.616, 94.651, 2409.528, 2000);
		
		try (Camera camera = Camera.begin()) {
			camera.setPosition(ball.getPosition().add(15, 10, 15));
			camera.setFocus(ball.getPosition());
		}
		
		playingVoice = false;
		score = 0;
		end = false;
		Task.start("bowlingBall", ball);
		Task.start("bowlingPin", Marker.create(pinPos.getPosition().add(0, 0, 0)));
		Task.start("bowlingPin", Marker.create(pinPos.getPosition().add(-size, 0, 2 * size)));
		Task.start("bowlingPin", Marker.create(pinPos.getPosition().add(size, 0, 2 * size)));
		Task.start("bowlingPin", Marker.create(pinPos.getPosition().add(-2 * size, 0, 4 * size)));
		Task.start("bowlingPin", Marker.create(pinPos.getPosition().add(0, 0, 4 * size)));
		Task.start("bowlingPin", Marker.create(pinPos.getPosition().add(2 * size, 0, 4 * size)));
		myChallengeNotify(ball, 60);
		
		while (score < 6) {}
		
		end = true;
		
		try (Dialogue dialog = Dialogue.begin()) {
			EvilSpirit.eject();
			dialog.say("You got a strike!", true);
			while (!dialog.hasBeenRead()) {}
			dialog.say("Thanks for playing!", true);
			while (!dialog.hasBeenRead()) {}
		}
	}
}