package iss.gui;

import iss.ISSFile;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.util.ArrayList;

/**
 * The transferable class to enable ArrayList<ISSFile> and ArrayList<File> to the clipboard.
 * 
 * @author Basanta Sharma
 *
 */
public class FileTransferable implements Transferable {
	
	private ArrayList<File> filelist;
	private ArrayList<ISSFile> isslist;
	
	public FileTransferable (ArrayList<File> filelist, ArrayList<ISSFile> ISSlist) {
		this.filelist= filelist;
		this.isslist= ISSlist;
	}

	// implemented method of Transferable interface
	@Override
	public DataFlavor[] getTransferDataFlavors() {
		DataFlavor[] flavors= new DataFlavor[3];
		flavors[0]= DataFlavor.javaFileListFlavor;
		flavors[1]= ISSTransferable.getISSFileListFlavor();
		flavors[2]= DataFlavor.stringFlavor;
		return flavors;
	}

	// implemented method of Transferable interface
	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		if (DataFlavor.javaFileListFlavor.equals(flavor) || DataFlavor.stringFlavor.equals(flavor)
				|| ISSTransferable.getISSFileListFlavor().equals(flavor))
			return true;
		return false;
	}

	
	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException{
		if (!isDataFlavorSupported(flavor))
			throw new UnsupportedFlavorException(flavor);
		if (flavor.equals(DataFlavor.javaFileListFlavor))
			return filelist.clone();
		else if (flavor.equals(ISSTransferable.getISSFileListFlavor()))
			return isslist.clone();
		else {
			String s= "";
			for (int i= 0; i < filelist.size(); i++) {
				s+= filelist.get(i).toString() + "\n";
			}
			return s;
		}
	}
	
	/*public static void main (String[] args) {
		System.out.println (FileTransferable.getISSFileListFlavor());
		System.out.println (DataFlavor.javaFileListFlavor);
	}*/
	
}

