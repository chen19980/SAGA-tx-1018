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
@ToString(of = {"id", "account", "amt"})
@Entity
@Table(name = "ir_quota")
public class Quota implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "quota_seq")
    @SequenceGenerator(name = "quota_seq", sequenceName = "quota_seq", allocationSize = 1)
    private Long id;

    @Column(name = "account", nullable=false)
    private String account;

    @Column(name = "amt", nullable=true)
    private BigDecimal amt;


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
        Quota other = (Quota) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }


}

