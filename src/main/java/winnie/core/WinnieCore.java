package winnie.core;


public class WinnieCore {

	private static WinnieCore instance = null;

	private WinnieCore() {

	}

	public static WinnieCore getInstance() {
		if (instance == null) {
			synchronized (WinnieCore.class) {
				if (instance == null) {
					instance = new WinnieCore();
				}
			}
		}
		return instance;
	}

}
