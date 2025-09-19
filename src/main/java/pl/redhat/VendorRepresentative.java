package pl.redhat;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "vendor_rep")
public class VendorRepresentative extends PanacheEntity {

    @Column(nullable = false, name = "vendor_name")
    public String vendorName;

    @Column(nullable = false, name = "sales_rep")
    public String salesRepName;

    @Column(nullable = false, name = "sales_rep_email")
    public String salesRepEmail;

    @Column(nullable = false, name = "presales_rep")
    public String presalesRepName;

    @Column(nullable = false, name = "presales_rep_email")
    public String presalesRepEmail;

    @Column(nullable = false, name = "vendor_tags")
    public String vendorTags;

    @Column(nullable = true, name = "similarity_score")
    public Float confidenceScore;
}
