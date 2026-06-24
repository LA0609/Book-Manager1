package com.library.dao;

import com.library.model.Reader;
import com.library.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 【数据层】ReaderDao（读者数据访问对象）
 *
 * 作用：负责 readers 表的增删改查操作。
 * 简单来说，UI 层想操作读者数据（新增、修改、注销、查询），都通过这个类与数据库交互。
 *
 * 设计说明：
 * - 读者注销采用"软删除"（修改 status 为"已注销"），不物理删除数据
 * - 提供手机号和证件号的查重方法，用于新增/修改时的唯一性校验
 *
 * @author 81382
 */
public class ReaderDao {

    /**
     * 查询所有读者
     * 应用场景：读者管理界面打开时加载表格数据。
     *
     * @return 包含所有读者对象的列表，无数据时返回空列表
     */
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
                r.setGender(rs.getString("gender"));
                r.setPhone(rs.getString("phone"));
                r.setStatus(rs.getString("status"));
                list.add(r);
            }
        } catch (SQLException e) {
            System.err.println("查询读者失败：" + e.getMessage());
        }
        return list;
    }

    /**
     * 添加读者（办证）
     * 简单来说，将一位新读者的信息写入数据库，完成"办证"操作。
     *
     * @param r 读者对象，包含姓名、性别、手机号、证件号等信息
     */
    public void add(Reader r) {
        String sql = "INSERT INTO readers (card_no, name, gender, phone, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, r.getCardNo());
            pstmt.setString(2, r.getName());
            pstmt.setString(3, r.getGender());
            pstmt.setString(4, r.getPhone());
            pstmt.setString(5, r.getStatus());

            pstmt.executeUpdate();
            System.out.println("读者办证成功！");
        } catch (SQLException e) {
            System.err.println("办证失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID查询单个读者
     * 简单来说，通过读者ID精确查找一个人，常用于修改窗口的数据回显。
     *
     * @param id 读者ID（数据库主键）
     * @return 对应的 Reader 对象，找不到时返回 null
     */
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

    /**
     * 注销读者（软删除）
     * 简单来说，不是真从数据库删人，而是把状态改为"已注销"。
     * 注销后的读者不能继续借书，但历史借阅记录仍然保留。
     *
     * @param id 要注销的读者ID
     * @throws SQLException 数据库操作异常时抛出
     */
    public void cancelReader(int id) throws SQLException {
        String sql = "UPDATE readers SET status = '已注销' WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    /**
     * 更新读者信息
     * 简单来说，把用户编辑后的新数据覆盖写回数据库。
     *
     * @param r 封装了完整新数据的读者对象（id 不能为0，用于 WHERE 条件定位）
     * @return 受影响的行数（1=修改成功，0=未找到对应记录）
     */
    public int updateReader(Reader r) {
        String sql = "UPDATE readers SET card_no=?, name=?, gender=?, phone=?, status=? WHERE id=?";
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

    /**
     * 模糊查询读者
     * 简单来说，根据关键词在证件号和姓名中模糊匹配，用于搜索功能。
     *
     * @param keyword 搜索关键词（匹配证件号或姓名）
     * @return 符合条件的读者列表
     */
    public List<Reader> searchReader(String keyword) {
        List<Reader> list = new ArrayList<>();
        String sql = "SELECT * FROM readers WHERE card_no LIKE ? OR name LIKE ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Reader r = new Reader();
                r.setId(rs.getInt("id"));
                r.setCardNo(rs.getString("card_no"));
                r.setName(rs.getString("name"));
                r.setGender(rs.getString("gender"));
                r.setPhone(rs.getString("phone"));
                r.setStatus(rs.getString("status"));
                list.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 根据手机号查重
     * 简单来说，检查数据库中是否已有这个手机号的读者，用于新增/修改时的唯一性校验。
     *
     * @param phone 手机号
     * @return 对应的 Reader 对象（手机号已存在时），null（手机号未被注册时）
     */
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

    /**
     * 根据证件号查重
     * 简单来说，检查数据库中是否已有这个身份证号的读者，用于新增/修改时的唯一性校验。
     *
     * @param cardNo 证件号码（18位身份证）
     * @return 对应的 Reader 对象（证件号已存在时），null（证件号未被注册时）
     */
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
