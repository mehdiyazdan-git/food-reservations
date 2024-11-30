package com.mapnaom.foodreservation.controllers;

import com.mapnaom.foodreservation.dtos.FoodDto;
import com.mapnaom.foodreservation.dtos.ImportResponse;
import com.mapnaom.foodreservation.services.FoodService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
public class FoodImportControllerTest {

    @Mock
    private FoodService foodService;

    @InjectMocks
    private FoodImportController foodImportController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(foodImportController).build();
    }

    @Test
    @DisplayName("Test successful food import")
    public void testImportFoods_Success() throws Exception {
        // Prepare mock ImportResponse
        ImportResponse<String, Map<String, String>, FoodDto> mockResponse = new ImportResponse<>();
        mockResponse.setSuccessCount(10);
        mockResponse.setFailCount(0);
        mockResponse.setErrorMessages(List.of("Import completed successfully."));

        // Mock the service method
        when(foodService.importFoodsFromExcel(any())).thenReturn(mockResponse);

        // Prepare a mock Excel file
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "foods.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "Dummy Excel Content".getBytes()
        );

        // Perform the multipart request
        mockMvc.perform(multipart("/api/foods/import")
                        .file(mockFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successCount").value(10))
                .andExpect(jsonPath("$.failCount").value(0))
                .andExpect(jsonPath("$.errorMessages[0]").value("Import completed successfully."));
    }
}
