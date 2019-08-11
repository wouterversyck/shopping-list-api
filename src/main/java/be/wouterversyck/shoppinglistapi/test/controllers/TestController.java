package be.wouterversyck.shoppinglistapi.test.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class TestController {

    @GetMapping("test")
    public String test() {
        return "test";
    }

    @GetMapping("public")
    public String publicTest() {
        return "public";
    }
}
