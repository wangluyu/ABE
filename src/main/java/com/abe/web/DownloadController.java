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
@RequestMapping("/operation")
public class DownloadController extends BaseController{
    @RequestMapping(value = "/download",method = RequestMethod.GET)
    public String download(ModelMap model){
        model.put("data","download");
        return "download";
    }

}
