package com.luqi.xunwu;

import com.luqi.entity.User;
import com.luqi.repository.UserRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by luqi
 * 2018-05-06 11:48.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void findOn() {
        User user = userRepository.findOne(1L);
        Assert.assertEquals("waliwali", user.getName());
    }
}
