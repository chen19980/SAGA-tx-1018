package tw.com.firstbank.type.converter;

import javax.persistence.AttributeConverter;
import java.math.BigDecimal;

public class IntegerBalanceConverter implements AttributeConverter<Integer, BigDecimal> {
    @Override
    public BigDecimal convertToDatabaseColumn(Integer attribute) {
        return BigDecimal.valueOf(attribute);
    }

    @Override
    public Integer convertToEntityAttribute(BigDecimal dbData) {
        return dbData.intValue();
    }
}
