package com.bankly.vendura.payment.giftcard.model;

import com.bankly.vendura.authentication.user.model.User;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "giftcards")
@NoArgsConstructor
@AllArgsConstructor
public class GiftCard {


  @Id private String id;

  private Date issueDate;
  private Date expirationDate;

  private double initialBalance;

  @DBRef private User issuer;

}
