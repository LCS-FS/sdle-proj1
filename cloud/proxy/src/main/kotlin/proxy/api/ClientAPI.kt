package proxy.api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import proxy.nodes.NodeService

@RestController
class ClientAPI {
    @GetMapping("/list/{id}")
    fun getListCoordinator(@PathVariable("id") id: Int) =
            NodeService.getNode(id) ?:
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("No nodes available.")
}
