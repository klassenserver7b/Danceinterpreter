
package de.danceinterpreter.songprocessing.dataprovider;

import de.danceinterpreter.songprocessing.SongData;

/**
**/
 
public interface SongDataProvider {
	
	SongData provideSongData();
	
	void provideAsync();

}
