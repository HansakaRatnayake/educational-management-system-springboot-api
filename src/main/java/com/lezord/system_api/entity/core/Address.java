package com.lezord.system_api.entity.core;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Column(name = "street", length = 100)
    private String street;

    @Column(name = "city", length = 25)
    private String city;

    @Column(name = "district", length = 25)
    private String district;

    @Column(name = "province", length = 25)
    private String province;

    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @Column(name = "country", length = 50)
    private String country;
}
