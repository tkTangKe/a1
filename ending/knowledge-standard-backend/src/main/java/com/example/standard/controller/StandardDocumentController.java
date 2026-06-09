package com.example.standard.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.standard.entity.StandardDocument;
import com.example.standard.service.StandardDocumentService;
import com.example.standard.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class StandardDocumentController {

	@Autowired
	private StandardDocumentService documentService;

	@Autowired
	private LogService logService;

	@Autowired
	private HttpServletRequest request;

	private String getUsername() {
		String token = request.getHeader("Authorization");
		if (token != null && token.startsWith("Bearer ")) {
			token = token.substring(7);
			try {
				String decoded = new String(Base64.getDecoder().decode(token));
				String[] parts = decoded.split(":");
				if (parts.length >= 1) return parts[0];
			} catch (Exception e) {}
		}
		return "unknown";
	}

	@GetMapping
	public List<StandardDocument> list() {
		return documentService.list();
	}

	@GetMapping("/{id}")
	public StandardDocument getById(@PathVariable Integer id) {
		return documentService.getById(id);
	}

	@PostMapping
	public boolean add(@RequestBody StandardDocument document) {
		boolean success = documentService.save(document);
		if (success) {
			try {
				logService.saveLog("新增文档", "document", document.getId(),
						"新增文档：" + document.getCode() + " - " + document.getName(),
						getUsername(), request.getRemoteAddr());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return success;
	}

	@PutMapping
	public boolean update(@RequestBody StandardDocument document) {
		boolean success = documentService.updateById(document);
		if (success) {
			try {
				logService.saveLog("编辑文档", "document", document.getId(),
						"编辑文档ID：" + document.getId(),
						getUsername(), request.getRemoteAddr());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return success;
	}

	@DeleteMapping("/{id}")
	public boolean delete(@PathVariable Integer id) {
		StandardDocument doc = documentService.getById(id);
		boolean success = documentService.removeById(id);
		if (success && doc != null) {
			try {
				logService.saveLog("删除文档", "document", id,
						"删除文档：" + doc.getCode() + " - " + doc.getName(),
						getUsername(), request.getRemoteAddr());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return success;
	}

	@GetMapping("/search")
	public List<StandardDocument> search(@RequestParam("keyword") String keyword) {
		LambdaQueryWrapper<StandardDocument> wrapper = new LambdaQueryWrapper<>();
		wrapper.like(StandardDocument::getName, keyword)
				.or()
				.like(StandardDocument::getCode, keyword);
		return documentService.list(wrapper);
	}
}