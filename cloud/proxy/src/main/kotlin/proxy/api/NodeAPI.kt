package proxy.api

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import proxy.nodes.Node
import proxy.nodes.NodeService
import org.zeromq.ZMQ

private const val PUBLISHER_PORT = 5556

@RestController
class NodeAPI {

    private final val context: ZMQ.Context = ZMQ.context(1)
    private final val publisher = context.socket(ZMQ.PUB)

    init {
        publisher.bind("tcp://*:$PUBLISHER_PORT")
    }

    @PostMapping("/join-circle")
    fun joinCircle(@RequestBody node: Node) {
        NodeService.addNode(node)
        NodeService.updatePreferenceLists(publisher)
    }

    @PostMapping("/leave-circle")
    fun leaveCircle(@RequestBody node: Node) {
        NodeService.removeNode(node)
        NodeService.updatePreferenceLists(publisher)
    }
}