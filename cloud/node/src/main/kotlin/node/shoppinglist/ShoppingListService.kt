package node.shoppinglist

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.query
import org.springframework.stereotype.Service
import java.util.concurrent.locks.ReentrantLock


@Service
class ShoppingListService(val db: JdbcTemplate) {

    private val lock = ReentrantLock()

    fun getListById(id: String): ShoppingList? {
        val name = db.query(
                "SELECT name FROM lists where id=?",
                id
        ) { response, _ ->
            response.getString("name")
        }.firstOrNull() ?: return null

        val commits = db.query(
                "SELECT * FROM commits WHERE listId=?",
                arrayOf(id)
        ) { rs, _ ->
            ShoppingListCommit(
                    rs.getString("hash"),
                    rs.getString("name"),
                    rs.getInt("quantity"),
                    if (rs.getBoolean("sum")) ShoppingListCommitType.ADD else ShoppingListCommitType.REMOVE,
            )
        }
        return ShoppingList(id, name, commits)
    }

    fun putList(shoppingList: ShoppingList) {
        lock.lock()
        // delete old list representation
        db.update(
                "DELETE FROM lists WHERE id = ?",
                shoppingList.id
        )

        // insert new list representation
        db.update(
                "INSERT INTO lists VALUES (?, ?)",
                shoppingList.id, shoppingList.name
        )
        for (commit in shoppingList.commits) {
            db.update(
                    "INSERT INTO commits(hash, name, quantity, sum, listId) VALUES (?, ?, ?, ?, ?)",
                    commit.hash, commit.itemName, commit.count, commit.type == ShoppingListCommitType.ADD, shoppingList.id
            )
        }
        lock.unlock()
    }
}