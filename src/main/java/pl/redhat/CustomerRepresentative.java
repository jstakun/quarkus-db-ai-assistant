package pl.redhat;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer_rep")
public class CustomerRepresentative extends PanacheEntity {

    @Column(nullable = false, name = "first_name")
    public String firstName;

    @Column(nullable = false, name = "last_name")
    public String lastName;

    @Column(nullable = false, name = "customer_name")
    public String customerName;

    @Column(nullable = false)
    public String email;

    @Column(nullable = false)
    public String phone;
}
