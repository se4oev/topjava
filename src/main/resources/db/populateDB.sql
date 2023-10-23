DELETE FROM user_role;
DELETE FROM users;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin'),
       ('Guest', 'guest@gmail.com', 'guest');

INSERT INTO user_role (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001);

INSERT INTO meals (user_id, date_time, description, calories)
VALUES ((SELECT u.id FROM users u WHERE u.name = 'User'), '2023-10-20 10:38:46', 'Завтрак', 500),
       ((SELECT u.id FROM users u WHERE u.name = 'User'), '2023-10-20 13:38:51', 'Обед', 700),
       ((SELECT u.id FROM users u WHERE u.name = 'User'), '2023-10-20 18:00:01', 'Ужин', 400),
       ((SELECT u.id FROM users u WHERE u.name = 'Admin'), '2023-10-20 9:03:05', 'Завтрак', 400),
       ((SELECT u.id FROM users u WHERE u.name = 'Admin'), '2023-10-20 13:31:00', 'Обед', 300),
       ((SELECT u.id FROM users u WHERE u.name = 'Admin'), '2023-10-20 20:16:01', 'Хрючево', 1200),
       ((SELECT u.id FROM users u WHERE u.name = 'User'), '2023-10-21 9:20:41', 'Завтрак', 700),
       ((SELECT u.id FROM users u WHERE u.name = 'User'), '2023-10-21 11:40:01', 'Обед', 800),
       ((SELECT u.id FROM users u WHERE u.name = 'User'), '2023-10-21 18:20:15', 'Ужин', 900),
       ((SELECT u.id FROM users u WHERE u.name = 'Admin'), '2023-10-21 8:40:05', 'Завтрак', 300),
       ((SELECT u.id FROM users u WHERE u.name = 'Admin'), '2023-10-21 13:57:56', 'Обед', 500),
       ((SELECT u.id FROM users u WHERE u.name = 'Admin'), '2023-10-21 21:20:01', 'Вкуснятинка', 1500);