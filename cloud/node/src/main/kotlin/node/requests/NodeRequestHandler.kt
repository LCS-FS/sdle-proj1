package node.requests

import kotlinx.serialization.json.Json
import node.shoppinglist.ShoppingList
import java.net.ConnectException
import java.net.HttpURLConnection

class NodeRequestHandler(address: String, port: Int) : RequestHandler("http://$address:$port") {

    fun getListById(
            id: Int,
            tries: Int,
            timeout: Int
    ): NodeRequestResponse {
        var triesCount = 0
        while (triesCount < tries) {
            try {
                val response = sendGET("/list/$id")
                return if (response.code == HttpURLConnection.HTTP_OK) NodeRequestResponse.Found(Json.decodeFromString<ShoppingList>(response.message))
                else NodeRequestResponse.NotFound
            } catch (e: ConnectException) {
                triesCount++
            }
            Thread.sleep(timeout.toLong())
        }
        return NodeRequestResponse.FailedToConnect
    }
}