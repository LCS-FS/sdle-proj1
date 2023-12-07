DROP TABLE IF EXISTS commits CASCADE;
DROP TABLE IF EXISTS lists CASCADE;

CREATE TABLE IF NOT EXISTS lists (
    id   INTEGER      PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS commits (
    id       INTEGER      PRIMARY KEY AUTO_INCREMENT,
    hash     VARCHAR(255) NOT NULL,
    name     VARCHAR(255) NOT NULL,
    quantity INTEGER      NOT NULL,
    sum      BOOLEAN      NOT NULL,
    listId   INTEGER      NOT NULL,
    FOREIGN KEY (listId)  REFERENCES lists(id) ON DELETE CASCADE
);

INSERT INTO lists(id, name) VALUES (1, 'My shopping list 1');
INSERT INTO lists(id, name) VALUES (2, 'My shopping list 2');

INSERT INTO commits(hash, name, quantity, sum, listId) VALUES ('1234', 'apple',  3, true, 1);
INSERT INTO commits(hash, name, quantity, sum, listId) VALUES ('1235', 'apple',  2, false, 1);
INSERT INTO commits(hash, name, quantity, sum, listId) VALUES ('1236', 'banana', 2, true, 1);
INSERT INTO commits(hash, name, quantity, sum, listId) VALUES ('1237', 'banana', 4, true, 1);

INSERT INTO commits(hash, name, quantity, sum, listId) VALUES ('1238', 'pear',  3, true, 2);
INSERT INTO commits(hash, name, quantity, sum, listId) VALUES ('1239', 'peach', 2, true, 2);