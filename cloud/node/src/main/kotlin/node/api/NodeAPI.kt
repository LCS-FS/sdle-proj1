package node.api

import node.shoppinglist.ShoppingList
import node.shoppinglist.ShoppingListService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class NodeAPI(@Qualifier("shoppingListService") val service: ShoppingListService) {

    @GetMapping("/node-list/{id}")
    fun getListData(@PathVariable("id") id: String): ResponseEntity<Any> {
        println("Received GET request from Coordinator for node list with id: $id")

        val result = service.getListById(id)
        return if (result != null) {
            println("Returning node list data for id: $id")
            ResponseEntity.ok(result)
        } else {
            println("Node list not found for id: $id. Returning 404 status.")
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found for node list with id: $id")
        }
    }

    @PutMapping("/node-list")
    fun putListData(@RequestBody shoppingList: ShoppingList): ResponseEntity<Any> {
        println("Received PUT request from Coordinator for node list with id: ${shoppingList.id}")

        val result = service.putList(shoppingList)
        println("Node list data updated successfully for id: ${shoppingList.id}")
        return ResponseEntity.ok(result)
    }
}
