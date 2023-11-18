package node.shoppinglist

data class ShoppingList(
        val id: Int,
        val name: String,
        val items: MutableList<ShoppingListItem>
)