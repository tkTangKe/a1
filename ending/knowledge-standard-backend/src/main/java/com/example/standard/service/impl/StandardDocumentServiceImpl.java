package com.example.standard.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.standard.entity.StandardDocument;
import com.example.standard.mapper.StandardDocumentMapper;
import com.example.standard.service.StandardDocumentService;
import com.example.standard.utils.PdfTextExtractor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;

@Service
public class StandardDocumentServiceImpl extends ServiceImpl<StandardDocumentMapper, StandardDocument>
		implements StandardDocumentService {

	@Value("${file.upload-dir}")
	private String uploadDir;

	@Override
	public void uploadPdf(MultipartFile file, String code, String name, Date publishDate, String version) throws Exception {
		// 1. 保存PDF文件到磁盘
		String originalFilename = file.getOriginalFilename();
		String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
		String newFileName = UUID.randomUUID().toString() + ext;
		Path uploadPath = Paths.get(uploadDir);
		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}
		Path filePath = uploadPath.resolve(newFileName);
		file.transferTo(filePath.toFile());

		// 2. 提取PDF文本内容
		String contentText = PdfTextExtractor.extractText(file);

		// 3. 保存到数据库
		StandardDocument doc = new StandardDocument();
		doc.setCode(code);
		doc.setName(name);
		doc.setPublishDate(publishDate);
		doc.setVersion(version);
		doc.setFilePath(filePath.toString());
		doc.setContentText(contentText);
		this.save(doc);
	}
}