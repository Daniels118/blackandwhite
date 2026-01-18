/**	This sample shows how to pick coordinates.
 *  Press left CTRL key to show the coordinates of the hand.
 */
package pick_coords;

import chl.lang.*;

@Challenge("LandControlAll")
public abstract class Sample {
	public static float MyVar;
	
	public static void coordsLoop() {
		Marker refPos = Marker.create(1920, 2240);
		Influence influence = Influence.create(2185.616, 94.651, 2409.528, 2000);
		
		try (Camera camera = Camera.begin()) {
			camera.setPosition(refPos.getPosition().add(15, 10, 15));
			camera.setFocus(refPos.getPosition());
		}
		
		while (true) {
			if (Keyboard.isPressed(Keyboard.KB_LCTRL) == true) {
				displayCoords();
			}
		}
	}
	
	public static void displayCoords() {
		Marker handPos = Marker.create(Hand.getPosition());
		float x = handPos.getX();
		float z = handPos.getZ();
		
		float u = 0;
		u = x + z;
		
		try (Dialogue dialog = Dialogue.begin()) {
			dialog.sayWithNumber("X: $d", x, true);
			while (!dialog.hasBeenRead()) {}
			dialog.sayWithNumber("Z: $d", z, true);
			while (!dialog.hasBeenRead()) {}
		}
	}
	
	@Export("LandControlAll")
	public static void LandControlAll() {	
		Map.load("scripts/LandT.txt");
		Effects.fadein(3);
		Task.start("coordsLoop");
	}
}