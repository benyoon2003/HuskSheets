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

/**
 * Tests the methods within the Server class.
 * @Author Ben
 */
@WebMvcTest(Server.class)
class ServerTests {

    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private Server server;
    private List<IAppUser> availUsers;

    // Registers testuser before every test method
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

    /**
     * Creates a basic auth header.
     * @param username a username
     * @param password a password
     * @return an encoded String
     */
    private String createBasicAuthHeader(String username, String password) {
        String auth = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
    }

    /**
     * Tests the basic authentication for getPublishers.
     * @throws Exception irrelevant to the actual test
     */
    @Test
    void testBasicAuthUnauthorizedGetPublishers() throws Exception {
        mockMvc.perform(get("/api/v1/getPublishers")
                        .header("Authorization", createBasicAuthHeader(
                                "", "creds")))
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.success").value(false));
    }

    /**
     * Tests when user (in basic auth) is not found.
     * @throws Exception irrelevant to the actual test
     */
    @Test
    void testUserNotFoundGetPublishers() throws Exception {
        mockMvc.perform(get("/api/v1/getPublishers")
                        .header("Authorization", createBasicAuthHeader(
                                "wrong", "creds")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    /**
     * Tests when getPublishers is successful.
     * @throws Exception irrelevant to the actual test
     */
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

    /**
     * Tests when user in basic auth header is invalid.
     * @throws Exception irrelevant to the actual test
     */
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

    /**
     * Tests when user in basic auth is not the one creating the sheet.
     * @throws Exception irrelevant to the actual test
     */
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

    /**
     * Tests when sheet name is blank when trying to create sheet.
     * @throws Exception irrelevant to the actual test
     */
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

    /**
     * Tests when sheet name is already taken when trying to create a sheet.
     * @throws Exception irrelevant to the actual test
     */
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

    /**
     * Tests when the user in the basic auth is not found when creating a sheet.
     * @throws Exception irrelevant to the actual test
     */
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

    /**
     * Tests when the user successfully creates a new sheet.
     * @throws Exception irrelevant to the actual test
     */
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

    /**
     * Tests when the sender of the delete sheet request is not the owner of the sheet.
     * @throws Exception irrelevant to the actual test
     */
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

    /**
     * Tests when user tries to delete a sheet with an empty name.
     * @throws Exception irrelevant to the actual test
     */
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

    /**
     * Tests when the basic auth is invalid for the deleteSheet request.
     * @throws Exception
     */
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

    /**
     * Tests when the user tries to delete a sheet that does not exist.
     * @throws Exception irrelevant to the actual test
     */
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

    /**
     * Tests when the deleteSheet request is successful.
     * @throws Exception irrelevant to the actual test
     */
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

    /**
     * Tests when the user tries to access the sheets of a user that does not exist.
     * @throws Exception irrelevant to the actual test
     */
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

    /**
     * Tests when a user attempts a getSheets call with invalid basic auth.
     * @throws Exception irrelevant to the actual test
     */
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

    /**
     * Tests when getSheets call is successful.
     * @throws Exception irrelevant to the actual test
     */
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

    /**
     * Tests when invalid basic auth is used to register.
     * @throws Exception irrelevant to the actual test
     */
    @Test
    void testUnauthorizedBasicAuthRegister() throws Exception {
        mockMvc.perform(get("/api/v1/register")
                        .header("Authorization", createBasicAuthHeader("", "password")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    /**
     * Tests when the user tries to register as a user that already exists.
     * @throws Exception irrelevant to the actual test
     */
    @Test
    void testRegisterUserAlreadyExists() throws Exception {
        mockMvc.perform(get("/api/v1/register")
                        .header("Authorization", createBasicAuthHeader("testuser", "password")))
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User already exists"));
    }

    /**
     * Tests when the user registers successfully.
     * @throws Exception irrelevant to the actual test
     */
    @Test
    void testRegisterSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/register")
                        .header("Authorization", createBasicAuthHeader("newuser", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Publisher registered successfully"));
    }

    /**
     * Tests when the user logs in with the wrong credentials.
     * @throws Exception irrelevant to the actual test
     */
    @Test
    void testLoginWrongCredentials() throws Exception {
        mockMvc.perform(get("/api/v1/login")
                        .header("Authorization", createBasicAuthHeader("testuser", "wrongpassword")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Wrong username or password"));
    }

    /**
     * Tests when the user logs in successfully.
     * @throws Exception irrelevant to the actual test
     */
    @Test
    void testLoginSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/login")
                        .header("Authorization", createBasicAuthHeader("testuser", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Publisher logged in successfully"));
    }

    /**
     * Tests when the suer tries to log in with invalid basic auth.
     * @throws Exception irrelevant to the actual test
     */
    @Test
    void testUnauthorizedBasicAuthLogin() throws Exception {
        mockMvc.perform(get("/api/v1/login")
                        .header("Authorization", createBasicAuthHeader("", "password")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    /**
     * Tsts when the user tries to update a published sheet when the publisher does not nexist.
     * @throws Exception irrelevant to the actual test
     */
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

    /**
     * Tests when the user tries to update a published sheet with a sheet that does not exist.
     * @throws Exception irrelevant to the actual test
     */
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

    /**
     * Tests when the user tries to update a published sheetq successfully.
     * @throws Exception irrelevant to the actual test
     */
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

    /**
     * Tests when the user tries to update a published sheet with an invalid basic auth.
     * @throws Exception irrelevant to the actual test
     */
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

    /**
     * Tests when the user tries to update a subscribed sheet when the sheet's publisher does not
     * exist.
     * @throws Exception irrelevant to the actual test
     */
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

    /**
     * Tests when a user updates a subscribed sheet successfully.
     * @throws Exception irrelevant to the actual test
     */
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

    /**
     * Tests when user tries to update a subscribed sheet with invalid basic auth.
     * @throws Exception irrelevant to the actual test
     */
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

    /**
     * Tests when user tries to update a subscribed sheet when that sheet does not exist.
     * @throws Exception irrelevant to the actual test
     */
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

    /**
     * Tests when the subscriber tries to get updates from the publisher when the publisher does not
     * exist.
     * not exist.
     * @throws Exception irrelevant to the actual test
     */
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

    /**
     * Tests when subscriber gets updates from the publisher successfully.
     * @throws Exception irrelevant to the actual test
     */
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

    /**
     * Tests when a subscriber tries to get updates from the publisher when the basic auth of the
     * subscriber is invalid.
     * @throws Exception irrelevant to the actual test
     */
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

    /**
     * Tests when a subscriber tries to get updates from a publisher when the publisher's sheet does
     * not exist.
     * @throws Exception irrelevant to the actual test
     */
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

    /**
     * Tests when the publisher tries to get updates from subscribers when the publisher does
     * not exist.
     * @throws Exception irrelevant to the actual test
     */
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

    /**
     * Tests when a publisher gets updates from subscribers successfully.
     * @throws Exception irrelevant to the actual test
     */
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

    /**
     * Tests when the publisher tries to get updates from subscribers when the basic auth is invalid.
     * @throws Exception
     */
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

    /**
     * Tests when the publisher tries to get updates from subscribers when the publisher's sheet
     * does not exist.
     * @throws Exception
     */
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
