package com.github.zoltar238.PrintStainServer.controller;

import com.github.zoltar238.PrintStainServer.dto.ResponsesEnum;
import com.github.zoltar238.PrintStainServer.dto.ItemDto;
import com.github.zoltar238.PrintStainServer.persistence.entity.ImageEntity;
import com.github.zoltar238.PrintStainServer.persistence.entity.ItemEntity;
import com.github.zoltar238.PrintStainServer.persistence.repository.ItemRepository;
import com.github.zoltar238.PrintStainServer.service.ImageTransformerService;
import com.github.zoltar238.PrintStainServer.utils.ResponseBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(value = "/item")
public class ItemController {

    @Autowired
    ImageTransformerService imageTransformerService;

    private final ItemRepository itemRepository;

    public ItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping("/getAllItems")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllItems() {
        try {

            // Get all items from the database
            List<ItemEntity> items = (List<ItemEntity>) itemRepository.findAll();

            // Create a dto list with each item
            List<ItemDto> itemsDto = new ArrayList<>();
            for (ItemEntity item : items) {

                List<String> base64Images = new ArrayList<>();
                // Process each image in the item and transform it to base64 format
                for (ImageEntity image : item.getImages()) {
                    try {
                        base64Images.add(imageTransformerService.transformImageToBase64(image.getUrl()));
                    } catch (IOException e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ResponseBuilder.buildResponse(true, "Unexpected error processing images", null));
                    }
                }

                // Add item to the list to be transferred
                ItemDto itemDTO = ItemDto.builder()
                        .itemId(item.getItemId())
                        .name(item.getName())
                        .base64Images(base64Images)
                        .build();
                itemsDto.add(itemDTO);
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ResponseBuilder.buildResponse(true, "Items retrieved successfully", itemsDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseBuilder.buildResponse(true, "Unexpected error", null));
        }
    }
}
