package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.config.OrikaConfig;
import org.example.dto.RestResponse;
import org.example.dto.UserDto;
import org.example.dto.UserParamsDto;
import org.example.entity.User;
import org.example.exceptions.FailedRequestError;
import org.example.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public RestResponse getCurrentUser(@PathVariable Long id) {
        User currentUser = userService.getUserFromSecurityContext();
        if (currentUser.getId().equals(id)) {
            UserDto userDto;
            userDto = OrikaConfig.getMapperFactory()
                    .getMapperFacade()
                    .map(currentUser, UserDto.class);
            return RestResponse.generateSuccessfulResponse(userDto);

        }
        return RestResponse.generateFailedResponse("isn't logged users");
    }

    @PutMapping("/{id}")
    public RestResponse updateUser(@RequestBody UserParamsDto userParamsDto, @PathVariable Long id) {
        try {
            userService.update(userParamsDto, id);
            return RestResponse.generateSuccessfulResponse("Updated");
        } catch (FailedRequestError error) {
            return RestResponse.generateFailedResponse(error.getMessage());
        }
    }

}
