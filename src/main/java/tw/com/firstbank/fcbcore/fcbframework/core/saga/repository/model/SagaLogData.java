package tw.com.firstbank.fcbcore.fcbframework.core.saga.repository.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tw.com.firstbank.fcbcore.fcbframework.core.saga.type.SagaStatus;
import tw.com.firstbank.fcbcore.fcbframework.core.saga.type.SagaStatusConverter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@ToString(of = {"id", "seq", "status"})
@Entity
@Table(name = "saga_log")
@IdClass(SagaLogDataKey.class)
public class SagaLogData implements Serializable {

    @Id
    @Column(name = "id", nullable=false)
    private String id;

    @Id
    @Column(name = "seq", nullable=false)
    private String seq;

    @Convert(converter = SagaStatusConverter.class)
    @Column(name = "status")
    private SagaStatus status;

    @Column(name = "start_ts")
    private Instant startTimestamp;

    @Column(name = "end_ts")
    private Instant endTimestamp;

    @Column(name = "input")
    private String inputData;

    @Column(name = "output")
    private String outputData;

    @Column(name = "before")
    private String beforeData;

    @Column(name = "after")
    private String afterData;

    public SagaLogData(String id, String seq) {
        this.id = id;
        this.seq = seq;
    }

    public SagaLogData(SagaLogDataKey key) {
        this.id = key.getId();
        this.seq = key.getSeq();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((seq == null) ? 0 : seq.hashCode());
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
        SagaLogData other = (SagaLogData) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (seq == null) {
            if (other.seq != null)
                return false;
        } else if (!seq.equals(other.seq))
            return false;
        return true;
    }


}
