package com.bpx.brainify.services.interfaces;

import com.bpx.brainify.models.dtos.GPTInputDTO;

import java.io.IOException;
import java.util.List;

public interface OpenAiService {
    List<Double> getEmbedding(GPTInputDTO text);

    String chat(GPTInputDTO input);
}
