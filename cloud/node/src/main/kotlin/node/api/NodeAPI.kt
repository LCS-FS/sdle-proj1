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
    fun getListData(@PathVariable("id") id: Int) =
            service.getListById(id) ?:
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found for list with id: $id")

    @PutMapping("/node-list")
    fun putListData(@RequestBody shoppingList: ShoppingList) = service.putList(shoppingList)
}