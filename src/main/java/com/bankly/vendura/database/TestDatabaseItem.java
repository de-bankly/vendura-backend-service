package com.bankly.vendura.database;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** This object is for the purpose of demonstration */
@Data // convenient shortcut annotation for ToString, EqualsAndHashCode, Getter, Setter,
// RequiredArgsConstructor
@Document("TestDatabaseItem") // Specifies the MongoDB collection
@NoArgsConstructor // Required for all database "Documents"
@AllArgsConstructor // Generates a constructor for all fields
public class TestDatabaseItem {

  @Id // Primary key of the database
  private String id;

  private String name; // database property
}
