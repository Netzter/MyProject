import java.awt.Color;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;


public class ColorPane extends JTextPane{
	private StyleContext sc;
	private  StyledDocument doc;
	private HTMLEditorKit kit;
	private HTMLDocument dc;
	public ColorPane(){
		sc = StyleContext.getDefaultStyleContext();
		doc = this.getStyledDocument();
		kit = new HTMLEditorKit();
		dc = new HTMLDocument();
		//setEditorKit(kit);
		//setDocument(dc);
		//setContentType("text/html");
	}
	  public void append(Color c , String s) {
		 
		  AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
				  							  StyleConstants.Foreground, c);
		  int len = getDocument().getLength(); // same value as
          // getText().length();

		  try {
			doc.insertString(len, s, aset);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
}
	  public void append(String s) {
	
		  AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
				  							  StyleConstants.Foreground, Color.black);
		  int len = getDocument().getLength(); 
         
		  try {
			doc.insertString(len, s, aset);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}
	  public void appendHTML(String s, String style){ 
		 /* String str = "<html>"
				  		"<head>"
		  				"<style>"
		  				"h1 {color:red;}"
		  				"p {color:blue;}"
		  				"</style>"
		  				"</head>"
		  				"<body>"

		  				"<h1>A heading</h1>"
		  				"<p>A paragraph.</p>"

		  				"</body>"
		  				"</html>"*/	
		  try {
			kit.insertHTML(dc, getDocument().getLength(), "<b style=" + style + ">" + s + "</b>"
							, 0, 0, null);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }

}
