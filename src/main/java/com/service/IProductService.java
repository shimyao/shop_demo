package com.service;

import com.common.ServerResponse;
import com.pojo.Product;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public interface IProductService {
    //后台--新增或更新商品
   ServerResponse saveOrUpdate(Product product);
   //后台--产品上下架
   ServerResponse set_sale_status(Integer productId,Integer status);
   //后台--查看商品详情
   ServerResponse detail(Integer productId);
   //后台--查看商品列表--分页
   ServerResponse list(Integer pageNum,Integer pageSize);
   //后台--产品搜索
   ServerResponse search(Integer productId,String productName,Integer pageNum,Integer pageSize);
   //图片上传
   ServerResponse upload(MultipartFile file,String path);
   //前台--商品详情
   ServerResponse detail_portal(Integer productId);
   //前台--商品搜索并排序
   ServerResponse list_portal( Integer categoryId, String keyword, Integer pageNum, Integer pageSize, String OrderBy);



}
