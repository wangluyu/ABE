package com.abe.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author LuyuWang
 * @date 2017-1-16 15:49:51
 */
@Controller
@RequestMapping("/operation")
public class UploadController{
    @RequestMapping(value = "/upload",method = RequestMethod.GET)
    public String upload(ModelMap model){
        model.put("data","upload");
        return "upload";
    }
}
