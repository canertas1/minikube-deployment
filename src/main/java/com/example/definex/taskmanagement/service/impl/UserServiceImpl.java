package com.example.definex.taskmanagement.service.impl;

import com.example.definex.taskmanagement.dto.request.CreateUserRequest;
import com.example.definex.taskmanagement.dto.request.UpdateUserRequest;
import com.example.definex.taskmanagement.dto.response.CreatedUserResponse;
import com.example.definex.taskmanagement.dto.response.UpdatedUserResponse;
import com.example.definex.taskmanagement.dto.response.UserResponse;
import com.example.definex.taskmanagement.dto.mapper.UserMapper;
import com.example.definex.taskmanagement.entities.Department;
import com.example.definex.taskmanagement.entities.User;
import com.example.definex.taskmanagement.exception.DepartmentNotFoundException;
import com.example.definex.taskmanagement.exception.UserNotFoundException;
import com.example.definex.taskmanagement.exception.constants.MessageKey;
import com.example.definex.taskmanagement.repository.DepartmentRepository;
import com.example.definex.taskmanagement.repository.UserRepository;
import com.example.definex.taskmanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
   private final UserRepository userRepository;
   private final UserMapper userMapper;
   private final DepartmentRepository departmentRepository;

   @Override
  public CreatedUserResponse save(CreateUserRequest createUserRequest){
     User createdUser = userMapper.createUserRequestToUser(createUserRequest);
     return userMapper.userToCreatedUserResponse(userRepository.save(createdUser));
   }
   @Override
   public UpdatedUserResponse update(UpdateUserRequest updateUserRequest,Long userId){

       User user = userRepository.findById(userId).orElseThrow(()->new UserNotFoundException(MessageKey.USER_NOT_FOUND_WITH_ID.toString()+userId));
       Department department = departmentRepository.findById(updateUserRequest.getDepartmentId()).orElseThrow(()->new DepartmentNotFoundException(MessageKey.DEPARTMENT_NOT_FOUND_WITH_ID.getMessage()));

       user.setName(updateUserRequest.getName());
       user.setEmail(updateUserRequest.getEmail());
       user.setRole(updateUserRequest.getRole());
       user.setDepartment(department);

       return userMapper.userToUpdatedUserResponse(userRepository.save(user));
   }
   @Override
   public UserResponse getById(Long id){
      User user = userRepository.findById(id).orElseThrow(()->new UserNotFoundException(MessageKey.USER_NOT_FOUND_WITH_ID.toString()+id));
      return userMapper.userToUserResponse(user);
   }
   @Override
   public void deleteById(Long id){
      User user = userRepository.findById(id).orElseThrow(()->new UserNotFoundException(MessageKey.USER_NOT_FOUND_WITH_ID.toString()+id));
      user.setIsDeleted(true);
      userRepository.save(user);
   }
   @Override
   public Page<UserResponse> findAll(Pageable pageable){
       return userRepository.findAll(pageable)
               .map(userMapper::userToUserResponse);
   }
}
