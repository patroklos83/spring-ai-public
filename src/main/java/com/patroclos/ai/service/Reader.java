package com.patroclos.ai.service;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class Reader {
	
	public String readStringFromPdf(StringBuilder textFromPdf) {
		//Set<String> stopWords = GreekStopWords.getGreekStopWords();
		
		StringBuilder text = new StringBuilder();
		
		String s = new String(textFromPdf.toString().getBytes(), Charset.forName("UTF8"));
		
		//String[] paragraphs = s.split("[0-9]+[\\.\\,]+[\\s]*[Α-Ω]+"); //catch Parartima-new line-number and dot-new line ...
		String[] paragraphs = s.split("ΜΕΡΟΣ(\\s?[Α-Ωα-ω]{2,4}\\n)");
		for (String paragraph : paragraphs) {
			//text.append("\n new paragraph \n");
			text.append("\n");
			
			String[] lines = paragraph.split("\\R");
			//String[] lines = paragraph.split("-|\\.\\R"); //split by new line char and .
			for (String line : lines) {
				line = line.replace("|", "").replace("-", "");
				text.append("\n" + line);
			}
		}
		
		return text.toString();
	}
	
	public Collection<String> getSentencesFromPdf(String textFromPdf) {
		List<String> sentences = new ArrayList<String>();
		String s = new String(textFromPdf.toString().getBytes(), Charset.forName("UTF8"));
		
		//String[] paragraphs = s.split("[0-9]+[\\.\\,]+[\\s]*[Α-Ω]+"); //catch Parartima-new line-number and dot-new line ...
		String[] paragraphs = s.split("(ΜΕΡΟΣ\\s[\\W]{1,5}\\n)");
		for (String paragraph : paragraphs) {
			sentences.add(paragraph);
		}
		
		return sentences;
	}

}
