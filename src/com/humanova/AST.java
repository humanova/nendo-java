package com.humanova;

import java.util.ArrayList;

public class AST {
    public enum BinaryOpType {
        ADD,
        SUB,
        MUL,
        DIV,
        MOD
    }

    public static abstract class Node {
    }

    public static abstract class Stmt extends Node {

    }

    public static class LoopStmt extends Stmt {
        public AST.Expr iteration;
        public ArrayList<AST.AssignStmt> body; // can only be assign statements

        LoopStmt(AST.Expr iteration, ArrayList<AST.AssignStmt> body) {
            this.iteration = iteration;
            this.body = body;
        }

        public String toString() {
            return String.format("[LoopStmt (%s) : %s]", iteration, body);
        }
    }

    public static class AssignStmt extends Stmt {
        public AST.IdNode left;
        public AST.Expr right;
        public BinaryOpType op;

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

    public static class FuncDeclStmt extends Stmt {
        public AST.IdNode name;
        public AST.Expr body;
        public ArrayList<IdNode> params;

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

    public static class Expr extends Node { }

    public static class FuncCall extends Expr {
        public AST.IdNode name;
        public ArrayList<AST.Node> args; // can be an idNode or expr

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

    public static class IdNode extends Expr {
        public String id;

        IdNode(String id) {
            this.id = id;
        }

        public String toString() {
            return String.format("[IdNode %s]", id);
        }
    }

    public static class Num extends Expr {
        public double value;

        Num(double value) {
            this.value = value;
        }

        public String toString() {
            return String.format("[Num %s]", value);
        }
    }

    public static class BinaryOp extends Expr {
        public BinaryOpType op;
        public AST.Expr left;
        public AST.Expr right;

        BinaryOp(BinaryOpType op, AST.Expr left, AST.Expr right) {
            this.op = op;
            this.left = left;
            this.right = right;
        }

        public String toString() {
            return String.format("[BinaryOp %s (%s %s)]", op.name(), left, right);
        }
    }

    public static class UnaryOp extends Expr {
        public BinaryOpType op;
        public AST.Expr child;

        UnaryOp(BinaryOpType op, AST.Expr child) {
            this.op = op;
            this.child = child;
        }

        public String toString() {
            return String.format("[UnaryOp %s (%s)]", op.name(), child);
        }
    }

}
