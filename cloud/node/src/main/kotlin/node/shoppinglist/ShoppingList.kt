package node.shoppinglist

data class ShoppingList(
        val name: String,
        val commits: List<ShoppingListCommit>
)