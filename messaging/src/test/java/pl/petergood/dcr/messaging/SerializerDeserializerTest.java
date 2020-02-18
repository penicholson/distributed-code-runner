package pl.petergood.dcr.messaging;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import pl.petergood.dcr.messaging.deserializer.ObjectDeserializer;
import pl.petergood.dcr.messaging.serializer.ObjectSerializer;

public class SerializerDeserializerTest {

    @Test
    public void verifySerializationAndDeserialization() {
        // given
        TestPojo testPojo = new TestPojo(137);
        ObjectSerializer<TestPojo> objectSerializer = new ObjectSerializer<>();
        ObjectDeserializer<TestPojo> objectDeserializer = new ObjectDeserializer<>(TestPojo.class);

        // when
        byte[] bytes = objectSerializer.serialize("test-topic", testPojo);
        TestPojo deserializedPojo = objectDeserializer.deserialize("test-topic", bytes);

        // then
        Assertions.assertThat(deserializedPojo.getA()).isEqualTo(137);
    }

    public static class TestPojo {
        int a;

        public TestPojo() {
        }

        public TestPojo(int a) {
            this.a = a;
        }

        public void setA(int a) {
            this.a = a;
        }

        public int getA() {
            return a;
        }
    }

}
