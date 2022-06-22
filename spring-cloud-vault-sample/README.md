## Spring-Cloud-Vault-Sample

---

This module contains articles about Spring Cloud Vault.

---
### Relevant information:

#### Docker安装Vault

具体二进制文件安装教程参考[官网](https://learn.hashicorp.com/tutorials/vault/getting-started-install?in=vault/getting-started)

docker安装命令
```
docker pull vault:latest

//仅适用在开发环境下运行
docker run --cap-add=IPC_LOCK -d --name=dev-vault vault
```
更多详情参考[https://registry.hub.docker.com/_/vault](https://registry.hub.docker.com/_/vault)

启动成功会看到与上述类似的输出：
```

             Api Address: http://127.0.0.1:8200
                     Cgo: disabled
         Cluster Address: https://127.0.0.1:8201
              Listener 1: tcp (addr: "127.0.0.1:8200", cluster address: "127.0.0.1:8201", max_request_duration: "1m30s", max_request_size: "33554432", tls: "disabled")
               Log Level: info
                   Mlock: supported: false, enabled: false
           Recovery Mode: false
                 Storage: inmem
                 Version: Vault v1.4.1

WARNING! dev mode is enabled! In this mode, Vault runs entirely in-memory
and starts unsealed with a single unseal key. The root token is already
authenticated to the CLI, so you can immediately begin using Vault.

You may need to set the following environment variable:

    $ export VAULT_ADDR='http://127.0.0.1:8200'

The unseal key and root token are displayed below in case you want to
seal/unseal the Vault or re-authenticate.

Unseal Key: 1+yv+v5mz+aSCK67X6slL3ECxb4UDL8ujWZU/ONBpn0=
Root Token: s.XmpNPoi9sRhYtdKHaQhkHP6x

Development mode should NOT be used in production installations!
```


启动开发服务器后，执行以下操作:
- 启动一个新的终端会话。

- `export VAULT_ADDR ...`从终端输出复制并运行命令。这会将 Vault 客户端配置为与开发服务器通信。
    ```
    export VAULT_ADDR='http://127.0.0.1:8200'
    ```

- 将开封密钥保存在某处。不要担心如何安全地保存它。现在，只需将其保存在任何地方。
- 将环境变量值设置为终端输出中显示VAULT_TOKEN的生成的 Root Token值。
    ```
    export VAULT_TOKEN="s.XmpNPoi9sRhYtdKHaQhkHP6x"
    ```
  要与 Vault 交互，您必须提供有效的令牌。设置此环境变量是一种通过 CLI 向 Vault 提供令牌的方法。稍后，在身份验证教程中，您将学习使用该vault login <token_value>命令对 Vault 进行身份验证。


验证服务器是否正在运行

```
vault status
```
如果出现一下错误：
```
Error checking seal status: Get "https://127.0.0.1:8200/v1/sys/seal-status": http: server gave HTTP response to HTTPS client
```
检查是否配置`export VAULT_ADDR ...`环境变量


其他详细信息请参考[官网](https://learn.hashicorp.com/tutorials/vault/getting-started-dev-server?in=vault/getting-started)

### Relevant Articles:
- [spring-cloud-vault认证方式]()
- [spring-vault KV秘密后端]()