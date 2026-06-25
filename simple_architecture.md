```mermaid
flowchart TD
    subgraph 用户层 [用户层]
        Browser[浏览器]
    end
    
    subgraph 前端层 [前端层]
        VueApp[Vue.js 应用]
    end
    
    subgraph 后端层 [后端层]
        SpringBoot[Spring Boot 服务]
    end
    
    subgraph 数据层 [数据层]
        MySQL[(MySQL 数据库)]
    end
    
    Browser --> VueApp : HTTP/HTTPS
    VueApp --> SpringBoot : RESTful API
    SpringBoot --> MySQL : JDBC
```