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
    	Environment current = this;
        Value val = current.env.get(varName);
        
        //Nick: multiple outer environments!!  Search them all, like peeling an onion!
        while (current.env.get(varName) == null && current.outerEnv != null) {
        	current = current.outerEnv;
        	val = current.env.get(varName);
        }
        
        if (val == null)
        	return new NullVal();
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
        Environment next = outerEnv;
        if(oldVal == null)
        {
            /*
            Environment next = outerEnv;
            while(next != null)
                next = next.outerEnv;
            env.put(key, v);
            */
            
            while(next.outerEnv != null)
                next = next.outerEnv;
            next.env.put(key, v);
            
        }
        else
        {
            //env.put(key, v);
            
            while (next.env.get(key) == null && next.outerEnv != null)
                next = next.outerEnv;
            next.env.put(key, v);
        } 
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
