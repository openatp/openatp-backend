## 开放接口测试平台

该项目是一个通用的接口测试平台，实现接口请求并验证响应。

> 该仓库只是后端服务，需要配合前端项目使用。前端项目在[这里](https://github.com/openatp/openatp-web)。

该项目使用 Spring Boot、Spring Data Jpa 和 MySql 开发。

![]()

## 构建并启动应用

1. 初始化数据库

创建数据库和表的语句在 `/docs/sql/<version>.sql` 文件中，请代码使用一致的版本。

2. 修改 application-prod.yml 配置文件

需要修改数据库连接地址、账号和密码。

3. 构建 jar 包

```
# macOS、Linux
./gradlew clean assemble
# Windows
gradlew.bat clean assemble
```

4. 启动 jar 包

```
java -jar build/libs/openatp-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```
