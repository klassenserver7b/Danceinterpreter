package de.danceinterpreter.threads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.danceinterpreter.AppModes;
import de.danceinterpreter.Main;
import de.danceinterpreter.graphics.SongWindow;
import de.danceinterpreter.songprocessing.DanceInterpreter;
import de.danceinterpreter.songprocessing.Songdata;

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
			long time = System.currentTimeMillis();

			while (!Main.exit) {
				if (System.currentTimeMillis() >= time + 5000) {
					time = System.currentTimeMillis();

					Songdata data = appmode.getDataProvider().provideSongData();

					if (data != null) {
						log.info(data.getTitle() + ", " + data.getAuthor() + ", " + data.getDance() + ", "
								+ data.getDuration());

						DanceInterpreter di = Main.Instance.getDanceInterpreter();

						if (di.getWindow() == null) {
							di.setSongWindow(new SongWindow(data.getTitle(), data.getAuthor(), data.getDance(),
									data.getImageURL()));
						} else {
							di.updateSongWindow(data.getTitle(), data.getAuthor(), data.getDance(), data.getImageURL());
						}
					}
				}
			}
		}

	}

	public void interrupt() {
		t.interrupt();
		log.info("interrupted");
	}

}
