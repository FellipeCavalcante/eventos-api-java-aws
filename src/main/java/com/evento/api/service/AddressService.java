package com.evento.api.service;

import com.evento.api.domain.address.Address;
import com.evento.api.domain.event.Event;
import com.evento.api.domain.event.EventRequestDto;
import com.evento.api.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AddressService {

    @Autowired
    private AddressRepository repository;

    public Address addAddress(Event event, EventRequestDto data) {
        Address address = new Address();
        address.setCity(data.city());
        address.setUf(data.uf());
        address.setEvent(event);

        return repository.save(address);
    }

    public Optional<Address> findByEventId(UUID eventId) {
        return repository.findByEventId(eventId);
    }
}
