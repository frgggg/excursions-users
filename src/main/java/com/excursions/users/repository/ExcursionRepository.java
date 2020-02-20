package com.excursions.users.repository;

public interface ExcursionRepository {
    Long userTicketsCount(Long id) throws IllegalStateException;
}
