package caesium.core;

import caesium.model.CaesiumRepository;

public class ScheduleLauncher implements Runnable {

	CaesiumRepository caesiumStatus;

	public ScheduleLauncher() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		while (caesiumStatus.isRunScheduling()) {
			try {
				
				
				
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
