package org.example.model;

public interface IAppUser {
  String authenticateUser(String username, String password);

  String createAccount(String username, String password);

  String getUsername();

  void setUsername(String username);

  String getPassword();

  void setPassword(String password);
}
