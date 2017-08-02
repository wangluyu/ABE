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
@RequestMapping("/concept")
public class ConceptController extends BaseController{
    @RequestMapping(value = "/definitions",method = RequestMethod.GET)
    public String definitions(ModelMap model){
        model.put("path","definitions");
        model.put("root","concept");
        return "definitions";
    }

    @RequestMapping(value = "/concept",method = RequestMethod.GET)
    public String algorithm(ModelMap model){
        model.put("path","concept");
        model.put("root","concept");
        return "concept";
    }

}
