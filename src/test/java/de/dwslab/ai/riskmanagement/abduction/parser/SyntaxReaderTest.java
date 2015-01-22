package de.dwslab.ai.riskmanagement.abduction.parser;

import org.junit.Assert;
import org.junit.Test;

import com.googlecode.rockit.javaAPI.Model;
import com.googlecode.rockit.parser.SyntaxReader;

public class SyntaxReaderTest {

    private static final String FILE_MODEL = "src/test/resources/smoke/prog.mln";
    private static final String FILE_GROUND_VALUES = "src/test/resources/smoke/evidence.db";

    @Test
    public void testGetModel() throws Exception {
        SyntaxReader parser = new SyntaxReader();
        Model model = parser.getModel(FILE_MODEL, FILE_GROUND_VALUES);
        Assert.assertEquals(0, 0);
    }

    @Test
    public void testGetModelForLearning() throws Exception {
        SyntaxReader parser = new SyntaxReader();
        Model model = parser.getModelForLearning(FILE_MODEL);
        model.getFormulas();
        Model grounding = parser.getGroundValuesForLearning(FILE_GROUND_VALUES, model);

        System.out.println(grounding.getAllHiddenPredicates());
        System.out.println(grounding.getAllHiddenPredicates());
        System.out.println(grounding.getAllHiddenPredicates());
        System.out.println(grounding.getFormulas());
    }

}
