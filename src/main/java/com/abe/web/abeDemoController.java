package com.abe.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import cpabe.Cpabe;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * @author LuyuWang
 * @date 2017-1-16 11:26:06
 */
@org.springframework.stereotype.Controller
@RequestMapping("/index")
public class abeDemoController{
    final static boolean DEBUG = true;

    static String pubfile = "D:/test/pub_key";
    static String mskfile = "D:/test/master_key";
    static String prvfile = "D:/test/prv_key";

    static String inputfile = "D:/test/aa.txt";
    static String encfile = "D:/test/eninput.txt";
    static String decfile = "D:/test/deinput.txt";

    static String attr = "baf fim foo";
    static String policy = "foo bar fim 2of3 baf 1of2";

    static String student_attr = "objectClass:inetOrgPerson objectClass:organizationalPerson "
                    + "sn:student2 cn:student2 uid:student2 userPassword:student2 "
                    + "ou:idp o:computer mail:student2@sdu.edu.cn title:student";

    static String student_policy = "sn:student2 cn:student2 uid:student2 3of3";

    @RequestMapping(value = "/demo",method = RequestMethod.GET)
    public String demo(ModelMap model) throws Exception {
        String attr_str;
        String str;
        attr_str = attr;
        policy = policy;

        Cpabe test = new Cpabe();
        str = "//start to setup<br>";
        /**
         * 初始化接口，生成公共参数 PK 和主密钥 MK，并分别存储到 pubfile 和 mskfile 对应的文件路径中去。 
         */
        test.setup(pubfile, mskfile);
        str +="//end to setup<br>";

        str += "//start to keygen<br>";
        /**
         * KeyGen 私钥生成算法接口
         * 用户的每个属性是由字符串表示的，将这些字符串用空格分割连接成一个长的字符串 S，称 S 为用户的属性串（子串的先后顺序无关）。
         * 比如说学生甲拥有 的属性分别用字符串描述为：“sid_2008001”（表示学号是 2008001）、 “birthdate_19900101”（生日是 19900101）、“ sname_jia”（表示名字叫“甲”）
         * 则学生甲的属性串 S 为： “sid_2008001  birthdate_19900101  sname_jia”
         */
        test.keygen(pubfile, prvfile, mskfile, attr_str);
        str +="//end to keygen<br>";

        str +="//start to enc<br>";
        /**
         * 加密算法从pubfile中读取公共参数，在访问策略policy下将inputfile 指定的文件加密为路径为encfile的文件。
         * 其中访问策略是用后序遍历门限编码的字符串。
         * 比如访问策略“ foo bar fim 2of3 baf 1of2 ”指定了含有两个门限四个叶子节点的访问策略
         * 并且拥有属性“ baf ”或者“ foo ”、“ bar ”、“ fim ”中 的至少两个的属性集合满足该访问策略。 
         */
        str += test.enc(pubfile, policy, inputfile, encfile);
        str +="<br>//end to enc<br>";

        str +="//start to dec<br>";
        /**
         * 解密算法从 pubfile 指定的文件中读入公共参数，从 prvfile 中读入用 户私钥，将加密文件 encfile 解密为 decfile。 
         */
        str += test.dec(pubfile, prvfile, encfile, decfile);
        str +="<br>//end to dec<br>";
        model.put("data",str);
        return "demo";
    }
    
    private static void println(Object o) {
		if (DEBUG)
			System.out.println(o);
	}
}
