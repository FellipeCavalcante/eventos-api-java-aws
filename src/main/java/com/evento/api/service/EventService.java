package com.evento.api.service;

import com.amazonaws.services.s3.AmazonS3;
import com.evento.api.domain.address.Address;
import com.evento.api.domain.coupon.Coupon;
import com.evento.api.domain.event.Event;
import com.evento.api.domain.event.EventDetailsDto;
import com.evento.api.domain.event.EventRequestDto;
import com.evento.api.domain.event.EventResponseDto;
import com.evento.api.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Value("${aws.bucket.name}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;

    @Autowired
    private EventRepository repository;

    @Autowired
    private AddressService addressService;

    @Autowired
    private CouponService couponService;

    public Event createEvent(EventRequestDto data) {
        String imgUrl = null;

        if (data.image() != null) {
            imgUrl = this.uploadImg(data.image());
        }

        Event newEvent = new Event();
        newEvent.setTitle(data.title());
        newEvent.setDescription(data.description());
        newEvent.setEventUrl(data.eventUrl());
        newEvent.setDate(new Date(data.date()));
        newEvent.setImgUrl(imgUrl);
        newEvent.setRemote(data.remote());

        repository.save(newEvent);

        if (!data.remote()) {
            this.addressService.addAddress(newEvent, data);
        }

        return newEvent;
    }

    public List<EventResponseDto> getEvents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventsPage = repository.findAll(pageable);

        return eventsPage.map(event -> new EventResponseDto(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getAddress() != null ? event.getAddress().getCity() : "",
                event.getAddress() != null ? event.getAddress().getUf() : "",
                event.getRemote(),
                event.getEventUrl(),
                event.getImgUrl()
        )).stream().toList();
    }

    public List<EventResponseDto> getUpcomingEvents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventsPage = repository.findUpcomingEvents(new Date(), pageable);

        return eventsPage.map(event -> new EventResponseDto(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getAddress() != null ? event.getAddress().getCity() : "",
                event.getAddress() != null ? event.getAddress().getUf() : "",
                event.getRemote(),
                event.getEventUrl(),
                event.getImgUrl()
        )).stream().toList();
    }

    public List<EventResponseDto> getFilteredEvents(int page, int size, String title, String city, String uf, Date startDate, Date endDate) {
        title = (title != null) ? title : "";
        city = (city != null) ? city : "";
        uf = (uf != null) ? uf : "";
        startDate = (startDate != null) ? startDate : new Date(0);
        endDate = (endDate != null) ? endDate : new Date();

        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventsPage = repository.findFilteredEvents(title, city, uf, startDate, endDate, pageable);

        return eventsPage.map(event -> new EventResponseDto(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getAddress() != null ? event.getAddress().getCity() : "",
                event.getAddress() != null ? event.getAddress().getUf() : "",
                event.getRemote(),
                event.getEventUrl(),
                event.getImgUrl()
        )).stream().toList();
    }

    public EventDetailsDto getEventDetail(UUID eventId) {
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));


        List<Coupon> coupons = couponService.consultCoupons(eventId, new Date());

        List<EventDetailsDto.CouponDTO> couponDTOs = coupons.stream()
                .map(coupon -> new EventDetailsDto.CouponDTO(
                        coupon.getCode(),
                        coupon.getDiscount(),
                        coupon.getValid()))
                .collect(Collectors.toList());

        return new EventDetailsDto(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getAddress() != null ? event.getAddress().getCity() : "",
                event.getAddress()  != null ? event.getAddress().getUf() : "",
                event.getImgUrl(),
                event.getEventUrl(),
                couponDTOs);
    }


    private String uploadImg(MultipartFile multipartFile) {
        String fileName = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();
        try {
            File file = this.convertMultipartToFile(multipartFile);
            s3Client.putObject(bucketName, fileName, file);
            file.delete();

            return s3Client.getUrl(bucketName, fileName).toString();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return "";
        }
    }

    private File convertMultipartToFile(MultipartFile multipartFile) throws IOException {

        File convFile = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(multipartFile.getBytes());
        fos.close();
        return convFile;
    }
}
