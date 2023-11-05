/**
 * 
 */
package de.klassenserver7b.danceinterpreter.graphics.listener;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.klassenserver7b.danceinterpreter.Main;
import de.klassenserver7b.danceinterpreter.loader.FileLoader;

/**
 * @author K7
 */
public class FileDropListener implements DropTargetListener {

	private final Logger log;

	/**
	 * 
	 */
	public FileDropListener() {
		this.log = LoggerFactory.getLogger(this.getClass());
	}

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
		// Nothing to do here

	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		// Nothing to do here

	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
		// Nothing to do here

	}

	@Override
	public void dragExit(DropTargetEvent dte) {
		// Nothing to do here

	}

	@Override
	public void drop(DropTargetDropEvent dtde) {

		dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
		Transferable transferable = dtde.getTransferable();
		try {

			DataFlavor[] flavors = transferable.getTransferDataFlavors();

			// Loop through the flavors
			for (DataFlavor flavor : flavors) {

				// If the drop items are files
				if (flavor.isFlavorJavaFileListType()) {

					// Get all of the dropped files
					@SuppressWarnings("unchecked")
					List<File> files = (List<File>) transferable.getTransferData(flavor);

					Main.Instance.getSongWindowServer().provideData(FileLoader.getDataFromFile(files.get(0)));

				}

			}

			dtde.dropComplete(true);

		} catch (UnsupportedFlavorException e) {
			this.log.error("Drop was not a file");
		} catch (IOException e) {
			this.log.error(e.getMessage(), e);
		}

	}

	/*
	 * if (flavor.isFlavorTextType()) {
	 * 
	 * if (transferable.getTransferData(flavor) instanceof String &&
	 * flavor.getHumanPresentableName().equalsIgnoreCase("text/plain")) {
	 * 
	 * String data = ((String) transferable.getTransferData(flavor));
	 * 
	 * System.err.println(data);
	 * 
	 * data = data.replace("file://", "");
	 * 
	 * Path path = Paths.get(data);
	 * 
	 * File f = path.toFile();
	 * 
	 * System.err.println( "Data: " + data + "; exists: " + f.exists() + "; Path: "
	 * + f.getAbsolutePath());
	 * 
	 * }
	 * 
	 * }
	 * 
	 */

}
