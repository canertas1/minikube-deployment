package com.example.definex.taskmanagement.service.impl;

import com.example.definex.taskmanagement.dto.mapper.DepartmentMapper;
import com.example.definex.taskmanagement.dto.request.CreateDepartmentRequest;
import com.example.definex.taskmanagement.dto.response.CreatedDepartmentResponse;
import com.example.definex.taskmanagement.dto.response.DepartmentResponse;
import com.example.definex.taskmanagement.entities.Department;
import com.example.definex.taskmanagement.exception.DepartmentNotFoundException;
import com.example.definex.taskmanagement.exception.DepartmentValidationException;
import com.example.definex.taskmanagement.exception.constants.MessageKey;
import com.example.definex.taskmanagement.repository.DepartmentRepository;
import com.example.definex.taskmanagement.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    @Override
    public CreatedDepartmentResponse save(CreateDepartmentRequest createDepartmentRequest){
        if (createDepartmentRequest.getName() == null || createDepartmentRequest.getName().trim().isEmpty()) {
            throw new DepartmentValidationException(MessageKey.DEPARTMENT_NAME_CANNOT_BE_EMPTY.toString());
        }
        Department department = departmentMapper.createDepartmentRequestToDepartment(createDepartmentRequest);

        return  departmentMapper.departmentToCreatedDepartmentResponse(departmentRepository.save(department));
    }
    @Override
    public DepartmentResponse findById(Long id) {
      Department department = departmentRepository.findById(id).orElseThrow(()->new DepartmentNotFoundException(MessageKey.DEPARTMENT_NOT_FOUND_WITH_ID.toString()));

        return departmentMapper.departmentToDepartmentResponse(department);
    }
    @Override
    public Page<DepartmentResponse> findAll(Pageable pageable) {
        return departmentRepository.findAll(pageable)
                .map(departmentMapper::departmentToDepartmentResponse);
    }
    @Override
    public void deleteById(Long id){
        Department department = departmentRepository.findById(id).orElseThrow(()->new DepartmentNotFoundException(MessageKey.DEPARTMENT_NOT_FOUND_WITH_ID.toString()+id));
        department.setIsDeleted(true);
        departmentRepository.save(department);
    }
}