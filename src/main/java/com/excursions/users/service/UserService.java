package com.excursions.users.service;

import com.excursions.users.model.User;
import java.util.List;

public interface UserService {

    User create(String name);
    User update(Long id, String name);

    User findById(Long id);

    List<User> findAll();

    void deleteById(Long id);

    User coinsUpByUser(Long id, Long coins);
    User coinsDownByUser(Long id, Long coins);

    User coinsUpByExcursion(Long id, Long coins);
    User coinsDownByExcursion(Long id, Long coins);
}
