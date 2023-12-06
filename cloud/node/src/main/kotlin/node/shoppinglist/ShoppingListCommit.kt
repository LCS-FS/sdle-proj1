package node.shoppinglist

data class ShoppingListCommit(
        val hash: Int,
        val itemName: String,
        val count: Int,
        val type: ShoppingListCommitType
)