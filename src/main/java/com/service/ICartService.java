package com.service;

import com.common.ServerResponse;

public interface ICartService {
    //购物车添加商品
    public ServerResponse add(Integer userId,Integer productId,Integer count);
    //购物车列表
    ServerResponse list(Integer userId);
    //更新购物车中某个商品的数量
    ServerResponse update(Integer userId,Integer productId,Integer count);
    //移除购物车某个商品
    ServerResponse delete_product(Integer userId,String productIds);
    //购物车选中某个商品
    ServerResponse select(Integer userId,Integer productId,Integer check);
    //查询购物车中商品数量
    ServerResponse get_cart_product_count(Integer userId);
}
