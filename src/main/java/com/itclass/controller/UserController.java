package com.itclass.controller;

import com.itclass.dto.RestResponse;
import com.itclass.dto.UserDto;
import com.itclass.dto.UserParamsDto;
import com.itclass.entity.User;
import com.itclass.service.UserService;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    @GetMapping("/{id}}")
    public RestResponse getCurrentUser(@PathVariable Long id) {
        User currentUser = userService.getUserFromSecurityContext();
        if (currentUser.getId().equals(id)) {
            mapperFactory.classMap(User.class, UserDto.class);
            MapperFacade mapper = mapperFactory.getMapperFacade();
            UserDto userDto = mapper.map(currentUser, UserDto.class);
            return RestResponse.builder()
                    .isSuccess(true)
                    .response(userDto)
                    .build();
        }
        return RestResponse.builder()
                .isSuccess(false)
                .response("isn't logged users")
                .build();
    }

    @PutMapping("/{id}")
    public RestResponse updateUser(@RequestBody UserParamsDto userParamsDto, @PathVariable Long id) {
        User currentUser = userService.getUserFromSecurityContext();
        if (currentUser.getId().equals(id)) {
            if (userService.update(currentUser, userParamsDto)) {
                return RestResponse.builder()
                        .isSuccess(true)
                        .response("Vse ok")
                        .build();
            } else {
                return RestResponse.builder()
                        .isSuccess(false)
                        .response("Что-то пошло нетак")
                        .build();
            }
        }
        return RestResponse.builder()
                .isSuccess(false)
                .response("Aаttempt to change another user")
                .build();
    }

}
