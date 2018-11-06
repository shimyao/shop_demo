package com.dao;

import com.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cart
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cart
     *
     * @mbg.generated
     */
    int insert(Cart record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cart
     *
     * @mbg.generated
     */
    Cart selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cart
     *
     * @mbg.generated
     */
    List<Cart> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table cart
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(Cart record);

    Cart selectCartByUserIdAndProductId(@Param("userId")Integer userId,
                                        @Param("productId") Integer productId);

     //查询用户购物车信息
    List<Cart> selectCartByUserId(Integer userId);
     //统计用户购物信息是否全选---返回值>0，说明未全选
    int isCheckedAll(Integer userId);
    //删除购物车某些商品
     int deleteByUseridAndProductIds(@Param("userId") Integer userId,
                                     @Param("productIdList") List<Integer> productIdList);
     //操作购物车商品是否选中
    int selectOrUnselectProduct(@Param("userId") Integer userId,
                                @Param("productId") Integer productId,
                                @Param("check") Integer check);
     //统计购物车中商品数量
     int get_cart_product_count(Integer userId);
}