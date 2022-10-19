package tw.com.firstbank.fcbcore.fcbframework.core.saga.type;

import javax.persistence.AttributeConverter;

public class SagaStatusConverter implements AttributeConverter<SagaStatus, String> {
    @Override
    public String convertToDatabaseColumn(SagaStatus attribute) {
        return (attribute == null ? null : attribute.getValue());
    }

    @Override
    public SagaStatus convertToEntityAttribute(String dbData) {
        return SagaStatus.fromValue(dbData);
    }
}
