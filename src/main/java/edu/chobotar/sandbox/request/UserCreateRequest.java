package edu.chobotar.sandbox.request;

/*
  @author User
  @project sandbox
  @class UserCreateRequest
  @version 1.0.0
  @since 22.05.2025 - 16.41
*/

public record UserCreateRequest(String name, String username, String email, String phoneNumber, String gender) {
}
