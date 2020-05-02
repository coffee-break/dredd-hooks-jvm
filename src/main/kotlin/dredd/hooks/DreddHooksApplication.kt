package dredd.hooks

import com.firefly.net.tcp.SimpleTcpServer
import com.firefly.net.tcp.TcpServerConfiguration
import dredd.hooks.handler.DreddHookEventListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DreddHooksApplication(val hooksPath: String? = null) {

    var tcpServer: SimpleTcpServer = createServer()

    private fun createServer(): SimpleTcpServer {
        val config = TcpServerConfiguration()
        config.host = "127.0.0.1"
        config.port = 61321

        return SimpleTcpServer(config)
    }

    fun start(): DreddHooksApplication {
        tcpServer.accept(DreddHookEventListener.receiveData())
        tcpServer.start()
        println("Starting Dredd Hook Server with hooks: $hooksPath")
        return this
    }

    fun shutdown() {
        tcpServer.stop()
    }
}

fun main(args: Array<String>) {

    val application = DreddHooksApplication(if(args.isNotEmpty()) args[0]  else null).start()

    GlobalScope.launch {
        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                println("Stopping Dredd Hook Server")
                application.shutdown()
            }
        })
    }
}