package pl.petergood.dcr.messaging.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

public class ObjectDeserializer<T> implements Deserializer<T> {

    private Class<T> targetClass;

    public ObjectDeserializer(Class<T> targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(data, targetClass);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
