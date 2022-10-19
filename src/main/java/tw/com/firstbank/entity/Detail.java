package tw.com.firstbank.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@ToString(of = {"txGuid", "irMasterId", "amt", "balance", "ts"})
@Entity
@Table(name = "ir_detail")
public class Detail implements Serializable {
    @Id
    @Column(name = "tx_guid", nullable=false)
    private String txGuid;

    @Column(name = "ir_master_id", nullable=false)
    private String irMasterId;

    @Column(name = "amt", nullable=true)
    private BigDecimal amt;

    @Column(name = "balance", nullable=true)
    private BigDecimal balance;

    @Column(name = "ts", nullable=true)
    private Instant ts = Instant.now();

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((txGuid == null) ? 0 : txGuid.hashCode());
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
        Detail other = (Detail) obj;
        if (txGuid == null) {
            if (other.txGuid != null)
                return false;
        } else if (!txGuid.equals(other.txGuid))
            return false;
        return true;
    }

}
