package tw.com.firstbank.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tw.com.firstbank.type.converter.FloatBalanceConverter;
import tw.com.firstbank.type.converter.IntegerBalanceConverter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@ToString(of = {"id", "holdMark", "balance"})
@Entity
@Table(name = "ir_master")
public class Master implements Serializable {

    private static final long serialVersionUID = -347185184625668388L;

    public Master(String id, BigDecimal balance) {
        this.setId(id);
        this.setBalance(balance);
    }

    @Id
    @Column(name = "id", nullable=false)
    private String id;

    @Column(name = "hold_mark", nullable=true)
    private String holdMark;

    //@Convert(converter = IntegerBalanceConverter.class)
    //@Convert(converter = FloatBalanceConverter.class)
    @Column(name = "balance", nullable=true)
    private BigDecimal balance;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Master other = (Master) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}

