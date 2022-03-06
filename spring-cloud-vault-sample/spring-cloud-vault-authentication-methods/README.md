# spring-cloud-vault-authentication-method

## 认证方式
不同的组织对安全性和身份验证有不同的要求。Vault 通过提供多种身份验证方法来反映这种需求。这里主要介绍几个常用认证方式

## 令牌认证
令牌是 Vault 中进行身份验证的核心方法。令牌身份验证需要使用配置提供静态令牌。令牌可以指定值也可以有Vault启动时生成。
```
spring:
  cloud:
    vault:
      authentication: token
      token: "0000-00000-00000"
```
令牌认证是默认的认证方法。但是存在一定风险，如果令牌被泄露，攻击方方将获得对 Vault 的访问权限，并可以访问预期客户的机密。

## 应用角色认证
在用户或机器可以进行身份验证之前，必须提前启用应用角色认证，启用角色认证可以使用CLI或者API接口启用，当然更简单的方式是通过UI界面启用。

**通过CLI方式：**
```
1.启用 AppRole 身份验证方法
vault auth enable approle

2.创建命名角色,角色名定义为my-role：
vault write auth/approle/role/my-role \
    secret_id_ttl=10m \ 
    token_num_uses=10 \
    token_ttl=20m \
    token_max_ttl=30m \
    secret_id_num_uses=40

```
参数 | 说明
---|---
secret_id_ttl | 任何 SecretID 过期的持续时间（秒）
token_num_uses | 生成的令牌可以使用的最大次数（在其生命周期内）；0 表示无限制
token_ttl | 生成令牌的增量生命周期
token_max_ttl | 生成令牌的最长生命周期
secret_id_num_uses | SecretID 可用于从该 AppRole 获取令牌的次数;0 表示无限制
更多创建/更新 AppRole参数说明请参考[官网API文档](https://www.vaultproject.io/api-docs/auth/approle#create-update-approle)

```
3. 获取 AppRole 的 RoleID：
vault read auth/approle/role/my-role/role-id

4.获取针对 AppRole 颁发的 SecretID：
vault write -f auth/approle/role/my-role/secret-id
```

**通过API方式：**<br>

1.启用 AppRole 身份验证方法：
```
curl \
    --header "X-Vault-Token: 000-0000-00000" \
    --request POST \
    --data '{"type": "approle"}' \
    http://127.0.0.1:8200/v1/sys/auth/approle

```
2.创建具有所需策略集的 AppRole：

首先创建payload.json文件，并将参数写入文件，这里指定策略集为默认`default`,策略我们放在后面讲，可以把策略当作权限。
```
{
  "token_ttl": "10m",
  "token_max_ttl": "15m",
  "token_policies": ["default"],
  "token_period": 0,
  "bind_secret_id": true
}
```
创建角色名为my-role，并指定参数payload文件
```
curl \
    --header "X-Vault-Token: 000-00000-00000" \
    --request POST \
    --data @payload.json \
    http://127.0.0.1:8200/v1/auth/approle/role/my-role

```
当然也可以将参数直接跟在data后面,如下：
```
curl \
    --header "X-Vault-Token: 0000-0000-0000" \
    --request POST \
    --data '{"token_policies": ["default"]}' \
    http://127.0.0.1:8200/v1/auth/approle/role/my-role

```

3. 获取 AppRole 的 RoleID：
```
curl \
    --header "X-Vault-Token: 000-0000-00000" \
    http://127.0.0.1:8200/v1/auth/approle/role/my-role/role-id
```
响应如下：
```
{
    "request_id":"be14da79-762a-5e2c-3a9f-39b2a38bc81a",
    "lease_id":"",
    "renewable":false,
    "lease_duration":0,
    "data":{
        "role_id":"0000-000000-0000000"
    },
    "wrap_info":null,
    "warnings":null,
    "auth":null
}
```

4.获取针对 AppRole 颁发的 SecretID：
```
curl \
    --header "X-Vault-Token: 0000-0000-00000" \
    --request POST \
     http://127.0.0.1:8200/v1/auth/approle/role/my-role/secret-id
```
响应如下：
```
{
    "request_id":"e39de134-ffc5-cedb-c129-826bfa54281c",
    "lease_id":"",
    "renewable":false,
    "lease_duration":0,
    "data":{
        "secret_id":"cdd92930-374a-7d6a-d9df-407ab41fae74",
        "secret_id_accessor":"45825c44-bd3a-1d3d-aec4-47c5259ad307",
        "secret_id_ttl":0
    },
    "wrap_info":null,
    "warnings":null,
    "auth":null
}
```
当然我们同样可以使用API更新，删除，查询角色，不过这些不再讨论范围内，可以自行查看[官网API文档](https://www.vaultproject.io/api-docs/auth/approle)

### 配置Spring Vault
当我们已经获取到role-id和secret-id时，在bootstrap.yml文件进行配置
```
spring:
  cloud:
    vault:
      app-role:
        role-id: 3e27fa28-e7fd-4eec-6b24-f9269baee603
        secret-id: 5b2e7518-d517-1b61-0f04-960554f83851
        role: my-role
        app-role-path: approle
```
Spring Vault 支持各种 AppRole 场景（推/拉模式和包装）,并可以结合角色名和token形成不同组合使用，具体组合方式可以参考[Spring Vault](https://docs.spring.io/spring-cloud-vault/docs/2.2.7.RELEASE/reference/html/#_approle_authentication) ;其中`secret_id`可以省略，但是请注意在此案例中`secret_id`是不可以省略的，原因是我们在配置角色时`bind_secret_id`参数设置为 true。

### 启动Spring Vault
如果你完成上述配置并启动工程，发现工程失败，此时你可能忽略了一个问题，Spring官网提到：

> Spring Cloud Vault 默认情况下，启用秘密后端（generic），它通过 JSON 端点访问秘密配置设置。
> HTTP 服务具有以下形式的资源：
>   ```
> /secret/{application}/{profile} 
> /secret/{application}
> /secret/{defaultContext}/{profile}
> /secret/{defaultContext}
> ```
>其中“application”作为spring.application.name,"profile"作为活动的配置文件。

`defaultContext`可以通过以下配置指定：
```
spring:
  cloud:
    vault:
      generic:
        default-context: test
```

结合上述说明，我们可以开启debug日志查看，日志中有以下输出：
```
2022-02-28 20:55:17.780 DEBUG 18116 --- [           main] org.apache.http.headers                  : http-outgoing-0 >> GET /v1/secret/data/relive/dev HTTP/1.1
2022-02-28 20:55:17.780 DEBUG 18116 --- [           main] org.apache.http.headers                  : http-outgoing-0 >> Accept: application/json, application/*+json
2022-02-28 20:55:17.780 DEBUG 18116 --- [           main] org.apache.http.headers                  : http-outgoing-0 >> X-Vault-Token: s.0horFZ8YY7mEsUA4gJML39rH
2022-02-28 20:55:17.780 DEBUG 18116 --- [           main] org.apache.http.headers                  : http-outgoing-0 >> Host: 127.0.0.1:8200
2022-02-28 20:55:17.780 DEBUG 18116 --- [           main] org.apache.http.headers                  : http-outgoing-0 >> Connection: Keep-Alive
2022-02-28 20:55:17.780 DEBUG 18116 --- [           main] org.apache.http.headers                  : http-outgoing-0 >> User-Agent: Apache-HttpClient/4.5.13 
```
可以看出在通过role登录拿到token令牌后请求`/v1/secret/data/relive/dev`接口，所以我们工程为什么启动报错呢? 那是因为在我们创建角色时`token_policies`指定的时default，该策略并没有访问/secret/data/*权限，所以我们可以更新角色`token_policies`.

1.首先我们先给vault添加新police,写入文件my-police.json
```
{
  "policy": "path \"secret/data/*\" {capabilities = [\"create\", \"update\",\"read\"]}"
}
```

```
curl \
    --header "X-Vault-Token: 000-0000-00000" \
    --request POST \
    --data @my-police.json \
    http://127.0.0.1:8200/v1/sys/policy/my-police
```

2.更新角色,添加my-police策略,写入police.json文件中

```
{
  "token_ttl": "10m",
  "token_max_ttl": "15m",
  "token_policies": ["default","my-police"],
  "token_period": 0,
  "bind_secret_id": true
}
```

```
curl \
    --header "X-Vault-Token: 000-00000-00000" \
    --request POST \
    --data @payload.json \
    http://127.0.0.1:8200/v1/auth/approle/role/my-role

```

此时我们在启动spring 工程，启动成功！！！ 我们现在可以将存储在`/secret/data/relive/dev` 中的所有键/值对用作任何其他Environment属性。以下片段显示了我们如何读取存储在此路径下的foo键的值：
```
@Autowired 
Environment env;

public String getFoo() {
    return env.getProperty("foo");
}
```
当然如果你并不想使用Vault添加你的Environment属性，我们也可以通过以下配置来禁用：
```
spring:
  cloud:
    vault:
      generic:
        enable: false
```


## TLS证书认证
