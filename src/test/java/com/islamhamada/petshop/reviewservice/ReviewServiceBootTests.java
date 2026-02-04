package com.islamhamada.petshop.reviewservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = "notification")
class ReviewServiceBootTests {

	@Test
	void contextLoads() {
	}

}
