package iss.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JTextPane;
import javax.swing.SizeRequirements;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
/**
 * Class for customized use of TextPane used to show the filename by IconButton
 * @author Basanta Sharma
 *
 */
public class IconLabelPane extends JTextPane {

	private static final long serialVersionUID = 1L;
	
	//String used to hold the previous name while renaming a file
	private String prevName;
	
	//keylistener used while rename
	private static KeyListener klistener;
	
	//Borders for name panel
	private Border renameBorder, nullBorder;
	
	//reference to the previous size when size of the name panel changes
	private Dimension prevSize;
	
	// The colors used in the different LFs of the Icon Namepane
	final static Color defaultFG= Color.black;
	final static Color focusedBG= Color.decode("#FFA17A");
	final static Color focusedFG= Color.blue;
	final static Color selectedFG= Color.blue;
	final static Color selectedBG= Preferences.getSelectionColor();
	
	//Document used by the namepane
	private StyledDocument document;
	
	public void addKeyListener () {
		addKeyListener(klistener);
	}
	
	public void removeKeyListener () {
		removeKeyListener(klistener);
	}
	
	public void setNullBorder () {
		setBorder (nullBorder);
	}
	
	public void setRenameBorder () {
		setBorder (renameBorder);
	}
	
	/**
	 * Constructor that customizes the textPane for showing filename
	 * @param name
	 */
	public IconLabelPane (String name) {

		setEditorKit(new ISSEditorKit());
		
		document = getStyledDocument();
		Style defaultStyle = document.getStyle(StyleContext.DEFAULT_STYLE);
		StyleConstants.setAlignment(defaultStyle, StyleConstants.ALIGN_CENTER);
        document.addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                insert();
            }
 
            public void removeUpdate(DocumentEvent e) {
                insert();
            }
 
            public void changedUpdate(DocumentEvent e) {
                insert();
            }
 
            public void insert() {
                SwingUtilities.invokeLater(new Runnable() {
                     public void run() {
                        Style defaultStyle = getStyle(StyleContext.DEFAULT_STYLE);
                        document.setCharacterAttributes(0, document.getLength(), defaultStyle, false);
                    }
                });
            }
        });
        setText(name);
        prevName= name;
        setEditable(false);
        setOpaque(false);
        setMaximumSize (new Dimension (100,30));
        setSelectionColor(Color.white);
        renameBorder= BorderFactory.createEtchedBorder(selectedBG, selectedFG);
        nullBorder= BorderFactory.createEmptyBorder();
        if (klistener == null)
        	klistener= new ImplementedKeyListeners.KL_RenameDone();
        //setPreferredSize(new Dimension (Preferences.getIconSize().width, 50));
	}
	
	/**
	 * It sets the current text in the textpane to the value contained by prevName field
	 */
	public void setOldName () {
		this.setText(prevName);
	}
	
	/**
	 * It sets the prevName field to the current text in the textpane
	 */
	public void setRenameOk () {
		prevName= getText();
	}
	
	/**
	 * It returns the previous name of the file while editing, same name otherwise;
	 */
	public String getPrevName () {
		return prevName;
	}
	
	
	/**
	 * paint method to set the new height of the textPane while editing the text(or filename)
	 */
	public void paint (Graphics g) {
		super.paint(g);
		Dimension d= this.getSize();
		if (prevSize == null) {
			prevSize= d;
			return;
		}
		if (!d.equals(prevSize)) {
			int diffh= d.height - prevSize.height;
			IconButton but= (IconButton)getParent();
			but.setPreferredSize(new Dimension (but.getSize().width, but.getSize().height + diffh));
			prevSize= d;
		}
	}
}

/**
 * The custom editor kit to enable center align and wrapping in JTextPane
 * @author Basanta Sharma
 *
 */
class ISSEditorKit extends StyledEditorKit {
	private static final long serialVersionUID = 1L;
	private ISSViewFactory factory;
 
    public ViewFactory getViewFactory() {
        if (factory == null) {
            factory = new ISSViewFactory();
        }
        return factory;
    }
}

 /**
  * The ViewFactory used by CustomEditorKit
  * @author Basanta Sharma
  *
  */
class ISSViewFactory implements ViewFactory {
    public View create(Element elem) {
        String kind = elem.getName();
        if (kind != null) {
            if (kind.equals(AbstractDocument.ContentElementName)) {
                return new ISSLabelView(elem);
            } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
                return new ISSParagraphView(elem);
            } else if (kind.equals(AbstractDocument.SectionElementName)) {
                return new BoxView(elem, View.Y_AXIS);
            } else if (kind.equals(StyleConstants.ComponentElementName)) {
                return new ComponentView(elem);
            } else if (kind.equals(StyleConstants.IconElementName)) {
                return new IconView(elem);
            }
        }
 
        // default to text display
        return new LabelView(elem);
    }
}
 
class ISSParagraphView extends ParagraphView {
 
    public ISSParagraphView(Element elem) {
        super(elem);
    }
    public void removeUpdate(DocumentEvent e, Shape a, ViewFactory f) {
        super.removeUpdate(e, a, f);
        resetBreakSpots();
    }
    public void insertUpdate(DocumentEvent e, Shape a, ViewFactory f) {
        super.insertUpdate(e, a, f);
        resetBreakSpots();
    }
 
    private void resetBreakSpots() {
        for (int i=0; i<layoutPool.getViewCount(); i++) {
            View v=layoutPool.getView(i);
            if (v instanceof ISSLabelView) {
                ((ISSLabelView)v).resetBreakSpots();
            }
        }
    }

    @Override
    protected SizeRequirements calculateMinorAxisRequirements(int axis, SizeRequirements r) {
        if (r == null) {
            r = new SizeRequirements();
        }
        float pref = this.layoutPool.getPreferredSpan(axis);
        float min = this.layoutPool.getMinimumSpan(axis);
        // Don't include insets, Box.getXXXSpan will include them. 
        r.minimum = (int) min;
        r.preferred = Math.max(r.minimum, (int) pref);
        r.maximum = Integer.MAX_VALUE;
        r.alignment = 0.5f;
        return r;
    }
}
 
class ISSLabelView extends LabelView {
 
    boolean isResetBreakSpots=false;
 
    public ISSLabelView(Element elem) {
        super(elem);
    }
    public View breakView(int axis, int p0, float pos, float len) {
        if (axis == View.X_AXIS) {
            resetBreakSpots();
        }
        return super.breakView(axis, p0, pos, len);
    }
    
    public void resetBreakSpots() {
        isResetBreakSpots=true;
        removeUpdate(null, null, null);
        isResetBreakSpots=false;
   }
 
    public void removeUpdate(DocumentEvent e, Shape a, ViewFactory f) {
        super.removeUpdate(e, a, f);
    }
 
    public void preferenceChanged(View child, boolean width, boolean height) {
        if (!isResetBreakSpots) {
            super.preferenceChanged(child, width, height);
        }
    }
}
