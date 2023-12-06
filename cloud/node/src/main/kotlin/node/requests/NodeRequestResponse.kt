package node.requests

import node.shoppinglist.ShoppingList

sealed class NodeRequestResponse {
    data class  Found(val shoppingList: ShoppingList) : NodeRequestResponse()
    data object NotFound                              : NodeRequestResponse()
    data object FailedToConnect                       : NodeRequestResponse()
}