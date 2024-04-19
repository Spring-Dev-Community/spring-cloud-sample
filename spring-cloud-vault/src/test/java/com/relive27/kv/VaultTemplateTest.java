package com.relive27.kv;

import lombok.Data;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultResponseSupport;
import org.springframework.vault.support.Versioned;

import java.util.Collections;
import java.util.Map;

/**
 * 注意：
 * 1.如果不熟悉kv引擎API操作可以先查看官网
 * 2.operationSecretV2Test3方法中path请/data，operationSecretV2Test，
 * operationSecretV2Test2由于指定了kv版本，所以VaultTemplate底层已默认添加。
 * 3.operationSecretV2Test3方法write注意已{"data":""}格式写入，这也是为什么我在Secret类外层使用Map函数，而其他
 * operationSecretV2Test，operationSecretV2Test2由于指定了kv版本，所以VaultTemplate底层已默认添加。
 * 4.以上2，3点也是与kv1版本不同之处。
 *
 * @author: ReLive27
 * @date: 2022/12/14 22:05
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class VaultTemplateTest {

    @Autowired
    private VaultTemplate vaultTemplate;

    @Test
    public void operationSecretV2Test() {
        Secrets secrets = new Secrets();
        secrets.username = "hello";
        secrets.password = "world";
        //新增和更新
        vaultTemplate.opsForVersionedKeyValue(/**/"/secret/").put("myapp", secrets);
        //查找
        Versioned<Secrets> secretsVersioned = vaultTemplate.opsForVersionedKeyValue("/secret/").get("myapp", Secrets.class);
        Assert.assertEquals(secrets, secretsVersioned.getData());
        //删除
        vaultTemplate.opsForVersionedKeyValue("/secret/").delete("myapp");
    }

    @Test
    public void operationSecretV2Test2() {
        Secrets secrets = new Secrets();
        secrets.username = "hello";
        secrets.password = "world";
        //新增或更新
        vaultTemplate.opsForKeyValue("/secret/", VaultKeyValueOperationsSupport.KeyValueBackend.KV_2).put("myapp2", secrets);
        //查找
        VaultResponseSupport<Secrets> responseSupport = vaultTemplate.opsForKeyValue("/secret/", VaultKeyValueOperationsSupport.KeyValueBackend.KV_2).get("myapp2", Secrets.class);
        Assert.assertEquals(secrets, responseSupport.getData());
        //删除
        vaultTemplate.opsForKeyValue("/secret/", VaultKeyValueOperationsSupport.KeyValueBackend.KV_2).delete("myapp2");

    }

    @Test
    public void operationSecretV2Test3() {
        Secrets secrets = new Secrets();
        secrets.username = "hello";
        secrets.password = "world";
        //新增或更新
        vaultTemplate.write("/secret/data/myapp3", Collections.singletonMap("data", secrets));
        //查找
        VaultResponseSupport<Map> response = vaultTemplate.read("/secret/data/myapp3", Map.class);
        System.out.println(response.getData().get("data"));
        //删除
        vaultTemplate.delete("/secret/data/myapp3");
    }


    @Test
    public void operationSecretV1Test() {
        Secrets secrets = new Secrets();
        secrets.username = "hello";
        secrets.password = "world";
        vaultTemplate.opsForKeyValue("/kv/", VaultKeyValueOperationsSupport.KeyValueBackend.KV_1).put("myapp5", secrets);
        VaultResponse response = vaultTemplate.opsForKeyValue("/kv/", VaultKeyValueOperationsSupport.KeyValueBackend.KV_1).get("myapp5");
        System.out.println(response.getData());
        vaultTemplate.delete("/kv/myapp5");
    }

    @Data
    public static class Secrets {

        String username;
        String password;

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}
