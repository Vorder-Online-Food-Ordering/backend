package com.example.backend.dto;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.util.List;

@Data
@Embeddable //co nay thi ben User moi het loi
public class RestaurantDto {
    private String title;

//    @Column(length = 1000)
    private List<String> images;

    private String description;
    private Long id;
}
