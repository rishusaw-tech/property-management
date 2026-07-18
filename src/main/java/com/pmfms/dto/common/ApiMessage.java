package com.pmfms.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Simple message payload for operations without a resource body. */
@Data @NoArgsConstructor @AllArgsConstructor
public class ApiMessage {
    private String message;
}
