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
#### Hot publishers
>[Hot publishers](http://projectreactor.io/docs/core/release/reference/#reactor.hotCold), on the other hand, do not depend on any number of subscribers. They might start publishing data right away and would continue doing so whenever a new Subscriber comes in (in which case said subscriber would only see new elements emitted after it subscribed). For hot publishers, something does indeed happen before you subscribe.

```
UnicastProcessor<String> hotSource = UnicastProcessor.create();

Flux<String> hotFlux = hotSource.publish()
                                .autoConnect()
                                .map(String::toUpperCase);


hotFlux.subscribe(d -> System.out.println("Subscriber 1 to Hot Source: "+d));

hotSource.onNext("blue");
hotSource.onNext("green");

hotFlux.subscribe(d -> System.out.println("Subscriber 2 to Hot Source: "+d));

hotSource.onNext("orange");
hotSource.onNext("purple");
hotSource.onComplete();
```
[网页访问](http://localhost:8000/)