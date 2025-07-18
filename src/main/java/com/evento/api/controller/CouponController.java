package com.evento.api.controller;

import com.evento.api.domain.coupon.Coupon;
import com.evento.api.domain.coupon.CouponRequestDto;
import com.evento.api.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/coupon")
public class CouponController {

    @Autowired
    private CouponService service;

    @PostMapping("/event/{eventId}")
    public ResponseEntity<Coupon> saveCoupon(@PathVariable UUID eventId, @RequestBody CouponRequestDto request) {
        Coupon coupon = service.addCouponEvent(eventId, request);

        return ResponseEntity.ok(coupon);
    }
}
