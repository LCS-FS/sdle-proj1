package node.shoppinglist

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
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
        subscriber.subscribe("$id")
    }

    private fun updatePreferenceList() {
        var message = subscriber.recvStr(ZMQ.NOBLOCK)
        var lastMessage = message
        while (message != null) {
            lastMessage = message
            message = subscriber.recvStr(ZMQ.NOBLOCK)
        }
        if (lastMessage == null) println("No updates to the preference list.")
        else {
            val separatorIndex = lastMessage.indexOf(';')
            val preferenceListString = lastMessage.slice(separatorIndex + 1 until lastMessage.length)
            val newPreferenceList = Json.decodeFromString<List<Node>>(preferenceListString)
            preferenceList.clear()
            preferenceList.addAll(newPreferenceList)
            println("New preference list: $preferenceList")
        }
    }

    fun getListById(id: Int): ShoppingList? {
        updatePreferenceList()

        val name = db.query(
                "SELECT name FROM lists where id=?",
                id
        ) { response, _ ->
            response.getString("name")
        }.firstOrNull() ?: return null

        val commits = db.query(
                "SELECT * FROM commits WHERE listId=?",
                arrayOf(id)
        ) { rs, _ ->
            ShoppingListCommit(
                    rs.getInt("hash"),
                    rs.getString("name"),
                    rs.getInt("quantity"),
                    if (rs.getBoolean("sum")) ShoppingListCommitType.ADD else ShoppingListCommitType.REMOVE,
            )
        }
        return ShoppingList(name, commits)
    }

    fun putList(shoppingList: ShoppingList) {
        updatePreferenceList()

        // TODO
    }
}

@Serializable
private data class Node(
        val address: String,
        val port: Int,
        val id: Int
)