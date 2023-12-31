package node.shoppinglist

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import node.id
import node.requests.NodeRequestHandler
import node.requests.NodeRequestResponse
import node.requests.ProxyRequestHandler
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.zeromq.ZMQ

private const val PUBLISHER_ADDRESS = "localhost"
private const val PUBLISHER_PORT = 5556

private const val REQUIRED_READS  = 1

@Service
class ShoppingListCoordinatorService(override val db: JdbcTemplate) : ShoppingListService(db) {

    private val context = ZMQ.context(1)
    private val subscriber = context.socket(ZMQ.SUB)
    private val preferenceList = mutableListOf<Node>()

    init {
        subscriber.connect("tcp://$PUBLISHER_ADDRESS:$PUBLISHER_PORT")
        subscriber.subscribe("$id;")
        println("Subscribed for id $id!")
    }

    fun getListByIdCoordinator(id: String): ShoppingList? {
        updatePreferenceList()

        if (preferenceList.size < REQUIRED_READS) return null

        val coordinatorList = getListById(id)

        val readLists = if (coordinatorList == null) mutableListOf() else mutableListOf(coordinatorList)

        var currentReads = 0
        for (node in preferenceList) {
            val nodeRequestHandler = NodeRequestHandler(node.address, node.port)
            when (val nodeResponse = nodeRequestHandler.getListById(id)) {
                is NodeRequestResponse.Found -> {
                    readLists.add(nodeResponse.shoppingList)
                    currentReads++
                }
                is NodeRequestResponse.NotFound -> currentReads++
                is NodeRequestResponse.FailedToConnect -> ProxyRequestHandler.leaveCircle(node.address, node.port, node.id, 1, 0)
            }
        }

        val mergedList = mergeShoppingLists(readLists)
        if (currentReads > 0 && mergedList != null) {
            putListCoordinator(mergedList)
        }

        if (currentReads < REQUIRED_READS) return null
        return mergedList
    }

    fun putListCoordinator(shoppingList: ShoppingList) {
        updatePreferenceList()

        putList(shoppingList)

        for (node in preferenceList) {
            val nodeRequestHandler = NodeRequestHandler(node.address, node.port)
            val response = nodeRequestHandler.putList(shoppingList)
            if (!response) ProxyRequestHandler.leaveCircle(node.address, node.port, node.id, 1, 0)
        }
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
}

@Serializable
private data class Node(
        val address: String,
        val port: Int,
        val id: Int
)
