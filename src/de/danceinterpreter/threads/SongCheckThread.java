package de.danceinterpreter.threads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.danceinterpreter.AppModes;
import de.danceinterpreter.Main;
import de.danceinterpreter.songprocessing.SongData;

public class SongCheckThread implements Runnable {

	private final Thread t;
	private final Logger log;
	private final AppModes appmode;

	public SongCheckThread(AppModes mode) {
		t = new Thread(this, "SongCheck");
		log = LoggerFactory.getLogger(this.getClass());
		appmode = mode;
		t.start();
	}

	@Override
	public void run() {
		if (!t.isInterrupted()) {

			while (!Main.exit) {

				SongData data = appmode.getDataProvider().provideSongData();

				if (data != null) {
					log.info(data.getTitle() + ", " + data.getAuthor() + ", " + data.getDance() + ", "
							+ data.getDuration());

					Main.Instance.getSongWindowServer().provideData(data);

				}

				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void interrupt() {
		t.interrupt();
		log.info("interrupted");
	}

}
