package com.tk.learn.cloudgateway.dynamic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/routes")
@RequiredArgsConstructor
public class DynamicRoutesController {

    private final CustomRouterFunctionMapping mapping;

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh() {
        mapping.refresh();
        return ResponseEntity.ok("Routes refreshed");
    }
}
