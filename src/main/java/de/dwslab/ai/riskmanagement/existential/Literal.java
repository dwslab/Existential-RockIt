package de.dwslab.ai.riskmanagement.existential;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Literal {

    boolean negated;
    String predicate;
    List<String> values;

    public Literal(boolean negated, String predicate, List<String> values) {
        this.negated = negated;
        this.predicate = predicate;
        this.values = values;
    }

    public Literal(String literal) {
        // negated + name
        Pattern pattern = Pattern.compile("\\s*(!)?(\\w+)\\((.*)\\)\\s*");
        Matcher matcher = pattern.matcher(literal.trim());

        String elements = "";
        negated = false;
        if (matcher.find()) {
            if ((matcher.group(1) != null) && matcher.group(1).equals("!")) {
                negated = true;
            }
            predicate = matcher.group(2);
            elements = matcher.group(3).trim();
        }

        // variables
        values = new ArrayList<>();

        // Pattern pattern2 = Pattern.compile("([\\w\\?]+|(\\[[^\\]]*\\]))+\\s*,?");
        // Matcher matcher2 = pattern2.matcher(elements);
        // while (matcher2.find()) {
        // values.add(matcher2.group(1));
        // }

        // System.out.println("LITERAL\t" + literal);
        // System.out.println("\tELEMENTS\t" + elements);

        int begin = 0;
        char delimiter = 0;
        boolean start = true;
        for (int i = 0; i < elements.length(); i++) {
            char currentChar = elements.charAt(i);
            char nextChar = 0;
            boolean last = (i + 1 == elements.length());
            if (!last) {
                nextChar = elements.charAt(i + 1);
            }
            // System.out.println(i + "\t" + currentChar + "\t" + nextChar + "\t" + start + "\t"
            // + delimiter);

            if (start) {
                begin = i;
            }
            if (currentChar == '[' && start) {
                start = false;
                delimiter = ']';
            } else if (currentChar == '\"' && start) {
                start = false;
                delimiter = '\"';
                // } else if (currentChar == delimiter) {
                // split = true;
            } else if (currentChar != ',' && currentChar != ' ' && currentChar != ']'
                    && delimiter == 0) {
                start = false;
                delimiter = ',';
            }

            if (nextChar == delimiter || last) {
                if (delimiter == ',') {
                    values.add(elements.substring(begin, i + 1).trim());
                    i = i + 1;
                } else {
                    values.add(elements.substring(begin, i + 2).trim());
                    i = i + 2;
                }

                start = true;
                delimiter = 0;
            }

            if (last) {
                break;
            }
        }
    }

    public boolean isNegated() {
        return negated;
    }

    public String getPredicate() {
        return predicate;
    }

    public List<String> getValues() {
        return values;
    }

    public String valuesToString() {
        StringBuilder sb = new StringBuilder();
        if (negated) {
            sb.append("!" + " ");
        }

        for (int i = 0; i < values.size(); i++) {
            sb.append(values.get(i));
            if (i < (values.size() - 1)) {
                sb.append(", ");
            }
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder atom = new StringBuilder();
        if (negated) {
            atom.append("!");
        }
        atom.append(predicate + "(");
        for (int i = 0; i < values.size(); i++) {
            atom.append(values.get(i));
            if (i < (values.size() - 1)) {
                atom.append(", ");
            }
        }
        atom.append(")");

        return atom.toString();
    }

    public static void main(String[] args) {

        Literal l;
        l = new Literal("hates([parent(?,x)],[parent(?,y)])");
        // l = new Literal("hasRisk(x, [hasRisk(x,?)])");
        // l = new Literal("predicate(xy, \"T A\", [parent(?,x)],[parent(?,y)])");
        // l = new Literal("hates([parent(?,xx)] , [parent(?,yy)])");
        // l = new Literal("parent(?,x)");

        System.out.println(l);
        for (String var : l.getValues()) {
            System.out.println("\t - " + var);
        }
    }

}
