package com.study.restfulwebservice.user;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class UserController {
    private UserDaoService service;

    // 생성자를 통한 의존성 주입
    public UserController(UserDaoService service) {
        this.service = service;
    }

    @GetMapping("/users")
    public List<User> retrieveAllUsers() {
        return service.findAll();
    }

    @GetMapping("/users/{id}")
    public Resource<User> retrieveUser(@PathVariable int id) {
        User user = service.findOne(id);

        if (user == null) {
            throw new UserNotFoundException(String.format("ID[%s] not find", id));
        }

        //HATEOAS 전체 사용자 목록 uri 링크를 추가
        Resource<User> resource = new Resource<>(user);
        ControllerLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllUsers());
        resource.add(linkTo.withRel("all-users"));

        return resource;
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User savedUser =  service.save(user);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable int id) {
        User user = service.deleteById(id);

        if (user == null) {
            throw new UserNotFoundException(String.format("ID[%s] not find", id));
        }
    }

    @PutMapping("/users/{id}")
    public void modifyUser(@RequestBody User user, @PathVariable int id) {
        User oldUser = new User();
        if (user != null) {
            oldUser = service.updateById(id, user);
        }

        if (oldUser == null) {
            throw new UserNotFoundException(String.format("ID[%s] not find", id));
        }
    }

}
