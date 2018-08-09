import java.awt.Color;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class ColorPane extends JTextPane{
	private StyleContext sc;
	private  StyledDocument doc;
	
	public ColorPane(){
		sc = StyleContext.getDefaultStyleContext();
		doc = this.getStyledDocument();
	}
	  public void append(Color c , String s) {
		 
		  AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
		  int len = getDocument().getLength(); // same value as getText().length();

		  try {
			doc.insertString(len, s, aset);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		  
}
	  public void append(String s) {
		  this.append(Color.black,s);
}

}
