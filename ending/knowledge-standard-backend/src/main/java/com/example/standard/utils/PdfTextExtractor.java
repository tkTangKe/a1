package com.example.standard.utils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class PdfTextExtractor {

	public static String extractText(MultipartFile file) throws IOException {
		// 直接从输入流读取，不依赖临时文件路径
		try (PDDocument document = PDDocument.load(file.getInputStream())) {
			PDFTextStripper stripper = new PDFTextStripper();
			String text = stripper.getText(document);
			return text;
		}
	}
}