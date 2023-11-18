package proxy.nodes

object NodeService {
    private const val NUM_VIRTUAL_NODES = 10
    private val circle: MutableMap<Int, Node> = mutableMapOf()

    fun addNode(node: Node) {
        for (i in 0 until NUM_VIRTUAL_NODES) {
            val virtualNodeName = "${node.address}:${node.port}#$i"
            val hash = virtualNodeName.hashCode()
            circle[hash] = node
        }
    }

    fun removeNode(node: Node) {
        for (i in 0 until NUM_VIRTUAL_NODES) {
            val virtualNodeName = "${node.address}:${node.port}#$i"
            val hash = virtualNodeName.hashCode()
            circle.remove(hash)
        }
    }

    fun getNode(listId: Int): Node? {
        val hash = listId.hashCode()
        circle[hash]?.let { return it }
        val greaterKeys = circle.filterKeys { it > hash }
        return if (greaterKeys.isEmpty()) circle.entries.firstOrNull()?.value else greaterKeys.entries.first().value
    }
}