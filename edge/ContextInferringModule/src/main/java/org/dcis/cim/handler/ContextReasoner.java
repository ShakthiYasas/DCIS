package org.dcis.cim.handler;

import java.util.Map;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import csiro.perccom.csto.util.CstoUtil;
import ltu.ecstra.context.result.ReasoningResult;
import ltu.ecstra.context.state.AxiswiseContextState;
import ltu.ecstra.context.situation.BasicSituationSpace;

import org.dcis.cim.proto.RegionDescription;
import org.dcis.cim.proto.SituationDescription;
import org.dcis.cim.proto.WeightedAttributeDescription;

public class ContextReasoner {
    public static double infer(SituationDescription description, String data)
            throws JsonProcessingException {
        return infer(description, new ObjectMapper().readValue(data, HashMap.class));
    }

    // Calculates the probability of a situation using a given situation model and the context information.
    // description: The situation model.
    // data: Map of key-value pairs representing the context information.
    // returns: Probability of the situation.
    public static double infer(SituationDescription description, Map<String,Object> data) {
        try {
            String situationName = CstoUtil.convertToAlnum(
                    description.getSituationName().replaceAll("\\s+", ""));
            BasicSituationSpace situationSpace = new BasicSituationSpace(situationName);

            for (WeightedAttributeDescription weightedAttributeDescription :
                    description.getAttributesList()) {
                String attrName = CstoUtil.convertToAlnum(
                        weightedAttributeDescription.getAttribute()
                                .getAttributeName().replaceAll("\\s+", ""));
                situationSpace.addRangeBasedAxis(attrName, weightedAttributeDescription.getWeight(),
                        BasicSituationSpace.B_NO_CONTRIBUTION);

                for (RegionDescription region :
                        weightedAttributeDescription.getAttribute().getRegionsList()) {
                    situationSpace.addNumericRange(
                            attrName, region.getRegionValue(), region.getRegionBelief());
                }
            }

            AxiswiseContextState contextState = new AxiswiseContextState();
            for (Map.Entry<String,Object> entry : data.entrySet()) {
                String attrName = CstoUtil.convertToAlnum(entry.getKey()
                        .replaceAll("\\s+", ""));
                contextState.addAxisState(attrName, entry.getValue().toString());
            }

            ReasoningResult reasoningResult = situationSpace.reason(contextState);
            return Double.parseDouble(reasoningResult.getResult());
        } catch (Exception ex) {
            return -1.0;
        }
    }
}
