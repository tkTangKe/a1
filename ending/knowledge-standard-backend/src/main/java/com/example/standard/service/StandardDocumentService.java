package com.example.standard.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.standard.entity.StandardDocument;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

public interface StandardDocumentService extends IService<StandardDocument> {
	void uploadPdf(MultipartFile file, String code, String name, Date publishDate, String version) throws Exception;
}