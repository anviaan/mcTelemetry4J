package net.anvian.mctelemetry4j.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class UtilControllerTests {

    private final MockMvc mockMvc = standaloneSetup(new UtilController()).build();

    @Test
    void compatibilityPrivacyRoutesServeHtml() throws Exception {
        for (String path : new String[]{"/telemetry", "/telemetry/"}) {
            mockMvc.perform(get(path))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                    .andExpect(content().string(containsString("Mod Privacy Notice")));
        }
    }
}
