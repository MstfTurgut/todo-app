package com.mstftrgt.todoapp.controller;

import com.mstftrgt.todoapp.dto.request.NewDependencyRequest;
import com.mstftrgt.todoapp.service.DependencyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("dependencies")
public class DependencyController {

    private final DependencyService dependencyService;

    @PostMapping("{itemId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void addDependency(@PathVariable String itemId, @Valid @RequestBody NewDependencyRequest newDependencyRequest) {
        dependencyService.add(newDependencyRequest, itemId);
    }
}
