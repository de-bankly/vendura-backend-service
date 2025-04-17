package com.bankly.vendura;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MongoTestingRepository {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest")
            .withExposedPorts(27017)
            .withReuse(true);

    static {
        mongoDBContainer.start();
    }

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        System.out.println("Adding to REGISTRY");
        System.out.println(System.getProperty("spring.data.mongodb.uri"));
        registry.add(
                "spring.data.mongodb.uri", MongoTestingRepository.mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.mongodb.database", () -> "vendura-test");
        System.out.println(System.getProperty("spring.data.mongodb.uri"));
    }

}
