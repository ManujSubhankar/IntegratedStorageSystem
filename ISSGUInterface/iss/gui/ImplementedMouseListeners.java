package iss.gui;

import iss.FileType;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JComponent;

public class ImplementedMouseListeners {
	
	static class ML_ViewPanel extends MouseAdapter {
		
		public void mousePressed (MouseEvent e) {
			ViewPanel panel= (ViewPanel) e.getSource();
	        if (e.isPopupTrigger())
	            doPop(e);
	        else {
	        	panel.setStartPos (e.getPoint());
	        }
		}
		
		public void mouseReleased (MouseEvent e) {
			ViewPanel panel= (ViewPanel) e.getSource();
	        if (e.isPopupTrigger())
	            doPop(e);
	        else {
				panel.setStartPos(null);
				panel.setEndPos(null);
				panel.repaint();
	        }
		}
		
		public void mouseDragged(MouseEvent e) {
			ViewPanel panel= (ViewPanel) e.getSource();
			ArrayList<IconButton> selectedIcons= panel.getSelectedIcons();
			if (panel.getStartPos() == null) panel.setStartPos(e.getPoint());
			panel.setEndPos(e.getPoint());
			panel.getSelector().setFrameFromDiagonal(panel.getStartPos(), panel.getEndPos());
			int n= panel.getComponentCount();
			for (int i= 0; i < n; i++) {
				IconButton but= (IconButton) panel.getComponent(i);
				if (panel.getSelector().getBounds().intersects(but.getBounds())) {
					if (!but.isSelected()) {
						but.setSelected(true);
						selectedIcons.add(but);
					}
				} else if (!e.isControlDown()) {
					if (but.isSelected()) {
						selectedIcons.remove(but);
						but.setSelected(false);
						but.setDefaultLF();
					}
				}
			}
			panel.repaint();
		}
		
		public void mouseMoved(MouseEvent e) {
			ViewPanel vp= (ViewPanel)e.getSource();
			IconButton focusedIcon= vp.getFocusedIcon();
			if (vp.hasRenamingIcon()) return;
			if (focusedIcon != null) {
				if (focusedIcon.isSelected()) {
					focusedIcon.setSelectedLF();
					focusedIcon.setBorder(focusedIcon.getEmptyBorder());
				}
				else {
					focusedIcon.setDefaultLF();
					vp.setFocusedIcon(null);
				}
			}
		}
		
		public void mouseClicked (MouseEvent e) {
			ViewPanel panel= (ViewPanel) e.getSource();
			if (panel.hasRenamingIcon())
				panel.getRenamingIcon().resetAfterRename(true);
			if (!e.isControlDown()) {
				panel.clearSelection();
			}
		}

	    private void doPop(MouseEvent e){
	    	ViewPanel panel= (ViewPanel) e.getSource();
	        PopupMenus.getPanelPopup().show(panel, e.getX(), e.getY());
	    }
	}
	
	static class ML_IconButton extends MouseAdapter {
		
		public void mouseClicked (MouseEvent e) {
			IconButton but= (IconButton) ((JComponent) e.getSource()).getParent();
			
			ViewPanel panel= ISSState.panelInView;
			
			int click= e.getClickCount();
			if (click == 2) {
				but.open();
			} else {
				if (panel.hasRenamingIcon()) {
					panel.getRenamingIcon().resetAfterRename(true);
				}
				but.select(e);// performs the selection/deselection operation
			}
		}
		
		public void mousePressed (MouseEvent e) {
			if (e.isPopupTrigger()) {
				IconButton but= (IconButton) ((Component)e.getSource()).getParent();
				but.select(e);
				if (but.getRepresentingFile().fileType.equals(FileType.Directory))
					PopupMenus.getFolderIconPopup().show(but,e.getX(),e.getY());
				else
					PopupMenus.getFileIconPopup().show(but,e.getX(),e.getY());
			}
		}
	}

}
