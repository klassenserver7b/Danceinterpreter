
package de.danceinterpreter.songprocessing.dataprovider;

import de.danceinterpreter.songprocessing.SongData;

/**
**/
 
public interface SongDataProvider {
	
	/**
	 * 
	 * @return
	 */
	SongData provideSongData();
	
	/**
	 * 
	 */
	void provideAsync();

}
