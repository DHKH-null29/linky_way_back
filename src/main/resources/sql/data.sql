-- 초기화
ALTER TABLE member auto_increment = 1;
ALTER TABLE tag auto_increment = 1;
ALTER TABLE card auto_increment = 1;
ALTER TABLE folder auto_increment = 1;
ALTER TABLE card_tag auto_increment = 1;


-- 초기 멤버 세팅
INSERT INTO member (email, nickname, password)
VALUES ('marrin1101@naver.com', 'hello', '$2a$10$qvny0E2x6zsD0HFtt8F3iOLdnTQFKPjSvlodzWyyLcFE223JMROf.');
-- "password": "aA@1123"
INSERT INTO member (email, nickname, password)
VALUES ('marrin1101@gmail.com', 'hello2', '$2a$10$p8Jl9MeHW8dYb/OlNOuCyeYN7GZ0w8Kq5ayqUWKJcBaUY2TQQ.fqC');
-- "password": "aA@1123"
INSERT INTO member (email, nickname, password)
VALUES ('pds0123@gmail.com', 'hello3', '$2a$10$BGENx/1mUjloPKZ311EGFe16UcWRM5NHy6qmbmr9oxiQO6YRrM.TS');
-- "password": "aA@123"


-- 태그 세팅
INSERT INTO tag (name, is_public, member_member_id)
VALUES ('java', 0, 1);

INSERT INTO tag (name, is_public, member_member_id)
VALUES ('food', 0, 1);

INSERT INTO tag (name, is_public, member_member_id)
VALUES ('spring', 0, 2);

INSERT INTO tag (name, is_public, member_member_id)
VALUES ('spring', 0, 3);

INSERT INTO tag (name, is_public, member_member_id)
VALUES ('firewall', 1, 1);

INSERT INTO tag (name, is_public, member_member_id)
VALUES ('shopping', 1, 1);



-- 폴더 세팅
INSERT INTO folder (name, depth, member_member_id)
VALUES ('IT', 1, 1);

INSERT INTO folder (name, depth, member_member_id)
VALUES ('JAVA', 2, 1);

INSERT INTO folder (name, depth, member_member_id)
VALUES ('PYTHON', 2, 1);

INSERT INTO folder (name, depth, member_member_id)
VALUES ('음식', 1, 1);

INSERT INTO folder (name, depth, member_member_id)
VALUES ('고기', 2, 1);

INSERT INTO folder (name, depth, member_member_id)
VALUES ('야채', 2, 1);


-- 카드 세팅
INSERT INTO card (link, title, content, is_public, is_deleted, folder_folder_id)
VALUES ('https://spring.io/', 'springFramework', '', 0, 0, 2);

INSERT INTO card (link, title, content, is_public, is_deleted, folder_folder_id)
VALUES ('https://khj93.tistory.com/entry/Spring-Spring-Framework%EB%9E%80-%EA%B8%B0%EB%B3%B8-%EA%B0%9C%EB%85%90-%ED%95%B5%EC%8B%AC-%EC%A0%95%EB%A6%AC', 'spring 블로그', '', 0, 0, 2);

INSERT INTO card (link, title, content, is_public, is_deleted, folder_folder_id)
VALUES ('https://hubl.tistory.com/38', '숯불탁탁', '맛있는 고기집', 0, 0, 5);

INSERT INTO card (link, title, content, is_public, is_deleted, folder_folder_id)
VALUES ('https://www.xn--w39at6wpzax3i7sdctt.kr/html/index.html', '명륜진사갈비', '맛집! 고기집', 0, 0, 5);

INSERT INTO card (link, title, content, is_public, is_deleted, folder_folder_id)
VALUES ('https://tracecooking.tistory.com/84', '명륜진사갈비 블로그', '', 0, 0, 5);

INSERT INTO card (link, title, content, is_public, is_deleted, folder_folder_id)
VALUES ('https://namu.wiki/w/%EC%B1%84%EC%86%8C', '채소 나무위키', '채소에 대한 정보', 1, 0, 2);



-- 카드 태그 연관관계 세팅
INSERT INTO card_tag (card_card_id, tag_tag_id)
VALUES (1, 1);

INSERT INTO card_tag (card_card_id, tag_tag_id)
VALUES (2, 1);

INSERT INTO card_tag (card_card_id, tag_tag_id)
VALUES (3, 2);

INSERT INTO card_tag (card_card_id, tag_tag_id)
VALUES (4, 2);

INSERT INTO card_tag (card_card_id, tag_tag_id)
VALUES (5, 2);

INSERT INTO card_tag (card_card_id, tag_tag_id)
VALUES (6, 2);