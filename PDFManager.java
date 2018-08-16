
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFManager {
	private Document document;
	//private String dest;
	private FileOutputStream writer;

	public PDFManager(String dest){
	try {
		//this.dest = dest;
		File file = new File(dest);
	    file.getParentFile().mkdirs(); 
		document = new Document();
		writer = new FileOutputStream(dest);
       
		}catch (IOException e) {
			e.printStackTrace();
		}
    }

	public PDFManager(File file){
		this(file.getAbsolutePath());
	}
	
	public void writePdf(String text, Paragraph firstline) 
			throws DocumentException{
	        PdfWriter.getInstance(
	        document, writer);
	        document.open();
	        firstline.setAlignment(Element.ALIGN_CENTER);
	        firstline.setFont(new PdfFont());
	        document.add(firstline);
	        document.add(new Paragraph(text));
	        document.close();
	    }
	public void writePdf(String text) 
			throws DocumentException{
	       this.writePdf(text, null);
	    }
}
