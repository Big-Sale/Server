package marshall;

import beans.Type;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MarshallHandler {

    /*public static <T> Class<T> unmarshall(String s, Class<T> valueType ) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            valueType = objectMapper.readValue(s, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return valueType;
    }*/
    public static <T> T unmarshall(String s, Class<T> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(s, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
