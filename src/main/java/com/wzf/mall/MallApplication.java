package com.wzf.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
@MapperScan("com.wzf.mall.mbg.mapper")
public class MallApplication {
    public static void main(String[] args){SpringApplication.run(MallApplication.class);}
}
