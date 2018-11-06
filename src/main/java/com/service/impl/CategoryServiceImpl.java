package com.service.impl;

import com.common.ServerResponse;
import com.dao.CategoryMapper;
import com.google.common.collect.Sets;
import com.pojo.Category;
import com.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    CategoryMapper categoryMapper;

    @Override
    public ServerResponse get_category(Integer categoryId) {
        //1.非空校验
            if (categoryId==null) {
                return ServerResponse.serverResponseByError("参数不能为空");
            }
        //2.根据categoryid查询类别
           Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category==null){
                return ServerResponse.serverResponseByError("查询的类别不存在");
            }
        //3.查询子类别
          List<Category> categoryList = categoryMapper.findChildCategory(categoryId);
        //4.返回结果集
        return ServerResponse.serverResponseBySuccess(categoryList);
    }

    @Override
    public ServerResponse add_category(Integer parentId, String categoryName) {
        //1.参数校验
        if (categoryName==null||categoryName.equals("")){
            return ServerResponse.serverResponseByError("类别名称不能为空");
        }
        //判断增加的节点是否已经添加过
        Category category1 = categoryMapper.findByParentIdAndCategoryName(parentId,categoryName);
        if (category1!=null){
            return ServerResponse.serverResponseByError("该节点已被添加");
        }
        //2.添加节点
         Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(1);
        int result = categoryMapper.insert(category);
        //3.返回结果
        if (result>0){
            //添加成功
            return ServerResponse.serverResponseBySuccess();
        }
        return ServerResponse.serverResponseByError("添加失败");
    }

    @Override
    public ServerResponse set_category_name(Integer categoryId, String categoryName) {
        //1.参数非空校验
        if (categoryId==null||categoryId.equals("")){
            return ServerResponse.serverResponseByError("类别id不能为空");
        }
        if (categoryName==null||categoryName.equals("")){
            return ServerResponse.serverResponseByError("类别名称不能为空");
        }
        //2.根据categoryId 查询
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category==null){
            return ServerResponse.serverResponseByError("要修改的类别不存在");
        }
        if(categoryName.equals(category.getName())){
            return ServerResponse.serverResponseByError("修改的类别名称已存在");
        }
        //3.修改
        category.setName(categoryName);
        int result= categoryMapper.updateByPrimaryKey(category);
        //4.返回结果
        if (result>0){
            //修改成功
            return ServerResponse.serverResponseBySuccess();
        }
        return ServerResponse.serverResponseByError("修改失败");

    }

    @Override
    public ServerResponse get_deep_category(Integer categoryId) {
        //1.参数非空校验
        if (categoryId==null){
            return ServerResponse.serverResponseByError("类别id不能为空");
        }
        //2.查询
         Set<Category> categorySet = Sets.newHashSet();
         categorySet = findAllChhildCategory(categorySet,categoryId);

        Set<Integer> integerSet = Sets.newHashSet();

        Iterator<Category> categoryIterator = categorySet.iterator();
        while (categoryIterator.hasNext()){
            Category category = categoryIterator.next();
            integerSet.add(category.getId());
        }
        return ServerResponse.serverResponseBySuccess(integerSet);
    }
   private Set<Category> findAllChhildCategory(Set<Category> categorySet ,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category!=null){
           categorySet.add(category);
        }
        //查找categroyId下的子节点（平级）
       List<Category> categoryList = categoryMapper.findChildCategory(categoryId);
        if (categoryList!=null&&categoryList.size()>0){
            for (Category category1:categoryList) {
                findAllChhildCategory(categorySet,category1.getId());
            }
        }
        return categorySet;
   }

}
