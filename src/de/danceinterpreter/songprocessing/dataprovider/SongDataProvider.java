/**
 * 
 */
package de.danceinterpreter.songprocessing.dataprovider;

import de.danceinterpreter.songprocessing.SongData;

/**
 * @author Felix
 *
 */
public interface SongDataProvider {
	
	public SongData provideSongData();
	
	public void provideAsynchronous();

}
