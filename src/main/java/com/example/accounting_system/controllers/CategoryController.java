package com.example.accounting_system.controllers;

import com.example.accounting_system.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/{category}")
    public ResponseEntity<List<Object>> getAllByCategory(@PathVariable("category") String category) {
        return ResponseEntity.ok(categoryService.getTransactionsAndDebtsByCategory(category));
    }
}
