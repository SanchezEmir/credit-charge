package com.nttdata.creditcharge.entity.dto;

import com.nttdata.creditcharge.entity.enums.ETypeCustomer;

import lombok.Data;

@Data
public class TypeCustomer {
  
  private String id;
  
  private ETypeCustomer value;
  
  private SubType subType;

}
