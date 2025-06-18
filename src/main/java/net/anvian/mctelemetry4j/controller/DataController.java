package net.anvian.mctelemetry4j.controller;

import lombok.RequiredArgsConstructor;
import net.anvian.mctelemetry4j.dto.request.DataRequest;
import net.anvian.mctelemetry4j.dto.response.DataResponse;
import net.anvian.mctelemetry4j.service.DataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/data")
@RequiredArgsConstructor
public class DataController {
    private final DataService dataService;

    @PostMapping
    public ResponseEntity<DataResponse> receiveData(@RequestBody DataRequest request) {
        return dataService.processData(request);
    }

    @DeleteMapping
    public ResponseEntity<Void> cleanData() {
        return dataService.cleanData();
    }
}
