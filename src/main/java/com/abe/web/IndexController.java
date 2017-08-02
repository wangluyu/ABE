package com.abe.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author LuyuWang
 * @date 2017/1/18
 * @time 17:27
 */
@Controller
@RequestMapping("/index")
public class IndexController {
    @RequestMapping(value = "/index",method = RequestMethod.GET)
    public String index(ModelMap model){
        model.put("data","");
        model.put("path","index");
        return "index";
    }
}
