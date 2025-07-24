package store.myproject.onlineshop.global.utils;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.nio.ByteBuffer;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@MappedTypes(UUID.class)
@MappedJdbcTypes(JdbcType.BINARY)
public class UUIDTypeHandler extends BaseTypeHandler<UUID> {
    
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, UUID parameter, JdbcType jdbcType) throws SQLException {
        // UUID를 BINARY로 변환 (UUIDBinaryConverter와 동일한 로직)
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(parameter.getMostSignificantBits());
        buffer.putLong(parameter.getLeastSignificantBits());
        ps.setBytes(i, buffer.array());
    }
    
    @Override
    public UUID getNullableResult(ResultSet rs, String columnName) throws SQLException {
        byte[] bytes = rs.getBytes(columnName);
        return convertBytesToUUID(bytes);
    }
    
    @Override
    public UUID getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        byte[] bytes = rs.getBytes(columnIndex);
        return convertBytesToUUID(bytes);
    }
    
    @Override
    public UUID getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        byte[] bytes = cs.getBytes(columnIndex);
        return convertBytesToUUID(bytes);
    }
    
    private UUID convertBytesToUUID(byte[] bytes) {
        if (bytes == null || bytes.length != 16) return null;
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        long high = buffer.getLong();
        long low = buffer.getLong();
        return new UUID(high, low);
    }
}