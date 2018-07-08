package com.lancq.activity.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author lancq
 * @Description
 * @Date 2018/7/8
 **/
@RestController
public class IndexController extends BaseController {
    @PostMapping("/doDraw")
    public String doDraw(){
        return "success";
    }

}
