package com.nttdata.creditcharge.entity.dto;

import com.nttdata.creditcharge.entity.enums.ESubType;

import lombok.Data;

@Data
public class SubType {
  
  private String id;
  
  private ESubType value;

}
