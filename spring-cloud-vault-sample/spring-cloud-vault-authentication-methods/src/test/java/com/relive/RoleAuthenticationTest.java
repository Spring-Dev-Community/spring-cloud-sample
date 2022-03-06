package com.relive;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: ReLive
 * @date: 2022/2/28 9:54 下午
 */
@SpringBootTest(classes = SpringCloudVaultApplication.class)
@RunWith(SpringRunner.class)
public class RoleAuthenticationTest {

    @Autowired
    Environment env;

    @Test
    public void whenRoleAuthenticationGetEnv() {
        Assert.assertEquals(env.getProperty("foo"), "helloword");
        Assert.assertEquals(env.getProperty("name"), "relive");
    }
}
