package ecertificate;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

public class AlignText {
	
	PDFont font;//Font object is instantiated.
	PDPageContentStream stream;//PDPageContentStream object is created.
	PDPage page;//PDPage object is created.
	
	public AlignText(PDFont font,PDPageContentStream stream,PDPage page) {
		this.font=font;
		this.stream=stream;
		this.page=page;
	}
	
	public void alignText(String title,int fontSize) throws Exception{
		/*
		 * alignText method for aligning the title of the pdf to appropriate position
		 */
		float titleWidth = font.getStringWidth(title) / 1000 * fontSize;//title width is calculated
		float titleHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;//title height is calculated
		
		stream.setFont(font, fontSize);//font size is set for the title
		float width=(page.getMediaBox().getWidth()-titleWidth)/2;//x ordinate of the tile is calculated.
		float height=(page.getMediaBox().getHeight()-titleHeight)/2+90;//y ordinate of the title is calculated.
		stream.newLineAtOffset(width, height);//Offset for the title is shifted.
		stream.showText(title);//Title is written in the pdf.
		stream.newLineAtOffset(-width, -height);//Offset moved to old place. 
	}

	public void alignDate(String date) throws Exception {
		/*
		 * This method is for aligning date of issue in the pdf e-certificate
		 */
		String year=date.substring(date.lastIndexOf("-")+1,date.length());//Year is extracted from the date.
		String dateMonth=date.substring(0,date.lastIndexOf("-"));//date and month is extracted.
		
		stream.setFont(font, 37);//Year font size is set.
		float yearWidth=font.getStringWidth(year)/1000*37;//Year width is calculated with font style.
		stream.newLineAtOffset(90+(135-yearWidth)/2, 135);//Offset for the year is moved to right place.
		stream.showText(year);//Year is printed in the pdf.
		
		stream.setFont(font, 15);//date and month font size is set
		stream.newLineAtOffset((yearWidth-font.getStringWidth(dateMonth)/1000*15)/2,28);//Offset for date and month is aligned.
		stream.showText(dateMonth);//date and month is printed in the pdf.
	}
}
