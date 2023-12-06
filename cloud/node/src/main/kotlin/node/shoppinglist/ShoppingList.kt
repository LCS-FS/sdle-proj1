package node.shoppinglist

import kotlinx.serialization.Serializable

@Serializable
data class ShoppingList(
        val id: Int,
        val name: String,
        val commits: List<ShoppingListCommit>
)