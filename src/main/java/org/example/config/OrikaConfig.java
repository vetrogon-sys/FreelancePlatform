package org.example.config;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.example.dto.*;
import org.example.entity.*;

public class OrikaConfig {
    private static final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    public static MapperFactory getMapperFactory() {
        mapperFactory.classMap(Employer.class, UserDto.class)
                .byDefault()
                .register();

        mapperFactory.classMap(Freelancer.class, UserDto.class)
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
}
