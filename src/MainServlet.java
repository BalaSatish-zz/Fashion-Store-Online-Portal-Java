import java.awt.List;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class MainServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection con=null;
		PreparedStatement pstmt=null;
		PreparedStatement pstmt1=null;
		PreparedStatement pstmt2=null;
		Statement stmt = null;
		HttpSession hs = request.getSession();
		ResultSet rs=null;
		ResultSet rs1=null;
		String query1 = "insert into fashionstore.users value(?,?,?,?,?,?,?,?)";
		String query2 = "select * from fashionstore.users where username=?";
		String query3 = "select * from fashionstore.users where username=? and password=?";
		ArrayList<ShopItems> Orders;
	
		PrintWriter out = response.getWriter();
		String pageValue = request.getParameter("PageValue");
		if(pageValue.equalsIgnoreCase("Register")){
			String username = request.getParameter("username");
			String pwd1 = request.getParameter("password1");
			String dob = request.getParameter("dob");
			String gender = request.getParameter("Gender");
			String mobile = request.getParameter("mobile");
			String email = request.getParameter("email");
			String otp="0";
			String status="false";
			try{
				MD5 m = new MD5();
				pwd1 = m.getMd5(pwd1);
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection("jdbc:mysql://localhost:3306?user=root&password=dinga");
				pstmt1 = con.prepareStatement(query2);
				pstmt1.setString(1, username);
				rs =pstmt1.executeQuery();
				if(rs.next()){
					RequestDispatcher rd = request.getRequestDispatcher("Register.html");
					rd.include(request, response);
					out.println("<script>Display('Username Already Taken');"
							+ "PlaceData('"+username+"','"+gender+"','"+mobile+"','"+email+"')</script>");
					return;
				}
				pstmt = con.prepareStatement(query1);
				pstmt.setString(1,username);
				pstmt.setString(2,pwd1);
				pstmt.setString(3,dob);
				pstmt.setString(4,gender);
				pstmt.setString(5,mobile);
				pstmt.setString(6,email);
				pstmt.setString(7,status);
				pstmt.setString(8,otp);
				int result=pstmt.executeUpdate();
				if(result>0){
					RequestDispatcher rd = request.getRequestDispatcher("Register.html");
					rd.include(request, response);
					out.println("<script>Registered();</script>");
					System.out.println("Registered...!");
				}else{
					RequestDispatcher rd = request.getRequestDispatcher("Register.html");
					rd.include(request, response);
					System.out.println("Registration Failed...!");
					out.println("<script>Display('Registration Failed...!');</script>");
				}
				
			}
			catch(Exception e){
				e.printStackTrace();
				RequestDispatcher rd = request.getRequestDispatcher("Register.html");
				rd.include(request, response);
				out.println("<script>Display('Registration Failed...!');</script>");
			}
		}
		if(pageValue.equalsIgnoreCase("Login")){
			String username = request.getParameter("username");
			String pwd1 = request.getParameter("password1");
			 MD5 m = new MD5();
			 pwd1 = m.getMd5(pwd1);
			try{
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection("jdbc:mysql://localhost:3306?user=root&password=dinga");
				pstmt2 = con.prepareStatement(query3);
				pstmt2.setString(1, username);
				pstmt2.setString(2, pwd1);
				rs1 =pstmt2.executeQuery();
				if(rs1.next()){
					String email = rs1.getString(6);
					rs1.getString(2);
					rs1.getString(3);
					rs1.getString(4);
					rs1.getString(5);
					rs1.getString(6);
					rs1.getString(6);
					hs.setAttribute("email",""+rs1.getString(6));
					hs.setAttribute("username", ""+username);
					response.sendRedirect("./Shopping.html");
				}
				else{
					RequestDispatcher rd = request.getRequestDispatcher("Login.html");
					rd.include(request, response);
					out.println("<script>PlaceData('"+username+"');</script>");
					out.println("<script>Display('Invalid Credentials');</script>");
					return;
				}
				}catch (Exception e) {
					e.printStackTrace();
					RequestDispatcher rd = request.getRequestDispatcher("Login.html");
					rd.include(request, response);
					out.println("<script>Display('Login Failed...!');</script>");	
				}
		}
		if(pageValue.equalsIgnoreCase("Shopping")){
			int total = 0;
			ArrayList<ShopItems> ItemsList= new ArrayList<ShopItems>(20);
			Orders= new ArrayList<ShopItems>(10);
			ItemsList.clear();
			Orders.clear();
			String user=null; 
			String email =null;
			try{
				email = (String) hs.getAttribute("email");
				user = (String) hs.getAttribute("username");
				if(user.isEmpty() || email.isEmpty()){
					RequestDispatcher rd = request.getRequestDispatcher("Login.html");
					rd.include(request, response);
					out.println("<script>Display('Login to Place Order');</script>");
					return;
					}
				if(!user.isEmpty() && !email.isEmpty()){
					ShopItems si;
					String type1="Cloth";
					String type2="Mobile";
					Class.forName("com.mysql.jdbc.Driver");
					con = DriverManager.getConnection("jdbc:mysql://localhost:3306?user=root&password=dinga");
					stmt = con.createStatement();
					si = new ShopItems(type1,"Pant",899);
					ItemsList.add(si);
					si = new ShopItems(type1,"Shirt",899);
					ItemsList.add(si);
					si = new ShopItems(type1,"Shorts",399);
					ItemsList.add(si);
					si = new ShopItems(type2,"Apple",99999);
					ItemsList.add(si);
					si = new ShopItems(type2,"Oneplus",49999);
					ItemsList.add(si);
					si = new ShopItems(type2,"Samsung",64999);
					ItemsList.add(si);
					si = new ShopItems(type2,"Xiaomi",21999);
					ItemsList.add(si);
					String ct = request.getParameter("ClothesTotal");
					String mt = request.getParameter("MobilesTotal");
					String cn = request.getParameter("Cloth");
					String mn = request.getParameter("Mobile");
					for(ShopItems x: ItemsList){
						if(x.getName().equalsIgnoreCase(cn)){
							Orders.add(x);
						}
						if(x.getName().equalsIgnoreCase(mn)){
							Orders.add(x);
						}
					}
					for(ShopItems x:Orders){
						if(x.getType().equalsIgnoreCase(""+type1)){
							x.setQuantity(Integer.parseInt(ct));
							total = total+(Integer.parseInt(ct)*x.getCost());
							System.out.println("Total:"+total);
						}
						if(x.getType().equalsIgnoreCase(""+type2)){
							x.setQuantity(Integer.parseInt(mt));
							total = total+(Integer.parseInt(mt)*x.getCost());
							System.out.println("Total:"+total);
						}
					}
					int RandomNumber;
					Random rand = new Random();
					RandomNumber = (int)(Math.random()*999999+111111);
					Document document = new Document();
					try{
						String FileName = ""+user.substring(0,6)+RandomNumber+".pdf";
						hs.setAttribute("file", FileName);
						PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("D:/"+user.substring(0,6)+RandomNumber+".pdf"));
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
						RequestDispatcher rd = request.getRequestDispatcher("Confirmation.html");
						rd.include(request, response);
						String OrderQuery;
						for (ShopItems x : Orders) {
//							c1 = new PdfPCell(new Paragraph(""+i++));
//							c2 = new PdfPCell(new Paragraph(""+x.getName()));
//							c3 = new PdfPCell(new Paragraph(""+x.getType()));
//							c4 = new PdfPCell(new Paragraph(""+x.getCost()));
//							c5 = new PdfPCell(new Paragraph(""+x.getQuantity()));
//							c6 = new PdfPCell(new Paragraph(""+(x.getQuantity()*x.getCost())));
							String IN = x.getName();
							String IT = x.getType();
							String P = ""+x.getCost();
							String Q = ""+x.getQuantity();
							String C = ""+(x.getQuantity()*x.getCost());
							out.println("<script>TableData('"+i+"','"+IN+"','"+IT+"','"+P+"','"+Q+"','"+C+"');</script>");
							OrderQuery = "insert into fashionstore.orders values('"+user+"','"+IN+"','"+IT+"','"+P+"','"+Q+"','"+C+"')";
							stmt.execute(OrderQuery);
							table.addCell(""+i++);
							table.addCell(""+IN);
							table.addCell(""+IT);
							table.addCell(""+P);
							table.addCell(""+Q);
							table.addCell(""+C);
							//System.out.println(i);
						}
						if(i>Orders.size()){
							c1 = new PdfPCell(new Paragraph("Total:"));
							c1.setColspan(5);
							c2 = new PdfPCell(new Paragraph(""+total));
							table.addCell(c1);
							table.addCell(c2);
							document.add(table);
							System.out.println("Table Created");
							document.close();
							writer.close();
							out.println("<script>PrintTable('"+total+"');</script>");
						}
					}catch (DocumentException e) {
						e.printStackTrace();
					}catch(FileNotFoundException e){
						e.printStackTrace();
					}
					System.out.println("Total:"+total);
				}	
			}catch (Exception e) {
					e.printStackTrace();
					RequestDispatcher rd = request.getRequestDispatcher("Confirmation.html");
					rd.include(request, response);
					out.println("<script>Display('Login Failed');</script>");
				}
		}
		if(pageValue.equalsIgnoreCase("Confirmation")){
			final String username = "xyz@gmail.com";
			final String password = "xxyyzz";

			Properties props = new Properties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.port", "587");

			Session session = Session.getInstance(props,
			  new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			  });

			try {
				String email = (String) hs.getAttribute("email");
				String user = (String) hs.getAttribute("username");
				String FileName = (String) hs.getAttribute("file");
				Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress("fashionstoreotp@gmail.com"));
				message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(""+email));
				message.setSubject("Fashion Store Order Confirmation Mail");
				BodyPart messageBodyPart = new MimeBodyPart();

		         // Now set the actual message
		         messageBodyPart.setText("Hello "+user+", This mail contains the invoice of your recent Order");

		         // Create a multipar message
		         Multipart multipart = new MimeMultipart();

		         // Set text message part
		         multipart.addBodyPart(messageBodyPart);

		         // Part two is attachment
		         messageBodyPart = new MimeBodyPart();
		         String filename = ""+FileName;
		         DataSource source = new FileDataSource(filename);
		         messageBodyPart.setDataHandler(new DataHandler(source));
		         messageBodyPart.setFileName(filename);
		         multipart.addBodyPart(messageBodyPart);

		         // Send the complete message parts
		         message.setContent(multipart);

		         // Send message
		         Transport.send(message);

		         System.out.println("Sent message successfully....");
		         System.out.println(""+filename);
		         RequestDispatcher rd = request.getRequestDispatcher("Login.html");
		         rd.include(request, response);
			   	 out.println("<script>Display('Mail Sent Successfully');</script>");
			   	 hs.invalidate();

			} catch (MessagingException e) {
				e.printStackTrace();
				 RequestDispatcher rd = request.getRequestDispatcher("Login.html");
		         rd.include(request, response);
			   	 out.println("<script>Display('Mail Sending Failed');</script>");	
			}
		}
	
	}
	
}
