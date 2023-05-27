package com.gugugu.myapplication.chatgpt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.net.ContentHandler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Choice {
    private String index;
    private String finish_reason;
    private Message message;

    public Message getMessage() {
        return message;
    }
}
