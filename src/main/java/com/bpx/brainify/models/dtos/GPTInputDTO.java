package com.bpx.brainify.models.dtos;

import com.bpx.brainify.enums.GptActions;
import lombok.Data;

@Data
public class GPTInputDTO {
    private String input;
    private GptActions action;
    private int questionNumber;
}
