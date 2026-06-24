/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.library.service;

/**
 * 【业务层】BookService（图书业务服务类）
 *
 * 设计意图：作为 UI 层和 DAO 层之间的"中间人"，负责编排复杂的业务逻辑。
 * 简单来说，如果 DAO 只做"单表增删改查"，那 Service 就负责"跨表协作 + 业务规则校验"。
 *
 * 当前状态：本项目的图书相关业务逻辑较简单，已直接在 UI 层调用 BookDao 完成，
 * 因此该类暂留作扩展预留（如后续需要借书时同时扣减库存、写日志等复合操作，可在此实现）。
 *
 * 典型职责（扩展时）：
 * - 新增图书时校验 ISBN 唯一性
 * - 删除图书时检查是否有未归还的借阅记录
 * - 批量导入图书时做事务控制
 *
 * @author LA
 */
public class BookService {

}
