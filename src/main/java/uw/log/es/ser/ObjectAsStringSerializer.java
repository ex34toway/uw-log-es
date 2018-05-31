package uw.log.es.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uw.httpclient.http.ObjectMapper;

import java.io.IOException;

/**
 * Json对象序列化成字符串而非Json对象,默认截取前 3000 byte
 * 主要用于序列化日志对象内嵌套对象的序列化
 *
 * @author liliang
 * @since 2018-05-18
 */
public class ObjectAsStringSerializer<T> extends JsonSerializer<T> {

    private static final Logger logger = LoggerFactory.getLogger(ObjectAsStringSerializer.class);

    /**
     * UTF-8编码保存最大的字节数
     */
    private static final long MAX_BYTE_COUNT = 3000;

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        okio.Buffer buffer = new okio.Buffer();
        try {
            ObjectMapper.DEFAULT_JSON_MAPPER.write(buffer.outputStream(), value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        long length = buffer.size();
        String result;
        if (length > MAX_BYTE_COUNT) {
            result = buffer.readByteString(MAX_BYTE_COUNT).utf8();
            buffer.clear();
        } else {
            result = buffer.readByteString().utf8();
        }
        gen.writeString(result);
    }
}
