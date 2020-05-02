package dredd.hooks

import com.firefly.net.tcp.SimpleTcpClient
import com.firefly.net.tcp.codec.flex.decode.StringParser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DreddHooksApplicationTest {

    private val tcpClient = SimpleTcpClient()

    @BeforeEach
    fun setup() {
        GlobalScope.launch {
            DreddHooksApplication().start()
        }
        Thread.sleep(2000)
    }

    @Test
    fun canConnectToTcpServer() {
        val message = """{"event": "beforeEach", "uuid": "1234-abcd", "data": {"key":"value"}}"""

        tcpClient.connect("localhost", 61321)
                .thenAccept { c ->
                    val parser = StringParser()
                    parser.complete { msg ->
                        val message = msg.trim()
                        println("client receives message -> $message")
                        assertThat(message).isEqualTo(message)
                        tcpClient.stop()
                    }
                    c.receive(parser::receive)
                    c.write(message)
                    c.write("\r\n")
                }
    }

}
