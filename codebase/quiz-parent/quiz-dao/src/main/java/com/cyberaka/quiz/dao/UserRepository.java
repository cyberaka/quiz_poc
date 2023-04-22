package com.cyberaka.quiz.dao;

import com.cyberaka.quiz.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
}

//public interface UserRepository extends CrudRepository<User, Integer> {
//
//    @Query("from User a where a.userName=:userName and a.password=:password")
//    User login(@Param("userName") String userName, @Param("password") String password);
//
//}
