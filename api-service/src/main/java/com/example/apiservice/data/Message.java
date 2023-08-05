package com.example.apiservice.data;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class Message {

    private String userId;
    private String messageId;
    private String content;
}
