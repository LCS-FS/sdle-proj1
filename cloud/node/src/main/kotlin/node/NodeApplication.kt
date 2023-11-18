package node

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.BufferedOutputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

@SpringBootApplication
class NodeApplication

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

		// Create the JSON payload as a string
		val jsonInputString = "{\"address\": \"localhost\", \"port\": \"8080\"}"

		// Get the output stream of the connection
		val outputStream: OutputStream = BufferedOutputStream(outputStream)

		// Write the JSON payload to the output stream
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