package dev.tuanm.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.tuanm.demo.common.constant.PathConstants;

@RestController
public class PrivateController {
    @RequestMapping(PathConstants.PRIVATE_TEST_URL)
    public String test() {
        return "OK";
    }
}
