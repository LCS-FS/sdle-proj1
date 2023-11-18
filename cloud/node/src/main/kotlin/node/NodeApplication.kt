package node

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.server.ConfigurableWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.context.annotation.Bean
import java.io.BufferedOutputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

private var PORT: Int = (1024..65535).random()

@SpringBootApplication
class NodeApplication {
	@Bean
	fun portSelector(): WebServerFactoryCustomizer<ConfigurableWebServerFactory>? {
		return WebServerFactoryCustomizer { factory ->
			factory.setPort(PORT)
		}
	}
}

private const val PROXY_ADDRESS: String = "localhost"
private const val PROXY_PORT: Int       = 12345

fun main(args: Array<String>) {
	runApplication<NodeApplication>(*args)
	connectToProxy()
}

fun connectToProxy() {
	val url = URL("http://$PROXY_ADDRESS:$PROXY_PORT/join-circle")
	with(url.openConnection() as HttpURLConnection) {
		requestMethod = "POST"
		doOutput = true
		setRequestProperty("Content-Type", "application/json")

		val jsonInputString = "{\"address\": \"localhost\", \"port\": \"${PORT}\"}"
		val outputStream: OutputStream = BufferedOutputStream(outputStream)
		outputStream.write(jsonInputString.toByteArray())
		outputStream.flush()

		// Close the output stream
		outputStream.close()

		inputStream.bufferedReader().use {
			it.lines().forEach { line ->
				println(line)
			}
		}
	}
}