package com.example.definex.taskmanagement.dto.mapper;

import com.example.definex.taskmanagement.dto.request.CreateUserRequest;
import com.example.definex.taskmanagement.dto.response.CreatedUserResponse;
import com.example.definex.taskmanagement.dto.response.UpdatedUserResponse;
import com.example.definex.taskmanagement.dto.response.UserResponse;
import com.example.definex.taskmanagement.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User createUserRequestToUser(CreateUserRequest createUserRequest);
    CreatedUserResponse userToCreatedUserResponse(User user);
    UserResponse userToUserResponse(User user);
    UpdatedUserResponse userToUpdatedUserResponse(User user);
}
