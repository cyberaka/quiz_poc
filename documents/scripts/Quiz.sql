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
  user_id VARCHAR(100) NOT NULL,
  password VARCHAR(100) NOT NULL,
  name VARCHAR(200) NOT NULL, 
  email VARCHAR(200) NOT NULL,
  status INT,
  created_timestamp timestamp,
  modified_timestamp timestamp,
  PRIMARY KEY(id)
);

-- Insert default users
insert into quiz_users (user_id, password, name, email, status, created_timestamp) 
  values ('cyberaka', password('1234'), 'Abhinav Anand', 'cyberaka@gmail.com', '1', now());

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

-- Insert default topics
insert into quiz_topic (topic_name, created_by_id, status, created_timestamp) 
  values ('Java', '1', '1', now());
  
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
