package com.nbcampif.ifstagram.domain.image.service;

import com.nbcampif.ifstagram.domain.image.entity.PostImage;
import com.nbcampif.ifstagram.domain.image.repository.PostImageRepository;
import com.nbcampif.ifstagram.domain.post.entity.Post;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Exception;
import io.awspring.cloud.s3.S3Operations;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.Response;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectAclResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectAclRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Service
@RequiredArgsConstructor
public class PostImageService {

  @Value("${upload.path}")
  private String uploadPath;

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  private final S3Client s3Client;

  private final PostImageRepository postImageRepository;

  @Transactional
  public void createImage(MultipartFile imageFile, Post post) throws Exception {
    PostImage image = getPostImage(imageFile, post);
    postImageRepository.save(image);
  }


  public String getImage(Long id) throws MalformedURLException {
    PostImage image = postImageRepository.findById(id).orElse(null);

    if (image == null || image.getFilePath() == null) {
       return null;
    }

    GetUrlRequest request = GetUrlRequest
      .builder()
      .bucket(bucket)
      .key(image.getFilePath())
      .build();

    return s3Client.utilities().getUrl(request).toExternalForm();
  }

  @Transactional
  public void updateImage(Post post, MultipartFile imageFile) throws IOException {
    PostImage image = getPostImage(imageFile, post);
    PostImage postImage = postImageRepository.findByPostId(post.getId())
      .orElseThrow(() -> new IllegalArgumentException("게시물 이미지가 존재하지 않습니다"));
    postImage.updatePostImage(image);
  }

  private PostImage getPostImage(MultipartFile file, Post post) {
    try {
      String originalFilename = file.getOriginalFilename();
      String extension = StringUtils.getFilenameExtension(originalFilename);
      String saveFileName = createSaveFileName(originalFilename);
      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucket)
        .key(uploadPath + saveFileName)
        .contentType(extension)
        .build();
      PutObjectResponse response = s3Client
        .putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

      String filePath = uploadPath + saveFileName;
      String contentType = file.getContentType();

      if(response.sdkHttpResponse().statusText().orElse("FAIL").equals("OK")){
        PostImage image = PostImage.builder()
          .fileName(originalFilename)
          .saveFileName(saveFileName)
          .contentType(contentType)
          .filePath(filePath)
          .post(post)
          .build();
        return image;
      }else{
        throw new IllegalStateException("AWS에 파일을 올리는데 실패했습니다.");
      }
    } catch(IOException ie){
      throw new RuntimeException(ie.getMessage());
    } catch (S3Exception ae){
      throw new RuntimeException(ae.getMessage());
    } catch (IllegalStateException se){
      throw new RuntimeException(se.getMessage());
    }
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
