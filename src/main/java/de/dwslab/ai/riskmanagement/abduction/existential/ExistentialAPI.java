package de.dwslab.ai.riskmanagement.abduction.existential;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExistentialAPI {

    public static void main(String[] args) throws Exception {
        String mln = "data/test4_2.mln";
        String db = "data/test4_2.db";

        String mlnOut = "out/test4_2out.mln";
        String dbOut = "out/test4_2out.db";

        existenitalAPI(mln, db, mlnOut, dbOut);
    }

    public static void existenitalAPI(String mln, String db, String mlnOut, String dbOut)
            throws Exception {
        Map<String, Predicate> predicates = getPredicates(mln);
        Map<String, List<String>> entities = getEntities(db, predicates);
        Map<String, Set<Literal>> groundings = getGroundings(db);
        Map<String, String> predMapping = getMappings(mln);

        // debugSysout(predicates, entities, groundings, predMapping);

        // DB

        // TODO state for which predicate all "missing" negated grounding should be created
        List<String> targetPredicates = new ArrayList<>();
        // all weighted predicates
        for (String p : predMapping.keySet()) {
            targetPredicates.add(p);
        }

        generateNegatedGroundings(predicates, entities, groundings, predMapping, targetPredicates);

        List<String> allGroundings = new ArrayList<>();
        for (Set<Literal> gs : groundings.values()) {
            for (Literal g : gs) {
                allGroundings.add(g.toString());
            }
        }
        write(dbOut, allGroundings);

        // MLN
        List<String> newMLN = transformMLN(mln, predicates, predMapping, entities, groundings);
        write(mlnOut, newMLN);

    }

    private static void debugSysout(Map<String, Predicate> predicates,
            Map<String, List<String>> entities, Map<String, Set<Literal>> groundings,
            Map<String, String> predMapping) {
        System.out.println();
        System.out.println("PREDICATES");
        for (Predicate p : predicates.values()) {
            System.out.println("\t" + p);
        }

        System.out.println("ENTITIES:");
        for (String t : entities.keySet()) {
            System.out.println("\t" + t);
            for (String e : entities.get(t)) {
                System.out.println("\t\t" + e);
            }
        }
        System.out.println();
        System.out.println("GROUNDINGS:");
        for (String p : groundings.keySet()) {
            System.out.println("\t" + p);
            for (Literal values : groundings.get(p)) {
                System.out.println("\t\t" + values.valuesToString());
            }
        }
        System.out.println();
        System.out.println("MAPPINGS:");
        for (String p : predMapping.keySet()) {
            System.out.println("\t\t" + p + " - " + predMapping.get(p));
        }

    }

    private static Map<String, String> getMappings(String mln) throws Exception {
        Map<String, String> mappings = new HashMap<>();

        for (String f : readFile(mln)) {
            // match weighted formula
            Pattern pattern = Pattern.compile("\\w*: .*");
            Matcher matcher = pattern.matcher(f);
            if (!matcher.find()) {
                continue;
            }

            // match g1 v g2
            Pattern pattern2 = Pattern
                    .compile(".* ([!]?\\w+\\(.*\\))\\s*v\\s*([!]?\\w+\\(.*\\)).*");
            Matcher matcher2 = pattern2.matcher(f);

            List<Literal> literals = new ArrayList<>();

            while (matcher2.find()) {
                literals.add(new Literal(matcher2.group(1)));
                literals.add(new Literal(matcher2.group(2)));
            }

            if (literals.size() == 2) {
                if (literals.get(1).getValues().size() == (literals.get(0).getValues().size() + 1)) {
                    mappings.put(literals.get(0).getPredicate(), literals.get(1).getPredicate());
                } else if (literals.get(0).getValues().size() == (literals.get(1).getValues()
                        .size() + 1)) {
                    mappings.put(literals.get(1).getPredicate(), literals.get(0).getPredicate());
                }
            }
        }

        return mappings;
    }

    private static Map<String, Set<Literal>> getGroundings(String db) throws Exception {
        Map<String, Set<Literal>> groundings = new HashMap<>();

        for (String s : readFile(db)) {
            Pattern pattern = Pattern.compile("(!)?\\s*(\\w*).*");
            Matcher matcher = pattern.matcher(s);
            String predicate = null;
            boolean negated = false;
            if (matcher.find()) {
                if ((matcher.group(1) != null) && matcher.group(1).equals("!")) {
                    negated = true;
                }
                predicate = matcher.group(2);
            }

            if ((predicate == null) || (predicate.length() == 0)) {
                continue;
            }

            Pattern pattern2 = Pattern.compile("([^\\s(),]+)[,\\s)]+");
            Matcher matcher2 = pattern2.matcher(s);

            List<String> values = new ArrayList<>();
            while (matcher2.find()) {
                values.add(matcher2.group(1));
            }

            if (!groundings.containsKey(predicate)) {
                groundings.put(predicate, new HashSet<>());
            }

            Literal g = new Literal(negated, predicate, values);

            groundings.get(predicate).add(g);
        }
        return groundings;
    }

    private static Map<String, List<String>> getEntities(String db,
            Map<String, Predicate> predicates) throws Exception {
        Map<String, Set<String>> entities = new HashMap<>();

        for (String groundAxiom : readFile(db)) {
            // predicate
            Pattern pattern = Pattern.compile("[*]?\\s*(\\w*).*");
            Matcher matcher = pattern.matcher(groundAxiom);
            String predicate = null;
            if (matcher.find()) {
                predicate = matcher.group(1);
            }

            if ((predicate == null) || (predicate.length() == 0)) {
                continue;
            }

            // entities
            Pattern pattern2 = Pattern.compile("([^\\s(),]+)[,\\s)]+");
            Matcher matcher2 = pattern2.matcher(groundAxiom);
            int counter = 0;

            List<String> types = predicates.get(predicate).getTypes();
            while (matcher2.find()) {
                String entity = matcher2.group(1);
                String type = types.get(counter++);

                if (!entities.containsKey(type)) {
                    entities.put(type, new HashSet<>());
                }
                entities.get(type).add(entity);

            }
        }

        // string -> list instead of string -> set
        Map<String, List<String>> entities2 = new HashMap<>();
        for (String type : entities.keySet()) {
            entities2.put(type, new ArrayList<>(entities.get(type)));
        }

        return entities2;
    }

    private static Map<String, Predicate> getPredicates(String mln) throws Exception {
        Map<String, Predicate> map = new HashMap<>();
        for (String p : readFile(mln)) {
            p = p.trim();
            if ((p.length() > 0) && !p.startsWith("//") && (!p.contains("v") && !p.contains("\""))) {
                boolean observed = false;
                if (p.startsWith("*")) {
                    observed = true;
                    p = p.substring(1);
                }

                String name = p.substring(0, p.indexOf("(")).trim();

                List<String> types = new ArrayList<String>();
                Pattern pattern = Pattern.compile("([^\\s(),]+)");
                Matcher matcher = pattern.matcher(p.substring(p.indexOf("(")));
                while (matcher.find()) {
                    types.add(matcher.group(1));
                }

                Predicate pred = new Predicate(observed, name, types);
                map.put(name, pred);
            }
        }
        return map;
    }

    private static Set<Literal> generateNegatedGroundings(Map<String, Predicate> predicates,
            Map<String, List<String>> entities, Map<String, Set<Literal>> groundings,
            Map<String, String> predMapping, List<String> targetPredicates) {
        Set<Literal> allNewGroundings = new HashSet<>();

        for (String p : targetPredicates) {
            Predicate pred = predicates.get(p);

            // collect all groundings
            List<List<String>> groundValuesList = new ArrayList<>();
            Set<Literal> predGroundValues = new HashSet<>();

            if (groundings.get(p) != null) {
                predGroundValues.addAll(groundings.get(p));
            }

            if (predMapping.containsKey(p) && (groundings.get(predMapping.get(p)) != null)) {
                for (Literal g : groundings.get(predMapping.get(p))) {
                    List<String> tempValues = new ArrayList<>(g.getValues());
                    tempValues.remove(tempValues.size() - 1);
                    Literal tempG = new Literal(g.isNegated(), g.getPredicate(), tempValues);
                    predGroundValues.add(tempG);
                }
            }

            for (Literal g : predGroundValues) {
                groundValuesList.add(g.getValues());
            }

            // generate all groundings
            // get all entities for each element of the predicate
            List<List<String>> predEntities = new ArrayList<>();
            for (String type : pred.getTypes()) {
                predEntities.add(new ArrayList<>(entities.get(type)));
            }

            int elements = pred.getTypes().size();
            int combinations = 1;
            List<Integer> mod = new ArrayList<>(predEntities.size());
            for (int i = 0; i < predEntities.size(); i++) {
                mod.add(0);
            }

            for (int i = 0; i < predEntities.size(); i++) {
                List<String> e = predEntities.get(i);
                combinations *= e.size();
                for (int j = 0; j < i; j++) {
                    mod.set(j, mod.get(j) + e.size());
                }
            }
            mod.set(mod.size() - 1, 1);
            combinations *= elements;

            List<List<String>> allCombinations = new ArrayList<>();
            List<String> tempComp = new ArrayList<>();

            for (int i = 1; i <= combinations; i++) {
                int position = (i - 1) % elements;
                int entityNumber = (((i - 1) / elements) / mod.get(position))
                        % predEntities.get(position).size();
                // System.out.print(i + " -> " + position + " - " + eNumber);
                String nextEntity = predEntities.get(position).get(entityNumber);
                // System.out.println("\t" + nextEntity);
                tempComp.add(nextEntity);
                if ((i % elements) == 0) {
                    allCombinations.add(tempComp);
                    tempComp = new ArrayList<>();
                }
            }

            // new combinations
            allCombinations.removeAll(groundValuesList);
            Set<Literal> newGroundings = new HashSet<>();
            for (List<String> newGrounding : allCombinations) {
                newGroundings.add(new Literal(true, p, newGrounding));
            }
            if (groundings.containsKey(p)) {
                groundings.get(p).addAll(newGroundings);
            } else {
                groundings.put(p, newGroundings);
            }

            allNewGroundings.addAll(newGroundings);
        }
        return allNewGroundings;
    }

    private static List<String> transformMLN(String mln, Map<String, Predicate> predicates,
            Map<String, String> predMapping, Map<String, List<String>> entities,
            Map<String, Set<Literal>> groundings) throws Exception {
        List<String> formulas = readFile(mln);

        List<String> out = new ArrayList<>();
        for (String f : formulas) {
            out.add(f);
            if (!f.startsWith("// ?")) {
                continue;
            }

            String[] literals = f.replace("// ?", "").split(" v ");

            // use literals to create the formula object
            Formula formula = new Formula();
            for (String l : literals) {
                formula.addLiteral(new Literal(l));
            }

            List<String> groundForumulas = formula.getGroundFormulas(predicates, predMapping,
                    entities, groundings);
            out.addAll(groundForumulas);
            out.add("");
        }
        return out;
    }

    public static List<String> readFile(String f) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f),
                "UTF-8"));
        String line;
        List<String> lines = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }
        br.close();
        return lines;
    }

    public static void write(String f, List<String> data) throws Exception {
        Writer outWriter = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(f, false), "UTF-8"));
        for (String line : data) {
            outWriter.write(line + System.lineSeparator());
        }
        outWriter.flush();
        outWriter.close();
    }

}
