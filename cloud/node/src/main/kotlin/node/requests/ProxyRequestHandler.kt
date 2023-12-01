package node.requests

import java.net.ConnectException
import java.net.HttpURLConnection

private const val PROXY_ADDRESS: String = "localhost"
private const val PROXY_PORT: Int = 12345

object ProxyRequestHandler : RequestHandler("http://$PROXY_ADDRESS:$PROXY_PORT") {

    private fun sendCircleRequest(
            endpoint: String,
            nodeAddress: String,
            nodePort: Int,
            tries: Int,
            timeout: Int
    ): Boolean {
        var triesCount = 0
        while (triesCount < tries) {
            try {
                val response = sendPOST(endpoint, "{\"address\": \"$nodeAddress\", \"port\": \"$nodePort\"}")
                if (response.code == HttpURLConnection.HTTP_OK) return true
                else triesCount++
            } catch (e: ConnectException) {
                triesCount++
            }
            Thread.sleep(timeout.toLong())
        }
        return false
    }

    fun joinCircle(
            nodeAddress: String,
            nodePort: Int,
            tries: Int,
            timeout: Int
    ): Boolean {
        return sendCircleRequest("/join-circle", nodeAddress, nodePort, tries, timeout)
    }

    fun leaveCircle(
            nodeAddress: String,
            nodePort: Int,
            tries: Int,
            timeout: Int
    ): Boolean {
        return sendCircleRequest("/leave-circle", nodeAddress, nodePort, tries, timeout)
    }
}
