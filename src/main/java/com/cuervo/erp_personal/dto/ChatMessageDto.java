package com.cuervo.erp_personal.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {
    private String sender;
    private String text;
}
