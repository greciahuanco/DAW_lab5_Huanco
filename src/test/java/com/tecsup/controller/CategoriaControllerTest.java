package com.tecsup.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.dto.CategoriaDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCrudCategoriaYValidaciones() throws Exception {
        CategoriaDTO crear = new CategoriaDTO();
        crear.setNombre("Categoria actividad");

        String respuesta = mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crear)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Categoria actividad"))
                .andReturn().getResponse().getContentAsString();
        JsonNode categoria = objectMapper.readTree(respuesta);
        long id = categoria.get("id").asLong();

        mockMvc.perform(get("/api/categorias/{id}", id))
                .andExpect(status().isOk());

        CategoriaDTO actualizar = new CategoriaDTO();
        actualizar.setNombre("Categoria actualizada");

        mockMvc.perform(put("/api/categorias/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizar)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Categoria actualizada"));

        mockMvc.perform(delete("/api/categorias/{id}", id))
                .andExpect(status().isOk());

        CategoriaDTO invalida = new CategoriaDTO();
        invalida.setNombre("");

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalida)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/categorias/{id}", 999999L))
                .andExpect(status().isNotFound());
    }
}
