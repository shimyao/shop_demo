package com.service.impl;

import com.Utils.DateUtils;
import com.Utils.PropertiesUtils;
import com.common.Const;
import com.common.ServerResponse;
import com.dao.CategoryMapper;
import com.dao.ProductMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pojo.Category;
import com.pojo.Product;
import com.service.ICategoryService;
import com.service.IProductService;
import com.vo.ProductDetailVO;
import com.vo.ProductListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class ProductServiceImpl implements IProductService {
    @Autowired
    ProductMapper productMapper;
    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    ICategoryService categoryService;

    @Override
    public ServerResponse saveOrUpdate(Product product) {
       //1.参数非空校验
        if (product==null){
            return ServerResponse.serverResponseByError("参数为空");
        }
        //2.设置商品主图 1.jpg     sub_images  -->1.jpg,2.jpg,3.jpg
        String subImages = product.getSubImages();
        if (subImages!=null&&!subImages.equals("")){
            String [] subImageArr = subImages.split(",");
            if (subImageArr.length>0){
                //设置主图
                product.setMainImage(subImageArr[0]);
            }
        }
        //3.商品 save or  update
         if (product.getId()==null){
            //判断商品信息是否重复
           //List<Product> productList = productMapper.selectAll();

            //添加
            int result= productMapper.insert(product);
            if (result>0){
                return ServerResponse.serverResponseBySuccess("添加成功");
            }else {
                return ServerResponse.serverResponseByError("添加失败");
            }
         }else {
            //更新
             int result= productMapper.updateByPrimaryKey(product);
             if (result>0){
                 return ServerResponse.serverResponseBySuccess("更新成功");
             }else {
                 return ServerResponse.serverResponseByError("更新失败");
             }
         }
    }

    @Override
    public ServerResponse set_sale_status(Integer productId, Integer status) {
        //1.参数非空校验
        if (productId==null){
            return ServerResponse.serverResponseByError("商品id参数不能为空");
        }
        if (status==null){
            return ServerResponse.serverResponseByError("商品状态参数不能为空");
        }
        //2.更新商品的状态
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int result = productMapper.updateProductKeySelective(product);
        //3.返回结果
        if (result>0){
            return ServerResponse.serverResponseBySuccess("更新商品状态成功");
        }else {
            return ServerResponse.serverResponseByError("更新失败");
        }
    }

    @Override
    public ServerResponse detail(Integer productId) {
        //1.参数校验
        if (productId==null){
            return ServerResponse.serverResponseByError("商品id参数不能为空");
        }
        //2.查询product
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product==null){
            return ServerResponse.serverResponseByError("商品不存在");
        }
        //3.product -->productDetailVO
        ProductDetailVO productDetailVO = assembleProductDetailVO(product);
        //4.返回结果
        return ServerResponse.serverResponseBySuccess(productDetailVO);
    }

    private ProductDetailVO assembleProductDetailVO(Product product){
        ProductDetailVO productDetailVO = new ProductDetailVO();
        productDetailVO.setCategoryId(product.getCategoryId());
        productDetailVO.setName(product.getName());
        productDetailVO.setCreateTime(DateUtils.dataToStr(product.getCreateTime()));
        productDetailVO.setDetail(product.getDetail());
        //productDetailVO.setImageHost("http://localhost:8080");
        productDetailVO.setImageHost(PropertiesUtils.readByKey("imageHost"));
        productDetailVO.setMainImage(product.getMainImage());
        productDetailVO.setId(product.getId());
        productDetailVO.setPrice(product.getPrice());
        productDetailVO.setStatus(product.getStatus());
        productDetailVO.setStock(product.getStock());
        productDetailVO.setSubImages(product.getSubImages());
        productDetailVO.setSubtitle(product.getSubtitle());
        productDetailVO.setUpdateTime(DateUtils.dataToStr(product.getUpdateTime()));
        //productDetailVO.setParentCategoryId();
         Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
         if (category!=null){
             productDetailVO.setParentCategoryId(category.getId());
         }else{
             //默认根节点
             productDetailVO.setParentCategoryId(0);
         }
        return productDetailVO;
    }

    @Override
    public ServerResponse list(Integer pageNum, Integer pageSize) {
         //使用分页插件
        PageHelper.startPage(pageNum,pageSize);
        //在执行select * from product语句之前，把它拿过来，然后加上limit
        //查询商品数据  select * from product limit ((pageNum-1)*pageSize,pageSize)
        List<Product> productList = productMapper.selectAll();
        List<ProductListVO> productListVOList = Lists.newArrayList();

        if (productList!=null&&productList.size()>0){
            for (Product product : productList) {
               ProductListVO productListVO = assembleProductListVO(product);
                productListVOList.add(productListVO);
            }
        }

        PageInfo pageInfo =new PageInfo(productListVOList);

        return ServerResponse.serverResponseBySuccess(pageInfo);
    }

    private ProductListVO assembleProductListVO(Product product){
        ProductListVO productListVO = new ProductListVO();
        productListVO.setId(product.getId());
        productListVO.setCategoryId(product.getCategoryId());
        productListVO.setMainImage(product.getMainImage());
        productListVO.setName(product.getName());
        productListVO.setSubtitle(product.getSubtitle());
        productListVO.setStatus(product.getStatus());
        productListVO.setPrice(product.getPrice());

        return productListVO;
    }

    @Override
    public ServerResponse search(Integer productId, String productName, Integer pageNum, Integer pageSize) {
        //select * from product where productId ? and productName like %name%;
        PageHelper.startPage(pageNum,pageSize);
        //非空校验
        if (productName!=null&&!productName.equals(" ")){
            productName="%"+productName+"%";
        }
        List<Product> productList = productMapper.findProductByProductIdAndProductName(productId,productName);
        List<ProductListVO> productListVOList = Lists.newArrayList();
        if (productList!=null&&productList.size()>0){
            for (Product product : productList) {
                ProductListVO productListVO = assembleProductListVO(product);
                productListVOList.add(productListVO);
            }
        }
        PageInfo pageInfo =new PageInfo(productListVOList);
        return ServerResponse.serverResponseBySuccess(pageInfo);
    }

    @Override
    public ServerResponse upload(MultipartFile file, String path) {
        //非空判断
        if (file==null){
            return ServerResponse.serverResponseByError();
        }
        //获取图片的名称,生成唯一的名称
        String originalFilename = file.getOriginalFilename();
            //获取扩展名
        String exName = originalFilename.substring(originalFilename.lastIndexOf("."));//.jpg
            //生成新的名字
        String newFileName = UUID.randomUUID().toString()+exName;

        File pathFile = new File(path);
        //判断路径存不存在
        if (!pathFile.exists()){
            //不存在，，设置可写
            pathFile.setWritable(true);
            //生成目录
            pathFile.mkdirs();
        }
        //把文件写到目录下面
        File file1 = new File(path,newFileName);
        try {
            file.transferTo(file1);
            //图片上传到图片服务器
            //......
            Map<String,String> map = Maps.newHashMap();
            map.put("uri",newFileName);
            map.put("url",PropertiesUtils.readByKey("imageHost")+"/"+newFileName);
            return ServerResponse.serverResponseBySuccess(map);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    //前台--商品详情
    @Override
    public ServerResponse detail_portal(Integer productId) {
        //参数非空校验
        if (productId==null){
            return ServerResponse.serverResponseByError("商品id参数不能为空");
        }

        //根据productId查询商品
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product==null){
            return ServerResponse.serverResponseByError("商品不存在");
        }
        //校验商品状态
         if (product.getStatus()!=Const.ProductStatusEnum.PRODUCT_ONLINE.getCode()){
            return ServerResponse.serverResponseByError("商品已下架或删除");
         }

        //获取productDetailVO
         ProductDetailVO productDetailVO = assembleProductDetailVO(product);
        //返回结果
        return ServerResponse.serverResponseBySuccess(productDetailVO);
    }

    @Override
    public ServerResponse list_portal(Integer categoryId, String keyword, Integer pageNum, Integer pageSize, String orderBy) {
        //参数校验，categroyId 和keyword 不能同时为空
        if (categoryId==null&&(keyword==null||keyword.equals(""))){
            return ServerResponse.serverResponseByError("参数错误");
        }
        //根据categroyId查询
        Set<Integer> integerSet = Sets.newHashSet();
         if (categoryId!=null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category==null&&(keyword==null||keyword.equals(""))){
                //说明没有商品数据
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVO> productListVOList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVOList);
                return ServerResponse.serverResponseBySuccess(pageInfo);
            }
            ServerResponse serverResponse = categoryService.get_deep_category(categoryId);

            if (serverResponse.isSuccess()){
             integerSet =(Set<Integer>) serverResponse.getData();
            }
        }
        //根据keyword查询
         if (keyword!=null&&!keyword.equals("")){
            keyword="%"+keyword+"%";
         }
         if (orderBy.equals("")){
             PageHelper.startPage(pageNum,pageSize);

         }else{
            String [] orderByArr = orderBy.split("_");
            if (orderByArr.length>1){
                PageHelper.startPage(pageNum,pageSize,orderByArr[0]+" "+orderByArr[1]);
            }else{
                PageHelper.startPage(pageNum,pageSize);
            }
         }
        //productList--->productListVO
        List<Product> productList = productMapper.searchProduct(integerSet,keyword);
         List<ProductListVO> productListVOList = Lists.newArrayList();
         if (productList!=null&&productList.size()>0){
             for (Product product : productList){
                 ProductListVO productListVO = assembleProductListVO(product);
                 productListVOList.add(productListVO);
             }
         }

        //分页
        PageInfo pageInfo = new PageInfo();
         pageInfo.setList(productListVOList);
        //返回结果
        return ServerResponse.serverResponseBySuccess(pageInfo);
    }


}
