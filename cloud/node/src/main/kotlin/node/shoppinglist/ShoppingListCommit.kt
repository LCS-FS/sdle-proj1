package node.shoppinglist

import kotlinx.serialization.Serializable

@Serializable
data class ShoppingListCommit(
        val hash: String,
        val itemName: String,
        val count: Int,
        val type: ShoppingListCommitType
)