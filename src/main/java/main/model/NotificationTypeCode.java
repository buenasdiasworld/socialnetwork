package main.model;

public enum NotificationTypeCode {


  POST("POST"),
  POST_COMMENT("POST_COMMENT"),
  COMMENT_COMMENT("COMMENT_COMMENT"),
  FRIEND_REQUEST("FRIEND_REQUEST"),
  MESSAGE("MESSAGE"),
  FRIEND_BIRTHDAY("FRIEND_BIRTHDAY"),
  LIKE("LIKE");

  protected String notificationType;

  NotificationTypeCode(String notificationType) {
    this.notificationType = notificationType;
  }

}
