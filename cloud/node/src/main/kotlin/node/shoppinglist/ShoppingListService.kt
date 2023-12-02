package node.shoppinglist

import node.id
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.query
import org.springframework.stereotype.Service
import org.zeromq.ZMQ

private const val PUBLISHER_ADDRESS = "localhost"
private const val PUBLISHER_PORT = 5556

@Service
class ShoppingListService(val db: JdbcTemplate) {

    private final val context = ZMQ.context(1)
    private final val subscriber = context.socket(ZMQ.SUB)
    private val preferenceList = mutableListOf<Node>()

    init {
        subscriber.connect("tcp://$PUBLISHER_ADDRESS:$PUBLISHER_PORT")
        subscriber.subscribe("$id".toByteArray())
    }

    private fun updatePreferenceList() {
        val message = subscriber.recvStr()
        println("Received: $message")
    }

    fun getListById(id: Int): ShoppingList? {
        updatePreferenceList()

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
        updatePreferenceList()

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
}

private data class Node(
        val address: String,
        val port: Int
)