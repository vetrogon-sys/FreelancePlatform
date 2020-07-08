package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.FilterRequestDto;
import org.example.dto.RestResponse;
import org.example.dto.UserParamsDto;
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
        try {
            return RestResponse.generateSuccessfulResponse(userService.getDtoById(id));
        } catch (FailedRequestError error) {
            return RestResponse.generateFailedResponse(error.getMessage());
        }
    }

    @GetMapping("/freelancers")
    public RestResponse getFreelancers(@RequestBody FilterRequestDto filterRequestDto) {
        return RestResponse.generateSuccessfulResponse(userService.getFreelancerDtoList(filterRequestDto));
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
