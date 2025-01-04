package mortum.restful_api_integration.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mortum.restful_api_integration.dto.VendorDataRequest;
import mortum.restful_api_integration.dto.VendorDataResponse;
import mortum.restful_api_integration.persistence.model.VendorData;
import mortum.restful_api_integration.persistence.repository.VendorDataRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VendorDataService {

    private final RestTemplate restTemplate;
    private final VendorDataRepository repository;

    @Value("${external-api.url}")
    private String url;

    public List<VendorDataResponse> fetchDataFromVendor() {

        String vendorApiUrl = url;

        List<VendorDataResponse> vendorDataResponse = null;
        try {
            ResponseEntity<VendorData[]> response = restTemplate.getForEntity(vendorApiUrl, VendorData[].class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<VendorData> vendorDataList = List.of(response.getBody());
                vendorDataResponse = vendorDataList
                        .stream()
                        .map(VendorDataResponse::new)
                        .toList();
                repository.saveAll(vendorDataList);
                return vendorDataResponse;
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Failed to fetch data from vendor");
            }
        } catch (RestClientException e) {
            if (e.getCause() instanceof java.net.SocketTimeoutException) {
                throw new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT, "Vendor API request timed out", e);
            }

            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Vendor API unavailable", e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return vendorDataResponse;
        }
    }

    public void pushDataToVendor(@Valid VendorDataRequest dataRequest) {
        VendorData data = new VendorData();
        data.setName(dataRequest.getName());
        data.setDescription(dataRequest.getDescription());
        data.setPrice(dataRequest.getPrice());

        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(url, data, Void.class);
            if (response.getStatusCode() != HttpStatus.CREATED) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Failed to push data to vendor");
            }
        } catch (Exception e) {
            if (e.getCause() instanceof java.net.SocketTimeoutException) {
                throw new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT, "Vendor API request timed out", e);
            }

            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Vendor API unavailable", e);
        }
    }
}
