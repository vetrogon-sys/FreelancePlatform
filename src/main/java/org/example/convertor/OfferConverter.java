package org.example.convertor;

import ma.glasnost.orika.CustomMapper;
import org.example.dto.OfferDto;
import org.example.entity.Offer;
import org.springframework.stereotype.Component;

@Component
public class OfferConverter extends CustomMapper<Offer, OfferDto> {
}
