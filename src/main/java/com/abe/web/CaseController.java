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
@RequestMapping("/case")
public class CaseController extends BaseController{
    @RequestMapping(value = "/cases",method = RequestMethod.GET)
    public String cases(ModelMap model){
        model.put("path","cases");
        return "cases";
    }

}
