
1. config server 기동이 가장 먼저 되어야 한다.
> ./gradlew bootRun

다만, cloud bus를 사용하게 된다면 config server 전에 docker를 이용해서 rabbitMQ

2. eureka server 기동
> cd eureka (해당 위치로 이동 필요)
> ./gradlew bootJar
> java -jar build/libs/xxxxxx.jar

3. api gateway 기동
> cd apigateway
> ./gradlew bootJar
> java -jar xxxxxx.jar

4. 각 서비스 객체가 기동 (user, product, order)
> cd user, product, order
> ./gradlew bootJar
> java -jar xxxxxx.jar

ps) 특이사항
order 기동 시 Kafak(비동기) - zookeeper - docker-compose
> docker-compose up

endpoint)
http://localhost:port/user-service/user/signIn
http://localhost:port/product-service/product/create
http://localhost:port/order-service/order/create
