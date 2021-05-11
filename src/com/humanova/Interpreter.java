package com.humanova;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Interpreter {
    Lexer lexer;
    Parser parser;

    AST.Node topNode;
    AST.Node currentNode;

    int currentScope = 0;
    ArrayList<Var> variableList;
    ArrayList<Function> functionList;
    HashMap<String, Symbol> symbolMap = new HashMap<String, Symbol>() {{
        put("pi",  new Var("pi", 3.141592653589, 0));
    }};

    static abstract class Symbol {
    }

    static class Var extends Symbol {
        String name;
        double value; // double cuz i am lazy...
        int scope;

        public Var(String name, double value, int scope) {
            this.name = name;
            this.value = value;
            this.scope = scope;
        }
    }

    static class Function extends Symbol {
        String name;
        ArrayList<String> params;
        AST.Expr body;

        public Function(String name, AST.Expr body) {
            this.name = name;
            this.body = body;
        }

        public Function(String name, ArrayList<AST.IdNode> params, AST.Expr body) {
            this.name = name;
            this.body = body;

            this.params = new ArrayList<String>();
            for (AST.IdNode id : params) {
                this.params.add(id.id);
            }
        }
    }

    public Interpreter() {
        lexer = new Lexer();
        parser = new Parser();
        variableList = new ArrayList<Var>();
        functionList = new ArrayList<Function>();

        Iterator it = symbolMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if (pair instanceof Var)
                variableList.add((Var) pair);
            else if (pair instanceof Function)
                functionList.add((Function) pair);
        }
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
        else {
            raiseInterpreterError();
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
        else {
            raiseInterpreterError();
        }
    }

    private Var getVariable(String name) {
        // if we are executing a function
        if (currentScope != 0) {
            for (Var v : variableList) {
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
        else if (getVariable(stmt.name.id) == null) {
            fn.body = stmt.body;
            fn.params = new ArrayList<String>();
            for (AST.IdNode param : stmt.params) {
                fn.params.add(param.id);
            }
        }
        else {
            raiseInterpreterError();
        }
    }

    private void visitAssignStmt(AST.AssignStmt stmt) {
        Var v = getVariable(stmt.left.id);
        double rVal = visitExpr(stmt.right);
        if (v != null) {
            if (stmt.op == null) {
                v.value = rVal;
            } else {
                v.value = doBinaryOp(v.value, rVal, stmt.op);
            }
        }
        else if (stmt.op == null) {
            v = new Var(stmt.left.id, rVal, 0);
            variableList.add(v);
            symbolMap.put(v.name, v);
        } else {
            raiseInterpreterError();
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
                raiseInterpreterError(); // todo: inform 'variable doesn't exist'
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
            // check params size == args size
            // currentScope += 1
            // add fn args to variables list (with the new scope)
            // visit the function body
            // currentScope -= 1
            // return the evaluated expression
            Function fn = getFunction(((AST.FuncCall) expr).name.id);
            if (fn != null && fn.params.size() == ((AST.FuncCall) expr).args.size()) {
                currentScope++;
                for (int i = 0; i < fn.params.size(); i++) {
                    double value = visitExpr((AST.Expr)((AST.FuncCall) expr).args.get(i));
                    variableList.add(new Var(fn.params.get(i), value, currentScope));
                }
                val = visitExpr(fn.body);
                currentScope--;
                // remove function arguments from the list
                for (int i = 0; i < fn.params.size(); i++) {
                    variableList.remove(variableList.size()-1);
                }
            } else {
                raiseInterpreterError();
            }
        }
        else {
            raiseInterpreterError(); // todo: inform about the error
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

    private void raiseInterpreterError() {
        throw new RuntimeException(String.format("[Interpreter] Invalid tree structure, current node : '%s'", currentNode.toString()));
    }
}