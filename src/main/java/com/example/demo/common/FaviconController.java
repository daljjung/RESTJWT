package com.example.demo.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
class FaviconController {

    @GetMapping("favicon.ico")
    @ResponseBody
    void returnNoFavicon(){

    }
}
