package com.abe.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author LuyuWang
 * @date 2017/1/22
 * @time 16:04
 */
@Controller
@RequestMapping("/algorithm")
public class AlgorithmController extends BaseController{
    @RequestMapping(value = "/operate",method = RequestMethod.GET)
    public String operate(ModelMap model){
        model.put("path","operate");
        model.put("root","algorithm");
        return "operate";
    }
    @RequestMapping(value = "/algorithm",method = RequestMethod.GET)
    public String algorithm(ModelMap model){
        model.put("path","algorithm");
        model.put("root","algorithm");
        return "algorithm";
    }

}
