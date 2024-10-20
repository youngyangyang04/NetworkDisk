package com.disk.user.infrastructure.mapper;

import com.disk.base.utils.AESUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 类描述: AES加密类型处理器
 *
 * @author weikunkun
 */
public class AesEncryptTypeHandler extends BaseTypeHandler<String> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, encrypt(parameter));
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String encrypted = rs.getString(columnName);
        return encrypted == null ? null : decrypt(encrypted);
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String encrypted = rs.getString(columnIndex);
        return encrypted == null ? null : decrypt(encrypted);
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String encrypted = cs.getString(columnIndex);
        return encrypted == null ? null : decrypt(encrypted);
    }

    /**
     * 加密方法
     * @param data
     * @return
     */
    private String encrypt(String data) {
        // 实现数据加密逻辑
        return AESUtil.aesEncryptString(data);
    }

    /**
     * 解密方法
     * @param data
     * @return
     */
    private String decrypt(String data) {
        // 实现数据解密逻辑
        return AESUtil.aesDecodeString(data);
    }
}
