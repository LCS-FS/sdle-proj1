package node

import node.shoppinglist.CRDTShoppingListItem
import node.shoppinglist.ShoppingListService
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest
class NodeApplicationTests {

	@Test
	fun contextLoads() {
	}

	@Test
	fun test_crdt_merge(){
		val db = JdbcTemplate()
		val service = ShoppingListService(db)
		val list1 = ArrayList<CRDTShoppingListItem>()
		val list2 = ArrayList<CRDTShoppingListItem>()
		val list3 = ArrayList<CRDTShoppingListItem>()

		list1.add(CRDTShoppingListItem("1", "Banana", "Add", 2, 1))
		list1.add(CRDTShoppingListItem("2", "Banana", "Rem", 1, 1))
		list1.add(CRDTShoppingListItem("3", "Apple", "Add", 3, 1))

		list2.add(CRDTShoppingListItem("1", "Banana", "Add", 2, 1))
		list2.add(CRDTShoppingListItem("2", "Banana", "Rem", 1, 1))
		list2.add(CRDTShoppingListItem("4", "Apple", "Rem", 1, 1))

		list3.add(CRDTShoppingListItem("4", "Apple", "Rem", 1, 1))
		list3.add(CRDTShoppingListItem("5", "Orange", "Add", 1, 1))
		list3.add(CRDTShoppingListItem("6", "Orange", "Add", 1, 1))

		val lists = ArrayList<ArrayList<CRDTShoppingListItem>>()
		lists.add(list1)
		lists.add(list2)
		lists.add(list3)

		val mergedList = service.mergeLists(lists)

		assert(mergedList.size == 6)
		assert(mergedList.contains(CRDTShoppingListItem("1", "Banana", "Add", 2, 1)))
		assert(mergedList.contains(CRDTShoppingListItem("2", "Banana", "Rem", 1, 1)))
		assert(mergedList.contains(CRDTShoppingListItem("3", "Apple", "Add", 3, 1)))
		assert(mergedList.contains(CRDTShoppingListItem("4", "Apple", "Rem", 1, 1)))
		assert(mergedList.contains(CRDTShoppingListItem("5", "Orange", "Add", 1, 1)))
		assert(mergedList.contains(CRDTShoppingListItem("6", "Orange", "Add", 1, 1)))
	}
}
