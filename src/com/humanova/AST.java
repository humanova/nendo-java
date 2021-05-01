package com.humanova;

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

    static class IdNode extends Expr {
        String id;

        IdNode(String id) {
            this.id = id;
        }

        public String toString() {
            return String.format("[IdNode %s]", id);
        }
    }

    static class Int extends Expr {
        long value;

        Int(long value) {
            this.value = value;
        }

        public String toString() {
            return String.format("[Int %s]", value);
        }
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

    static class Expr extends Node { }

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
