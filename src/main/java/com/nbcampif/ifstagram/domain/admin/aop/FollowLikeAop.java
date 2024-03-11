package com.nbcampif.ifstagram.domain.admin.aop;

import com.nbcampif.ifstagram.domain.admin.entity.ApiUseTime;
import com.nbcampif.ifstagram.domain.admin.repository.ApiUseTimeRepository;
import com.nbcampif.ifstagram.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class FollowLikeAop {

  private final ApiUseTimeRepository apiUseTimeRepository;

  @Pointcut(
    "execution(* com.nbcampif.ifstagram.domain.user.controller.UserController.follow(..))"
  )
  private void followAop() {
  }

  @Pointcut("execution(* com.nbcampif.ifstagram.domain.like.controller.LikeController.*(..))")
  private void likeAop() {
  }

  @Around("followAop() || likeAop()")
  public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
    long startTime = System.currentTimeMillis();

    try {
      Object output = joinPoint.proceed();
      return output;
    } finally {
      long endTime = System.currentTimeMillis();
      long runTime = endTime - startTime;

      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if (auth != null && auth.getPrincipal().getClass() == User.class) {
        User user = (User) auth.getPrincipal();

        ApiUseTime apiUseTime = apiUseTimeRepository.findByUserId(user.getUserId())
          .orElse(null);
        if (apiUseTime == null) {
          apiUseTime = new ApiUseTime(user.getUserId(), runTime);
        } else {
          apiUseTime.addUseTime(runTime);
        }

        apiUseTimeRepository.save(apiUseTime);
      }
    }
  }
}
