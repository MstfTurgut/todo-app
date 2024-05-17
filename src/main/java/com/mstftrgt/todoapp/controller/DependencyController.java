package com.mstftrgt.todoapp.controller;

import com.mstftrgt.todoapp.dto.request.NewDependencyRequest;
import com.mstftrgt.todoapp.service.DependencyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("dependencies")
public class DependencyController {

    private final DependencyService dependencyService;

    public DependencyController(DependencyService dependencyService) {
        this.dependencyService = dependencyService;
    }

    @PostMapping("{itemId}")
    public ResponseEntity<Void> addDependency(@Valid @RequestBody NewDependencyRequest newDependencyRequest, @PathVariable String itemId) {

        dependencyService.addDependency(newDependencyRequest, itemId);
        return ResponseEntity.noContent().build();
    }


}
