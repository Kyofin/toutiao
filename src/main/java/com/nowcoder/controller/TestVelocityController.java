package com.nowcoder.controller;

import com.nowcoder.model.ViewObject;
import com.nowcoder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestVelocityController {
        @Autowired
        UserService userService;

        @RequestMapping("/test")
        public  String test1(Model model )
        {
                ViewObject viewObject = new ViewObject();
                viewObject.set("hello","world");
                viewObject.set("user",userService.getUser(2));

                model.addAttribute("viewObj",viewObject);
                return "test";
        }

}
