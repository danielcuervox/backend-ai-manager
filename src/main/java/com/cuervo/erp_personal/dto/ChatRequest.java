package com.cuervo.erp_personal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data // <-- Lombok genera automáticamente getDate(), getMessage() y getHistory()
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    private String date;
    private String message;
    private List<ChatMessageDto> history;
    private String language;
}
