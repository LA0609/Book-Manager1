package com.library.dao;

import com.library.model.Reader;
import com.library.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 【数据层】ReaderDao
 * 负责 readers 表的增删改查操作。
 */
public class ReaderDao {

    // 1. 查询所有读者
    public List<Reader> findAll() {
        List<Reader> list = new ArrayList<>();
        String sql = "SELECT * FROM readers";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Reader r = new Reader();
                r.setId(rs.getInt("id"));
                r.setCardNo(rs.getString("card_no"));
                r.setName(rs.getString("name"));
                r.setGender(rs.getString("gender")); // 新增读取性别
                r.setPhone(rs.getString("phone"));
                r.setStatus(rs.getString("status"));
                list.add(r);
            }
        } catch (SQLException e) {
            System.err.println("查询读者失败：" + e.getMessage());
        }
        return list;
    }

    // 2. 添加读者 (办证)
    public void add(Reader r) {
        // 新增 gender 字段
        String sql = "INSERT INTO readers (card_no, name, gender, phone, status) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, r.getCardNo());
            pstmt.setString(2, r.getName());
            pstmt.setString(3, r.getGender()); // 存入性别
            pstmt.setString(4, r.getPhone());
            pstmt.setString(5, r.getStatus());
            
            pstmt.executeUpdate();
            System.out.println("读者办证成功！");
        } catch (SQLException e) {
            System.err.println("办证失败：" + e.getMessage());
        }
    }
    
    // 3. 根据ID查读者 (修改窗口回显用)
    public Reader findById(int id) {
        String sql = "SELECT * FROM readers WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Reader r = new Reader();
                    r.setId(rs.getInt("id"));
                    r.setCardNo(rs.getString("card_no"));
                    r.setName(rs.getString("name"));
                    r.setGender(rs.getString("gender"));
                    r.setPhone(rs.getString("phone"));
                    r.setStatus(rs.getString("status"));
                    return r;
                }
            }
        } catch (SQLException e) {
            System.err.println("查询失败：" + e.getMessage());
        }
        return null;
    }

    // 4.注销：修改状态为已注销
    public void cancelReader(int id) throws SQLException{
        String sql = "UPDATE readers SET status = '已注销' WHERE id = ?";
        try(Connection conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
                              pstmt.setInt(1, id);
                              pstmt.executeUpdate();
            }
    }

    // 5. 更新读者信息（修改窗口保存）
    public int updateReader(Reader r) {
        String sql = "UPDATE readers SET card_no=?,name=?,gender=?,phone=?,status=? WHERE id=?";
        int rows = 0;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, r.getCardNo());
            pstmt.setString(2, r.getName());
            pstmt.setString(3, r.getGender());
            pstmt.setString(4, r.getPhone());
            pstmt.setString(5, r.getStatus());
            pstmt.setInt(6, r.getId());
            rows = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    // 6. 模糊查询（查询按钮）
    public List<Reader> searchReader(String keyword) {
        List<Reader> list = new ArrayList<>();
        String sql = "SELECT * FROM readers WHERE card_no LIKE ? OR name LIKE ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                Reader r = new Reader();
                r.setId(rs.getInt("id"));
                r.setCardNo(rs.getString("card_no"));
                r.setName(rs.getString("name"));
                r.setGender(rs.getString("gender"));
                r.setPhone(rs.getString("phone"));
                r.setStatus(rs.getString("status"));
                list.add(r);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return list;
    }
        // 7. 根据手机号查重
    public Reader findByPhone(String phone) {
        String sql = "SELECT * FROM readers WHERE phone = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phone);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Reader r = new Reader();
                    r.setId(rs.getInt("id"));
                    r.setCardNo(rs.getString("card_no"));
                    r.setName(rs.getString("name"));
                    r.setGender(rs.getString("gender"));
                    r.setPhone(rs.getString("phone"));
                    r.setStatus(rs.getString("status"));
                    return r;
                }
            }
        } catch (SQLException e) {
            System.err.println("手机号查重异常：" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // 8. 根据证件号查重
    public Reader findByCardNo(String cardNo) {
        String sql = "SELECT * FROM readers WHERE card_no = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cardNo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Reader r = new Reader();
                    r.setId(rs.getInt("id"));
                    r.setCardNo(rs.getString("card_no"));
                    r.setName(rs.getString("name"));
                    r.setGender(rs.getString("gender"));
                    r.setPhone(rs.getString("phone"));
                    r.setStatus(rs.getString("status"));
                    return r;
                }
            }
        } catch (SQLException e) {
            System.err.println("证件号查重异常：" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
