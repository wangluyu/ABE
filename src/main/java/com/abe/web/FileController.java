package com.abe.web;

import com.abe.entity.FileData;
        import com.abe.entity.Message;
        import cpabe.Cpabe;
        import org.springframework.stereotype.Controller;
        import org.springframework.web.bind.annotation.RequestMapping;
        import org.springframework.web.bind.annotation.RequestMethod;
        import org.springframework.web.bind.annotation.RequestParam;
        import org.springframework.web.bind.annotation.ResponseBody;
        import org.springframework.web.multipart.MultipartFile;

        import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
        import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Date;
        import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author LuyuWang
 * @date 2017/1/19
 * @time 9:30
 */
@Controller
@RequestMapping("/file")
public class FileController extends BaseController{
    /**
     * setup
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @RequestMapping(value = "/setup",method = RequestMethod.POST)
    public void setup(HttpServletResponse response) throws IOException, ClassNotFoundException {
        //生成随机文件夹
        String path = randomDir(15);

        String publicKey = path+"/"+"publicKey";
        String masterKey = path+"/"+"masterKey";
        Cpabe abe = new Cpabe();
        abe.setup(publicKey,masterKey);
        //2个源文件
        File f1 = new File(publicKey);
        File f2 = new File(masterKey);
        File[] srcfile = { f1, f2 };
        //压缩后的文件
        String zipPath = path+"/"+"公钥与主密钥.zip";
        File zipfile = new File(zipPath);
        zipFiles(srcfile, zipfile);

        //下载打包好的文件
        File file = new File(zipPath);
        response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(file.getName(),"UTF-8"));
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(zipPath);
            int len = 0;
            byte[] buffer = new byte[1024];
            out = response.getOutputStream();
            while((len = in.read(buffer)) > 0) {
                out.write(buffer,0,len);
            }

        }catch(Exception e) {
            deleteDir(file.getParentFile());
            throw new RuntimeException(e);
        }finally {
            if(in != null) {
                try {
                    in.close();
                    deleteDir(file.getParentFile());
                }catch(Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     *
     * @param keygenfiles
     * @param request
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    @RequestMapping(value = "keygen",method = RequestMethod.POST)
    @ResponseBody
    public List<Message> keygen(@RequestParam("keygenfile") MultipartFile[] keygenfiles,HttpServletRequest request) throws IOException, NoSuchAlgorithmException {
        List<Message> list = new ArrayList<Message>();
        Message msg = new Message();
        String path = randomDir(15);
        Integer i;
        try {
            //保存上传的公共参数与主密钥
            for (i=0;i<keygenfiles.length;i++){
        /*上传文件*/
                MultipartFile keygenfile = keygenfiles[i];
                //获取文件名
                String filename = path+"/"+keygenfile.getOriginalFilename();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(filename)));
                // 在本地储存文件
                stream.write(keygenfile.getBytes());
                stream.close();
            }
        } catch (Exception e) {
            msg.setCode(0);
            msg.setMsg("文件上传失败");
            String[] reason = {e.getMessage()};
            msg.setData(reason);
            System.out.println("文件上传失败"+e.getMessage());
        }
        try{
            //keygen算法生成私钥
            Cpabe abe = new Cpabe();
            String privateKey = path+"/privateKey";
            String attrStr = request.getParameter("attrStr");
            String[] a = {path+"/publicKey",privateKey,path+"/masterKey",attrStr};
            systemOutPrint("keygen算法生成私钥",a);
            abe.keygen(path+"/publicKey",privateKey,path+"/masterKey",attrStr);
            msg.setCode(1);
            msg.setMsg("上传成功");
            String[] arr = {privateKey};
            msg.setData(arr);
        } catch (Exception e) {
            msg.setCode(0);
            msg.setMsg("私钥生成失败");
            String[] reason = {e.getMessage()};
            msg.setData(reason);
            System.out.println("私钥生成失败"+e.getMessage());
        }

        list.add(msg);
        return list;
    }

    @RequestMapping(value = "/download", method = RequestMethod.POST)
    @ResponseBody
    public void download(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getParameter("path");
        File file = new File(path);
        response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(file.getName(),"UTF-8"));
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(path);
            int len = 0;
            byte[] buffer = new byte[1024];
            out = response.getOutputStream();
            while((len = in.read(buffer)) > 0) {
                out.write(buffer,0,len);
            }

        }catch(Exception e) {
            deleteDir(file.getParentFile());
            throw new RuntimeException(e);
        }finally {
            if(in != null) {
                try {
                    in.close();
                    deleteDir(file.getParentFile());
                }catch(Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
//    public ResponseEntity<InputStreamResource> download(HttpServletRequest request) throws IOException {
//        String path = request.getParameter("path");
//        FileSystemResource file = new FileSystemResource(path);
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
//        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", URLEncoder.encode(file.getFilename(),"UTF-8")));
//        headers.add("Pragma", "no-cache");
//        headers.add("Expires", "0");
//
//        return ResponseEntity
//                .ok()
//                .headers(headers)
//                .contentLength(file.contentLength())
//                .contentType(MediaType.parseMediaType("application/octet-stream"))
//                .body(new InputStreamResource(file.getInputStream()));
//    }
        /**
         * 加密文件
         * @param encryptfiles 接收到的文件
         * @param request   其他数据
         * @return
         */
    @RequestMapping(value = "/encrypt", method = RequestMethod.POST)
    @ResponseBody
    public List<Message> encrypt(@RequestParam("encryptfile") MultipartFile[] encryptfiles,HttpServletRequest request) throws IOException {
        List<Message> list = new ArrayList<Message>();
        Message msg = new Message();
        Integer i = 0;
        Integer fileLen = encryptfiles.length;
        String publicKey = null;
        String[] files = new String[fileLen-1];
        String[] encfiles = new String[fileLen-1];
        int j = 0;
        //获取当前年月日,充当文件夹路径一部分
        String pathDate = new SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
        String directory = "temp/file/"+pathDate;
        //判断文件夹是否存在,若不存在则创建
        File dir = new File(directory);
        judeDirExists(dir);
        judeDirExists(new File(directory+"/enc"));
        String filepath = "";
            for(i = 0;i<fileLen;i++) {
                /*上传文件*/
                MultipartFile encryptfile = encryptfiles[i];
                //获取文件名
                String filename = encryptfile.getOriginalFilename();
                if(filename.equals("publicKey")){
                    publicKey = randomDir(15)+"/publicKey";
                    filepath = publicKey;
                }else{
                    filepath = directory+"/"+filename;
                    //已存在文件重命名
                    filepath = reName(filepath);
                    files[j] = filepath;
                    encfiles[j++] = reName(directory+"/enc/"+filename);
                }
                // 在本地储存文件
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(filepath)));
                stream.write(encryptfile.getBytes());
                stream.close();
            }
        try{
            Cpabe abe = new Cpabe();
            String policy = request.getParameter("policy");
            for (j=0;j<files.length;j++){
                judeFileExists(new File(encfiles[j]));
                abe.enc(publicKey,policy,files[j],encfiles[j]);
                //删除明文
//                deleteFile(files[j]);
            }
            msg.setCode(1);
            msg.setMsg("加密成功");
            Object[] arr = {j+"个加密上传成功",encfiles};
            msg.setData(arr);
        } catch (Exception e) {
            msg.setCode(0);
            msg.setMsg("文件加密失败");
            String[] reason = {e.getMessage()};
            msg.setData(reason);
            System.out.println("文件加密失败 "+e.getMessage());
        }
        list.add(msg);
        return list;
    } // method uploadFile

    /**
     * jiemi文件
     * @param request
     * @throws IOException
     */
    @RequestMapping(value="/decrypt",method=RequestMethod.POST)
    @ResponseBody
    public List<Message> decrypt(@RequestParam("decryptfile") MultipartFile[] decryptfiles,HttpServletRequest request) throws IOException {
        List<Message> list = new ArrayList<Message>();
        Message msg = new Message();
        Integer i = 0;
        Integer fileLen = decryptfiles.length;
        String publicKey = null;
        String privateKey = null;
        String dir = randomDir(15);
        for(i = 0;i<fileLen;i++) {
            MultipartFile decryptfile = decryptfiles[i];
            //获取文件名
            String filename = decryptfile.getOriginalFilename();
            if(filename.equals("publicKey")){
                publicKey = dir+"/publicKey";
                // 在本地储存文件
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(publicKey)));
                stream.write(decryptfile.getBytes());
                stream.close();
            }else if(filename.equals("privateKey")){
                privateKey = dir+"/privateKey";
                // 在本地储存文件
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(privateKey)));
                stream.write(decryptfile.getBytes());
                stream.close();
            }
        }
        try{
            Cpabe abe = new Cpabe();
            String encfile = "temp/file/"+request.getParameter("encDate")+"/enc/"+request.getParameter("encName");
            String decfile = dir+"/"+request.getParameter("encName");
            String[] test = {publicKey, privateKey, encfile, decfile};
            systemOutPrint("解密:",test);
            abe.dec(publicKey, privateKey, encfile, decfile);
            msg.setCode(1);
            msg.setMsg("加密成功");
            String[] arr = {decfile};
            msg.setData(arr);
        } catch (Exception e) {
            msg.setCode(0);
            msg.setMsg("文件解密失败");
            String[] reason = {e.getMessage()};
            msg.setData(reason);
            System.out.println("文件解密失败 "+e.getMessage());
        }
        list.add(msg);
        return list;
    }

    /**
     * 读取某个文件夹下的所有文件
     */
    @RequestMapping(value = "/getFile",method = RequestMethod.GET)
    @ResponseBody
    public Message readfile(HttpServletRequest request){
//        String filepath = request.getSession().getServletContext().getRealPath(request.getParameter("filePath"));
        List arr = new ArrayList();
        String filepath = null;
        if(request.getParameter("filePath")==null || request.getParameter("filePath").length() <= 0){
            filepath = "temp/file";
        }else{
            filepath = "temp/file/"+request.getParameter("filePath")+"/enc";
        }
        Message msg = new Message();
        try {
            File file = new File(filepath);
            if (!file.isDirectory()) {
                System.out.println("path=" + file.getPath());
                System.out.println("absolutepath=" + file.getAbsolutePath());
                System.out.println("name=" + file.getName());

            } else if (file.isDirectory()) {
                String[] filelist = file.list();
                for (int i = 0; i < filelist.length; i++) {
                    File readfile = new File(filepath + "/" + filelist[i]);
                    FileData fileData = new FileData();
                    if (!readfile.isDirectory()) {
                        System.out.println("name=" + readfile.getName()+file.length());
                        fileData.setFileName(readfile.getName());
                        String fileSize = null;
                        double size = readfile.length();
                        DecimalFormat df = new DecimalFormat("#.00");
                        if (size < 1024) {
                            fileSize = df.format(size) + "B";
                        } else if (size < 1048576) {
                            fileSize = df.format(size / 1024) + "KB";
                        } else if (size < 1073741824) {
                            fileSize = df.format(size / 1048576) + "MB";
                        } else {
                            fileSize = df.format(size / 1073741824) +"GB";
                        }
                        fileData.setFileSize(fileSize);
                        fileData.setCreateTime(new SimpleDateFormat("yyyy-MM-dd").format(new Date(readfile.lastModified())));
                        fileData.setDir(0);
                        fileData.setExtension(getFileExtension(readfile.getName()));
                    }else {
                        fileData.setFileName(readfile.getName());
                        fileData.setFileSize("/");
                        fileData.setCreateTime(new SimpleDateFormat("yyyy-MM-dd").format(new Date(readfile.lastModified())));
                        fileData.setDir(1);
                        fileData.setExtension(null);
                    }
                    arr.add(fileData);
                }
            }

        } catch (Exception e) {
            System.out.println("readfile()   Exception:" + e.getMessage());
        }
        msg.setData(arr);
        msg.setCode(1);
        msg.setMsg("获取成功");
        return msg;
    }

    /**
     * 删除文件
     * @param path 被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public boolean deleteFile(String path) {
        boolean flag = false;
        File file = new File(path);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     *                 If a deletion fails, the method stops attempting to
     *                 delete and returns "false".
     */
    public boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            systemOutPrint("删除文件",dir.getName());
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    // 判断文件是否存在
    public boolean judeFileExists(File file) {
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    // 判断文件夹是否存在
    public void judeDirExists(File dir) {
        if (!dir.exists()) {
            try{
                dir.mkdirs();
                systemOutPrint("judeDirExists","已创建");
            }catch (Exception e){
                systemOutPrint("judeDirExists",e.getMessage());
            }
        }
    }

    //文件重命名
    public String reName(String filepath){
        File file =new File(filepath);
        Integer i = 1;
        String filepathCopy = filepath;//文件名copy
        String[] pathArray;
        //重命名 循环
        while (judeFileExists(file)){
            pathArray = filepath.split("\\.");
            filepathCopy = pathArray[0]+"("+i.toString()+")."+pathArray[1];
            file =new File(filepathCopy);
            systemOutPrint("file",file);
            i++;
        }
        filepath = filepathCopy;
        return filepath;
    }

    /**
     * 获取后缀名
     * @param name
     * @return
     */
    public String getFileExtension(String name){
        return name == null || name.indexOf(".") == -1? null:name.substring(name.lastIndexOf(".") + 1);
    }

    /**
     * 压缩多个文件成一个zip文件
     * @param srcfile：源文件列表
     * @param zipfile：压缩后的文件
     */
    public static void zipFiles(File[] srcfile, File zipfile) {
        byte[] buf = new byte[1024];
        try {
            //ZipOutputStream类：完成文件或文件夹的压缩
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));
            for (int i = 0; i < srcfile.length; i++) {
                FileInputStream in = new FileInputStream(srcfile[i]);
                out.putNextEntry(new ZipEntry(srcfile[i].getName()));
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.closeEntry();
                in.close();
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成随机名字文件夹
     * @param length
     * @return
     */
    public String randomDir(int length){
        Boolean flag = true;
        String path = "temp/";
        while (flag){
            path += getRandomString(length);
            File dir = new File(path);
            if (!dir.exists()){
                flag = false;
                dir.mkdirs();
            }
        }
        return path;
    }
}