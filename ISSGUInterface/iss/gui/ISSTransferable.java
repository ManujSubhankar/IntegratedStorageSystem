package iss.gui;

import iss.ISSFile;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
/**
 * The transferable class which enables writing of ArrayList<ISSFile> into the clipboard.
 * @author Basanta Sharma
 *
 */
public class ISSTransferable implements Transferable {
	
	private ArrayList<ISSFile> issfiles;
	private static DataFlavor flavor;
	
	/**
	 * Static Method which provides an instance of ISSFileListFlavor
	 * @return static ISSFileListFlavor instance
	 */
	static DataFlavor getISSFileListFlavor () {
		if (flavor == null) {
			Class<?> ISSfilelist= null;
			try {
				ISSfilelist= Class.forName("java.util.ArrayList");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			flavor= new DataFlavor(ISSfilelist,"ISSFile");
		}
		return flavor;
	}
	
	public ISSTransferable (ArrayList<ISSFile> files) {
		issfiles= files;
	}
	
	@Override
	public DataFlavor[] getTransferDataFlavors() {
		DataFlavor[] flavors= new DataFlavor[2];
		flavors[0]= getISSFileListFlavor();
		flavors[1]= DataFlavor.stringFlavor;
		return flavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return getISSFileListFlavor().equals(flavor) || DataFlavor.stringFlavor.equals(flavor);
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		if (!isDataFlavorSupported(flavor))
			throw new UnsupportedFlavorException(flavor);
		if (getISSFileListFlavor().equals(flavor)) {
			return issfiles.clone();
		} else {
			String s= "", p= "";
			for (int i= 0; i < issfiles.size(); i++) {
				if (i == 0) {
					p= issfiles.get(0).getName();
					continue;
				}
				s+= p + issfiles.get(i).getName() + "\n";
			}
			return s;
		}
	}

}