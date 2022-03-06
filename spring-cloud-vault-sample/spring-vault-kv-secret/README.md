# Spring Cloud Vault kv秘密后端

## Generic 秘密后端
在认证方式一节中，我们已经初步认识到Generic Backend，下面我们在详细介绍下它的其他参数。
*注意此后端即将被启用，我们可以简单的了解，取而代之的是kv后端*。

spring cloud vault对于Generic后端的属性配置包括：
```
spring: 
  cloud: 
    vault:
      generic:
        enabled: true
        backend: secret
        profile-separator: '/'
        default-context: application
        application-name: my-app
```
- `enabled`将此值设置为false禁用秘密后端配置使用,默认为true
- `backend`设置要使用的秘密挂载的路径
- `default-context`设置所有应用程序使用的上下文名称
- `application-name`覆盖用于键值后端的应用程序名称
- `profile-separator`使用配置文件将配置文件名称与属性源中的上下文分开

而读取vault上下文路径由应用程序名称或默认上下文名称和活动配置文件以下方式决定：
```
/secret/{application}/{profile} 
/secret/{application} 
/secret/{default-context}/{profile} 
/secret/{default-context}
```
在`{application}`选择时优先级：`spring.cloud.vault.generic.application-name`>`spring.cloud.vault.application-name`>`spring.application.name`；
而`{default-context}`用于设置所有应用程序使用的上下文名称，也就是说在实际场景中我们通用秘密将存放于`{default-context}`所指定的路径中，对于适用于单个应用的秘密存放于`{application}`所指定的路径中。

## KV秘密后端
前面说的Generic 后端对应Vault的kv引擎版本1，er此kv后端对应vault的kv引擎版本2，kv秘密后端主要配置也和generic一样，这里就不再赘述了。
```
spring: 
  cloud: 
    vault:
      generic:
        enabled: true
        backend: secret
        profile-separator: '/'
        default-context: application
        application-name: my-app
```
在认证方式小节中我们介绍了当我们启用秘密引擎后可以通过`Environment`获取到秘密信息。但是如果我们想要操作秘密信息呢，下面介绍Spring 封装好的VaultTemplate:
```
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
```
以上示例操作为kv版本2，kv版本1及其他方式可以查看VaultTemplateTest。