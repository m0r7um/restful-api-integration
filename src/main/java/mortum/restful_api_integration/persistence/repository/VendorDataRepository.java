package mortum.restful_api_integration.persistence.repository;

import mortum.restful_api_integration.persistence.model.VendorData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorDataRepository extends JpaRepository<VendorData, Long> { }
