package com.humanova;

import java.util.ArrayList;

public class Interpreter {
    Lexer lexer;
    Parser parser;

    AST.Node topNode;
    AST.Node currentNode;
    ArrayList<Var> variableList;
    int currentScope = 0;

    static class Var {
        String name;
        int scope;
        double value; // double cuz i am lazy...

        public Var(String name, int scope, double value) {
            this.name = name;
            this.scope = scope;
            this.value = value;
        }
    }

    public Interpreter() {
        lexer = new Lexer();
        parser = new Parser();
        variableList = new ArrayList<Var>();
    }

    public void interpret(String text) {
        ArrayList<Token> tokens = lexer.generateTokens(text);
        AST.Node ast = parser.parse(tokens);

        // System.out.println("Tokens : " + tokens.toString());
        // System.out.println("AST : " + ast.toString());

        topNode = ast;
        currentNode = ast;

        if (ast instanceof AST.AssignStmt) {
            visitAssignStmt((AST.AssignStmt) ast);
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

        // System.out.println("Tokens : " + tokens.toString());
        // System.out.println("AST : " + ast.toString());

        if (ast instanceof AST.AssignStmt) {
            visitAssignStmt((AST.AssignStmt) ast);
        }
        else if (ast instanceof AST.Expr) {
            double val = visitExpr(((AST.Expr) ast));
            System.out.printf("%s\n", val);
        }
        else {
            raiseInterpreterError();
        }
    }

    private Var getVariable(String name, int scope) {
        for (Var v : variableList) {
            if (v.name.equals(name) && v.scope == scope) {
                return v;
            }
        }
        return null;
    }

    private void visitAssignStmt(AST.AssignStmt assignStmt) {
        Var v = getVariable(assignStmt.left.id, currentScope);
        double rVal = visitExpr(assignStmt.right);
        if (v != null) {
            if (assignStmt.op == null) {
                v.value = rVal;
            } else {
                v.value = doBinaryOp(v.value, rVal, assignStmt.op);
            }
        }
        else if (assignStmt.op == null) {
            v = new Var(assignStmt.left.id, currentScope, rVal);
            variableList.add(v);
        } else {
            raiseInterpreterError();
        }
    }

    private double visitExpr(AST.Expr expr) {
        currentNode = expr;
        double val = 0; // had to initialize

        if (expr instanceof AST.IdNode) {
            Var v = getVariable(((AST.IdNode)expr).id , currentScope);
            if (v != null){
                val = v.value;
            } else {
                raiseInterpreterError(); // todo: inform 'variable doesn't exist'
            }
        } else if (expr instanceof AST.Num) {
            val = ((AST.Num) expr).value;
        } else if (expr instanceof AST.BinaryOp) {
            double v1 = visitExpr(((AST.BinaryOp) expr).left);
            double v2 = visitExpr(((AST.BinaryOp) expr).right);
            val =  doBinaryOp(v1, v2, ((AST.BinaryOp) expr).op);
        } else if (expr instanceof AST.UnaryOp) {
            BinaryOpType op = ((AST.UnaryOp) expr).op;
            if (op == BinaryOpType.ADD) {
                val = +visitExpr(((AST.UnaryOp) expr).child);
            }
            else if (op == BinaryOpType.SUB) {
                val = -visitExpr(((AST.UnaryOp) expr).child);
            }
        } else {
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