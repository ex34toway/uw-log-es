package uw.log.es.ser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;

/**
 * Json对象反序列化成字符串而非Json对象
 *
 * @author liliang
 * @since 2018-05-18
 */
public class ObjectAsStringDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode node = mapper.readTree(jp);
        return mapper.writeValueAsString(node);
    }
}
