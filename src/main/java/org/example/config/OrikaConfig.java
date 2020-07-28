package org.example.config;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.example.dto.*;
import org.example.entity.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class OrikaConfig {
    private static final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    private static MapperFactory getMapperFactory() {
        mapperFactory.classMap(Employer.class, UserProfileDto.class)
                .field("login", "login")
                .field("name", "name")
                .field("surname", "surname")
                .field("createdOn", "createdOn")
                .field("jobs", "jobs")
                .field("reviews", "reviews")
                .customize(new CustomMapper<Employer, UserProfileDto>() {
                    @Override
                    public void mapAtoB(Employer a, UserProfileDto b, MappingContext mappingContext) {
                        try {
                            b.setImage(Files.readAllBytes(Paths.get(a.getImgSrc())));
                        } catch (IOException ignored) {
                        }
                    }
                })
//                .byDefault()
                .register();

        mapperFactory.classMap(Freelancer.class, UserProfileDto.class)
                .field("login", "login")
                .field("name", "name")
                .field("surname", "surname")
                .field("createdOn", "createdOn")
                .field("skills", "skills")
                .field("jobs", "jobs")
                .field("reviews", "reviews")
                .customize(new CustomMapper<Freelancer, UserProfileDto>() {
                    @Override
                    public void mapAtoB(Freelancer a, UserProfileDto b, MappingContext mappingContext) {
                        try {
                            b.setImage(Files.readAllBytes(Paths.get(a.getImgSrc())));
                        } catch (IOException ignored) {
                        }
                    }
                })
//                .byDefault()
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
