package de.dwslab.ai.riskmanagement.abduction;

import org.junit.Test;

import com.googlecode.rockit.javaAPI.Model;
import com.googlecode.rockit.parser.SyntaxReader;

public class AbductiveMlnReasonerTest {

    private static final String FILE_MODEL = "src/test/resources/smoke/prog.mln";
    private static final String MODEL_TEST_2 = "data/test4_2.mln";

    @Test
    public void testGetExtendedModel() throws Exception {
        SyntaxReader parser = new SyntaxReader();
        Model model = parser.getModelForLearning(MODEL_TEST_2);

        AbductiveMlnReasoner reasoner = new AbductiveMlnReasoner();
        Model extendedModel = reasoner.getExtendedModel(model);

        System.out.println(extendedModel);

    }

}
