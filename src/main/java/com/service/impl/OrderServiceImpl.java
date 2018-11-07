package com.service.impl;

import com.Utils.BigDecimalUtils;
import com.Utils.DateUtils;
import com.Utils.PropertiesUtils;
import com.common.Const;
import com.common.ServerResponse;
import com.dao.*;
import com.google.common.collect.Lists;
import com.pojo.*;
import com.service.IOrderService;
import com.vo.OrderItemVO;
import com.vo.OrderVO;
import com.vo.ShippingVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service
public class OrderServiceImpl implements IOrderService {
    @Autowired
    CartMapper cartMapper;
    @Autowired
    ProductMapper productMapper;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderItemMapper orderItemMapper;
    @Autowired
    ShippingMapper shippingMapper;

    @Override
    public ServerResponse createOrder(Integer userId,Integer shippingId) {
        //1.参数非空校验
         if (shippingId==null){
             return ServerResponse.serverResponseByError("地址参数不能为空");
         }
        //2.根据userId查询购物车中已选中商品 -->List<Cart>
        List<Cart> cartList= cartMapper.findCartListByUserIdAndChecked(userId);

        //3.List<Cart>-->List<OrderItem>
        ServerResponse serverResponse = getCartOrderItem(userId,cartList);
        if(!serverResponse.isSuccess()){
            return serverResponse;
        }
        //4.创建订单order并将其保存到数据库
           //计算订单的价格
        BigDecimal orderTotalPrice = new BigDecimal("0");
        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
        if (orderItemList==null||orderItemList.size()==0){
            return ServerResponse.serverResponseByError("购物车为空");
        }
        orderTotalPrice= getOrderPrice(orderItemList);
        Order order= createOrder(userId,shippingId,orderTotalPrice);
        if (order==null){
            return ServerResponse.serverResponseByError("订单创建失败");
        }
        //5.将List<OrderItem>保存到数据库
        for (OrderItem orderItem : orderItemList){
            orderItem.setOrderNo(order.getOrderNo());
        }
            //批量插入
         orderItemMapper.insertBatch(orderItemList);
        //6.扣库存
        reduceProductStock(orderItemList);
        //7.购物车清空已下单的商品
         cleanCart(cartList);
        //8.返回OrderVO
          OrderVO orderVO = assembleOrderVO(order,orderItemList,shippingId);
        return ServerResponse.serverResponseBySuccess(orderVO);
    }

    //构建orderVO
    private OrderVO assembleOrderVO(Order order,List<OrderItem> orderItemList,Integer shippingId){
        OrderVO orderVO = new OrderVO();
        List<OrderItemVO> orderItemVOList = Lists.newArrayList();
        for (OrderItem orderItem:orderItemList){
            OrderItemVO orderItemVO=assembleOrderItemVO(orderItem);
            orderItemVOList.add(orderItemVO);
        }
        orderVO.setOrderItemVOList(orderItemVOList);
        orderVO.setImageHost(PropertiesUtils.readByKey("imageHost"));
        Shipping shipping = shippingMapper.selectByPrimaryKey(shippingId);
        if (shipping!=null){
            orderVO.setShippingId(shippingId);
            ShippingVO shippingVO =assembleShippingVO(shipping);
            orderVO.setShippingVo(shippingVO);
            orderVO.setReceiverName(shipping.getReceiverName());
        }
        orderVO.setStatus(order.getStatus());
        Const.OrderStatusEnum orderStatusEnum = Const.OrderStatusEnum.codeOf(order.getStatus());
        if (orderStatusEnum!=null){
            orderVO.setStatusDesc(orderStatusEnum.getDesc());
        }
        orderVO.setPostage(0);
        orderVO.setPayment(order.getPayment());
        orderVO.setPaymentType(order.getPaymentType());
         Const.PaymentEnum paymentEnum = Const.PaymentEnum.codeOf(order.getPaymentType());
         if (paymentEnum!=null){
             orderVO.setPaymentTypeDesc(paymentEnum.getDesc());
         }
         orderVO.setOrderNo(order.getOrderNo());
        return orderVO;
    }

    //构建shippingVO
    private ShippingVO assembleShippingVO(Shipping shipping){
        ShippingVO shippingVO = new ShippingVO();
        if (shipping!=null){
            shippingVO.setReceiverAddress(shipping.getReceiverAddress());
            shippingVO.setReceiverCity(shipping.getReceiverCity());
            shippingVO.setReceiverDistrict(shipping.getReceiverDistrict());
            shippingVO.setReceiverMobile(shipping.getReceiverMobile());
            shippingVO.setReceiverName(shipping.getReceiverName());
            shippingVO.setReceiverPhone(shipping.getReceiverPhone());
            shippingVO.setReceiverProvince(shipping.getReceiverProvince());
            shippingVO.setReceiverZip(shipping.getReceiverZip());
        }
        return shippingVO;
    }

    //构建OrderItemVO
    private OrderItemVO assembleOrderItemVO(OrderItem orderItem){
        OrderItemVO orderItemVO = new OrderItemVO();
        if (orderItem!=null){
            orderItemVO.setQuantity(orderItem.getQuantity());
            orderItemVO.setCreateTime(DateUtils.dataToStr(orderItem.getCreateTime()));
            orderItemVO.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
            orderItemVO.setOrderNo(orderItem.getOrderNo());
            orderItemVO.setProductId(orderItem.getProductId());
            orderItemVO.setProductImage(orderItem.getProductImage());
            orderItemVO.setProductName(orderItem.getProductName());
            orderItemVO.setTotalPrice(orderItem.getTotalPrice());
        }
        return orderItemVO;
    }

    //清空购物车中已选中的商品
    private void cleanCart(List<Cart> cartList){
        if (cartList!=null&&cartList.size()>0){
            cartMapper.batchDelete(cartList);
        }
    }

    //扣库存
    private void reduceProductStock(List<OrderItem> orderItemList){
        if (orderItemList!=null&&orderItemList.size()>0){
            for (OrderItem orderItem:orderItemList){
               Integer productId = orderItem.getProductId();
               Integer quantity = orderItem.getQuantity();
               Product product = productMapper.selectByPrimaryKey(productId);
               product.setStock(product.getStock()-quantity);
               productMapper.updateByPrimaryKey(product);
            }
        }
    }

    //计算订单的总价格
    private BigDecimal getOrderPrice(List<OrderItem> orderItemList){
        BigDecimal bigDecimal = new BigDecimal("0");
        for(OrderItem orderItem : orderItemList){
           bigDecimal = BigDecimalUtils.add(bigDecimal.doubleValue(),orderItem.getTotalPrice().doubleValue());
        }
        return bigDecimal;
    }

    //创建订单
    private Order createOrder(Integer userId,Integer shippingId,BigDecimal orderTotalPrice){
        Order order = new Order();
        order.setOrderNo(generateOrderNO());
        order.setUserId(userId);
        order.setShippingId(shippingId);
        order.setStatus(Const.OrderStatusEnum.ORDER_UN_PAY.getCode());
        //订单金额
        order.setPayment(orderTotalPrice);
        //邮费
        order.setPostage(0);
        //支付类型
        order.setPaymentType(Const.PaymentEnum.ONLINE.getCode());
        //保存订单
        int result = orderMapper.insert(order);
        if (result>0){
            return order;
        }
        return null;
    }

    //生成订单编号---唯一性
    private Long generateOrderNO(){
        //当前下单的时间戳，精确到毫秒  拿到毫秒值，在随机生成一个100以内的值
        return System.currentTimeMillis()+new Random().nextInt(100);
    }

    //List<Cart>-->List<OrderItem>
    private ServerResponse getCartOrderItem(Integer userId,List<Cart> cartList){
        if (cartList==null||cartList.size()==0){
            return ServerResponse.serverResponseByError("购物车为空");
        }
        List<OrderItem> orderItemList = Lists.newArrayList();

        for(Cart cart:cartList){
            OrderItem orderItem = new OrderItem();
            orderItem.setUserId(userId);
            Product product =  productMapper.selectByPrimaryKey(cart.getProductId());
            if (product==null){
                return ServerResponse.serverResponseByError("id为"+cart.getProductId()+"的商品不存在");
            }
            if(product.getStatus()!=Const.ProductStatusEnum.PRODUCT_ONLINE.getCode()){
                //status!=1 即不是在售状态，商品下架
                return ServerResponse.serverResponseByError("id为"+product.getId()+"的商品已经下架");
            }
            if (product.getStock()<cart.getQuantity()){
                //库存不足
                return ServerResponse.serverResponseByError("id为"+product.getId()+"的商品库存不足");
            }
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setProductId(product.getId());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setProductName(product.getName());
            orderItem.setTotalPrice(BigDecimalUtils.mul(product.getPrice().doubleValue(),cart.getQuantity().doubleValue()));

            orderItemList.add(orderItem);
        }
        return ServerResponse.serverResponseBySuccess(orderItemList);
    }

}
