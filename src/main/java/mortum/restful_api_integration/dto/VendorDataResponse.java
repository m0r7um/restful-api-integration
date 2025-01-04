package mortum.restful_api_integration.dto;

import lombok.Getter;
import mortum.restful_api_integration.persistence.model.VendorData;

@Getter
public class VendorDataResponse {

    private final Long id;
    private final String name;
    private final String description;
    private final Double price;

    public VendorDataResponse(VendorData vendorData) {
        this.id = vendorData.getId();
        this.name = vendorData.getName();
        this.description = vendorData.getDescription();
        this.price = vendorData.getPrice();
    }
}
