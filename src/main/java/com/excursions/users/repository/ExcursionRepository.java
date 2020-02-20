package com.excursions.users.repository;

import java.util.List;

public interface ExcursionRepository {
    Long userTicketsCount(Long id) throws IllegalStateException;
}
