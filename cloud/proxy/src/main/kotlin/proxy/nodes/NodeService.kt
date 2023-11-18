package proxy.nodes

import java.security.MessageDigest


object NodeService {
    private const val NUM_VIRTUAL_NODES = 10
    private val circle: MutableMap<Int, Node> = mutableMapOf()

    fun addNode(node: Node) {
        println("Node ${node.address}:${node.port} is joining the circle.")
        for (i in 0 until NUM_VIRTUAL_NODES) {
            val virtualNodeName = "${node.address}:${node.port}#$i"
            val hash = getHash(virtualNodeName)
            circle[hash] = node
        }
    }

    fun removeNode(node: Node) {
        println("Node ${node.address}:${node.port} is leaving the circle.")
        for (i in 0 until NUM_VIRTUAL_NODES) {
            val virtualNodeName = "${node.address}:${node.port}#$i"
            val hash = getHash(virtualNodeName)
            circle.remove(hash)
        }
    }

    fun getNode(listId: Int): Node? {
        println("Circle: ${circle.toList()}")
        val hash = getHash(listId.toString())
        println(hash)
        circle[hash]?.let { return it }
        val greaterKeys = circle.filterKeys { it > hash }
        return if (greaterKeys.isEmpty()) circle.entries.firstOrNull()?.value else greaterKeys.entries.first().value
    }

    private fun getHash(input: String): Int {
        val md = MessageDigest.getInstance("MD5")
        val bytes = md.digest(input.toByteArray())
        var hash = 0
        for (i in 0..3) {
            hash += bytes[i].toInt() and 0xFF shl 8 * i
        }
        return hash
    }
}