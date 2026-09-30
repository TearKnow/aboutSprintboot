package com.example.sprintbootinit;

import com.itranswarp.rich.Millionaire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Value("${myname}")
    public String myname;

    @Autowired
    private Environment env;

    @Value("${person.name}")
    private String pname;

    @Autowired
    private Person person;

    @RequestMapping("/hello")
    public String Hello() {
        System.out.println(env.getProperty("useforhaha"));
        System.out.println(env.getProperty("address[1]"));

        System.out.println("=================");
        System.out.println(pname);
        System.out.println(myname);

        System.out.println("=================");
        System.out.println(person);
        return "Hello my spring boot";
    }

    @RequestMapping("/rich")
    public String Rich() {
        Millionaire millionaire = new Millionaire();
        System.out.println(millionaire.howToBecomeRich());
        return "引入第三方包";
    }
}
