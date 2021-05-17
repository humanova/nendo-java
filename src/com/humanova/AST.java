package com.humanova;

import java.util.ArrayList;

enum BinaryOpType {
    ADD,
    SUB,
    MUL,
    DIV,
    OR,
    AND,
    XOR,
    MOD
}

public class AST {
    static abstract class Node {
        int line;
    }

    static abstract class Stmt extends Node {

    }

    static class AssignStmt extends Stmt {
        AST.IdNode left;
        AST.Expr right;
        BinaryOpType op;

        AssignStmt(AST.IdNode left, AST.Expr right) {
            this.left = left;
            this.right = right;
        }

        AssignStmt(AST.IdNode left, AST.Expr right, BinaryOpType op) {
            this.left = left;
            this.right = right;
            this.op = op;
        }

        public String toString() {
            String opName = "";
            if (op != null) {
                opName = op.name();
            }
            return String.format("[AssignStmt %s (%s %s)]", opName, left, right);
        }
    }

    static class FuncDeclStmt extends Stmt {
        AST.IdNode name;
        AST.Expr body;
        ArrayList<IdNode> params;

        FuncDeclStmt(AST.IdNode name, AST.Expr body, ArrayList<IdNode> params) {
            this.name = name;
            this.body = body;
            this.params = params;
        }

        public String toString() {
            String paramsStr = "";
            if (params != null) {
                paramsStr = params.toString();
            }
            return String.format("[FuncDecl %s (%s) = %s]", name, paramsStr, body);
        }

    }

    static class Expr extends Node { }

    static class FuncCall extends Expr {
        AST.IdNode name;
        ArrayList<AST.Node> args; // can be an idNode or expr

        FuncCall(AST.IdNode name, ArrayList<AST.Node> args) {
            this.name = name;
            this.args = args;
        }

        FuncCall(AST.IdNode name) {
            this.name = name;
        }

        public String toString() {
            String argsStr = "";
            if (args != null) {
                argsStr = args.toString();
            }
            return String.format("[FuncCall %s (%s)]", name, argsStr);
        }
    }

    static class IdNode extends Expr {
        String id;

        IdNode(String id) {
            this.id = id;
        }

        public String toString() {
            return String.format("[IdNode %s]", id);
        }
    }

    static class Num extends Expr {
        double value;

        Num(double value) {
            this.value = value;
        }

        public String toString() {
            return String.format("[Num %s]", value);
        }
    }

    static class BinaryOp extends Expr {
        BinaryOpType op;
        AST.Expr left;
        AST.Expr right;

        BinaryOp(BinaryOpType op, AST.Expr left, AST.Expr right) {
            this.op = op;
            this.left = left;
            this.right = right;
        }

        public String toString() {
            return String.format("[BinaryOp %s (%s %s)]", op.name(), left, right);
        }
    }

    static class UnaryOp extends Expr {
        BinaryOpType op;
        AST.Expr child;

        UnaryOp(BinaryOpType op, AST.Expr child) {
            this.op = op;
            this.child = child;
        }

        public String toString() {
            return String.format("[UnaryOp %s (%s)]", op.name(), child);
        }
    }

}
