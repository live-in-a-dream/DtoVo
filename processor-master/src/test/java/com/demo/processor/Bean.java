package com.demo.processor;/**
 * @title: LXC
 * @projectName processor
 * @author Administrator
 * @date 2022/1/2714:21
 */

import lombok.Data;

/**
 * @author LXC
 * @date 2022/1/27 14:21
 */
@Data
@EnabledDtoVo
public class Bean {

    @DtoVo
    private String name;

    @Vo
    private Integer user;

    @DtoVo
    private Integer id;


}
