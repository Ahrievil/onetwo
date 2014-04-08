package org.onetwo.common.utils.convert;

public class ToDoubleConvertor extends AbstractTypeConvert<Double> {

	public ToDoubleConvertor() {
		super();
	}

	public ToDoubleConvertor(Double defValue) {
		super(defValue);
	}

	@Override
	public Double doConvert(Object value, Class<?> componentType) {
//		if (value == null)
//            return 0.0;
        Class<?> c = value.getClass();
        if (c.getSuperclass() == Number.class)
            return ((Number) value).doubleValue();
        if (c == Boolean.class)
            return ((Boolean) value).booleanValue() ? 1.0 : 0.0;
        if (c == Character.class)
            return (double)((Character) value).charValue();
        return Double.parseDouble(value.toString().trim());

	}

}
