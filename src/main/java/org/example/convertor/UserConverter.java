package org.example.convertor;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.example.dto.UserDto;
import org.example.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserConverter extends CustomConverter<User, UserDto> {

    @Override
    public UserDto convert(User source, Type<? extends UserDto> destinationType, MappingContext mappingContext) {
        return null;
    }
}
