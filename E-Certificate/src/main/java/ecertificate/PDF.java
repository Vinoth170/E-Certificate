package ecertificate;
/**
 * @author Vinoth
 * 
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;



public class PDF {
	public static void main(String[] args) {
		String date;
		try {
			 date=args[0];//Date will be stored form the command line for the certificate issue date.
		}catch(IndexOutOfBoundsException e) {
			date=new SimpleDateFormat("dd-MM-yyyy").format(new Date());//If the date is not provided in the command line the current date will be taken as certificate issue date.
		}
		CreatePDF pdf=new CreatePDF(date);//CreatedPDF object is created.
		pdf.ePDF();
	}
}

class CreatePDF{
	private String date;
	
	public CreatePDF(String date) {
		this.date=date;//Date will be stored for certificate issue date.
	}
	
	public void ePDF() {
		try {
			Properties addresses=getName();//Achiever email id and name is stored in the properties object as key and value respectively.
			
			Set set=addresses.entrySet();
			Iterator iter=set.iterator();
			
			while(iter.hasNext()) {//Exctracts email id and name form addresses and creates pdf by calling create method for each one.
				Map.Entry<String, String> map=(Map.Entry<String, String>)iter.next();
				create(map.getKey(), map.getValue());
			}
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	private Properties getName() throws IOException{//Used to extract achievers name and email id from a text file and return it to ePDF method.
		File names=new File("src/main/resources/NameResources");
		Scanner readName=new Scanner(names);//Scanner object is created
		Properties addresses=new Properties();
		int lastIndex;
		String address;
		while(readName.hasNext()) {//Reads all name and email id form the text file and store it in properties object.
			address=readName.nextLine();
			lastIndex=address.lastIndexOf("-");
			addresses.put(address.substring(0,lastIndex),address.substring(lastIndex+1,address.length()) );
		}
		return addresses;//returns addresses to ePDF method.
	}
	
	private void create(String emailID,String name) {
		/*
		 * Creates pdf using the pdf template resources and for text font here georgia style is used which is stored in the resources folder
		 */
		try {
			
			int len=name.length();
			int size = (len<14)?37:(int)(37-len*0.5);//font size is chosen as per the name length.

			File file=new File("src/main/resources/certificate2.pdf");
			File fontfile=new File("src/main/resources/georgia.ttf");
			PDDocument doc1=PDDocument.load(file);//PDDocument object is created for manipulating the pdf.
			
			PDPage pages=doc1.getPage(0);
			
			PDPageContentStream edit=new PDPageContentStream(doc1, pages,PDPageContentStream.AppendMode.APPEND,true,true);
			
			PDFont font=PDType0Font.load(doc1,fontfile);
			
			edit.beginText();
			
			AlignText align=new AlignText(font, edit, pages);//AlignText object is created.
			align.alignText(name, size);//alignText method is called to align the name.
			
			align.alignDate(date);//To align the date alignDate is called.
			
			edit.endText();
			edit.close();
			
//			doc1.save("src/main/resources/"+name+".pdf");//If we want we can save the pdf in our local system.
			mail(name, emailID, doc1);//mail method is called to send the e-certificate to the receiver.
			
			doc1.close();
			
//			System.out.println("PDF created successfully...");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void mail(String name, String emailID, PDDocument pdfFile) {
		/*
		 * For composing mail content and attaching the file to the mail.
		 */
		try {
			File file=File.createTempFile(name, "xlsx");
		FileOutputStream outfile=new FileOutputStream(file);
		pdfFile.save(outfile);

		BodyPart textPart=new MimeBodyPart();
		MimeBodyPart bodyPart=new MimeBodyPart();
		
		//Content of the mail composed with setText method
		textPart.setText("Dear "+name+"\n\nCongradulations you secured e-certificate for completing the program"
				+ "\n\nWith Regards,\n"+System.getenv("USER_NAME"));
		DataSource source=new FileDataSource(file);
		bodyPart.setDataHandler(new DataHandler(source));
		bodyPart.setFileName(name+".pdf");
		file.deleteOnExit();
		
		Multipart multipart=new MimeMultipart();
		multipart.addBodyPart(textPart);
		multipart.addBodyPart(bodyPart);
		
		SendMail send=new SendMail(name, emailID, multipart);//Creates SendMail object.
		
			send.mail();//mail method of SendMail is called to send the e-certificate via mail.
		}catch(Exception ex){
			ex.printStackTrace();
			}
	}
}