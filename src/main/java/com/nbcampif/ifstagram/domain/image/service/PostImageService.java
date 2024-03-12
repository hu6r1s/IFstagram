package com.nbcampif.ifstagram.domain.image.service;

import com.nbcampif.ifstagram.domain.image.entity.PostImage;
import com.nbcampif.ifstagram.domain.image.repository.PostImageRepository;
import com.nbcampif.ifstagram.domain.post.entity.Post;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostImageService {

  @Value("${upload.path}")
  private String uploadPath;

  private final PostImageRepository postImageRepository;


  @Transactional
  public void createImage(MultipartFile imageFile, Post post) throws Exception {
    PostImage image = getPostImage(imageFile, post);
    postImageRepository.save(image);
  }


  public String getImage(Long id) throws MalformedURLException {
    Optional<PostImage> image = postImageRepository.findById(id);
    if (!image.isPresent()) {
      return "";
    }
    return image.get().getFilePath();
  }

  @Transactional
  public void updateImage(Post post, MultipartFile imageFile) throws IOException {
    PostImage image = getPostImage(imageFile, post);
    PostImage postImage = postImageRepository.findByPostId(post.getId())
      .orElseThrow(() -> new IllegalArgumentException("게시물 이미지가 존재하지 않습니다"));
    postImage.updatePostImage(image);
  }

  private PostImage getPostImage(MultipartFile file, Post post) throws IOException {
    if (file != null) {
      String originalFilename = file.getOriginalFilename();
      String saveFileName = createSaveFileName(originalFilename);
      file.transferTo(new File(getFullPath(saveFileName)));
      String filePath = uploadPath + saveFileName;

      String contentType = file.getContentType();

      PostImage image = PostImage.builder()
        .fileName(originalFilename)
        .saveFileName(saveFileName)
        .contentType(contentType)
        .filePath(filePath)
        .post(post)
        .build();
      return image;
    }
    return PostImage.builder()
      .fileName(null)
      .saveFileName(null)
      .contentType(null)
      .filePath(null)
      .post(post)
      .build();
  }

  private String createSaveFileName(String originalFilename) {
    String ext = extractExt(originalFilename);
    String uuid = UUID.randomUUID().toString();
    return uuid + "." + ext;
  }

  private String extractExt(String originalFilename) {
    int pos = originalFilename.lastIndexOf(".");
    return originalFilename.substring(pos + 1);
  }

  private String getFullPath(String filename) {
    return uploadPath + filename;
  }

}
