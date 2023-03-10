package gov.hhs.aspr.gcm.gcmprotobuf.globalproperties.translators;

import com.google.protobuf.Descriptors.Descriptor;

import gov.hhs.aspr.gcm.gcmprotobuf.core.AbstractTranslator;
import plugins.globalproperties.input.GlobalPropertyIdInput;
import plugins.globalproperties.support.GlobalPropertyId;

public class GlobalPropertyIdTranslator extends AbstractTranslator<GlobalPropertyIdInput, GlobalPropertyId> {

    @Override
    protected GlobalPropertyId convertInputObject(GlobalPropertyIdInput inputObject) {
        return this.translator.getObjectFromAny(inputObject.getGlobalPropertyId(), getSimObjectClass());
    }

    @Override
    protected GlobalPropertyIdInput convertSimObject(GlobalPropertyId simObject) {
        return GlobalPropertyIdInput.newBuilder().setGlobalPropertyId(this.translator.getAnyFromObject(simObject))
                .build();
    }

    @Override
    public Descriptor getDescriptorForInputObject() {
        return GlobalPropertyIdInput.getDescriptor();
    }

    @Override
    public GlobalPropertyIdInput getDefaultInstanceForInputObject() {
        return GlobalPropertyIdInput.getDefaultInstance();
    }

    @Override
    public Class<GlobalPropertyId> getSimObjectClass() {
        return GlobalPropertyId.class;
    }

    @Override
    public Class<GlobalPropertyIdInput> getInputObjectClass() {
        return GlobalPropertyIdInput.class;
    }

}
