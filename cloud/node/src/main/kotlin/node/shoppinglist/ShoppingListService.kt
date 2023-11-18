package node.shoppinglist

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.query
import org.springframework.stereotype.Service

@Service
class ShoppingListService(val db: JdbcTemplate) {
    fun getListById(id: Int): ShoppingList? {
        val name = db.query(
                "SELECT name FROM lists where id=?",
                id
        ) { response, _ ->
            response.getString("name")
        }.firstOrNull() ?: return null

        val items = db.query(
                "SELECT * FROM items WHERE listId=?",
                arrayOf(id)
        ) { rs, _ ->
            ShoppingListItem(
                    rs.getString("name"),
                    rs.getInt("quantity")
            )
        }
        return ShoppingList(id, name, items)
    }

    fun putList(shoppingList: ShoppingList) {
        db.update(
                "INSERT INTO lists VALUES (?, ?)",
                shoppingList.id, shoppingList.name
        )
        for (item in shoppingList.items) {
            db.update(
                    "INSERT INTO items(name, quantity, listId) VALUES (?, ?, ?)",
                    item.name, item.quantity, shoppingList.id
            )
        }
    }
}