package node.shoppinglist

import kotlinx.serialization.Serializable

@Serializable
data class ShoppingList(
        val id: String,
        val name: String,
        val commits: List<ShoppingListCommit>
)