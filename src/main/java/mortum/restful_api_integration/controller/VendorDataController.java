package mortum.restful_api_integration.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mortum.restful_api_integration.dto.VendorDataRequest;
import mortum.restful_api_integration.dto.VendorDataResponse;
import mortum.restful_api_integration.service.VendorDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendor")
@RequiredArgsConstructor
public class VendorDataController {
    private final VendorDataService service;

    @GetMapping("/data")
    public List<VendorDataResponse> getVendorData() {
        return service.fetchDataFromVendor();
    }

    @PostMapping("/data")
    public ResponseEntity<Void> postVendorData(@RequestBody @Valid VendorDataRequest data) {
        service.pushDataToVendor(data);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
