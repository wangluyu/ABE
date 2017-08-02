package com.abe.web;

import org.springframework.stereotype.Controller;

import java.io.File;

/**
 * @author LuyuWang
 * @date 2017/1/19
 * @time 17:11
 */
@Controller
public class BaseController {
    //控制窗口输出
    public void systemOutPrint(String index,Object text){
        if(text.getClass().isArray()){
            String temp = null;
            String[] arr = (String[]) text;
            for (Integer i = 0;i<arr.length;i++){
                temp += i+":"+arr[i]+" ,";
            }
            System.out.println(index+" ----- "+ temp);
        }else{
            System.out.println(index+" ----- "+ text);
        }
    }

    /**
     * 生成随机字符串
     * @param length
     * @return
     */
    public static String getRandomString(int length){
        String string = "qQwWeErRtTyYuUiIoOpPAasSdDfFgGhHjJkKlLzZxXcCvVbBnNmM";
        int stringLen = string.length();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            sb.append(string.charAt((int) Math.round(Math.random() * (stringLen-1))));
        }
        return sb.toString();
    }


}
