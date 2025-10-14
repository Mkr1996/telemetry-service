package com.example.demo;

import com.example.demo.entity.Machine;
import com.example.demo.repository.MachineRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TelemetryIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private MachineRepository machineRepo;
    @Autowired private ObjectMapper mapper;

    @Test
    void shouldCreateTelemetrySuccessfully() throws Exception {
        Machine machine = new Machine();
        machine.setName("TestMachine");
        machine.setLocation("TX");
        machineRepo.save(machine);

        String telemetryJson = """
            {"temperature": 45.6, "vibration": 0.02, "pressure": 1.2}
        """;

        mockMvc.perform(post("/api/telemetry/" + machine.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(telemetryJson))
                .andExpect(status().isOk());
    }
}
