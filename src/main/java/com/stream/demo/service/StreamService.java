package com.stream.demo.service;

import com.stream.demo.model.dto.StreamDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class StreamService {

    public StreamDTO startStream(String streamKey) {
        log.info("Starting stream with key: {}", streamKey);
        return StreamDTO.builder()
                .streamKey(streamKey)
                .isLive(true)
                .startedAt(LocalDateTime.now())
                .build();
    }

    public StreamDTO endStream(String streamKey) {
        log.info("Ending stream with key: {}", streamKey);
        return StreamDTO.builder()
                .streamKey(streamKey)
                .isLive(false)
                .endedAt(LocalDateTime.now())
                .build();
    }
}
