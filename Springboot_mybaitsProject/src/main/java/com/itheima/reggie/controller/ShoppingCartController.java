package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import com.itheima.reggie.domain.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    //15112345678
    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/list")
    public R<List<ShoppingCart>> list(ShoppingCart shoppingCart){

        return null;
    }
    @PostMapping("/add")
    public R<ShoppingCart> add(ShoppingCart shoppingCart){

        return null;
    }
}
