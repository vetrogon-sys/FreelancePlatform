package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.FilterRequestDto;
import org.example.dto.RestResponse;
import org.example.dto.UserParamsDto;
import org.example.exceptions.FailedRequestError;
import org.example.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("authenticated()")
    public RestResponse getUserByID(@PathVariable Long id) {
        try {
            return RestResponse.generateSuccessfulResponse(userService.getDtoById(id));
        } catch (FailedRequestError error) {
            return RestResponse.generateFailedResponse(error.getMessage());
        }
    }

    @GetMapping("/current")
    @PreAuthorize("authenticated()")
    public RestResponse getCurrentUser() {
        try {
            return RestResponse.generateSuccessfulResponse(userService.getCurrentDto());
        } catch (FailedRequestError error) {
            return RestResponse.generateFailedResponse(error.getMessage());
        }
    }

    @GetMapping("/freelancers")
    @PreAuthorize("hasRole('EMPLOYER')")
    public RestResponse getFreelancers(@RequestBody FilterRequestDto filterRequestDto,
                                       @PageableDefault(page = 0, size = 20)
                                       @SortDefault(sort = "createdOn", direction = Sort.Direction.ASC)

                                               Pageable pageable) {
        return RestResponse.generateSuccessfulResponse(userService.getFreelancerDtoByFilter(filterRequestDto, pageable));
    }

    @PutMapping("/current")
    @PreAuthorize("authenticated()")
    public RestResponse updateUser(@RequestBody UserParamsDto userParamsDto) {
        try {
            userService.update(userParamsDto);
            return RestResponse.generateSuccessfulResponse("Updated");
        } catch (FailedRequestError error) {
            return RestResponse.generateFailedResponse(error.getMessage());
        }
    }

}
