package net.anvian.mctelemetry4j.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.anvian.mctelemetry4j.dto.request.DataRequest;
import net.anvian.mctelemetry4j.dto.response.DataResponse;
import net.anvian.mctelemetry4j.service.DataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/data", "/telemetry/data"})
@RequiredArgsConstructor
@Tag(name = "Telemetry", description = "Public ingestion of anonymous mod telemetry.")
public class DataController {
    private final DataService dataService;

    @PostMapping
    @Operation(summary = "Submit telemetry", description = "Records an anonymous usage event. Limited to 20 requests per client IP per minute.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Telemetry recorded", content = @Content(schema = @Schema(implementation = DataResponse.class)), headers = @Header(name = "X-Rate-Limit-Remaining", description = "Requests remaining in the current one-minute window", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "Request body is invalid"),
            @ApiResponse(responseCode = "404", description = "The submitted mod is not registered"),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\":\"Too many requests\"}")), headers = @Header(name = "Retry-After", description = "Seconds until another request can be made", schema = @Schema(type = "string")))
    })
    public ResponseEntity<DataResponse> receiveData(@Valid @RequestBody DataRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dataService.processData(request));
    }
}
