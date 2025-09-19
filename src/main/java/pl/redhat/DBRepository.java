package pl.redhat;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@ApplicationScoped
public class DBRepository implements PanacheRepository<Event> {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.of("pl"));
    private static final float minSimilarityScore = 0.2f;
    
    @PersistenceContext
    EntityManager entityManager;

    public List<Event> searchEvents(Double latitude, Double longitude, String type, String startDateStr, String endDateStr) {
        StringBuilder queryStr = new StringBuilder("SELECT e FROM Event e WHERE 1=1");

        Date startDate = null;
        if (startDateStr != null) {
            try {
                startDate = formatter.parse(startDateStr);
            } catch (Exception e) {
                Log.error("Invalid date format " + startDateStr);
            }
        }

        Date endDate = null;
        if (endDateStr != null) {
            try {
                endDate = formatter.parse(endDateStr);
            } catch (Exception e) {
                Log.error("Invalid date format " + endDateStr);
            }
        }
        
        if (latitude != null && longitude != null && latitude != 0 && longitude != 0) {
            //queryStr.append(" AND e.latitude = :latitude AND e.longitude = :longitude");
            queryStr.append(" AND ABS(e.latitude - :latitude) < 0.5 AND ABS(e.longitude - :longitude) < 0.5");
        }
        if (type != null && !type.isEmpty()) {
            queryStr.append(" AND e.type = :type");
        }
        if (startDate != null) {
            queryStr.append(" AND e.eventDate >= :startDate");
        }
        if (endDate != null) {
            queryStr.append(" AND e.eventDate <= :endDate");
        }
        
        TypedQuery<Event> query = entityManager.createQuery(queryStr.toString(), Event.class);
        
        if (latitude != null && longitude != null && latitude != 0 && longitude != 0) { 
            query.setParameter("latitude", latitude);
            query.setParameter("longitude", longitude);
        }
        if (type != null && !type.isEmpty() && !type.equals("Any")) {
            query.setParameter("type", type.toLowerCase(Locale.of("pl")));
        }
        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }
        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        List<Event> resultList = query.getResultList();
        
        Log.info("Found " + resultList.size() + " events");

        return resultList;
    }

    public List<CustomerRepresentative> searchCustomerRep(String customer) {
        StringBuilder queryStr = new StringBuilder("SELECT c FROM CustomerRepresentative c WHERE 1=1");
        
        if (customer != null && !customer.isEmpty()) {
            queryStr.append(" AND c.customerName = :customer");
        }
        
        TypedQuery<CustomerRepresentative> query = entityManager.createQuery(queryStr.toString(), CustomerRepresentative.class);
        
        if (customer != null && !customer.isEmpty()) {
            query.setParameter("customer", customer);
        }

        List<CustomerRepresentative> resultList = query.getResultList();
        
        Log.info("Found " + resultList.size() + " records");

        return resultList;
    }

    public List<VendorRepresentative> searchVendorRep(String vendor) {
        Query query = getEntityManager().createNativeQuery(
            "SELECT *, similarity(vendor_name, :vendorName) AS similarity_score FROM vendor_rep WHERE similarity(vendor_name, :vendorName) > :minSimilarityScore ORDER BY similarity_score DESC",
            VendorRepresentative.class
        );
        
        // Set the parameter for the query
        query.setParameter("vendorName", vendor);
        query.setParameter("minSimilarityScore", minSimilarityScore);

        // Execute the query and get the results
        List<VendorRepresentative> resultList = (List<VendorRepresentative>) query.getResultList();

        Log.info("Found " + resultList.size() + " record");

        if (!resultList.isEmpty()) {
            VendorRepresentative firstResult = resultList.get(0);
            Float score = firstResult.confidenceScore;
            Log.info("Confidence score of the first record: " + score);
        }

        return resultList;
    }

    public List<VendorRepresentative> searchSalesRep(String salesRep) {
        Query query = getEntityManager().createNativeQuery(
            "SELECT *,similarity(sales_rep, :salesRep) AS similarity_score FROM vendor_rep WHERE similarity(sales_rep, :salesRep) > :minSimilarityScore ORDER BY similarity_score DESC",
            VendorRepresentative.class
        );
        
        query.setParameter("salesRep", salesRep);
        query.setParameter("minSimilarityScore", minSimilarityScore);

        List<VendorRepresentative> resultList = (List<VendorRepresentative>) query.getResultList();

        Log.info("Found " + resultList.size() + " records");
        if (!resultList.isEmpty()) {
            VendorRepresentative firstResult = resultList.get(0);
            Float score = firstResult.confidenceScore;
            Log.info("Confidence score of the first record: " + score);
        }

        return resultList;
    }

    public List<VendorRepresentative> searchPresalesRep(String presalesRep) {
        Query query = getEntityManager().createNativeQuery(
            "SELECT *, similarity(presales_rep, :presalesRep) AS similarity_score FROM vendor_rep WHERE similarity(presales_rep, :presalesRep) > :minSimilarityScore ORDER BY similarity_score DESC",
            VendorRepresentative.class
        );
        
        query.setParameter("presalesRep", presalesRep);
        query.setParameter("minSimilarityScore", minSimilarityScore);

        List<VendorRepresentative> resultList = (List<VendorRepresentative>) query.getResultList();

        Log.info("Found " + resultList.size() + " records");
        if (!resultList.isEmpty()) {
            VendorRepresentative firstResult = resultList.get(0);
            Float score = firstResult.confidenceScore;
            Log.info("Confidence score of the first record: " + score);
        }

        return resultList;
    }

    public List<VendorRepresentative> findByAllTags(String tags) {
        if (tags == null || tags.trim().isEmpty()) {
            return List.of();
        }

        // Split the input string into a list of individual tags, trim all whitespace
        List<String> tagList = Arrays.stream(tags.split(","))
                                     .map(tag -> tag.replace("solutions", "").replace("products", "").trim())
                                     .collect(Collectors.toList());

               
        // Construct the HQL query string with ILIKE for case-insensitive search
        StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT *, 1 AS similarity_score FROM vendor_rep WHERE vendor_tags ILIKE ?1");

        // Append additional ILIKE clauses for each subsequent tag, separated by AND
        for (int i = 1; i < tagList.size(); i++) {
            queryString.append(" AND vendor_tags ILIKE ?").append(i + 1);
        }

        // Prepare the parameters for the query, wrapping each tag with '%' for ILIKE
        Object[] params = tagList.stream()
                                  .map(tag -> "%" + tag + "%")
                                  .toArray();

        Query query = getEntityManager().createNativeQuery(
            queryString.toString(),
            VendorRepresentative.class
        );
 
        for (int i = 0; i < params.length; i++) {
            query.setParameter(i + 1, params[i]);
        }
        
        List<VendorRepresentative> resultList = (List<VendorRepresentative>) query.getResultList();

        Log.info("Found " + resultList.size() + " records");
        
        return resultList;
    }
}
