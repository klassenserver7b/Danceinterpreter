/**
 * 
 */
package de.klassenserver7b.danceinterpreter.graphics.listener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import de.klassenserver7b.danceinterpreter.Main;
import de.klassenserver7b.danceinterpreter.graphics.PlaylistViewGenerator;
import de.klassenserver7b.danceinterpreter.songprocessing.dataprovider.PlaylistSongDataProvider;

/**
 * 
 */
public class PlaylistViewClickListener implements MouseListener {

	private final int clickid;
	private final ClickListenerType type;
	private final PlaylistViewGenerator playlistViewGen;

	public PlaylistViewClickListener(ClickListenerType type, int id, PlaylistViewGenerator playlistViewGen) {
		this.clickid = id;
		this.type = type;
		this.playlistViewGen = playlistViewGen;
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		switch (type) {

		case PLAYLIST -> {
			PlaylistSongDataProvider pd = (PlaylistSongDataProvider) Main.Instance.getAppMode().getDataProvider();
			pd.setDirection(PlaylistSongDataProvider.Direction.SAME);
			pd.setPosition(this.clickid + 1);
			pd.provideAsync();
		}

		case STATIC_SONG -> {
			Main.Instance.getSongWindowServer().provideData(playlistViewGen.getStaticSong(clickid));
		}

		case STATIC_LABEL -> {
			Main.Instance.getSongWindowServer().provideData(playlistViewGen.getStaticLabel(clickid));
		}

		default -> throw new IllegalArgumentException("Unexpected value: " + type);
		}

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// Nothing to do here

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// Nothing to do here

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// Nothing to do here

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// Nothing to do here

	}

	public enum ClickListenerType {
		PLAYLIST(0), STATIC_SONG(1), STATIC_LABEL(2), UNKNOWN(-1);

		private final int id;

		private ClickListenerType(int id) {
			this.id = id;
		}

		public ClickListenerType byId(int id) {
			for (ClickListenerType t : values()) {
				if (t.getId() == id) {
					return t;
				}
			}
			return UNKNOWN;
		}

		public int getId() {
			return id;
		}
	}
}
