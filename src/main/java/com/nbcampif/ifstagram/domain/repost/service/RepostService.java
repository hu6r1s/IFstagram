package com.nbcampif.ifstagram.domain.repost.service;

import com.nbcampif.ifstagram.domain.user.model.User;

public interface RepostService {

  void createRepost(Long postId, User user);
}
