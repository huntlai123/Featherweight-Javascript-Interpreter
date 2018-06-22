package edu.sjsu.fwjs;

import java.util.Map;
import java.util.HashMap;

public class Environment {
    private Map<String,Value> env = new HashMap<String,Value>();
    private Environment outerEnv;

    /**
     * Constructor for global environment
     */
    public Environment() {}

    /**
     * Constructor for local environment of a function
     */
    public Environment(Environment outerEnv) {
        this.outerEnv = outerEnv;
    }

    /**
     * Handles the logic of resolving a variable.
     * If the variable name is in the current scope, it is returned.
     * Otherwise, search for the variable in the outer scope.
     * If we are at the outermost scope (AKA the global scope)
     * null is returned (similar to how JS returns undefined.
     */
    public Value resolveVar(String varName) {
        Value val = env.get(varName);
        if(val == null && outerEnv != null)
            return outerEnv.resolveVar(varName);
        else
            return val;
    }

    /**
     * Used for updating existing variables.
     * If a variable has not been defined previously in the current scope,
     * or any of the function's outer scopes, the var is stored in the global scope.
     */
    public void updateVar(String key, Value v) {
        Value oldVal = resolveVar(key);
        if(oldVal == null)
        {
            Environment next = outerEnv;
            while(next != null)
                next = next.outerEnv;
            env.put(key, v);
        }
        else
            env.put(key, v);
    }

    /**
     * Creates a new variable in the local scope.
     * If the variable has been defined in the current scope previously,
     * a RuntimeException is thrown.
     */
    public void createVar(String key, Value v) {
        if(!env.containsKey(key))
            env.put(key, v);
        else
            throw new RuntimeException();
    }
}
