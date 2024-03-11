package com.nbcampif.ifstagram.domain.like.service;

import com.nbcampif.ifstagram.domain.user.model.User;

public interface LikeService {

  Long countLike(Long postId, User user);
}
