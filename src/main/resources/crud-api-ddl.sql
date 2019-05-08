CREATE TABLE users (
  id       INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(64) NOT NULL,
  password VARCHAR(64) NOT NULL,
  age      INTEGER,
  gender   INTEGER
);

CREATE TABLE posts (
  id      INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  title   VARCHAR(256) NOT NULL,
  content TEXT NOT NULL,
  CONSTRAINT user_fk FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE comments (
  id      INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  post_id INT NOT NULL,
  content TEXT NOT NULL,
  CONSTRAINT comment_user_fk FOREIGN KEY(user_id) REFERENCES users(id),
  CONSTRAINT comment_post_fk FOREIGN KEY(post_id) REFERENCES posts(id)
);
