package com.relive27.csrf;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

/**
 * @author: ReLive27
 * @date: 2022/12/12 19:32
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CsrfTest {

    @Autowired
    ApplicationContext context;

    WebTestClient rest;

    @Before
    public void setup() {
        this.rest = WebTestClient
                .bindToApplicationContext(this.context)
                .apply(springSecurity())
                .configureClient()
                .filter(basicAuthentication())
                .build();
    }

    @Test
    public void getTest() {
        this.rest
                .mutateWith(csrf())
                .get()
                .uri("/test");
    }

    @Test
    public void postTest() {
        this.rest
                .mutateWith(csrf())
                .post()
                .uri("/test");
    }
}
