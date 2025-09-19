package pl.redhat;

import java.util.List;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class VendorRepresentativeSearchTool {
 
    @Inject
    DBRepository dbRepository;    

    @Tool("Search for vendor sales and presales representatives")
    @Transactional
    public List<VendorRepresentative> searchVendorRepresentative(@P(required = true, value = "Vendor name") String vendor) {
        return dbRepository.searchVendorRep(vendor);
    }

    @Tool("Search for vendors managed by sales representative")
    @Transactional
    public List<VendorRepresentative> searchVendorsBySales(@P(required = true, value = "Sales representative name in format: first name last name") String salesRep) {
        return dbRepository.searchSalesRep(salesRep);
    }

    @Tool("Search for vendors managed by presales representative")
    @Transactional
    public List<VendorRepresentative> searchVendorsByPreales(@P(required = true, value = "Presales representative name in format: first name last name") String presalesRep) {
        return dbRepository.searchPresalesRep(presalesRep);
    }

    @Tool("Search for vendors offering specific solutions")
    @Transactional
    public List<VendorRepresentative> findVendorsBySolutions(@P(required = true, value = "Comma seperated list of solution names translated to english")String solutions) {
        return dbRepository.findByAllTags(solutions);
    }
}    

