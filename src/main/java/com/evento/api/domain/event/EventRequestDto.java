package com.evento.api.domain.event;

import org.springframework.web.multipart.MultipartFile;

public record EventRequestDto(
        String title,
        String description,
        Long date,
        String city,
        String uf,
        Boolean remote,
        String eventUrl,
        MultipartFile image
) {
}
