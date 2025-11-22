package com.lezord.system_api.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/testv1")
public class TestV1Controller {
    @GetMapping("/check")
    public String check() {
        return "Test V1 Ok";
    }
}
