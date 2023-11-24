package node

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.server.ConfigurableWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.context.annotation.Bean
import java.io.BufferedOutputStream
import java.io.OutputStream
import java.net.ConnectException
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
private const val CONNECTION_TRIES: Int = 3

fun main(args: Array<String>) {
	runApplication<NodeApplication>(*args)

	// Adding a shutdown hook to handle the termination signal
	Runtime.getRuntime().addShutdownHook(Thread {
		println("Shutting down... Disconnecting from proxy.")
		disconnectToProxy()
	})

	connectToProxy()
}

fun connectToProxy(tries: Int = 1) {
	if (tries > CONNECTION_TRIES) {
		System.err.println("TIMEOUT: Proxy unavailable at the moment. Try later!")
		return
	}
	val url = URL("http://$PROXY_ADDRESS:$PROXY_PORT/join-circle")
	try {
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

			val responseCode = responseCode // Get the HTTP response code
			if (responseCode == HttpURLConnection.HTTP_OK) {
				println("SUCCESS: Connection to proxy done")
				inputStream.bufferedReader().use {
					it.lines().forEach { line ->
						println(line)
						// Handle the response as needed
					}
				}
			} else {
				// Handle unsuccessful response (e.g., error handling)
				System.err.println("Error: Unexpected response from proxy")
			}
		}
	}
	catch ( e : ConnectException ) {
		println("Proxy unavailable trying again in 3 seconds. ($tries / $CONNECTION_TRIES)")
		Thread.sleep(3000)
		connectToProxy(tries + 1)
	}
	return
}

fun disconnectToProxy(tries : Int = 1) {
	if (tries > CONNECTION_TRIES) {
		System.err.println("TIMEOUT: Proxy unavailable at the moment. Try later!")
		return
	}
	val url = URL("http://$PROXY_ADDRESS:$PROXY_PORT/leave-circle")
	try {
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

			val responseCode = responseCode // Get the HTTP response code
			if (responseCode == HttpURLConnection.HTTP_OK) {
				println("SUCCESS: Disconnected from proxy")
				inputStream.bufferedReader().use {
					it.lines().forEach { line ->
						println(line)
						// Handle the response as needed
					}
				}
			} else {
				// Handle unsuccessful response (e.g., error handling)
				System.err.println("Error: Unexpected response from proxy")
			}
		}
	}
	catch ( e : ConnectException) {
		println("Proxy unavailable trying again in 3 seconds. ($tries / $CONNECTION_TRIES)")
		Thread.sleep(3000)
		disconnectToProxy(tries + 1)
	}
}