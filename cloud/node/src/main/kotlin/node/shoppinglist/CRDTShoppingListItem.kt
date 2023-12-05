package node.shoppinglist

data class CRDTShoppingListItem(
        val hash: String,
        val name: String,
        val type: String,
        val count: Int,
        val listId: Int
)