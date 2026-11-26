package com.m19collection.api;

import com.jayway.jsonpath.JsonPath;
import com.m19collection.api.entity.Modelo;
import com.m19collection.api.entity.Role;
import com.m19collection.api.entity.Usuario;
import com.m19collection.api.entity.Variante;
import com.m19collection.api.repository.ColeccionRepository;
import com.m19collection.api.repository.ModeloRepository;
import com.m19collection.api.repository.UsuarioRepository;
import com.m19collection.api.repository.VarianteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    private static final String PASSWORD = "12345678";
    private static final String ADMIN_EMAIL = "admin@test.com";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ModeloRepository modeloRepository;
    @Autowired
    private VarianteRepository varianteRepository;
    @Autowired
    private ColeccionRepository coleccionRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long varianteId;

    @BeforeEach
    void setUp() {
        coleccionRepository.deleteAll();
        varianteRepository.deleteAll();
        modeloRepository.deleteAll();
        usuarioRepository.deleteAll();

        Usuario admin = new Usuario("Admin", ADMIN_EMAIL, passwordEncoder.encode(PASSWORD));
        admin.setRole(Role.ADMIN);
        usuarioRepository.save(admin);

        Modelo modelo = modeloRepository.save(new Modelo("Twin Mill III", 2024, "desc"));
        varianteId = varianteRepository.save(
                new Variante("Rojo", "Basico", "desc", "img.jpg", modelo)).getId();
    }

    @Test
    void register_hashesPasswordAndDoesNotExposeIt() throws Exception {
        postJson("/api/auth/register", userJson("Ana", "ana@test.com"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("ana@test.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        String stored = usuarioRepository.findByEmailIgnoreCase("ana@test.com")
                .orElseThrow().getPassword();
        assertThat(stored).startsWith("$2").isNotEqualTo(PASSWORD);
    }

    @Test
    void register_rejectsDuplicateEmailIgnoringCase() throws Exception {
        postJson("/api/auth/register", userJson("Ana", "ana@test.com"))
                .andExpect(status().isCreated());

        postJson("/api/auth/register", userJson("Ana Dos", "ANA@test.com"))
                .andExpect(status().isConflict());
    }

    @Test
    void register_ignoresRoleSentByClient() throws Exception {
        String body = """
                {"nombre": "Intruso", "email": "intruso@test.com",
                 "password": "%s", "role": "ADMIN"}
                """.formatted(PASSWORD);

        postJson("/api/auth/register", body).andExpect(status().isCreated());

        assertThat(usuarioRepository.findByEmailIgnoreCase("intruso@test.com")
                .orElseThrow().getRole()).isEqualTo(Role.USER);

        String token = login("intruso@test.com", PASSWORD);
        postJson("/api/modelos", "{\"nombre\": \"X\", \"anio\": 2020}", token)
                .andExpect(status().isForbidden());
    }

    @Test
    void login_withWrongPassword_returns401() throws Exception {
        postJson("/api/auth/register", userJson("Ana", "ana@test.com"))
                .andExpect(status().isCreated());

        postJson("/api/auth/login", credentials("ana@test.com", "wrong-password"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_withUnknownEmail_returns401() throws Exception {
        postJson("/api/auth/login", credentials("nobody@test.com", PASSWORD))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/me/coleccion"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_withInvalidToken_returns401() throws Exception {
        getWithToken("/api/me/coleccion", "this-is-not-a-valid-jwt")
                .andExpect(status().isUnauthorized());
    }

    @Test
    void regularUser_canReadCatalogButNotModifyIt() throws Exception {
        String ana = registerAndLogin("Ana", "ana@test.com");

        getWithToken("/api/modelos", ana).andExpect(status().isOk());

        postJson("/api/modelos", "{\"nombre\": \"Bone Shaker\", \"anio\": 2006}", ana)
                .andExpect(status().isForbidden());
    }

    @Test
    void admin_canCreateCatalogItems() throws Exception {
        String admin = login(ADMIN_EMAIL, PASSWORD);

        postJson("/api/modelos", "{\"nombre\": \"Bone Shaker\", \"anio\": 2006}", admin)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Bone Shaker"));
    }

    @Test
    void onlyAdmin_canListUsers() throws Exception {
        String ana = registerAndLogin("Ana", "ana@test.com");
        String admin = login(ADMIN_EMAIL, PASSWORD);

        getWithToken("/api/usuarios", ana).andExpect(status().isForbidden());
        getWithToken("/api/usuarios", admin).andExpect(status().isOk());
    }

    @Test
    void collection_isIsolatedBetweenUsers() throws Exception {
        String ana = registerAndLogin("Ana", "ana@test.com");
        String luis = registerAndLogin("Luis", "luis@test.com");

        postJson("/api/me/coleccion", addBody(2), ana)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidad").value(2));

        getWithToken("/api/me/coleccion", luis)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        getWithToken("/api/me/coleccion", ana)
                .andExpect(jsonPath("$[0].cantidad").value(2));
    }

    @Test
    void deletingAnItem_onlyAffectsTheCaller() throws Exception {
        String ana = registerAndLogin("Ana", "ana@test.com");
        String luis = registerAndLogin("Luis", "luis@test.com");

        postJson("/api/me/coleccion", addBody(2), ana).andExpect(status().isOk());
        postJson("/api/me/coleccion", addBody(1), luis).andExpect(status().isOk());

        deleteWithToken("/api/me/coleccion/" + varianteId, luis)
                .andExpect(status().isNoContent());

        getWithToken("/api/me/coleccion", luis)
                .andExpect(jsonPath("$").isEmpty());
        getWithToken("/api/me/coleccion", ana)
                .andExpect(jsonPath("$[0].cantidad").value(2));
    }

    @Test
    void addToCollection_withEmptyBody_returns400WithFieldErrors() throws Exception {
        String ana = registerAndLogin("Ana", "ana@test.com");

        postJson("/api/me/coleccion", "{}", ana)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.varianteId").exists());
    }

    @Test
    void addToWishlist_whenAlreadyInCollection_returns409() throws Exception {
        String ana = registerAndLogin("Ana", "ana@test.com");

        postJson("/api/me/coleccion", addBody(1), ana).andExpect(status().isOk());

        postJson("/api/me/wishlist", "{\"varianteId\": " + varianteId + "}", ana)
                .andExpect(status().isConflict());
    }

    @Test
    void unknownVariante_returns404WithJsonBody() throws Exception {
        String ana = registerAndLogin("Ana", "ana@test.com");

        getWithToken("/api/variantes/999999", ana)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());
    }

    // ---------- Helpers ----------

    private String addBody(int cantidad) {
        return "{\"varianteId\": %d, \"cantidad\": %d}".formatted(varianteId, cantidad);
    }

    private String userJson(String nombre, String email) {
        return "{\"nombre\": \"%s\", \"email\": \"%s\", \"password\": \"%s\"}"
                .formatted(nombre, email, PASSWORD);
    }

    private String credentials(String email, String password) {
        return "{\"email\": \"%s\", \"password\": \"%s\"}".formatted(email, password);
    }

    private String registerAndLogin(String nombre, String email) throws Exception {
        postJson("/api/auth/register", userJson(nombre, email)).andExpect(status().isCreated());
        return login(email, PASSWORD);
    }

    private String login(String email, String password) throws Exception {
        MvcResult result = postJson("/api/auth/login", credentials(email, password))
                .andExpect(status().isOk())
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.accessToken");
    }

    private ResultActions postJson(String url, String body) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private ResultActions postJson(String url, String body, String token) throws Exception {
        return mockMvc.perform(post(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private ResultActions getWithToken(String url, String token) throws Exception {
        return mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token));
    }

    private ResultActions deleteWithToken(String url, String token) throws Exception {
        return mockMvc.perform(delete(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token));
    }
}