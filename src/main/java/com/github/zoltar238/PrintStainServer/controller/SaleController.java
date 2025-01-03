package com.github.zoltar238.PrintStainServer.controller;

import com.github.zoltar238.PrintStainServer.dto.GetAllSalesDto;
import com.github.zoltar238.PrintStainServer.dto.ResponsesEnum;
import com.github.zoltar238.PrintStainServer.dto.SaleCreationDto;
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

import java.util.ArrayList;
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
    public ResponseEntity<?> createNewSale(@Valid @RequestBody SaleCreationDto saleCreationDto) {
        // Find item by id
        Optional<ItemEntity> item = itemRepository.findById(saleCreationDto.getItemId());

        // Check if item exists
        if (item.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseBuilder.buildResponse(false,
                            ResponsesEnum.SALE_ITEM_NOT_FOUND.toString(),
                            "Item with ID " + saleCreationDto.getItemId() + " associated with sale not found"));
        }

        // Validate cost and price
        if (saleCreationDto.getCost() == null || saleCreationDto.getPrice() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseBuilder.buildResponse(false,
                            ResponsesEnum.BAD_REQUEST.toString(),
                            "Cost and Price are required and must be valid values"));
        }

        if (saleCreationDto.getCost().compareTo(saleCreationDto.getPrice()) > 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseBuilder.buildResponse(false,
                            ResponsesEnum.BAD_REQUEST.toString(),
                            "Cost cannot be greater than Price"));
        }

        try {
            // Create new sale and save to database
            SaleEntity saleEntity = SaleEntity.builder()
                    .cost(saleCreationDto.getCost())
                    .price(saleCreationDto.getPrice())
                    .date(saleCreationDto.getDate())
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
        // Retrieve all sales from database
        Iterable<SaleEntity> sales = saleRepository.findAll();

        // Return list of sale data to client
        ArrayList<SaleCreationDto> saleData = new ArrayList<SaleCreationDto>();

        sales.forEach(sale -> {
            GetAllSalesDto getAllSalesDto = GetAllSalesDto.builder()
                    .saleId(sale.getSaleId())
                    .cost(sale.getCost())
                    .price(sale.getPrice())
                    .date(sale.getDate())
                    .itemName(sale.getItem().getName())
                    .build();
        });

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseBuilder.buildResponse(true,
                        ResponsesEnum.OK.toString(),
                        saleData));
    }
}
