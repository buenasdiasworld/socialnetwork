package main.model;

import javax.persistence.*;

import lombok.Data;

@Entity
@Table(name = "notification_settings")
@Data
public class NotificationSettings {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @ManyToOne
  @JoinColumn(name = "person_id", referencedColumnName = "id")
  private Person personId;

  @Column(name = "is_enabled", nullable = false, columnDefinition = "TINYINT")
  private boolean isEnabled = true;

  @OneToOne
  @JoinColumn(name = "notification_type_id")
  private NotificationType type;

}
