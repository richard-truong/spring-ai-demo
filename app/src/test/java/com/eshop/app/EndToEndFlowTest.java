package com.eshop.app;

import com.eshop.app.support.PostgresIntegrationTest;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class EndToEndFlowTest extends PostgresIntegrationTest {

    private static final String ESPRESSO_ID = "10000000-0000-0000-0000-000000000001";

    @Autowired
    MockMvc mockMvc;

    @Test
    void registerLoginPurchaseHappyPath() throws Exception {
        String email = uniqueEmail();

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerJson(email)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.passwordHash").doesNotExist());

        String token = login(email);

        mockMvc.perform(post("/api/v1/orders")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"items\":[{\"productId\":\"" + ESPRESSO_ID + "\",\"quantity\":2}]}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.total").value(25.0))
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andExpect(jsonPath("$.items[0].name").value("Espresso"));
    }

    @Test
    void duplicateEmailReturns409() throws Exception {
        String email = uniqueEmail();

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerJson(email)))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerJson(email)))
            .andExpect(status().isConflict());
    }

    @Test
    void loginWithWrongPasswordReturns401() throws Exception {
        String email = uniqueEmail();

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerJson(email)))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"wrong-pass\"}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void purchaseUnknownProductReturns404() throws Exception {
        String email = uniqueEmail();
        register(email);
        String token = login(email);

        mockMvc.perform(post("/api/v1/orders")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"items\":[{\"productId\":\"does-not-exist\",\"quantity\":1}]}"))
            .andExpect(status().isNotFound());
    }

    @Test
    void purchaseWithoutTokenReturns401() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"items\":[{\"productId\":\"" + ESPRESSO_ID + "\",\"quantity\":1}]}"))
            .andExpect(status().isUnauthorized());
    }

    private void register(String email) throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerJson(email)))
            .andExpect(status().isCreated());
    }

    private String login(String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"S3cret!Pass\"}"))
            .andExpect(status().isOk())
            .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.accessToken");
    }

    private String registerJson(String email) {
        return "{\"email\":\"" + email + "\",\"password\":\"S3cret!Pass\",\"name\":\"Alice\"}";
    }

    private String uniqueEmail() {
        return "alice+" + System.nanoTime() + "@example.com";
    }

}
