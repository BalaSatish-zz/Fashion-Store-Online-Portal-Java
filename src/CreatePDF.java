import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class CreatePDF {
	
	
	public CreatePDF(){
		
	}

	public void MakePDF(ArrayList<ShopItems> orders, int Total) {
		// TODO Auto-generated constructor stub
		ArrayList<ShopItems> Orders = new ArrayList<>(10);
		Orders.clear();
		Orders = orders;
		Document document = new Document();
		try{
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("Doc.pdf"));
			document.open();
			document.add(new Paragraph("This is the auto-generated Invoice for your Purchase"));
			PdfPTable table = new PdfPTable(6);
			table.setWidthPercentage(105);
			table.setSpacingBefore(11f);
			table.setSpacingAfter(11f);
			
			float[] colwidth={2f,2f,2f,2f,2f,2f};
			table.setWidths(colwidth);
			PdfPCell c1 = new PdfPCell(new Paragraph("SL.No"));
			PdfPCell c2 = new PdfPCell(new Paragraph("Item Name"));
			PdfPCell c3 = new PdfPCell(new Paragraph("Item Type"));
			PdfPCell c4 = new PdfPCell(new Paragraph("Price"));
			PdfPCell c5 = new PdfPCell(new Paragraph("Quantity"));
			PdfPCell c6 = new PdfPCell(new Paragraph("Cost"));
			table.addCell(c1);
			table.addCell(c2);
			table.addCell(c3);
			table.addCell(c4);
			table.addCell(c5);
			table.addCell(c6);
			int i=1;
			for (ShopItems x : Orders) {
//				c1 = new PdfPCell(new Paragraph(""+i++));
//				c2 = new PdfPCell(new Paragraph(""+x.getName()));
//				c3 = new PdfPCell(new Paragraph(""+x.getType()));
//				c4 = new PdfPCell(new Paragraph(""+x.getCost()));
//				c5 = new PdfPCell(new Paragraph(""+x.getQuantity()));
//				c6 = new PdfPCell(new Paragraph(""+(x.getQuantity()*x.getCost())));
				table.addCell(""+i++);
				table.addCell(""+x.getName());
				table.addCell(""+x.getType());
				table.addCell(""+x.getCost());
				table.addCell(""+x.getQuantity());
				table.addCell(""+(x.getQuantity()*x.getCost()));
				System.out.println(i);
			}
			if(i>Orders.size()){
				c1 = new PdfPCell(new Paragraph("Total:"));
				c1.setColspan(5);
				c2 = new PdfPCell(new Paragraph(""+Total));
				table.addCell(c1);
				table.addCell(c2);
				document.add(table);
				System.out.println("Table Created");
				document.close();
				writer.close();
			}
		}catch (DocumentException e) {
			e.printStackTrace();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
	}

}
