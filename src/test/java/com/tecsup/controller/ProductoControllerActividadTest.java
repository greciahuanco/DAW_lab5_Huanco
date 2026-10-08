package com.tecsup.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.dto.ProductoDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductoControllerActividadTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testActualizarYEliminarProducto() throws Exception {
        ProductoDTO crear = new ProductoDTO();
        crear.setNombre("Producto actividad");
        crear.setPrecio(50);
        crear.setStock(4);

        String respuesta = mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crear)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        JsonNode producto = objectMapper.readTree(respuesta);

        ProductoDTO actualizar = new ProductoDTO();
        actualizar.setNombre("Producto actualizado");
        actualizar.setPrecio(75);
        actualizar.setStock(6);

        mockMvc.perform(put("/api/productos/{id}", producto.get("id").asLong())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizar)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Producto actualizado"));

        mockMvc.perform(delete("/api/productos/{id}", producto.get("id").asLong()))
                .andExpect(status().isOk());
    }
}
