package com.ekartapplication.ekart.service.testing;

import com.ekartapplication.ekart.dao.UserDAO;
import com.ekartapplication.ekart.exception.exceptions.EntityNotFoundException;
import com.ekartapplication.ekart.repository.testing.TestMessageRepo;
import com.ekartapplication.ekart.repository.user.UserRepository;
import org.aspectj.weaver.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TestMessageServiceImpl implements TestMessageService {

    @Autowired
    private TestMessageRepo testMessage;

    @Autowired
    UserRepository repository;


    @Override
    public List<UserDAO> getMessage() {

        List<UserDAO> users = repository.getUsers();

        return users.stream().sorted(Comparator.comparing(UserDAO::getFullName)).collect(Collectors.toList());

        //throw new EntityNotFoundException("Not Found FYI");
    }
}
