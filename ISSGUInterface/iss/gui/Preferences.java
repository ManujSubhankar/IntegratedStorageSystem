package iss.gui;

import java.awt.Color;
import java.awt.Dimension;

public class Preferences {
	
	//color for selection
	private static Color selectionColor=  Color.decode("#F95505");
	
	//size of the file icon
	private static Dimension iconSize= new Dimension (100,100);
	
	//horizontal and vertical gaps between the icons
	private static int horizontalGap= 80, verticalGap= 30;

	//Getters and setters
	public static Color getSelectionColor() {
		return selectionColor;
	}

	public static void setSelectionColor(Color sColor) {
		selectionColor = sColor;
	}

	public static Dimension getIconSize() {
		return iconSize;
	}

	public static void setIconSize(Dimension iSize) {
		iconSize = iSize;
		ISSIcons.fileIcons.clear();
		ISSIcons.folderIcons.clear();
		ISSIcons.buttonIcons.clear();
		ISSIcons.loadIcons();
	}
	
	public static int getHorizontalGap() {
		return horizontalGap;
	}

	public static void setHorizontalGap(int hGap) {
		horizontalGap = hGap;
	}

	public static int getVerticalGap() {
		return verticalGap;
	}

	public static void setVerticalGap(int vGap) {
		verticalGap = vGap;
	}
}
