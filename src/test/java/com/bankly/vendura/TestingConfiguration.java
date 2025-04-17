package com.bankly.vendura;

import com.mongodb.client.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@Configuration
public class TestingConfiguration {

  @Bean
  public MongoTemplate mongoTemplate(MongoClient mongoClient) {
    return new MongoTemplate(mongoClient, "vendura-test");
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
