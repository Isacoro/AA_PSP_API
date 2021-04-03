package com.isabel.aapspapi.domain;

import lombok.*;
import lombok.experimental.FieldNameConstants;

/**
 * @author Isabel
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Country {

    private String name;
    private String capital;
    private String region;
    private int population;
    private String flag;
    private String subregion;


    //ToString para la lista de todos los países
    @Override
    public String toString() {
        return name;
    }

}


