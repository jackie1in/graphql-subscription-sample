#### overview
| 技术| 版本 |
| :------ | :------ |
| spring-boot | `2.0.0.RELEASE` |
| graphql-java-tools | `5.2.3` |
| graphql-spring-boot-starter | `5.0.1` |
| spring-kafka | `2.1.8.RELEASE` |
| reactor-core | `3.1.8.RELEASE` |
#### run
kafka server地址
```
localhost:9092
```
#### flux 消息分发
[flux broadcast文档](http://projectreactor.io/docs/core/release/reference/#advanced-broadcast-multiple-subscribers-connectableflux)
```
Flux<Integer> source = Flux.range(1, 3)
                .doOnSubscribe(s -> System.out.println("subscribed to source"));

        Flux<Integer> autoCo1 = source.publish().autoConnect();

        autoCo1.subscribe(System.out::println, e -> {}, () -> {});
        System.out.println("subscribed first");
        Thread.sleep(500);
        Flux<Integer> autoCo2 = source.publish().autoConnect();
        System.out.println("subscribing second");
        autoCo2.subscribe(System.out::println, e -> {}, () -> {});
```
[网页访问](http://localhost:8000/)