DROP TABLE IF EXISTS authors;

CREATE TABLE authors (
	id INTEGER PRIMARY KEY AUTO_INCREMENT,
	full_name VARCHAR(50),
	birthday DATETIME,
	lang VARCHAR(50),
	country VARCHAR(10)
);

INSERT INTO authors(id, full_name, birthday, lang, country) VALUES (1, 'Stephen King', '1947-09-21', 'English', 'US');
INSERT INTO authors(id, full_name, birthday, lang, country) VALUES (2, 'Joanne Rowling', '1965-07-31', 'English', 'GB');
INSERT INTO authors(id, full_name, birthday, lang, country) VALUES (3, 'Erich Maria Remaeque', '1870-09-25', 'German', 'DE');
INSERT INTO authors(id, full_name, birthday, lang, country) VALUES (4, 'Marcel Proust', '1871-07-10', 'French', 'FR');
INSERT INTO authors(id, full_name, birthday, lang, country) VALUES (5, 'Sergey Yesenin', '1895-10-03', 'Russian', 'RU');
INSERT INTO authors(id, full_name, birthday, lang, country) VALUES (6, 'Taras Shevchenko', '1814-03-09', 'Ukrainian', 'UA');

DROP TABLE IF EXISTS books;

CREATE TABLE books (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    id_author INTEGER NOT NULL,
    title VARCHAR(100),
    genre VARCHAR(100),
    _year INTEGER,
    FOREIGN KEY (id_author) REFERENCES authors(id)
);

INSERT INTO books (id, id_author, title, genre, _year) VALUES (1, 1, 'It', 'horror', 1986);
INSERT INTO books (id, id_author, title, genre, _year) VALUES (2, 1, 'Shining', 'horror', 1977);
INSERT INTO books (id, id_author, title, genre, _year) VALUES (3, 1, 'Green Mile', 'fantasy', 1986);
INSERT INTO books (id, id_author, title, genre, _year) VALUES (4, 2, 'Harry Potter and Philosophy Stone', 'fantasy', 1997);
INSERT INTO books (id, id_author, title, genre, _year) VALUES (5, 2, 'Fantastic Beasts and Were to Find Them', 'fantasy', 2001);
INSERT INTO books (id, id_author, title, genre, _year) VALUES (6, 2, 'Harry Potter and Deathly Hollows', 'fantasy', 2007);
INSERT INTO books (id, id_author, title, genre, _year) VALUES (7, 3, 'Three Comrades', 'drama', 1936);
INSERT INTO books (id, id_author, title, genre, _year) VALUES (8, 3, 'Shadows in the Paradise', 'novel', 1971);
INSERT INTO books (id, id_author, title, genre, _year) VALUES (9, 3, 'Time to Live and Time to Die', 'novel', 1954);
INSERT INTO books (id, id_author, title, genre, _year) VALUES (10, 4, 'Against Sainte-Beuve', 'modernist', 1954);
INSERT INTO books (id, id_author, title, genre, _year) VALUES (11, 4, 'Pleasures and Days', 'modernist', 1896);
INSERT INTO books (id, id_author, title, genre, _year) VALUES (12, 4, 'The Prisoner', 'novel', 1923);
INSERT INTO books (id, id_author, title, genre, _year) VALUES (13, 5, 'Goodbye, My Friend, Goodbye', ' poem', 1925);
INSERT INTO books (id, id_author, title, genre, _year) VALUES (14, 5, 'The Black Man', 'poem', 1925);
INSERT INTO books (id, id_author, title, genre, _year) VALUES (15, 5, 'Hooligan', 'poem', 1919);
INSERT INTO books (id, id_author, title, genre, _year) VALUES (16, 6, 'Testament', 'poem', 1845);
INSERT INTO books (id, id_author, title, genre, _year) VALUES (17, 6, 'Catherine', 'poem', 1839);
INSERT INTO books (id, id_author, title, genre, _year) VALUES (18, 6, 'Haidamaky', 'poem', 1841);

SELECT * FROM authors a ;

SELECT * FROM books b;

DROP TABLE IF EXISTS users;
CREATE TABLE IF NOT EXISTS users (
	id INTEGER PRIMARY KEY AUTO_INCREMENT,
	username VARCHAR(128) NOT NULL,
	password VARCHAR(128) NOT NULL,
	user_role VARCHAR(64) NOT NULL
);

INSERT INTO users (username, password, user_role) VALUES ('sa', 'sa', 'SUPER_ADMIN');

SELECT * FROM users u ;

DROP TABLE IF EXISTS libraries;

CREATE TABLE libraries(
	id INTEGER PRIMARY KEY AUTO_INCREMENT,
	owner_id INTEGER NOT NULL
);

DROP TABLE IF EXISTS books_to_libraries;

CREATE TABLE books_to_libraries(
	book_id INTEGER NOT NULL,
	library_id INTEGER NOT NULL,
	FOREIGN KEY (book_id)
	REFERENCES books(id),
	FOREIGN KEY (library_id)
	REFERENCES libraries(id)
);

DROP TABLE IF EXISTS authors_to_libraries;

CREATE TABLE authors_to_libraries(
	author_id INTEGER NOT NULL,
	library_id INTEGER NOT NULL,
	FOREIGN KEY (author_id)
	REFERENCES authors(id),
	FOREIGN KEY (library_id)
	REFERENCES libraries(id)
);

INSERT INTO libraries (owner_id) VALUES (1);
INSERT INTO books_to_libraries (book_id, library_id) VALUES (1, 1);
INSERT INTO books_to_libraries (book_id, library_id) VALUES (2, 1);
INSERT INTO authors_to_libraries (author_id, library_id) VALUES (1, 1);


SELECT * FROM libraries;
SELECT * FROM books_to_libraries;
SELECT * FROM authors_to_libraries;

DELETE FROM books_to_libraries;
DELETE FROM authors_to_libraries;


SELECT btl.book_id
FROM books_to_libraries btl
INNER JOIN books b
ON btl.book_id = b.id
WHERE btl.library_id = 1;

SELECT atl.author_id
FROM authors_to_libraries atl
INNER JOIN authors a
ON atl.author_id = a.id
WHERE atl.library_id = 1;