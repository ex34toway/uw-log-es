package uw.log.es.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uw.httpclient.http.ObjectMapper;

import java.io.IOException;

/**
 * Json对象序列化成字符串而非Json对象
 * 主要用于序列化日志对象内嵌套对象的序列化
 *
 * @author liliang
 * @since 2018-05-18
 */
public class ObjectAsStringSerializer<T> extends JsonSerializer<T> {

    private static final Logger logger = LoggerFactory.getLogger(ObjectAsStringSerializer.class);

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        try {
            gen.writeString(ObjectMapper.DEFAULT_JSON_MAPPER.toString(value));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
