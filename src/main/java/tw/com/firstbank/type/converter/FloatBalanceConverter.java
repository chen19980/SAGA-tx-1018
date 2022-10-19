package tw.com.firstbank.type.converter;

import javax.persistence.AttributeConverter;
import java.math.BigDecimal;

public class FloatBalanceConverter implements AttributeConverter<Float, BigDecimal> {
    @Override
    public BigDecimal convertToDatabaseColumn(Float attribute) {
        return BigDecimal.valueOf(attribute);
    }

    @Override
    public Float convertToEntityAttribute(BigDecimal dbData) {
        return dbData.floatValue();
    }
}
