package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.Exception.CustomEXception;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.dao.OrdersDao;
import com.itheima.reggie.domain.*;
import com.itheima.reggie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersDao, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;
    /**
     * 用户下单
     * @param orders
     */
    @Override
    @Transactional
    public void submit(Orders orders) {

        //获取用户id
        Long userid = BaseContext.getCurrentId();

        //查询用户购物车信息
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper();
        lqw.eq(ShoppingCart::getUserId,userid);
        List<ShoppingCart> list = shoppingCartService.list(lqw);

        //查询用户
        User user = userService.getById(userid);

        //查询地址
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if (addressBook == null){
            throw new CustomEXception("用户地址有误，不能下单");
        }

        long orderId = IdWorker.getId();

        //原子操作，保证线程安全
        AtomicInteger amount = new AtomicInteger(0);
        List<OrderDetail> orderDetailList = list.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail. setOrderId (orderId);
            orderDetail. setNumber (item. getNumber ());
            orderDetail. setDishFlavor (item. getDishFlavor ());
            orderDetail. setDishId (item. getDishId ());
            orderDetail. setSetmealId (item. getSetmealId ());
            orderDetail. setName (item. getName ());
            orderDetail. setImage (item.getImage ());
            orderDetail. setAmount (item. getAmount ());
            amount. addAndGet (item. getAmount (). multiply (new BigDecimal (item. getNumber ())). intValue ());
            return orderDetail;
        }).collect(Collectors.toList());

        //向订单插入数据
        if (list == null || list.size() == 0){
            throw new CustomEXception("购物车里没有菜品");
        }
        orders. setId (orderId);
        orders. setOrderTime (LocalDateTime.now());
        orders. setCheckoutTime (LocalDateTime.now());
        orders. setStatus (2);//待派送状态
        orders. setAmount (new BigDecimal(amount.get()));
        orders. setUserId (userid);
        orders. setNumber (String. valueOf(orderId));
        orders. setUserName (user. getName ());
        orders. setConsignee (addressBook. getConsignee ());
        orders. setPhone (addressBook. getPhone ());
        orders. setAddress ((addressBook.getProvinceName() == null ? "" : addressBook. getProvinceName ())
                + (addressBook. getCityName () == null ?"" : addressBook. getCityName ())
                + (addressBook. getDistrictName () == null ? "" : addressBook. getDistrictName ())
                + (addressBook. getDetail () == null ? "" : addressBook. getDetail ())
        );
        this.save(orders);

        //向订单明细表插入数据
        orderDetailService.saveBatch(orderDetailList);

        //清空购物车数据
        shoppingCartService.remove(lqw);
    }
}
