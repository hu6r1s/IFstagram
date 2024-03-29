package com.nbcampif.ifstagram.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequestDto {

  private String introduction;
  @NotBlank
  @Pattern(regexp = "^[가-힣a-zA-Z0-9]*$")
  private String nickname;
  private String profileImage;
  private String password;
  private String confirmPassword;
}
