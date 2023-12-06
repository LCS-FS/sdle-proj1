package node.shoppinglist

data class ShoppingList(
        val id: Int,
        val name: String,
        val commits: List<ShoppingListCommit>
)