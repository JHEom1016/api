package com.rest.api.controller;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {
    /*
    화면에 Hello World 출력
     */
    @GetMapping(value = "/helloworld/string")
    @ResponseBody
    public String helloworldString() {
        return "helloworld";
    }
    /*
    화면에 {message: "helloworld"} 라고 출력
     */
    @GetMapping(value = "/helloworld/json")
    @ResponseBody
    public Hello hellpworldJson() {
        Hello hello = new Hello();
        hello.message = "helloworld";
        return hello;
    }
    /*
    화면에 helloworld.ftl 내용 출력
     */
    @GetMapping(value = "/helloworld/page")
    @ResponseBody
    public String helloworld() {
        return "helloworld";
    }

    @Getter
    @Setter
    public static class Hello {
        private String message;
    }
}
