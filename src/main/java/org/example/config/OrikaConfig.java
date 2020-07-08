package org.example.config;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.example.dto.FreelancerDto;
import org.example.dto.JobDto;
import org.example.dto.OfferDto;
import org.example.dto.UserDto;
import org.example.entity.Freelancer;
import org.example.entity.Job;
import org.example.entity.Offer;
import org.example.entity.User;

public class OrikaConfig {
    private static final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    public static MapperFactory getMapperFactory() {
        mapperFactory.classMap(User.class, UserDto.class)
                .byDefault()
                .register();

        mapperFactory.classMap(Freelancer.class, FreelancerDto.class)
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
