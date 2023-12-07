package node.api

import node.shoppinglist.ShoppingList
import node.shoppinglist.ShoppingListCoordinatorService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class ClientAPI(val service: ShoppingListCoordinatorService) {
    @GetMapping("/list/{id}")
    fun getListData(@PathVariable("id") id: String) =
            service.getListByIdCoordinator(id) ?:
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found for list with id: $id")

    @PutMapping("/list")
    fun putListData(@RequestBody shoppingList: ShoppingList) = service.putListCoordinator(shoppingList)
}