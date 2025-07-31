package com.disk.web.serializer;

import com.disk.base.utils.EmptyUtil;
import com.disk.base.utils.IdUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Objects;

/**
 * 类描述: Id 自动加密的 JSON 序列化器
 *  用于返回实体 Long 类型 ID 字段的自动序列化
 *  控制 Long 类型的 ID 字段在序列化为 JSON 时的加密处理，保护 ID 敏感信息的安全
 * @author weikunkun
 */
public class IdEncryptSerializer extends JsonSerializer<Long> {

    @Override
    public void serialize(Long aLong, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (EmptyUtil.isEmpty(aLong)) {
            // 如果 value 为 null，则使用 StringUtils.EMPTY 写一个空字符串。
            jsonGenerator.writeString(StringUtils.EMPTY);
        } else {
            // 如果 value 不为 null，则使用 IdUtil.encrypt(value) 将 Long 值加密，并写入 JSON。
            jsonGenerator.writeString(IdUtil.encrypt(aLong));
        }
    }
}
