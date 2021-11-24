package com.distichain.productsynchronizer.controller;

import com.distichain.productsynchronizer.service.SynchronizeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/synchronize")
public class SynchronizeController {

    @GetMapping
    public ResponseEntity<String> checkStatus() {
        if (SynchronizeService.isSynchronizing)
            return ResponseEntity.ok("Running");
        else
            return ResponseEntity.ok("Stoped");
    }

    @PostMapping
    public ResponseEntity<Void> start() {
        SynchronizeService.isAllow = true;
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> stop() {
        SynchronizeService.isAllow = false;
        return ResponseEntity.ok().build();
    }
}
