package com.gugugu.myapplication.chatgpt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatGptResponse {
    private String id;
    private String object;
    private Long created;
    private List<Choice> choices;

    public List<Choice> getChoices() {
        return choices;
    }
}
