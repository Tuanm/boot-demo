package dev.tuanm.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.tuanm.demo.common.constant.PathConstants;

@RestController
public class PrivateController {
    @RequestMapping(PathConstants.PRIVATE_TEST_URL)
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("OK");
    }
}
