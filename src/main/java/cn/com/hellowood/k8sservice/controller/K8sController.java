package cn.com.hellowood.k8sservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class K8sController {

    @GetMapping("/")
    public String root() {
        return "Hello Kubernetes";
    }

    @GetMapping("/healthz")
    public String healthz() {
        return "ok";
    }
}


