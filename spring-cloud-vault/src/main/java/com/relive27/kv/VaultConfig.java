package com.relive27.kv;

import org.springframework.context.annotation.Configuration;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;

import java.net.URI;

/**
 * @author: ReLive27
 * @date: 2022/12/14 22:05
 */
@Configuration
public class VaultConfig extends AbstractVaultConfiguration {
    @Override
    public VaultEndpoint vaultEndpoint() {
        return VaultEndpoint.from(URI.create("http://127.0.0.1:8200"));
    }

    @Override
    public ClientAuthentication clientAuthentication() {
        return new TokenAuthentication("0000-0000-0000");
    }
}
