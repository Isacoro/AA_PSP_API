package com.isabel.aapspapi.domain;

import com.google.gson.annotations.SerializedName;
import lombok.*;

/**
 * @author Isabel
 */

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Country {

    private String name;
    private String country;
    private String capital;
    private String region;
    private String continent;
    private int population;
}
