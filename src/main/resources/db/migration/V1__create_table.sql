CREATE TABLE users (
                       id          integer PRIMARY KEY,
                       email       VARCHAR(255),
                       firstname   VARCHAR(255),
                       lastname    VARCHAR(255),
                       password    VARCHAR(255),
                       role        VARCHAR(255),
                       photo_url   VARCHAR(255)
);

CREATE TABLE todos (
                       id          BIGSERIAL PRIMARY KEY,
                       description VARCHAR(255),
                       header      VARCHAR(255),
                       target_date DATE,
                       todo_status VARCHAR(255),
                       user_id     INTEGER REFERENCES users
);
