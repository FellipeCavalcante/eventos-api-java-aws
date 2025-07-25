package com.evento.api.repository;

import com.evento.api.domain.address.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {

    public Optional<Address> findByEventId(UUID eventId);
}
