package node.requests

import java.io.BufferedOutputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

open class RequestHandler(private val address: String) {

    protected fun sendGET(resource: String): RequestResponse {
        val url = URL("$address$resource")
        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "GET"
            return RequestResponse(responseCode, inputStream.bufferedReader().readText())
        }
    }

    protected fun sendPOST(resource: String, body: String): RequestResponse {
        return sendWriteRequest(resource, body, "POST")
    }

    protected fun sendPUT(resource: String, body: String): RequestResponse {
        return sendWriteRequest(resource, body, "PUT")
    }

    private fun sendWriteRequest(resource: String, body: String, method: String): RequestResponse {
        val url = URL("$address$resource")
        with(url.openConnection() as HttpURLConnection) {
            requestMethod = method
            doOutput = true
            setRequestProperty("Content-Type", "application/json")

            val outputStream: OutputStream = BufferedOutputStream(outputStream)
            outputStream.write(body.toByteArray())
            outputStream.flush()
            outputStream.close()

            return RequestResponse(responseCode, inputStream.bufferedReader().readText())
        }
    }
}