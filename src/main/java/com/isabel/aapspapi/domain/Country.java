package com.isabel.aapspapi.domain;

import com.google.gson.annotations.SerializedName;
import lombok.*;

/**
 * @author Isabel
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Country {

    private String name;
    private String capital;
    private String region;
    private int population;


    @Override
    public String toString(){
        return name + "   " + region;
    }

}


