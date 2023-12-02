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

var id: Int = 1

fun main(args: Array<String>) {
	val context = runApplication<NodeApplication>(*args)

	if (args.size != 1) {
		System.err.println("Please provide an ID for this node.")
		context.close()
		return
	}

	id = args.first().toInt()

	if (!setUp(id)) {
		System.err.println("Found error while setting up node, terminating...")
		context.close()
		return
	}
}

private fun setUp(id: Int): Boolean {

	// Inform proxy of node shutdown
	Runtime.getRuntime().addShutdownHook(Thread {
		ProxyRequestHandler.leaveCircle(
				ADDRESS,
				PORT,
				id,
				CONNECTION_TRIES,
				CONNECTION_TIMEOUT_MS
		)
	})

	// Inform proxy of node startup
	return if (ProxyRequestHandler.joinCircle(
					ADDRESS,
					PORT,
					id,
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