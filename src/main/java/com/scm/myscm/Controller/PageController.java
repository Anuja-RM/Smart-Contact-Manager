package com.scm.myscm.Controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

    @RequestMapping("/home")
    public String home() {
        return "home";
    }

    @RequestMapping("/about")
    public String aboutPage() {
        System.out.println("About Page Loading");
        return "about";
    }

    @RequestMapping("services")
    public String servicesPage() {
        System.out.println("Services Page Loading");
        return "services";
    }
}
