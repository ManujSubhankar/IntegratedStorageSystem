package iss.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
/**
 * Packaging class for all classes implementing KeyListener
 * @author Basanta Sharma
 *
 */
public class ImplementedKeyListeners {
	
	/**
	 * KeyListener called when user pressed ENTER or ESCAPE after renaming a file
	 * @author Basanta Sharma
	 *
	 */
	static class KL_RenameDone implements KeyListener {
		@Override
		public void keyTyped(KeyEvent e) {}
		@Override
		public void keyPressed(KeyEvent e) {
			IconLabelPane pane= (IconLabelPane)e.getSource();
			IconButton but= (IconButton) pane.getParent();
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				but.resetAfterRename(true);
			}
			else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				but.resetAfterRename(false);
			}
		}
		@Override
		public void keyReleased(KeyEvent e) {}
	}
	
	static class KL_ViewPanel implements KeyListener {
		@Override
		public void keyTyped(KeyEvent e) {}

		@Override
		public void keyPressed(KeyEvent e) {
			ViewPanel panel= (ViewPanel) e.getSource();
			int code= e.getKeyCode();
			switch (code) {
			case KeyEvent.VK_DELETE:
				BackEnd.deleteFileInISS(panel.getWorkingDirectory(), panel.getSelectedFiles());
				return;
			default: return;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {}
	}
	
}
