package mortum.restful_api_integration;

import mortum.restful_api_integration.dto.VendorDataRequest;
import mortum.restful_api_integration.dto.VendorDataResponse;
import mortum.restful_api_integration.persistence.model.VendorData;
import mortum.restful_api_integration.persistence.repository.VendorDataRepository;
import mortum.restful_api_integration.service.VendorDataService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
class RestfulApiIntegrationApplicationTests {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private VendorDataRepository repository;

    @InjectMocks
    private VendorDataService service;

    @Test
    void fetchDataFromVendor_success() {
        // Mock data
        VendorData[] mockData = {
                new VendorData(1L, "Item1", "Description1", 10.0),
                new VendorData(2L, "Item2", "Description2", 20.0)
        };
        when(restTemplate.getForEntity(any(String.class), eq(VendorData[].class)))
                .thenReturn(new ResponseEntity<>(mockData, HttpStatus.OK));

        // Call service
        List<VendorDataResponse> result = service.fetchDataFromVendor();

        // Verify
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repository, times(1)).saveAll(any());
    }

    @Test
    void fetchDataFromVendor_timeout() {
        when(restTemplate.getForEntity(any(String.class), eq(VendorData[].class)))
                .thenThrow(new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, service::fetchDataFromVendor);
        assertEquals(HttpStatus.REQUEST_TIMEOUT, exception.getStatusCode());
    }

    @Test
    void pushDataToVendor_success() {
        VendorDataRequest mockRequest = new VendorDataRequest();
        mockRequest.setName("Item1");
        mockRequest.setDescription("Description1");
        mockRequest.setPrice(10.0);

        when(restTemplate.postForEntity(any(String.class), any(), eq(Void.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.CREATED));

        assertDoesNotThrow(() -> service.pushDataToVendor(mockRequest));
    }

    @Test
    void pushDataToVendor_timeout() {
        VendorDataRequest mockRequest = new VendorDataRequest();
        mockRequest.setName("Item1");
        mockRequest.setDescription("Description1");
        mockRequest.setPrice(10.0);

        when(restTemplate.postForEntity(any(String.class), any(), eq(Void.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.pushDataToVendor(mockRequest));
        assertEquals(HttpStatus.REQUEST_TIMEOUT, exception.getStatusCode());
    }

}
