package com.example.standard.controller;
import org.springframework.web.multipart.MultipartFile;
import com.example.standard.dto.CodeTableTreeDto;
import com.example.standard.entity.CodeItem;
import com.example.standard.entity.CodeTable;
import com.example.standard.service.CodeItemService;
import com.example.standard.service.CodeTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 代码表管理 Controller
 * 支持树形结构
 */
@RestController
@RequestMapping("/api/code-tables")
public class CodeTableController {

	@Autowired
	private CodeTableService codeTableService;

	@Autowired
	private CodeItemService codeItemService;

	/**
	 * 获取所有代码表（列表，不含树形结构）
	 */
	@GetMapping
	public List<CodeTable> listAllTables() {
		return codeTableService.list();
	}

	/**
	 * 获取单个代码表基本信息
	 */
	@GetMapping("/{tableId}")
	public CodeTable getTableById(@PathVariable Integer tableId) {
		return codeTableService.getById(tableId);
	}

	/**
	 * 新增代码表
	 */
	@PostMapping
	public boolean addTable(@RequestBody CodeTable codeTable) {
		return codeTableService.save(codeTable);
	}

	/**
	 * 更新代码表
	 */
	@PutMapping
	public boolean updateTable(@RequestBody CodeTable codeTable) {
		return codeTableService.updateById(codeTable);
	}

	/**
	 * 删除代码表（同时会级联删除其下的所有代码项）
	 */
	@DeleteMapping("/{tableId}")
	public boolean deleteTable(@PathVariable Integer tableId) {
		return codeTableService.removeById(tableId);
	}

	/**
	 * 获取某个代码表下的树形代码项
	 */
	@GetMapping("/{tableId}/tree")
	public List<CodeItem> getCodeTree(@PathVariable Integer tableId) {
		// 假设 service 中有方法根据 tableId 查询所有 codeItem，并组装成树形结构
		return codeItemService.getTreeByTableId(tableId);
	}

	/**
	 * 新增一个代码项（可以指定父级）
	 */
	@PostMapping("/items")
	public boolean addCodeItem(@RequestBody CodeItem item) {
		return codeItemService.save(item);
	}

	/**
	 * 更新代码项
	 */
	@PutMapping("/items")
	public boolean updateCodeItem(@RequestBody CodeItem item) {
		return codeItemService.updateById(item);
	}

	/**
	 * 删除代码项
	 */
	@DeleteMapping("/items/{itemId}")
	public boolean deleteCodeItem(@PathVariable Integer itemId) {
		return codeItemService.removeById(itemId);
	}

	/**
	 * 批量导入代码项（Excel）
	 */
	@PostMapping("/{tableId}/import")
	public String importCodeItems(@PathVariable Integer tableId, @RequestParam("file") MultipartFile file) {
		codeItemService.importExcel(tableId, file);
		return "导入成功";
	}
}