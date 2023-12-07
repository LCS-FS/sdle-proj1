package node.requests

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import node.shoppinglist.ShoppingList
import java.net.ConnectException
import java.net.HttpURLConnection

class NodeRequestHandler(address: String, port: Int) : RequestHandler("http://$address:$port") {

    fun getListById(
            id: String,
            tries: Int = 1,
            timeout: Int = 0
    ): NodeRequestResponse {
        var triesCount = 0
        while (triesCount < tries) {
            try {
                val response = sendGET("/node-list/$id")
                return if (response.code == HttpURLConnection.HTTP_OK) NodeRequestResponse.Found(Json.decodeFromString<ShoppingList>(response.message))
                else NodeRequestResponse.NotFound
            } catch (e: ConnectException) {
                triesCount++
            }
            Thread.sleep(timeout.toLong())
        }
        return NodeRequestResponse.FailedToConnect
    }

    fun putList(
            shoppingList: ShoppingList,
            tries: Int = 1,
            timeout: Int = 0
    ): Boolean {
        var triesCount = 0
        while (triesCount < tries) {
            try {
                val response = sendPUT("/node-list", Json.encodeToString(shoppingList))
                return response.code == HttpURLConnection.HTTP_OK
            } catch (e: ConnectException) {
                triesCount++
            }
            Thread.sleep(timeout.toLong())
        }
        return false
    }
}