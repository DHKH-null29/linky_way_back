ALTER TABLE member auto_increment = 1;
ALTER TABLE tag auto_increment = 1;
ALTER TABLE card auto_increment = 1;
ALTER TABLE folder auto_increment = 1;


INSERT INTO member (email, nickname, password)
VALUES ('marrin1101@naver.com', 'hello', '$2a$10$qvny0E2x6zsD0HFtt8F3iOLdnTQFKPjSvlodzWyyLcFE223JMROf.');
-- "password": "aA@1123"

INSERT INTO member (email, nickname, password)
VALUES ('marrin1101@gmail.com', 'hello2', '$2a$10$p8Jl9MeHW8dYb/OlNOuCyeYN7GZ0w8Kq5ayqUWKJcBaUY2TQQ.fqC');
-- "password": "aA@1123"
INSERT INTO member (email, nickname, password)
VALUES ('pds0123@gmail.com', 'hello3', '$2a$10$BGENx/1mUjloPKZ311EGFe16UcWRM5NHy6qmbmr9oxiQO6YRrM.TS');
-- "password": "aA@123"



INSERT INTO tag (name, shareable, views, member_member_id)
VALUES ('spring', 0, 0, 1);

INSERT INTO tag (name, shareable, views, member_member_id)
VALUES ('food', 0, 0, 1);

INSERT INTO tag (name, shareable, views, member_member_id)
VALUES ('spring', 0, 0, 2);

INSERT INTO tag (name, shareable, views, member_member_id)
VALUES ('spring', 0, 0, 3);

INSERT INTO tag (name, shareable, views, member_member_id)
VALUES ('firewall', 1, 0, 1);
INSERT INTO tag (name, shareable, views, member_member_id)
VALUES ('shopping', 1, 0, 1);