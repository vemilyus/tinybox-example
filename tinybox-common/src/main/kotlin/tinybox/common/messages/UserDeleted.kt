package tinybox.common.messages

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer

data class UserDeleted(val username: String) {
    class Deserializer : ObjectMapperDeserializer<UserDeleted>(UserDeleted::class.java)
}
