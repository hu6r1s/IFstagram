package com.nbcampif.ifstagram.domain.admin.repository;

import com.nbcampif.ifstagram.domain.admin.entity.QApiUseTime;
import com.nbcampif.ifstagram.domain.user.model.User;
import com.nbcampif.ifstagram.domain.user.repository.entity.QUserEntity;
import com.nbcampif.ifstagram.domain.user.repository.entity.UserEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ApiUseTimeQuery {

  private final JPAQueryFactory query;

  public List<User> getEvent() {
    QUserEntity user = QUserEntity.userEntity;
    QApiUseTime time = QApiUseTime.apiUseTime;
    List<UserEntity> result = query.select(user)
      .from(user)
      .join(time)
      .on(user.userId.eq(time.userId))
      .orderBy(time.totalTime.desc())
      .limit(3)
      .fetch();
    return result.stream().map(userEntity -> userEntity.toModel()).toList();
  }
}
