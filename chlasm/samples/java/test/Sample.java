package test;

import chl.lang.*;
import chl.lang.Math;

@Challenge("LandControlAll")
public abstract class Sample {
	private static Flock flock;
	
	/**
	 * Test complex math expression with parentheses and several operators with different priority
	 */
	private static void testMath() {
		try (Dialogue dialog = Dialogue.begin()) {
			dialog.say("Test math", true);
			while (!dialog.hasBeenRead()) {}
			
			while (true) {
				float v = (3 * 2 + 5 * 3 - (1 + 3 * -2)) % 16;
				if (v != 10f) {
					dialog.say("Expression failed", true);
					while (!dialog.hasBeenRead()) {}
				} else {
					break;
				}
			}
			dialog.say("Expression OK");
			Task.sleep(0.75);
			
			while (true) {
				float v = Math.signum(-3);
				if (v != -1) {
					dialog.say("signum(-3) failed");
					Task.sleep(0.5);
				} else {
					break;
				}
			}
			while (true) {
				float v = Math.signum(0);
				if (v != 0) {
					dialog.say("signum(0) failed");
					Task.sleep(0.5);
				} else {
					break;
				}
			}
			while (true) {
				float v = Math.signum(5);
				if (v != 1) {
					dialog.say("signum(5) failed");
					Task.sleep(0.5);
				} else {
					break;
				}
			}
			dialog.say("signum OK");
			Task.sleep(0.75);
			
			while (true) {
				float v = Math.round(-3.14);
				if (v != -3f) {
					dialog.say("round(-3.14) failed");
					Task.sleep(0.5);
				} else {
					break;
				}
			}
			while (true) {
				float v = Math.round(-1.7);
				if (v != -2f) {
					dialog.say("round(-1.7) failed");
					Task.sleep(0.5);
				} else {
					break;
				}
			}
			while (true) {
				float v = Math.round(3.14);
				if (v != 3) {
					dialog.say("round(3.14) failed");
					Task.sleep(0.5);
				} else {
					break;
				}
			}
			dialog.say("round OK");
			Task.sleep(0.75);
			
			while (true) {
				float v = Math.reduceRadians(-Math.PI);
				if (v != -Math.PI && v != Math.PI) {
					dialog.say("reduceRadians(-Math.PI) failed");
					Task.sleep(0.5);
				} else {
					break;
				}
			}
			while (true) {
				float v = Math.reduceRadians(-Math.PI / 2);
				if (v != -Math.PI / 2) {
					dialog.say("reduceRadians(-Math.PI / 2) failed");
					Task.sleep(0.5);
				} else {
					break;
				}
			}
			while (true) {
				float v = Math.reduceRadians(0);
				if (v != 0) {
					dialog.say("reduceRadians(0) failed");
					Task.sleep(0.5);
				} else {
					break;
				}
			}
			while (true) {
				float v = Math.reduceRadians(-2.25 * Math.PI);
				if (Math.abs(v - (-0.25 * Math.PI)) > 0.001) {
					dialog.say("reduceRadians(-2.25 * Math.PI) failed");
					Task.sleep(0.5);
				} else {
					break;
				}
			}
			dialog.say("reduceRadians OK");
			Task.sleep(0.75);
			
			dialog.say("testing sin/cos (visual)");
			flock = Flock.create(new Coord(1920, 2240));
			final float r = 10;
			for (float a = 0; a <= 1.9999 * Math.PI; a += Math.PI / 6) {
				float x = Math.cos(a) * r;
				float z = Math.sin(a) * r;
				/*LHObject obj = Factory.createMobileStatic(MobileStaticInfo.SINGING_STONE_1,
						Math.toDegrees(a) + 90f, 1f, new Coord(1920, 2240).add(x, z));*/
				LHObject obj = Factory.createVillager(VillagerInfo.CELTIC_HOUSEWIFE_FEMALE, new Coord(1920, 2240).add(x, z));
				flock.attach(obj);
			}
			Task.sleep(2f);
			
			dialog.say("testing for-each loop (visual)");
			final float r2 = 20;
			float a = 0;
			@NoYield
			for (Living obj : flock) {
				float x = Math.cos(a) * r2;
				float z = Math.sin(a) * r2;
				obj.moveTo(new Coord(1920, 2240).add(x, z));
				a += Math.PI / 6;
			}
			Task.sleep(2f);
		}
	}
	
	/**
	 * Test ternary conditional operator. This will also test parameter code relocation (i.e. update jump address)
	 * since the TCO is passed to a function with non-linear parameters mapping.
	 */
	private static void testTernaryConditionalOperator() {
		try (Dialogue dialog = Dialogue.begin()) {
			dialog.say("Test ternary conditional operator", true);
			while (!dialog.hasBeenRead()) {}
			@NoYield
			for (int i = 1; i < 6; i++) {
				//String msg = i % 2 == 0 ? "$d is even" : "$d is odd";
				dialog.sayWithNumber(i % 2 == 0 ? "$d is even" : "$d is odd", i);
				Task.sleep(1f);
			}
		}
	}
	
	/**
	 * Test for loop with continue statement.
	 */
	private static void testFor() {
		try (Dialogue dialog = Dialogue.begin()) {
			dialog.say("Test for loop (1 to 5 skipping 3)", true);
			while (!dialog.hasBeenRead()) {}
			for (int i = 1; i < 6; i++) {
				if (i == 3) {
					continue;
				}
				dialog.sayWithNumber("$d", i);
				Task.sleep(1f);
			}
		}
	}
	
	/**
	 * Test switch case with multiple labels.
	 * @param v
	 */
	private static void testSwitch1(int v) {
		try (Dialogue dialog = Dialogue.begin()) {
			switch (v) {
				case 1:
					dialog.sayWithNumber("$d -> 1", v);
					break;
				case 2:
					dialog.sayWithNumber("$d -> 2", v);
					break;
				case 3:
				case 4:
					dialog.sayWithNumber("$d -> 3/4", v);
					break;
				case 5:
					dialog.sayWithNumber("$d -> 5", v);
					break;
				default:
					dialog.sayWithNumber("$d -> other", v);
					break;
			}
			Task.sleep(1f);
		}
	}
	
	private static void testSwitch() {
		try (Dialogue dialog = Dialogue.begin()) {
			dialog.say("Test switch case", true);
			while (!dialog.hasBeenRead()) {}
		}
		testSwitch1(1);
		testSwitch1(2);
		testSwitch1(3);
		testSwitch1(4);
		testSwitch1(5);
		testSwitch1(6);
	}
	
	@Export("LandControlAll")
	public static void LandControlAll() {	
		Map.load("scripts/LandT.txt");
		Effects.fadein(3);
		
		Influence influence = Influence.create(2185.616, 94.651, 2409.528, 2000);
		Marker center = Marker.create(1920, 2240);
		try (Camera camera = Camera.begin()) {
			camera.setPosition(center.getPosition().add(15, 10, 15));
			camera.setFocus(center.getPosition());
		}
		
		testMath();
		testTernaryConditionalOperator();
		testFor();
		testSwitch();
	}
}