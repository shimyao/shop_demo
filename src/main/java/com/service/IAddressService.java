package com.service;

import com.common.ServerResponse;
import com.pojo.Shipping;

public interface IAddressService {
    //添加收货地址
    ServerResponse add(Integer userId, Shipping shipping);
    //删除地址
    ServerResponse del(Integer userId,Integer shippingId);
    //更新收货地址
    ServerResponse update(Shipping shipping);
    //选中查看具体地址
    ServerResponse select(Integer shippingId);
    //地址列表--分页
    ServerResponse list(Integer pageNum,Integer pageSize);

}
