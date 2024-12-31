package com.github.zoltar238.PrintStainServer.controller;

import com.github.zoltar238.PrintStainServer.dto.ResponsesEnum;
import com.github.zoltar238.PrintStainServer.dto.SaleDto;
import com.github.zoltar238.PrintStainServer.persistence.entity.ItemEntity;
import com.github.zoltar238.PrintStainServer.persistence.entity.SaleEntity;
import com.github.zoltar238.PrintStainServer.persistence.repository.ItemRepository;
import com.github.zoltar238.PrintStainServer.persistence.repository.SaleRepository;
import com.github.zoltar238.PrintStainServer.utils.ResponseBuilder;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@Slf4j
@RequestMapping(value = "/sale")
public class SaleController {

    @Autowired
    private SaleRepository saleRepository;
    @Autowired
    private ItemRepository itemRepository;

    @PostMapping("/newSale")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createNewSale(@Valid @RequestBody SaleDto saleDto) {
        // Find item by id
        Optional<ItemEntity> item = itemRepository.findById(saleDto.getItemId());

        // Check if item exists
        if (item.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseBuilder.buildResponse(false,
                            ResponsesEnum.SALE_ITEM_NOT_FOUND.toString(),
                            "Item with ID " + saleDto.getItemId() + " associated with sale not found"));
        }

        // Validate cost and price
        if (saleDto.getCost() == null || saleDto.getPrice() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseBuilder.buildResponse(false,
                            ResponsesEnum.BAD_REQUEST.toString(),
                            "Cost and Price are required and must be valid values"));
        }

        if (saleDto.getCost().compareTo(saleDto.getPrice()) > 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseBuilder.buildResponse(false,
                            ResponsesEnum.BAD_REQUEST.toString(),
                            "Cost cannot be greater than Price"));
        }

        try {
            // Create new sale and save to database
            SaleEntity saleEntity = SaleEntity.builder()
                    .cost(saleDto.getCost())
                    .price(saleDto.getPrice())
                    .date(saleDto.getDate())
                    .item(item.get())
                    .build();

            saleRepository.save(saleEntity);

            // Return success response
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseBuilder.buildResponse(true,
                            ResponsesEnum.CREATED.toString(),
                            "Sale created successfully"));
        } catch (Exception e) {
            // Handle unexpected exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseBuilder.buildResponse(false,
                            ResponsesEnum.UNEXPECTED_ERROR.toString(),
                            "An unexpected error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/getAllSales")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllSales() {
        Iterable<SaleEntity> sales = saleRepository.findAll();

        return null;
    }
}
