package com.humanova;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import com.humanova.Symbol.*;

public class Interpreter {
    Lexer lexer;
    Parser parser;

    AST.Node topNode;
    AST.Node currentNode;

    int currentScope = 0;
    ArrayList<Var> variableList;
    Stack<Double> functionStack;
    ArrayList<Function> functionList;

    // these are immutable
    HashMap<String, Symb> symbolMap = new HashMap<String, Symb>() {{
        put("pi",    new Var("pi", 3.14159265358979323, -1));
        put("euler", new Var("euler", 2.718281828459045235, -1));
        put("phi",   new Var("phi", 1.6180339887498, -1));
        put("sqrt",  new SqrtFunction());
        put("abs",   new AbsFunction());
        put("ceil",  new CeilFunction());
        put("floor", new FloorFunction());
        put("log",   new LogFunction());
        put("log10", new Log10Function());
        put("sin",   new SinFunction());
        put("cos",   new CosFunction());
        put("acos",  new AcosFunction());
        put("asin",  new AsinFunction());
        put("atan",  new AtanFunction());
        put("pow",  new PowFunction());
    }};

    public Interpreter() {
        lexer = new Lexer();
        parser = new Parser();
        variableList = new ArrayList<Var>();
        functionList = new ArrayList<Function>();
        functionStack = new Stack<Double>();
    }

    public void interpret(String text) {
        ArrayList<Token> tokens = lexer.generateTokens(text);
        AST.Node ast = parser.parse(tokens);

        topNode = ast;
        currentNode = ast;

        if (ast instanceof AST.Stmt) {
            visitStmt((AST.Stmt) ast);
        }
        else if (ast instanceof AST.Expr) {
            double val = visitExpr(((AST.Expr) ast));
            System.out.printf("%s\n", val);
        }
        else if (ast == null) {
            // may be a comment or empty space
            // nothing to do, skip this one
        }
        else {
            raiseInterpreterError("invalid AST");
        }
    }

    public void interpret(AST.Node ast) {
        topNode = ast;
        currentNode = ast;

        if (ast instanceof AST.Stmt) {
            visitStmt((AST.Stmt) ast);
        }
        else if (ast instanceof AST.Expr) {
            double val = visitExpr(((AST.Expr) ast));
            System.out.printf("%s\n", val);
        }
        else if (ast == null) {
            // may be a comment or empty space
            // nothing to do, skip this one
        }
        else {
            raiseInterpreterError("invalid AST");
        }
    }

    private Var getVariable(String name) {
        // if we are executing a function
        if (currentScope != 0 && !symbolMap.containsKey(name)) {
            for (Var v : variableList) {
                // variable must be in the same scope or must be a global constant
                if (v.name.equals(name) && v.scope == currentScope)
                    return v;
            }
        }
        else if (symbolMap.containsKey(name) && symbolMap.get(name) instanceof Var)
            return (Var)symbolMap.get(name);
        return null;
    }

    private Function getFunction(String name) {
        if (symbolMap.containsKey(name) && symbolMap.get(name) instanceof Function)
            return (Function)symbolMap.get(name);
        return null;
    }

    private void visitStmt(AST.Stmt stmt) {
        //implement assignstmt and funcdeclstmt
        if (stmt instanceof AST.AssignStmt) {
            visitAssignStmt((AST.AssignStmt) stmt);
        }
        else if (stmt instanceof AST.FuncDeclStmt) {
            visitFuncDeclStmt((AST.FuncDeclStmt) stmt);
        }
    }

    private void visitFuncDeclStmt(AST.FuncDeclStmt stmt) {
        Function fn = getFunction(stmt.name.id);
        // if there is not any variable&func defined as the same with the funcDecl
        if (fn == null && getVariable(stmt.name.id) == null) {
            fn = new Function(stmt.name.id, stmt.params, stmt.body);
            functionList.add(fn);
            symbolMap.put(stmt.name.id, fn);
        }
        // if there isn't a variable named the same with the function, update the function body
        else if (getVariable(stmt.name.id) == null && !(fn instanceof BuiltinFunction)) {
            fn.body = stmt.body;
            fn.params = new ArrayList<String>();
            for (AST.IdNode param : stmt.params) {
                fn.params.add(param.id);
            }
        }
        else if (fn instanceof BuiltinFunction) {
            raiseInterpreterError("built-in functions can not be declared");
        }
        else {
            raiseInterpreterError("invalid function declaration");
        }
    }

    private void visitAssignStmt(AST.AssignStmt stmt) {
        Var v = getVariable(stmt.left.id);
        double rVal = visitExpr(stmt.right);
        if (v != null && v.scope != -1) {
            if (stmt.op == null) {
                v.value = rVal;
            } else {
                v.value = doBinaryOp(v.value, rVal, stmt.op);
            }
        }
        else if (v == null && stmt.op == null) {
            v = new Var(stmt.left.id, rVal, 0);
            variableList.add(v);
            symbolMap.put(v.name, v);
        }
        else if (v.scope == -1) {
            raiseInterpreterError("built-in constants are immutable");
        }
        else {
            raiseInterpreterError("invalid assign statement");
        }
    }

    private double visitExpr(AST.Expr expr) {
        currentNode = expr;
        double val = 0; // had to initialize

        if (expr instanceof AST.IdNode) {
            Var v = getVariable(((AST.IdNode)expr).id);
            if (v != null){
                val = v.value;
            } else {
                raiseInterpreterError("variable doesn't exist");
            }
        }
        else if (expr instanceof AST.Num) {
            val = ((AST.Num) expr).value;
        }
        else if (expr instanceof AST.BinaryOp) {
            double v1 = visitExpr(((AST.BinaryOp) expr).left);
            double v2 = visitExpr(((AST.BinaryOp) expr).right);
            val =  doBinaryOp(v1, v2, ((AST.BinaryOp) expr).op);
        }
        else if (expr instanceof AST.UnaryOp) {
            BinaryOpType op = ((AST.UnaryOp) expr).op;
            if (op == BinaryOpType.ADD) {
                val = +visitExpr(((AST.UnaryOp) expr).child);
            }
            else if (op == BinaryOpType.SUB) {
                val = -visitExpr(((AST.UnaryOp) expr).child);
            }
        }
        else if (expr instanceof AST.FuncCall) {
            Function fn = getFunction(((AST.FuncCall) expr).name.id);

            if (fn instanceof BuiltinFunction) {
                int argCount = ((AST.FuncCall) expr).args.size();
                if (fn instanceof BuiltinOneArgFunction && argCount == 1) {
                    double argVal = visitExpr((AST.Expr)((AST.FuncCall) expr).args.get(0));
                    val = ((BuiltinOneArgFunction<Double>) fn).execute(argVal);
                }
                else if (fn instanceof BuiltinTwoArgFunction && argCount == 2) {
                    double argVal1 = visitExpr((AST.Expr)((AST.FuncCall) expr).args.get(0));
                    double argVal2 = visitExpr((AST.Expr)((AST.FuncCall) expr).args.get(1));
                    val = ((BuiltinTwoArgFunction<Double, Double>) fn).execute(argVal1, argVal2);
                }
                else {
                    raiseInterpreterError("invalid built-in function argument count");
                }
            }
            else {
                // check params size == args size
                // add fn args to stack
                // currentScope += 1
                // add fn args to variable list
                // visit the function body
                // currentScope -= 1
                // return the value
                if (fn != null && fn.params.size() == ((AST.FuncCall) expr).args.size()) {
                    // push arguments to stack first (to support nested func calls)
                    for (int i = 0; i < fn.params.size(); i++) {
                        double argValue = visitExpr((AST.Expr)((AST.FuncCall) expr).args.get(i));
                        functionStack.push(argValue);
                    }
                    currentScope++;
                    // create temp variables with the arg names
                    for (int i = 0; i < fn.params.size(); i++) {
                        int idx = fn.params.size()-1-i;
                        variableList.add(new Var(fn.params.get(idx), functionStack.pop(), currentScope));
                    }
                    val = visitExpr(fn.body);
                    currentScope--;
                    // remove function arguments from the list
                    for (int i = 0; i < fn.params.size(); i++) {
                        variableList.remove(variableList.size()-1);
                    }
                } else {
                    raiseInterpreterError("invalid function call");
                }
            }
        }
        else {
            raiseInterpreterError("invalid expression");
        }

        return val;
    }

    public double doBinaryOp(double v1, double v2, BinaryOpType op) {
        double res = -Double.MAX_VALUE;
        switch (op) {
            case ADD:
                res = v1 + v2;
                break;
            case SUB:
                res =  v1 - v2;
                break;
            case MUL:
                res = v1 * v2;
                break;
            case DIV:
                res =  v1 / v2;
                break;
            case MOD:
                res = v1 % v2;
                break;
            case LOGICALOR:
                res = (v1 == 0.0 && v2 == 0.0) ? 0.0 : 1.0; // C-like logic, if one isn't zero then its 'true'
                break;
            case LOGICALAND:
                res = (v1 == 0.0 || v2 == 0.0) ? 0.0 : 1.0; // C-like logic, if both isn't zero then its 'true'
                break;
        }
        return res;
    }

    private void raiseInterpreterError(String err) {
        throw new RuntimeException(String.format("[Interpreter] Invalid tree structure : %s\n\tcurrent node : '%s'", err ,currentNode.toString()));
    }
}