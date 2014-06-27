package de.dwslab.riskmanagement.abduction;

import org.junit.Test;

import de.dwslab.riskmanagement.abduction.model.Model;
import de.dwslab.riskmanagement.abduction.parser.SyntaxReader;

public class AbductiveMlnReasonerTest {

    private static final String FILE_MODEL = "src/test/resources/smoke/prog.mln";

    @Test
    public void testGetExtendedModel() throws Exception {
        SyntaxReader parser = new SyntaxReader();
        Model model = parser.getModelForLearning(FILE_MODEL);

        AbductiveMlnReasoner reasoner = new AbductiveMlnReasoner();
        reasoner.getExtendedModel(model);

    }
}
