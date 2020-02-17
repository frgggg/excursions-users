package com.excursions.users.client;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@FeignClient(value = "ExcursionClient", url = "${excursions-excursions.url}")
public interface ExcursionClient {

    @GetMapping(value = "${excursions-excursions.api-user-tickets}", produces = "application/json")
    @Headers("Content-Type: application/json")
    @ResponseBody
    ResponseEntity<List<Long>> userTickets(@RequestParam(name = "user-id") Long id);
}
