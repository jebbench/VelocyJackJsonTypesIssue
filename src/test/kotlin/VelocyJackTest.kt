import com.arangodb.VelocyJack
import com.arangodb.velocypack.VPackParser
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.jupiter.api.Test

class VelocyJackTest {


    data class TestObject (
        val attributes: Map<String, Type>,
        val aql: List<String>
    )

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
    @JsonSubTypes(
        JsonSubTypes.Type(
            name = "s",
            value = Type.FirstType::class
        ),
        JsonSubTypes.Type(
            name = "NUMBER",
            value = Type.OtherType::class
        )
    )
    sealed class Type() {
        data class FirstType (
            val key: String,
            val value: String
        ) : Type()

        data class OtherType (
            val key: String,
            val value: String
        ) : Type()
    }

    @Test
    fun deserialize(){
        val json = """
            {
              "attributes": {
                "string_attr_0": {
                  "key": "string_attr_0",
                  "value": "ytniidkxxykxezq",
                  "type": "s"
                },
                "string_attr_1": {
                  "key": "string_attr_1",
                  "value": "valuedafasf",
                  "type": "s"
                }
              },
              "aql": []
            }
        """.trimIndent()

        val mapper = VelocyJack().apply {
            configure {
                it.registerModule(KotlinModule())
                    .registerModule(JavaTimeModule())
            }
        }

        val slice = VPackParser.Builder().build().fromJson(json)
        val result = mapper.deserialize<TestObject>(slice, TestObject::class.java)
        println(result)

    }

}
