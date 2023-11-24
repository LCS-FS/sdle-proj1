package proxy.api

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import proxy.nodes.Node
import proxy.nodes.NodeService

@RestController
class NodeAPI {
    @PostMapping("/join-circle")
    fun joinCircle(@RequestBody node: Node){
        NodeService.addNode(node)
        NodeService.updatePreferenceLists()
        NodeService.printPreferenceLists()
    }

    @PostMapping("/leave-circle")
    fun leaveCircle(@RequestBody node: Node) {
        NodeService.removeNode(node)
        NodeService.updatePreferenceLists()
        NodeService.printPreferenceLists()
    }
}