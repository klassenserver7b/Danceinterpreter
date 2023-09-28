
package de.klassenserver7b.danceinterpreter.songprocessing.dataprovider;

import de.klassenserver7b.danceinterpreter.songprocessing.SongData;

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
