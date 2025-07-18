package com.evento.api.service;

import com.evento.api.domain.coupon.Coupon;
import com.evento.api.domain.coupon.CouponRequestDto;
import com.evento.api.domain.event.Event;
import com.evento.api.repository.CouponRepository;
import com.evento.api.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CouponService {

    @Autowired
    private CouponRepository repository;

    @Autowired
    private EventRepository eventRepository;

    public Coupon addCouponEvent(UUID eventId, CouponRequestDto data) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        Coupon coupon = new Coupon();
        coupon.setCode(data.code());
        coupon.setDiscount(data.discount());
        coupon.setValid(new Date(data.valid()));
        coupon.setEvent(event);

        return repository.save(coupon);
    }

    public List<Coupon> consultCoupons(UUID eventId, Date currentDate) {
        return repository.findByEventIdAndValidAfter(eventId, currentDate);
    }
}
