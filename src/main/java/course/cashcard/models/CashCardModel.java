package course.cashcard.models;

import jakarta.persistence.*;

@Entity
@Table(name = "cash_card")
public class CashCardModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String owner;

    public CashCardModel() {

    }

    public CashCardModel(Long id, Double amount, String owner) {
        this.id = id;
        this.amount = amount;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getOwner() { return owner; }

    public void setOwner(String owner) { this.owner = owner; }
}
