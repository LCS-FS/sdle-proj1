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
        // delete old list representation
        db.update(
                "DELETE FROM items WHERE listId = ?",
                shoppingList.id
        )

        // insert new list representation
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

    fun mergeLists(lists: ArrayList<ArrayList<CRDTShoppingListItem>>): ArrayList<CRDTShoppingListItem> {
        val set = mutableSetOf<CRDTShoppingListItem>()
        for (list in lists){
            for (item in list){
                set.add(item)
            }
        }

        return ArrayList(set)
    }
}