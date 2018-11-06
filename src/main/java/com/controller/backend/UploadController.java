package com.controller.backend;

import com.common.ServerResponse;
import com.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping(value = "/manage/product")
public class UploadController {
    @Autowired
    IProductService productService;

    @RequestMapping(value = "/upload",method =RequestMethod.GET)
    public String upload(){
        return "upload";//逻辑试图  springmvc 加载文件 前缀+逻辑视图+后缀
    }

    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    @ResponseBody
    //返回值对象转为json格式   @ResponseBody
    public ServerResponse upload2(@RequestParam(value = "upload_file",required = false)MultipartFile file){
        String path = "E:\\java 学习\\java学习下\\Git与阿里云配置\\ftpfile";
        return productService.upload(file,path);
    }


}
