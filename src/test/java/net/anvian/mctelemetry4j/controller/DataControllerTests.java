package net.anvian.mctelemetry4j.controller;

import net.anvian.mctelemetry4j.dto.request.DataRequest;
import net.anvian.mctelemetry4j.dto.response.DataResponse;
import net.anvian.mctelemetry4j.service.DataService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DataControllerTests {

    private final DataService dataService = mock(DataService.class);
    private final DataController dataController = new DataController(dataService);

    @Test
    void receiveDataReturnsCreatedWithServiceResponse() {
        DataRequest request = new DataRequest("1.21.1", "example-mod", "1.2.0", "fabric");
        DataResponse expected = new DataResponse("Data received successfully");
        when(dataService.processData(request)).thenReturn(expected);

        ResponseEntity<DataResponse> response = dataController.receiveData(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(expected);
        verify(dataService).processData(request);
    }
}
