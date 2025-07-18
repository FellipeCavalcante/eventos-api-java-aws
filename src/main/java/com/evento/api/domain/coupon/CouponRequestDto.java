package com.evento.api.domain.coupon;

import java.util.UUID;

public record CouponRequestDto(
        String code,
        Integer discount,
        Long valid
) {
}
