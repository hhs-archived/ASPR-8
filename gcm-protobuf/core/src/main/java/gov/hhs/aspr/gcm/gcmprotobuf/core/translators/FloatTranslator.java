package gov.hhs.aspr.gcm.gcmprotobuf.core.translators;

import com.google.protobuf.Descriptors.Descriptor;

import gov.hhs.aspr.gcm.gcmprotobuf.core.AbstractTranslator;

import com.google.protobuf.FloatValue;

public class FloatTranslator extends AbstractTranslator<FloatValue, Float> {

    @Override
    protected Float convertInputObject(FloatValue inputObject) {
        return inputObject.getValue();
    }

    @Override
    protected FloatValue convertSimObject(Float simObject) {
        return FloatValue.of(simObject);
    }

    @Override
    public Descriptor getDescriptorForInputObject() {
        return FloatValue.getDescriptor();
    }

    @Override
    public FloatValue getDefaultInstanceForInputObject() {
        return FloatValue.getDefaultInstance();
    }

    @Override
    public Class<Float> getSimObjectClass() {
        return Float.class;
    }

    @Override
    public Class<FloatValue> getInputObjectClass() {
        return FloatValue.class;
    }
}
