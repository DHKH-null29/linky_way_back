DROP table IF EXISTS endorse cascade ;
DROP table IF EXISTS card_tag cascade;
DROP table IF EXISTS tag cascade;
DROP table IF EXISTS card cascade;
DROP table IF EXISTS folder cascade;
DROP table IF EXISTS member;



-- Member 테이블
CREATE TABLE member (
  member_id BIGINT AUTO_INCREMENT NOT NULL,
   nickname VARCHAR(10) NOT NULL,
   password VARCHAR(72) NOT NULL,
   email VARCHAR(255) NOT NULL,
   created_by datetime NULL,
   modified_by datetime NULL,

   CONSTRAINT pk_member PRIMARY KEY (member_id)
);

CREATE FULLTEXT INDEX MEMBER_IX_FT_EMAIL on member(email);



-- Tag 테이블
CREATE TABLE tag (
  tag_id BIGINT AUTO_INCREMENT NOT NULL,
   created_by datetime NULL,
   modified_by datetime NULL,
   name VARCHAR(10) NOT NULL,
   is_public BIT(1) NOT NULL,
   member_member_id BIGINT NOT NULL,
   CONSTRAINT pk_tag PRIMARY KEY (tag_id)
);

CREATE INDEX TAG_IX_NAME ON tag(name);
CREATE INDEX TAG_IX_MEMBER_MEMBER_ID_NAME on tag(member_member_id, name);
ALTER TABLE tag ADD CONSTRAINT FK_TAG_ON_MEMBER_MEMBER FOREIGN KEY (member_member_id) REFERENCES member (member_id);







-- Folder 테이블
CREATE TABLE folder (
  folder_id BIGINT AUTO_INCREMENT NOT NULL,
   name VARCHAR(10) NOT NULL,
   depth BIGINT NOT NULL,
   parent_folder_id BIGINT NULL,
   member_member_id BIGINT NOT NULL,
   CONSTRAINT pk_folder PRIMARY KEY (folder_id)
);

CREATE INDEX FOLDER_IX_DEPTH ON folder(depth);
CREATE INDEX FOLDER_IX_NAME ON folder(name);
ALTER TABLE folder ADD CONSTRAINT FK_FOLDER_ON_MEMBER_MEMBER FOREIGN KEY (member_member_id) REFERENCES member (member_id);
ALTER TABLE folder ADD CONSTRAINT FK_FOLDER_ON_PARENT_FOLDER FOREIGN KEY (parent_folder_id) REFERENCES folder (folder_id);



-- Endorse 테이블
CREATE TABLE endorse (
   endorse_id BIGINT AUTO_INCREMENT NOT NULL,
   member_member_id BIGINT NULL,
   tag_tag_id BIGINT NULL,
   CONSTRAINT pk_endorse PRIMARY KEY (endorse_id)
);

ALTER TABLE endorse ADD CONSTRAINT FK_ENDORSE_ON_MEMBER_MEMBER FOREIGN KEY (member_member_id) REFERENCES member (member_id);
ALTER TABLE endorse ADD CONSTRAINT FK_ENDORSE_ON_TAG_TAG FOREIGN KEY (tag_tag_id) REFERENCES tag (tag_id);



-- Card 테이블
CREATE TABLE card (
   card_id BIGINT AUTO_INCREMENT NOT NULL,
   created_by datetime NULL,
   modified_by datetime NULL,
   link VARCHAR(255) NOT NULL,
   title VARCHAR(30) NOT NULL,
   content VARCHAR(255) NOT NULL,
   is_public BIT(1) NOT NULL,
   is_deleted BIT(1) NOT NULL,
   folder_folder_id BIGINT NOT NULL,
   CONSTRAINT pk_card PRIMARY KEY (card_id)
);

ALTER TABLE card ADD CONSTRAINT FK_CARD_ON_FOLDER_FOLDER FOREIGN KEY (folder_folder_id) REFERENCES folder (folder_id);
CREATE FULLTEXT INDEX CARD_IX_FT_LINK on card(link);
CREATE FULLTEXT INDEX CARD_IX_FT_TITLE on card(title);
CREATE FULLTEXT INDEX CARD_IX_FT_CONTENT on card(content);



-- Card_Tag 테이블
CREATE TABLE card_tag (
  card_tag_id BIGINT AUTO_INCREMENT NOT NULL,
   card_card_id BIGINT NOT NULL,
   tag_tag_id BIGINT NOT NULL,
   CONSTRAINT pk_card_tag PRIMARY KEY (card_tag_id)
);

ALTER TABLE card_tag ADD CONSTRAINT FK_CARD_TAG_ON_CARD_CARD FOREIGN KEY (card_card_id) REFERENCES card (card_id);
ALTER TABLE card_tag ADD CONSTRAINT FK_CARD_TAG_ON_TAG_TAG FOREIGN KEY (tag_tag_id) REFERENCES tag (tag_id);