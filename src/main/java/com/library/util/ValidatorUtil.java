/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.library.util;

import java.util.regex.Pattern;

/**
 * 数据格式校验工具类
 * 工具类特点：全部静态方法，无需实例化，项目全局通用
 * 作用：统一校验手机号、身份证号正则格式，解耦UI层校验代码，复用、便于维护
 * 使用位置：新增读者弹窗AddReaderFrame、修改读者弹窗UpdateReaderFrame
 * @author 81382
 */
public class ValidatorUtil {
    /**
     * 手机号正则常量
     * 规则：以1开头，第二位3-9，后面紧跟9位数字，总共11位中国大陆手机号
     */
    private static final String PHONE_REG = "^1[3-9]\\d{9}$";

    /**
     * 18位身份证正则常量
     * 规则：
     * 1. 前6位：地址码，第一位不能为0
     * 2. 第7-8位：年份19/20开头
     * 3. 第9-10位：年份后两位
     * 4. 第11-12位：月份 01~12
     * 5. 第13-14位：日期 01~31
     * 6. 第15-17位：顺序码
     * 7. 第18位：数字或大写/小写X
     */
    private static final String ID_CARD_REG = "^[1-9]\\d{5}(19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[0-9Xx]$";

    /**
     * 校验手机号是否合法
     * @param phone 界面传入的手机号字符串
     * @return true=格式合法；false=空值/空格/位数错误/非法开头
     */
    public static boolean checkPhone(String phone) {
        // 判空：null或全空白字符串直接返回不合法
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        // 去除首尾空格后匹配正则表达式
        return Pattern.matches(PHONE_REG, phone.trim());
    }

    /**
     * 校验18位身份证证件号格式
     * @param cardNo 界面传入的证件号码
     * @return true=符合身份证规则；false=空/位数不对/日期非法/末位字符错误
     */
    public static boolean checkCardNo(String cardNo) {
        // 判空拦截，空字符串不参与正则匹配
        if (cardNo == null || cardNo.trim().isEmpty()) {
            return false;
        }
        // 去除空格后与身份证正则匹配
        return Pattern.matches(ID_CARD_REG, cardNo.trim());
    }
}