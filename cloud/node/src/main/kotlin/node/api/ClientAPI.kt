package node.api

import node.shoppinglist.ShoppingList
import node.shoppinglist.ShoppingListCoordinatorService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class ClientAPI(val service: ShoppingListCoordinatorService) {

    @GetMapping("/list/{id}")
    fun getListData(@PathVariable("id") id: String): ResponseEntity<Any> {
        println("Received GET request from Client for list with id: $id")

        val result = service.getListByIdCoordinator(id)
        return if (result != null) {
            println("Returning list data for list with id: $id")
            ResponseEntity.ok(result)
        } else {
            println("List not found for id: $id. Returning 404 status.")
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found for list with id: $id")
        }
    }

    @PutMapping("/list")
    fun putListData(@RequestBody shoppingList: ShoppingList): ResponseEntity<Any> {
        println("Received PUT request from Client for list with id: ${shoppingList.id}")

        val result = service.putListCoordinator(shoppingList)
        println("List data updated successfully for list with id: ${shoppingList.id}")
        return ResponseEntity.ok(result)
    }
}
