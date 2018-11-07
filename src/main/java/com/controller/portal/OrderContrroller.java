package com.controller.portal;

import com.common.Const;
import com.common.ServerResponse;
import com.pojo.UserInfo;
import com.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/order")
public class OrderContrroller {
    @Autowired
    IOrderService iOrderService;

    //创建订单
    @RequestMapping(value = "/create.do")
    public ServerResponse createOrder(HttpSession session, Integer shippingId){
        UserInfo userInfo = (UserInfo) session.getAttribute(Const.CURRENTUSER);
        if (userInfo==null){
            return ServerResponse.serverResponseByError("需要登录");
        }
        return iOrderService.createOrder(userInfo.getId(),shippingId);
    }

}
