package com.nbcampif.ifstagram.domain.image.entity;

import com.nbcampif.ifstagram.domain.post.entity.Post;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Getter
@Table(name = "images")
@AllArgsConstructor
@NoArgsConstructor
public class PostImage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "file_name")
  private String fileName;
  @Column(name = "save_file_name")
  private String saveFileName;
  @Column(name = "content_type")
  private String contentType;
  @Column(name = "file_path")
  private String filePath;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false)
  public Post post;

  public void updatePostImage(PostImage image) {
    this.fileName = image.getFileName();
    this.saveFileName = image.getSaveFileName();
    this.contentType = image.getContentType();
    this.filePath = image.getFilePath();
  }
}
