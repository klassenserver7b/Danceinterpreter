package de.klassenserver7b.danceinterpreter.threads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.klassenserver7b.danceinterpreter.AppModes;
import de.klassenserver7b.danceinterpreter.Main;
import de.klassenserver7b.danceinterpreter.songprocessing.SongData;

public class SongCheckThread implements Runnable {

	private final Thread t;
	private final Logger log;
	private final AppModes appmode;

	public SongCheckThread(AppModes mode) {
		this.t = new Thread(this, "SongCheck");
		this.log = LoggerFactory.getLogger(this.getClass());
		this.appmode = mode;
		this.t.start();
	}

	@Override
	public void run() {
		if (!this.t.isInterrupted()) {

			while (!Main.exit) {

				SongData data = this.appmode.getDataProvider().provideSongData();

				if (data != null) {
					int mins = (int) (data.getDuration() / 60);
					this.log.info(data.getTitle() + ", " + data.getAuthor() + ", " + data.getDance() + ", " + mins + "min "
							+ (data.getDuration() - mins * 60) + "s");

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
		this.t.interrupt();
		this.log.info("interrupted");
	}

}
