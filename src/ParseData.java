import java.util.*;

public class ParseData {

    private static final List<String> PRIMITIVE_TYPES = Arrays.asList("Num","Point","Bool","Color");

    private List<Variable> variables;
    private List<Action> actions;
    private List<Type> types;
    private List<Shape> shapes;

    public ParseData() {
        variables = new ArrayList<>();
        actions = new ArrayList<>();
        types = new ArrayList<>();
        shapes = new ArrayList<>();
    }

    public boolean canAssignTo(String identifier) {
        return !PRIMITIVE_TYPES.contains(identifier)
                && shapes.stream().noneMatch(i->(i.getName().equals(identifier)))
                && actions.stream().noneMatch(i->(i.getName().equals(identifier)))
                && types.stream().noneMatch(i->(i.getName().equals(identifier)))
                && variables.stream().noneMatch(i->(i.getName().equals(identifier) && (i.getAssignment() != null)));
    }

    public void assignTo(Expression target, Expression assignment, boolean action, Stack<Structure> context, List<Error> errors) {

        switch (target.getType()) {
            case TYPE:

            case VARIABLE:
                String id = target.getSrcToken().getData();
                if ()
            case INDEX:
                yield canAssignTo(expr.getChild(0));
            default:
                yield false;
        };
    }

    public class Identifier {
        private String name;
        public Identifier(String name) {
            this.name = name;
        }
        public String getName() {return name;}
    }
    public class Variable extends Identifier {
        private String name;
        private Type type;
        boolean isList;
        private Expression assignment;
        public Variable(String name, Type type, boolean isList, Expression assignment) {
            super(name);
            this.type = type;
            this.isList = isList;
            this.assignment = assignment;
        }
        public Type getType() {return type;}
        public void setAssignment(Expression expr) {this.assignment = expr;}
        public Expression getAssignment() {return assignment;}
    }
    public class Function extends Variable {
        private Map<String,Type> parameters;

        public Function(String name, Type type, Map<String,Type> params, boolean isList, Expression assignment) {
            super(name, type, isList, assignment);
            this.parameters = params;
        }
        public Function(String name, Type type, Map<String,Type> params, boolean isList, Expression assignment) {
            super(name, type, isList, assignment);
            this.parameters = params;
        }
        public Map<String, Type> getParameters() {return parameters;}
    }

    public class Structure extends Identifier {
        private List<Variable> innerVars;
        public Structure(String name) {
            super(name);
            innerVars = new ArrayList<>();
        }
        public void addVariable(Variable var) {innerVars.add(var);}
        public List<Variable> getInnerVars() {return innerVars;}
    }

    public class Shape extends Structure {
        private Map<String,Type> parameters;
        public Shape(String name, Map<String,Type> params) {
            super(name);
            this.parameters = params;
        }


        public Map<String, Type> getParameters() {return parameters;}
    }
    public class Type extends Structure {
        private List<Function> operators;
        private List<Function> constructors;

        public Type(String name) {
            super(name);
        }
    }

    public class Action extends Structure {
        private Map<String,Type> parameters;
        List<Action> innerActions;
        public Action(String name, Map<String,Type> params) {
            super(name);
            this.parameters = params;
        }
    }
}
