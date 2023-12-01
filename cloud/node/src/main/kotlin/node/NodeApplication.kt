package node

import node.requests.ProxyRequestHandler
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.server.ConfigurableWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.context.annotation.Bean

private const val ADDRESS: String = "localhost"
private var       PORT:    Int    = (1024..65535).random()

private const val CONNECTION_TRIES:      Int = 3
private const val CONNECTION_TIMEOUT_MS: Int = 3000

@SpringBootApplication
class NodeApplication {
	@Bean
	fun portSelector(): WebServerFactoryCustomizer<ConfigurableWebServerFactory>? {
		return WebServerFactoryCustomizer { factory ->
			factory.setPort(PORT)
		}
	}
}

fun main(args: Array<String>) {
	runApplication<NodeApplication>(*args)

	println("args: $args")

	if (!setUp()) {
		System.err.println("Found error while setting up node, terminating...")
		return
	}

}

private fun setUp(): Boolean {

	// Inform proxy of node shutdown
	Runtime.getRuntime().addShutdownHook(Thread {
		ProxyRequestHandler.leaveCircle(
				ADDRESS,
				PORT,
				CONNECTION_TRIES,
				CONNECTION_TIMEOUT_MS
		)
	})

	// Inform proxy of node startup
	return if (ProxyRequestHandler.joinCircle(
					ADDRESS,
					PORT,
					CONNECTION_TRIES,
					CONNECTION_TIMEOUT_MS
			)) {
		println("Successfully joined the circle.")
		true
	} else {
		println("Failed to join the circle after $CONNECTION_TRIES tries.")
		false
	}
}