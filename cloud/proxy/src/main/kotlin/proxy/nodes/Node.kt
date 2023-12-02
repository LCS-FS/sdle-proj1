package proxy.nodes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Node(
        @SerialName("address") val address: String,
        @SerialName("port") val port: Int,
        @SerialName("id") val id: Int
)
