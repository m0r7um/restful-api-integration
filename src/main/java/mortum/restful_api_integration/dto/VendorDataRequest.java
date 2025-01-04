package mortum.restful_api_integration.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VendorDataRequest {

    @NotEmpty
    private String name;

    @NotEmpty
    private String description;

    @NotNull
    private Double price;

}