package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.domain.AddressBook;
import com.itheima.reggie.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.callback.LanguageCallback;
import java.util.List;

@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook){

        //通过BaseContext通用类获取用户Id
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook){

        //Update条件构造器
        LambdaUpdateWrapper<AddressBook> lqw = new LambdaUpdateWrapper<>();

        //把所有地址IsDefault改成0
        lqw.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        lqw.set(AddressBook::getIsDefault,0);
        addressBookService.update(lqw);

        //把用户要改的地址设置成默认地址1
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R get(@PathVariable Long id){

        AddressBook addressBook = addressBookService.getById(id);
        //判断查询到的数据是否为空
        if (addressBook != null){
            return R.success(addressBook);
        }else {
            return R.error("没有找到该对象");
        }
    }

    /**
     * 查询默认地址
     * @return
     */
    @GetMapping("default")
    public R<AddressBook> getdefault(){

        LambdaQueryWrapper<AddressBook> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        lqw.eq(AddressBook::getIsDefault,1);
        AddressBook addressBook = addressBookService.getOne(lqw);

        if (addressBook != null){
            return R.success(addressBook);
        }else {
            return R.error("没有找到该对象");
        }
    }

    /**
     * 查询用户所有地址信息
     * @param addressBooks
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> getlist(AddressBook addressBooks){

        addressBooks.setUserId(BaseContext.getCurrentId());

        LambdaQueryWrapper<AddressBook> lqw = new LambdaQueryWrapper<>();
        lqw.eq(addressBooks.getUserId() != null,AddressBook::getUserId,addressBooks.getUserId());
        lqw.orderByDesc(AddressBook::getUpdateTime);

        return R.success(addressBookService.list(lqw));
    }
}
