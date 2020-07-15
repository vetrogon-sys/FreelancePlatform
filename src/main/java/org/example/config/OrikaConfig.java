package org.example.config;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.example.dto.*;
import org.example.entity.*;

public class OrikaConfig {
    private static final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    private static MapperFactory getMapperFactory() {
        mapperFactory.classMap(Employer.class, UserProfileDto.class)
                .byDefault()
                .register();

        mapperFactory.classMap(Freelancer.class, UserProfileDto.class)
                .byDefault()
                .register();

        mapperFactory.classMap(Employer.class, UserDto.class)
                .byDefault()
                .register();

        mapperFactory.classMap(Freelancer.class, UserDto.class)
                .byDefault()
                .register();

        mapperFactory.classMap(Review.class, ReviewDto.class)
                .byDefault()
                .register();

        mapperFactory.classMap(Job.class, JobDto.class)
                .byDefault()
                .register();

        mapperFactory.classMap(Offer.class, OfferDto.class)
                .byDefault()
                .register();

        return mapperFactory;
    }

    public static MapperFacade getMapperFacade() {
        return getMapperFactory().getMapperFacade();
    }
}
