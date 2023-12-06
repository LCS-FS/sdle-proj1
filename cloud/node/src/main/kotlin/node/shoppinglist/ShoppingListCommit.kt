package node.shoppinglist

import kotlinx.serialization.Serializable

@Serializable
data class ShoppingListCommit(
        val hash: Int,
        val itemName: String,
        val count: Int,
        val type: ShoppingListCommitType
)