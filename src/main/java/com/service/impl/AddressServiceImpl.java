package com.service.impl;

import com.common.ServerResponse;
import com.dao.ShippingMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.pojo.Shipping;
import com.service.IAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AddressServiceImpl implements IAddressService {
    @Autowired
    ShippingMapper shippingMapper;

    @Override
    public ServerResponse add(Integer userId, Shipping shipping) {
       //参数校验
       if (shipping==null){
           return ServerResponse.serverResponseByError("参数错误");
       }
       //添加
        shipping.setUserId(userId);
        shippingMapper.insert(shipping);
        //返回结果
        Map<String,Integer> map = Maps.newHashMap();
        map.put("shippingId",shipping.getId());
        return ServerResponse.serverResponseBySuccess(map);
    }

    @Override
    public ServerResponse del(Integer userId, Integer shippingId) {
        //参数非空校验
        if (shippingId==null){
            return ServerResponse.serverResponseByError("参数错误");
        }
        //删除
        int result = shippingMapper.deleteByUserIdAndShippingId(userId,shippingId);
        //返回结果
        if (result>0){
            return ServerResponse.serverResponseBySuccess();
        }
        return ServerResponse.serverResponseByError("删除失败");
    }

    @Override
    public ServerResponse update(Shipping shipping) {
        //非空判断
        if (shipping==null){
            return ServerResponse.serverResponseByError("参数错误");
        }
        //更新
        int result= shippingMapper.updateBySelectiveKey(shipping);
        //返回结果
        if (result>0){
            return ServerResponse.serverResponseBySuccess();
        }
        return ServerResponse.serverResponseByError("更新失败");
    }

    @Override
    public ServerResponse select(Integer shippingId) {
        //参数非空校验
        if (shippingId==null){
            return ServerResponse.serverResponseByError("参数错误");
        }
        //查看
        Shipping shipping =shippingMapper.selectByPrimaryKey(shippingId);
        if (shipping==null){
            return ServerResponse.serverResponseByError("地址不存在");
        }
        //返回结果
        return ServerResponse.serverResponseBySuccess(shipping);
    }

    @Override
    public ServerResponse list(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.selectAll();
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.serverResponseBySuccess(pageInfo);
    }
}
