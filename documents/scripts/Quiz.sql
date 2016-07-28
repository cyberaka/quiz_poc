-- Drop all existing tables. 
SET FOREIGN_KEY_CHECKS=0;
drop table quiz_users;
drop table quiz_topic;
drop table quiz_sub_topic;
drop table quiz_question_bank;
SET FOREIGN_KEY_CHECKS=1;

-- Create the users who will use the system 
create table quiz_users (
  id INT NOT NULL AUTO_INCREMENT,
  status INT,
  created_timestamp timestamp,
  modified_timestamp timestamp,
  PRIMARY KEY(id)
);

-- Create the quiz topic which will list down all the subjects in the system 
create table quiz_topic (
  id INT NOT NULL AUTO_INCREMENT,
  topic_name VARCHAR(100) NOT NULL,
  created_by_id INT,
  status INT,
  created_timestamp timestamp,
  modified_timestamp timestamp,
  PRIMARY KEY(id),
  FOREIGN KEY fk_topic_user(created_by_id)
  REFERENCES quiz_users(id)
  ON UPDATE CASCADE
  ON DELETE RESTRICT
);

-- Create the quiz sub topic which will list down all the sub topics within a topic
create table quiz_sub_topic (
  id INT NOT NULL AUTO_INCREMENT,
  topic_id INT,
  sub_topic_name VARCHAR(100) NOT NULL,
  created_by_id INT,
  status INT,
  created_timestamp timestamp,
  modified_timestamp timestamp,
  PRIMARY KEY(id),
  FOREIGN KEY fk_sub_topic_user(created_by_id)
  REFERENCES quiz_users(id)
  ON UPDATE CASCADE
  ON DELETE RESTRICT,
  FOREIGN KEY fk_sub_topic_parent(topic_id)
  REFERENCES quiz_topic(id)
  ON UPDATE CASCADE
  ON DELETE RESTRICT
);

-- Create a question bank which will have relation with a topic and sub topic.
-- Every question has a 
create table quiz_question_bank (
  id INT NOT NULL AUTO_INCREMENT,
  topic_id INT,
  subtopic_id INT,
  question_text TEXT, 
  options_text TEXT,
  answer_text VARCHAR(100) NOT NULL,
  created_by_id INT,
  status INT,
  created_timestamp timestamp,
  modified_timestamp timestamp,
  PRIMARY KEY(id),
  FOREIGN KEY fk_sub_topic_user(created_by_id)
  REFERENCES quiz_users(id)
  ON UPDATE CASCADE
  ON DELETE RESTRICT,
  FOREIGN KEY fk_question_topic(topic_id)
  REFERENCES quiz_topic(id)
  ON UPDATE CASCADE
  ON DELETE RESTRICT,
  FOREIGN KEY fk_question_sub_topic(subtopic_id)
  REFERENCES quiz_sub_topic(id)
  ON UPDATE CASCADE
  ON DELETE RESTRICT
);
