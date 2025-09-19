package pl.redhat;

import java.util.List;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CustomerRepresentativeSearchTool {
 
    @Inject
    DBRepository dbRepository;    

    @Tool("Search for customer representative based on extracted details from user input")
    @Transactional
    public List<CustomerRepresentative> searchCustomerRepresentative(@P(required = true, value = "customer name") String customer) {
        return dbRepository.searchCustomerRep(customer);
    }
}    

