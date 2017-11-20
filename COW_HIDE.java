package main;

import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.GroundItem;

import java.awt.*;


@ScriptManifest(author = "Brian", info = "Cowhide collector", name = "COWHIDE2Bank", version = 0, logo = "")
public class COW_HIDE extends Script {

	private long CowhideCollected;
	private long prevInvCount;
	private long timeBegan;
	private long timeRan;
	private long hph;

	@Override
	public void onStart() {
		prevInvCount = getInventory().getAmount("COWHIDE");
		timeBegan = System.currentTimeMillis();
	}

	private enum State {
		COLLECT, FULL, WAIT
	};

	private State getState() {
		if (inventory.isFull())
			return State.FULL;
		if (!inventory.isFull())
			return State.COLLECT;
		return State.WAIT;
	}

	@SuppressWarnings("deprecation")
	@Override
	public int onLoop() throws InterruptedException {
		long invCount = getInventory().getAmount("COWHIDE");
		if (invCount > prevInvCount)
			setCOWHIDECollected(getCOWHIDECollected()
					+ (invCount - prevInvCount));
		prevInvCount = invCount;

		GroundItem COWHIDE = groundItems.closest("COWHIDE");
		switch (getState()) {
		case COLLECT:

			if (COWHIDE != null && getMap().canReach(COWHIDE))
				COWHIDE.interact("take");
			else
				walking.webWalk(new Position(3256, 3278, 0));
			sleep(2000);

			break;

		case FULL:

			walking.webWalk(new Position(3209, 3220, 2));
			if (bank.isOpen()) {
				bank.depositAll();
			} else {
				objects.closest("Bank booth").interact("Bank");
			}

			break;

		case WAIT:
		}

		return (random(400, 1500));
	}

	@Override
	public void onExit() {
		log("THANKS APAEC FOR ALL THE HELP");
	}

	@Override
	public void onPaint(Graphics2D g) {

		hph = (int) (CowhideCollected / ((System.currentTimeMillis() - timeBegan) / 3600000.0D));
		timeRan = System.currentTimeMillis() - this.timeBegan;
		Graphics2D gr = (Graphics2D) g;

		gr.setColor(Color.WHITE);
		gr.setFont(new Font("Arial", Font.BOLD, 12));
		g.drawString(formatTime(timeRan), 440, 25);
		gr.drawString("Time:", 400, 25);
		gr.drawString("" + CowhideCollected, 440, 40);
		gr.drawString("Hides:", 400, 40);
		gr.drawString("Hides/h:", 400, 65);
		g.drawString("" + hph, 450, 65);
		gr.setColor(Color.YELLOW);
		g.drawString("Gold/h", 400, 78);
		g.drawString("" + hph * 120, 450, 78);
		g.drawString("Gold:", 400, 52);
		g.drawString("" + CowhideCollected * 120, 440, 52);
		g.setColor(Color.WHITE);
		g.drawRect(390, 10, 100, 75);

	}

	public String formatTime(long ms) {

		long s = ms / 1000, m = s / 60, h = m / 60, d = h / 24;
		s %= 60;
		m %= 60;
		h %= 24;

		return d > 0 ? String.format("%02d:%02d:%02d:%02d", d, h, m, s)
				: h > 0 ? String.format("%02d:%02d:%02d", h, m, s) : String
						.format("%02d:%02d", m, s);
	}

	public long getCOWHIDECollected() {
		return CowhideCollected;
	}

	public void setCOWHIDECollected(long COWHIDECollected) {
		CowhideCollected = COWHIDECollected;
	}

}
