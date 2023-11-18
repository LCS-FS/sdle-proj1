DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS lists;

CREATE TABLE IF NOT EXISTS lists (
    id   INTEGER      PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS items (
    id       INTEGER      PRIMARY KEY AUTO_INCREMENT,
    name     VARCHAR(255) NOT NULL,
    quantity INTEGER      NOT NULL,
    listId   INTEGER      NOT NULL,
    FOREIGN KEY (listId)  REFERENCES lists(id)
);

INSERT INTO lists(id, name) VALUES (1, 'My shopping list 1');
INSERT INTO lists(id, name) VALUES (2, 'My shopping list 2');

INSERT INTO items(name, quantity, listId) VALUES ('apple',  3, 1);
INSERT INTO items(name, quantity, listId) VALUES ('banana', 2, 1);

INSERT INTO items(name, quantity, listId) VALUES ('pear',  3, 2);
INSERT INTO items(name, quantity, listId) VALUES ('peach', 2, 2);