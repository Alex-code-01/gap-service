package com.txacon.gap.domain.customer.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(of = {"countryPrefix", "phoneNumber"})
@ToString
public class Phone {

    private Long id;
    private String countryPrefix;
    private String phoneNumber;
}
