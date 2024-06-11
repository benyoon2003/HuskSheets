package org.example.server;

import org.example.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(Server.class)
class ServerTests {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private Server server;

    private List<IAppUser> availUsers;

    @BeforeEach
    void setUp() throws Exception {
        availUsers = new ArrayList<>();

        // Inject availUsers into the server
        server = new Server();
        server.availUsers = availUsers;
        mockMvc.perform(get("/api/v1/register")
                .header("Authorization", createBasicAuthHeader(
                        "testuser", "password")));
    }

    private String createBasicAuthHeader(String username, String password) {
        String auth = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
    }

    @Test
    void testBasicAuthUnauthorizedGetPublishers() throws Exception {
        mockMvc.perform(get("/api/v1/getPublishers")
                        .header("Authorization", createBasicAuthHeader(
                                "", "creds")))
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testUserNotFoundGetPublishers() throws Exception {
        mockMvc.perform(get("/api/v1/getPublishers")
                        .header("Authorization", createBasicAuthHeader(
                                "wrong", "creds")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void testGetPublishersSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/getPublishers")
                        .header("Authorization", createBasicAuthHeader("testuser", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.value[0].publisher").value("testuser"))
                .andExpect(jsonPath("$.value[0].sheet").isEmpty())
                .andExpect(jsonPath("$.value[0].id").isEmpty())
                .andExpect(jsonPath("$.value[0].payload").isEmpty());
    }

    @Test
    void testBasicAuthUnauthorizedCreateSheet() throws Exception {
        String json = "{\"publisher\":\"otherUser\", \"sheet\":\"newsheet\"}";
        mockMvc.perform(post("/api/v1/createSheet")
                        .header("Authorization", createBasicAuthHeader(
                                "testuser", "")) // This should be properly Base64 encoded
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testSenderIsNotOwnerCreateSheet() throws Exception {
        String json = "{\"publisher\":\"otherUser\", \"sheet\":\"newsheet\"}";
        mockMvc.perform(post("/api/v1/createSheet")
                        .header("Authorization", createBasicAuthHeader(
                                "testuser", "password")) // This should be properly Base64 encoded
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(
                        "Unauthorized: sender is not owner of sheet"));
    }

    @Test
    void testSheetNameCannotBeBlankCreateSheet() throws Exception {
        String json = "{\"publisher\":\"testuser\", \"sheet\":\"\"}";
        mockMvc.perform(post("/api/v1/createSheet")
                        .header("Authorization", createBasicAuthHeader(
                                "testuser", "password")) // This should be properly Base64 encoded
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(
                        "Sheet name cannot be blank"));
    }

    @Test
    void testSheetNameExistsCreateSheet() throws Exception {
        String json = "{\"publisher\":\"testuser\", \"sheet\":\"sheet\"}";
        mockMvc.perform(post("/api/v1/createSheet")
                .header("Authorization", createBasicAuthHeader(
                        "testuser", "password")) // This should be properly Base64 encoded
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
        json = "{\"publisher\":\"testuser\", \"sheet\":\"sheet\"}";
        mockMvc.perform(post("/api/v1/createSheet")
                        .header("Authorization", createBasicAuthHeader(
                                "testuser", "password")) // This should be properly Base64 encoded
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(
                        "Sheet already exists: sheet"));
    }

    @Test
    void testUserNotFoundCreateSheet() throws Exception {
        String json = "{\"publisher\":\"random\", \"sheet\":\"sheet\"}";
        mockMvc.perform(post("/api/v1/createSheet")
                        .header("Authorization", createBasicAuthHeader(
                                "random", "password")) // This should be properly Base64 encoded
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(
                        "User not found"));
    }

    @Test
    void testCreateSheetSuccess() throws Exception {
        String json = "{\"publisher\":\"testuser\", \"sheet\":\"newsheet\"}";
        mockMvc.perform(post("/api/v1/createSheet")
                        .header("Authorization", createBasicAuthHeader("testuser", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Sheet created successfully"));
    }

    @Test
    void testSenderIsNotOwnerDeleteSheet() throws Exception {
        String json = "{\"publisher\":\"testuser\", \"sheet\":\"newsheet\"}";
        mockMvc.perform(post("/api/v1/deleteSheet")
                        .header("Authorization", createBasicAuthHeader(
                                "otherUser", "password")) // This should be properly Base64 encoded
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(
                        "Unauthorized: sender is not owner of sheet"));
    }

    @Test
    void testEmptySheetNameDeleteSheet() throws Exception {
        String json = "{\"publisher\":\"testuser\", \"sheet\":\"\"}";
        mockMvc.perform(post("/api/v1/deleteSheet")
                        .header("Authorization", createBasicAuthHeader(
                                "testuser", "password")) // This should be properly Base64 encoded
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(
                        "Sheet name cannot be blank"));
    }

    @Test
    void testBasicAuthUnauthorizedDeleteSheet() throws Exception {
        String json = "{\"publisher\":\"testuser\", \"sheet\":\"newsheet\"}";
        mockMvc.perform(post("/api/v1/deleteSheet")
                        .header("Authorization", createBasicAuthHeader(
                                "", "")) // This should be properly Base64 encoded
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }


    @Test
    void testSheetDoesNotExistDeleteSheet() throws Exception {
        String json = "{\"publisher\":\"testuser\", \"sheet\":\"random\"}";
        mockMvc.perform(post("/api/v1/deleteSheet")
                        .header("Authorization", createBasicAuthHeader(
                                "testuser", "password")) // This should be properly Base64 encoded
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(
                        "Sheet does not exist: random"));
    }

    @Test
    void testDeleteSheetSuccess() throws Exception {
        String json = "{\"publisher\":\"testuser\", \"sheet\":\"newsheet\"}";
        mockMvc.perform(post("/api/v1/deleteSheet")
                        .header("Authorization", createBasicAuthHeader("testuser", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Sheet deleted successfully"));
    }

    @Test
    void testUserDoesNotExistGetSheets() throws Exception {
        String json = "{\"publisher\":\"random\"}";
        mockMvc.perform(post("/api/v1/getSheets")
                        .header("Authorization", createBasicAuthHeader("testuser", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void testUnauthorizedBasicAuthsGetSheets() throws Exception {
        String json = "{\"publisher\":\"testuser\"}";
        mockMvc.perform(post("/api/v1/getSheets")
                        .header("Authorization", createBasicAuthHeader("", ""))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testGetSheetsSuccess() throws Exception {
        String json = "{\"publisher\":\"testuser\", \"sheet\":\"sheet\"}";
        mockMvc.perform(post("/api/v1/createSheet")
                .header("Authorization", createBasicAuthHeader("testuser", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
        json = "{\"publisher\":\"testuser\"}";
        mockMvc.perform(post("/api/v1/getSheets")
                        .header("Authorization", createBasicAuthHeader("testuser", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Sheets retrieved successfully"))
                .andExpect(jsonPath("$.value[1].publisher").value("testuser"))
                .andExpect(jsonPath("$.value[1].sheet").value("sheet"))
                .andExpect(jsonPath("$.value[1].id").isEmpty())
                .andExpect(jsonPath("$.value[1].payload").isEmpty());
    }

    @Test
    void testUnauthorizedBasicAuthRegister() throws Exception {
        mockMvc.perform(get("/api/v1/register")
                        .header("Authorization", createBasicAuthHeader("", "password")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testRegisterUserAlreadyExists() throws Exception {
        mockMvc.perform(get("/api/v1/register")
                        .header("Authorization", createBasicAuthHeader("testuser", "password")))
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User already exists"));
    }

    @Test
    void testRegisterSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/register")
                        .header("Authorization", createBasicAuthHeader("newuser", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Publisher registered successfully"));
    }

    @Test
    void testLoginWrongCredentials() throws Exception {
        mockMvc.perform(get("/api/v1/login")
                        .header("Authorization", createBasicAuthHeader("testuser", "wrongpassword")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Wrong username or password"));
    }

    @Test
    void testLoginSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/login")
                        .header("Authorization", createBasicAuthHeader("testuser", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Publisher logged in successfully"));
    }

    @Test
    void testUnauthorizedBasicAuthLogin() throws Exception {
        mockMvc.perform(get("/api/v1/login")
                        .header("Authorization", createBasicAuthHeader("", "password")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testUserNotFoundUpdatePublished() throws Exception {
        String json = "{\"publisher\":\"random\", \"sheet\":\"newsheet\", \"payload\":\"\"}";
        mockMvc.perform(post("/api/v1/updatePublished")
                        .header("Authorization", createBasicAuthHeader("testuser", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void testSheetNotFoundUpdatePublished() throws Exception {
        String json = "{\"publisher\":\"testuser\", \"sheet\":\"random\", \"payload\":\"\"}";
        mockMvc.perform(post("/api/v1/updatePublished")
                        .header("Authorization", createBasicAuthHeader("testuser", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Sheet not found"));
    }

    @Test
    void testUpdatePublishedSuccess() throws Exception {
        String json = "{\"publisher\":\"testuser\", \"sheet\":\"sheet1\"}";
        mockMvc.perform(post("/api/v1/createSheet")
                .header("Authorization", createBasicAuthHeader("testuser", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
        json = "{\"publisher\":\"testuser\", \"sheet\":\"sheet1\", \"payload\":\"$A1 2\\n\"}";
        mockMvc.perform(post("/api/v1/updatePublished")
                        .header("Authorization", createBasicAuthHeader("testuser", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Sheet updated successfully"));
    }

    @Test
    void testUnauthorizedBasicAuthUpdatePublished() throws Exception {
        String json = "{\"publisher\":\"testuser\", \"sheet\":\"sheet1\", \"payload\":\"$A1 2\\n\"}";
        mockMvc.perform(post("/api/v1/updatePublished")
                        .header("Authorization", createBasicAuthHeader("", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testUserNotFoundUpdateSubscription() throws Exception {
        String json = "{\"publisher\":\"random\", \"sheet\":\"sheet1\", \"payload\":\"$A1 3\\n\"}";
        mockMvc.perform(post("/api/v1/updateSubscription")
                        .header("Authorization", createBasicAuthHeader("testuser", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User not found"));
    }
    @Test
    void testUpdateSubscriptionSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/register")
                .header("Authorization", createBasicAuthHeader(
                        "subscriber", "password")));
        String json = "{\"publisher\":\"testuser\", \"sheet\":\"sheet1\", \"payload\":\"$A1 3\\n\"}";
        mockMvc.perform(post("/api/v1/updateSubscription")
                        .header("Authorization", createBasicAuthHeader("subscriber", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Sheet updated successfully"));
    }

    @Test
    void testUnauthorizedBasicAuthUpdateSubscription() throws Exception {
        String json = "{\"publisher\":\"testuser\", \"sheet\":\"sheet1\", \"payload\":\"$A1 3\\n\"}";
        mockMvc.perform(post("/api/v1/updateSubscription")
                        .header("Authorization", createBasicAuthHeader("subscriber", ""))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testSheetNotFoundUpdateSubscription() throws Exception {
        String json = "{\"publisher\":\"testuser\", \"sheet\":\"randomSheet\", \"payload\":\"$A1 3\\n\"}";
        mockMvc.perform(post("/api/v1/updateSubscription")
                        .header("Authorization", createBasicAuthHeader("testuser", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Sheet not found"));
    }

    @Test
    void testUserNotFoundGetUpdatesSubscription() throws Exception {
        String json = String.format("{\"publisher\":\"f\", \"sheet\":\"randomsheet\", \"id\":\"0\"}");
        mockMvc.perform(post("/api/v1/getUpdatesForSubscription")
                        .header("Authorization", createBasicAuthHeader("subscriber", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void testGetUpdatesSubscriptionSuccess() throws Exception {
        String json = "{\"publisher\":\"testuser\", \"sheet\":\"sheet2\"}";
        mockMvc.perform(post("/api/v1/createSheet")
                .header("Authorization", createBasicAuthHeader("testuser", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
        json = "{\"publisher\":\"testuser\", \"sheet\":\"sheet2\", \"payload\":\"$A1 2\\n\"}";
        mockMvc.perform(post("/api/v1/updatePublished")
                .header("Authorization", createBasicAuthHeader("testuser", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
        json = String.format("{\"publisher\":\"testuser\", \"sheet\":\"sheet2\", \"id\":\"0\"}");
        mockMvc.perform(post("/api/v1/getUpdatesForSubscription")
                        .header("Authorization", createBasicAuthHeader("subscriber", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Updates received"))
                .andExpect(jsonPath("$.value[0].publisher").value("testuser"))
                .andExpect(jsonPath("$.value[0].sheet").value("sheet2"))
                .andExpect(jsonPath("$.value[0].id").value("0"))
                .andExpect(jsonPath("$.value[0].payload").value("$A1 2\\n"));
    }

    @Test
    void testUnauthorizedBasicAuthGetUpdatesSubscription() throws Exception {
        String json = String.format("{\"publisher\":\"testuser\", \"sheet\":\"sheet2\", \"id\":\"0\"}");
        mockMvc.perform(post("/api/v1/getUpdatesForSubscription")
                        .header("Authorization", createBasicAuthHeader("", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testSheetNotFoundGetUpdatesSubscription() throws Exception {
        String json = String.format("{\"publisher\":\"testuser\", \"sheet\":\"sheet10\", \"id\":\"0\"}");
        mockMvc.perform(post("/api/v1/getUpdatesForSubscription")
                        .header("Authorization", createBasicAuthHeader("subscriber", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Sheet not found"));
    }

    @Test
    void testUserNotFoundGetUpdatesPublisher() throws Exception {
        String json = String.format("{\"publisher\":\"2\", \"sheet\":\"sheet\", \"id\":\"0\"}");
        mockMvc.perform(post("/api/v1/getUpdatesForPublished")
                        .header("Authorization", createBasicAuthHeader("testuser", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void testGetUpdatesPublisherSuccess() throws Exception {
        String json = "{\"publisher\":\"testuser\", \"sheet\":\"sheet4\"}";
        mockMvc.perform(post("/api/v1/createSheet")
                .header("Authorization", createBasicAuthHeader("testuser", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
        json = "{\"publisher\":\"testuser\", \"sheet\":\"sheet4\", \"payload\":\"$A1 Hey\\n\"}";
        mockMvc.perform(post("/api/v1/updateSubscription")
                .header("Authorization", createBasicAuthHeader("subscriber", "password"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
        json = String.format("{\"publisher\":\"testuser\", \"sheet\":\"sheet4\", \"id\":\"0\"}");
        mockMvc.perform(post("/api/v1/getUpdatesForPublished")
                        .header("Authorization", createBasicAuthHeader("testuser", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Updates received"))
                .andExpect(jsonPath("$.value[0].publisher").value("testuser"))
                .andExpect(jsonPath("$.value[0].sheet").value("sheet4"))
                .andExpect(jsonPath("$.value[0].id").value("0"))
                .andExpect(jsonPath("$.value[0].payload").value("$A1 Hey\\n"));
    }

    @Test
    void testUnauthorizedBasicAuthGetUpdatesPublisher() throws Exception {
        String json = String.format("{\"publisher\":\"testuser\", \"sheet\":\"sheet4\", \"id\":\"0\"}");
        mockMvc.perform(post("/api/v1/getUpdatesForPublished")
                        .header("Authorization", createBasicAuthHeader("testuser", ""))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testSheetNotFoundGetUpdatesPublisher() throws Exception {
        String json = String.format("{\"publisher\":\"testuser\", \"sheet\":\"random\", \"id\":\"0\"}");
        mockMvc.perform(post("/api/v1/getUpdatesForPublished")
                        .header("Authorization", createBasicAuthHeader("testuser", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Sheet not found"));
    }

}
