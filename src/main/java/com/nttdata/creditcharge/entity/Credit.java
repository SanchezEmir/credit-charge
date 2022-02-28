package com.nttdata.creditcharge.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.nttdata.creditcharge.entity.dto.CreditCard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Document(collection = "credit")
@AllArgsConstructor
@NoArgsConstructor
public class Credit {
  
  @Id
  private String id;
  
  private CreditCard creditCard;
  
  private Double amount;
  
  private LocalDateTime createdAt;

}
