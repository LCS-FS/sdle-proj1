package proxy.nodes

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ConcurrentSkipListMap
import org.zeromq.ZMQ

object NodeService {
    private const val NUM_VIRTUAL_NODES = 10
    private const val MAX_NUM_REPLICAS = 3
    private val circle: ConcurrentMap<Int, Node> = ConcurrentSkipListMap()
    private val preferenceLists: ConcurrentMap<Node, MutableList<Node>> = ConcurrentHashMap()
    private var connectedNodes : Int = 0


    private fun getUniqueNodes(): Set<Node> = circle.values.toSet()

    fun updatePreferenceLists(publisher: ZMQ.Socket) {
        Thread.sleep(1000)
        for (node in getUniqueNodes()) {
            val preferenceList = mutableListOf<Node>()

            val physicalNodeName = "${node.id}#0"
            var hash = getHash(physicalNodeName)
            val selfNode = circle[hash]

            if (selfNode === null) {
                System.err.println("Node not found in circle")
                return
            }

            // define number of replicas
            val numReplicas = if (connectedNodes <= MAX_NUM_REPLICAS) connectedNodes - 1 else MAX_NUM_REPLICAS
            var count = 0
            while (count < numReplicas) {
                val greaterKeys = circle.filterKeys { it > hash }
                val nextEntry = if (greaterKeys.isEmpty()) circle.entries.firstOrNull() else greaterKeys.entries.firstOrNull()
                val nextNode = nextEntry?.value
                if (nextNode === null) {
                    System.err.println("Not enough replicas in the circle.")
                    return
                }
                if (nextNode != selfNode && !preferenceList.contains(nextNode)) {
                    preferenceList.add(nextNode)
                    count++
                }
                hash = nextEntry.key
            }
            preferenceLists[selfNode] = preferenceList

            publisher.send("${selfNode.id};${Json.encodeToString(preferenceList)}")
        }
    }
    fun addNode(node: Node) {
        println("Node ${node.address}:${node.port} is joining the circle with id ${node.id}.")
        connectedNodes++
        for (i in 0 until NUM_VIRTUAL_NODES) {
            val virtualNodeName = "${node.id}#$i"
            val hash = getHash(virtualNodeName)
            circle[hash] = node
        }
    }

    fun removeNode(node: Node) {
        var nodeWasRemoved = false

        for (i in 0 until NUM_VIRTUAL_NODES) {
            val virtualNodeName = "${node.id}#$i"
            val hash = getHash(virtualNodeName)
            circle.remove(hash)?.let { nodeWasRemoved = true }
        }

        if (nodeWasRemoved) {
            println("Node ${node.address}:${node.port} with id ${node.id} is leaving the circle.")
            connectedNodes--
        }
    }

    fun getNode(listId: String): Node? {
        val hash = getHash(listId)
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