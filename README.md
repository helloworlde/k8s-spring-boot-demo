# 在 Kubernetes 中部署 SpringBoot 应用

> 在 Kubernetes 中通过yaml 配置文件预先声明部署 SpringBoot 应用
> 代码地址 [https://github.com/helloworlde/k8s-service](https://github.com/helloworlde/k8s-service)

## 创建 SpringBoot 应用

- 创建名为  k8s-service 的 SpringBoot 应用
- 添加 REST API
	- K8sController.java
```java
package cn.com.hellowood.k8sservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class K8sController {

    @GetMapping("/")
    public String root() {
        return "Hello Kubernetes";
    }

    @GetMapping("/healthz")
    public String healthz() {
        return "ok";
    }
}
```

- 添加 Dockerfile
```dockerfile
FROM openjdk:8-jdk-alpine
VOLUME /tmp
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ARG JAR_FILE
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-Duser.timezone=GMT+08", "-jar","/app.jar"]
```

- 编译打包
```bash
./gradlew clean build -x test
```

- 生成并推送镜像
```bash
./gradlew dockerPush -i
```

## 在  Kubernetes 中添加服务

- 添加 k8s-demo.yaml

```yaml
apiVersion: v1
kind: Service
metadata:
  name: k8s-service
  namespace: default
  labels:
    app: k8s-service
spec:
  type: NodePort
  ports:
  - port: 8080
    nodePort: 30002
  selector:
    app: k8s-service

---

apiVersion: apps/v1
kind: Deployment
metadata:
  name: k8s-service
  labels:
    app: k8s-service
spec:
  replicas: 1
  selector:
    matchLabels:
      app: k8s-service
  template:
    metadata:
      labels:
        app: k8s-service
    spec:
      containers:
      - name: k8s-service
        image: registry.cn-qingdao.aliyuncs.com/hellowoodes/k8s-service
        imagePullPolicy: IfNotPresent
        ports:
        - containerPort: 8080
        livenessProbe:
          httpGet:
            port: 8080
            path: /healthz
            scheme: HTTP
          periodSeconds: 15
          initialDelaySeconds: 30
```

- 创建服务
```
kubectl apply -f k8s-service.yaml
```

- 等待服务启动之后访问 `${NodeIP}:30002`，会返回 `Hello Kubernetes`，部署完成
