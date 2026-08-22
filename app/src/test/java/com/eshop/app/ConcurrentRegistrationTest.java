package com.eshop.app;

import com.eshop.app.support.PostgresIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@AutoConfigureMockMvc
class ConcurrentRegistrationTest extends PostgresIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void concurrentDuplicateRegistrationYieldsOneSuccessAndOneConflict() throws Exception {
        String email = "race+" + System.nanoTime() + "@example.com";
        String body = "{\"email\":\"" + email + "\",\"password\":\"S3cret!Pass\",\"name\":\"Race\"}";

        int threads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        Set<Integer> statuses = ConcurrentHashMap.newKeySet();

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                ready.countDown();
                start.await();
                MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                    .andReturn();
                statuses.add(result.getResponse().getStatus());
                return null;
            });
        }

        ready.await(10, TimeUnit.SECONDS);
        start.countDown();
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);

        assertThat(statuses).containsExactlyInAnyOrder(201, 409);
    }

}
