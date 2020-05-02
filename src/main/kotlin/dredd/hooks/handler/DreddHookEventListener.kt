package dredd.hooks.handler

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.firefly.net.tcp.TcpConnection
import com.firefly.net.tcp.codec.flex.decode.StringParser
import com.firefly.utils.function.Action1
import com.firefly.utils.io.IO
import java.nio.ByteBuffer

data class DreddHookEvent(val event: String, val uuid: String, val data: Map<String, Any>)

object DreddHookEventListener {

    private val objectMapper = jacksonObjectMapper()

    fun receiveData(): Action1<TcpConnection>? = Action1 { connection: TcpConnection ->
        val parser = StringParser()
        parser.complete { msg: String ->
            val message = msg.trim { it <= ' ' }
            println("server receives message -> $message")
            when (message) {
                "quit" -> {
                    println("server handling goodbye")
                    connection.write("bye!\r\n")
                    IO.close(connection)
                }
                else -> {
                    try {
                        var event: DreddHookEvent = objectMapper.readValue(message)
                        println("server sending json: $event")
                        connection.write(message)
                    }catch (e: Exception) {
                        println("server was unable to parse: $message")
                    }
                }
            }
        }
        connection.receive { obj: ByteBuffer -> parser.receive(obj) }
    }
}

