package com.mhealth.admin.service;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.dto.PermissionDto;
import com.mhealth.admin.dto.enums.UserType;
import com.mhealth.admin.dto.request.PermissionRoleRequest;
import com.mhealth.admin.dto.response.PermissionRoleDto;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.dto.response.RolesResponseDto;
import com.mhealth.admin.exception.AdminModuleExceptionHandler;
import com.mhealth.admin.model.Permission;
import com.mhealth.admin.model.PermissionRole;
import com.mhealth.admin.repository.PermissionRepository;
import com.mhealth.admin.repository.PermissionRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PermissionRoleService {

    @Autowired
    private PermissionRoleRepository permissionRoleRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private PermissionRepository permissionRepository;

    public ResponseEntity<Response> addPermissionRole(PermissionRoleRequest request, Locale locale) {

        boolean isFound = validateProvidedPermissions(request.getPermissions());
        if (!isFound) throw new AdminModuleExceptionHandler(messageSource.getMessage(Constants.PERMISSION_NOT_FOUND,null,locale));

        PermissionRole permissionRole = new PermissionRole();
        permissionRole.setRoleType(request.getRoleType());
        permissionRole.setPermissions(request.getPermissions());
        permissionRoleRepository.save(permissionRole);
        return ResponseEntity.ok(new Response(
                Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.PERMISSION_ROLE_ADDED,null,locale), permissionRole));
    }

    private boolean validateProvidedPermissions(String permissions) {
        boolean isValid = true;
        String[] permissionIds = permissions.split(",");
        for (String permissionId : permissionIds) {
            if (!permissionRepository.existsById(Integer.parseInt(permissionId))) {
                isValid = false;
                break;
            }
        }
        return isValid;
    }

    public ResponseEntity<Response> updatePermissionRole(Integer id, PermissionRoleRequest request, Locale locale) {
        PermissionRole permissionRole = permissionRoleRepository.findById(id).orElse(null);
        if (permissionRole == null) {
            return ResponseEntity.status(404).body(new Response(
                    Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                    messageSource.getMessage(Constants.PERMISSION_ROLE_NOT_FOUND,null,locale)));
        }

        permissionRole.setRoleType(request.getRoleType());
        permissionRole.setPermissions(request.getPermissions());
        permissionRoleRepository.save(permissionRole);

        return ResponseEntity.ok(new Response(
                Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.PERMISSION_ROLE_UPDATED,null,locale),
                permissionRole));    }

    public ResponseEntity<Response> fetchPermissionRoles(Locale locale) {
        List<PermissionRoleDto> respose = new ArrayList<>();
        List<PermissionRole> roles = permissionRoleRepository.findAll();
        for(PermissionRole role:roles){

            List<String> rolesStringIds = Arrays.asList(role.getPermissions().split(","));
            List<Integer> ids = new ArrayList<>();
            for(String s:rolesStringIds){
                ids.add(Integer.parseInt(s));
            }
            List<Permission> permissionList = new ArrayList<>();
            if(!ids.isEmpty()){
                permissionList = permissionRepository.findByIds(ids);
            }

            List<Permission> level0 = new ArrayList<>();
            List<Permission> level1 = new ArrayList<>();
            List<Permission> level2 = new ArrayList<>();

            for(Permission p:permissionList){
                if(p.getLevel()==0){
                    level0.add(p);
                }else if(p.getLevel()==1){
                    level1.add(p);
                } else if (p.getLevel()==2) {
                    level2.add(p);
                }
            }

            List<PermissionDto> data = getTreeStructure(level0,level1,level2);
            respose.add(new PermissionRoleDto(data,role));
        }

        return ResponseEntity.ok(new Response(
                Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.PERMISSION_ROLE_FETCHED,null,locale),
                roles));
    }

    public List<PermissionDto> getTreeStructure(
            List<Permission> firstLevel,List<Permission>second,List<Permission>third){
        List<PermissionDto> response = new ArrayList<>();
        for(Permission p:firstLevel){
            response.add(new PermissionDto(p));
        }
        for(PermissionDto dto :response){
            for(Permission p:second){
                if(p.getParent()!= null && p.getParent().getId()==dto.getId()){
                    dto.getSubPermission().add(new PermissionDto(p));
                }
            }
        }
        for(PermissionDto external :response){
            for(PermissionDto internal :external.getSubPermission()){
                for(Permission p:third){
                    if(p.getParent()!=null && p.getParent().getId()==internal.getId()){
                        internal.getSubPermission().add(new PermissionDto(p));
                    }
                }
            }
        }

        return response;
    }

    public Response getAllRoles(Locale locale) {
        List<PermissionRole> roleList = permissionRoleRepository.findAll();
        if(roleList.isEmpty()){
            return new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                    messageSource.getMessage(Constants.NO_RECORD_FOUND,null,locale));
        }
        List<RolesResponseDto> responseDtoList = mapRoleListToRoleResponseDto(roleList);

        return new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.PERMISSION_ROLE_FETCHED,null,locale), responseDtoList);
    }

    private List<RolesResponseDto> mapRoleListToRoleResponseDto(List<PermissionRole> roleList) {
        return roleList.stream().map(row-> new RolesResponseDto(row.getId(), row.getPermissions(), row.getRoleType().name())).toList();
    }

    public Response findByRole(Locale locale, String role) {
        if(!checkUserType(role)){
            return new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                    messageSource.getMessage(Constants.USER_TYPE_NOT_FOUND,null,locale));
        }
        PermissionRole roles = permissionRoleRepository.findByUserType(UserType.valueOf(role)).orElse(null);
        if(roles == null){
            return new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                    messageSource.getMessage(Constants.PERMISSION_ROLE_NOT_FOUND,null,locale));
        }
        List<String> permissions = null;
        List<Integer> list = Arrays.stream(roles.getPermissions().split(","))
                .map(Integer::parseInt)
                .toList();
        List<Permission> permissionList = permissionRepository.findByIds(list);

        if(permissionList.isEmpty()){
            return new Response(Status.FAILED, Constants.NO_RECORD_FOUND_CODE,
                    messageSource.getMessage(Constants.PERMISSION_NOT_FOUND,null,locale));
        }

        Map<String, List<String>> response = mapPermissionListIntoResponse(permissionList);

        return new Response(Status.SUCCESS, Constants.SUCCESS_CODE,
                messageSource.getMessage(Constants.PERMISSIONS_FETCHED,null,locale), response);
    }

    private Map<String, List<String>> mapPermissionListIntoResponse(List<Permission> permissionList) {
        Map<String, List<String>> response = new HashMap<>();
        permissionList.forEach(row -> {
            String key = "list" + row.getLevel();

            List<String> permissions = response.computeIfAbsent(key, k -> new ArrayList<>());

            permissions.add(row.getCode());
        });
        return response;
    }

    private boolean checkUserType(String role) {
        for(UserType values : UserType.values()){
            if(values.name().equals(role)) return true;
        }
        return false;
    }
}

