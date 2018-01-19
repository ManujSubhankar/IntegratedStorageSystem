package iss.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;
import javax.swing.JTextPane;

public class ImplementedMouseMotionListeners {
	
	static class MML_IconButton implements MouseMotionListener {
		@Override
		public void mouseDragged(MouseEvent e) {}
		@Override
		public void mouseMoved(MouseEvent e) {
			Object source= e.getSource();
			IconButton ib= null;
			
			if (source instanceof IconButton) {
				ib= (IconButton) e.getSource();
			}else if (source instanceof JTextPane) {
				ib= (IconButton)((JTextPane)source).getParent();
			}else {
				ib= (IconButton)((JPanel)source).getParent();
			}
			
			if (ib.isSelected()) {
				ib.setBorder(ib.getFocusedBorder());
			} else {
				ib.setFocusedLF();
			}
			
			ViewPanel p= (ViewPanel)ib.getParent();
			IconButton focusedIcon= p.getFocusedIcon();
			if (focusedIcon != null && focusedIcon != ib && focusedIcon != p.getRenamingIcon()) {
				if (focusedIcon.isSelected()) {
					focusedIcon.setSelectedLF();
					focusedIcon.setBorder(focusedIcon.getEmptyBorder());
				}
				else {
					focusedIcon.setDefaultLF();
				}
			}
			p.setFocusedIcon(ib);
		}
	}
}
