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
    abstract class Node {
        int line;
    }

    class IdNode extends Node {
        String id;

        IdNode(String id) {
            this.id = id;
        }

        public String toString() {
            return String.format("[IdNode %s]", id);
        }
    }

    class Int extends Node {
        long value;

        Int(long value) {
            this.value = value;
        }

        public String toString() {
            return String.format("[Int %s]", value);
        }
    }

    abstract class Stmt extends Node {

    }

    class AssignStmt extends Stmt {
        AST.Expr left;
        AST.Expr right;
        BinaryOpType op;

        AssignStmt(AST.Expr left, AST.Expr right) {
            this.left = left;
            this.right = right;
        }

        AssignStmt(AST.Expr left, AST.Expr right, BinaryOpType op) {
            this.left = left;
            this.right = right;
            this.op = op;
        }

        public String toString() {
            return String.format("[AssignStmt %s (%s %s)]", op.name(), left.line, right.line);
        }
    }

    class Expr extends Node { }

    class BinaryOp extends Expr {
        BinaryOpType op;
        AST.Expr left;
        AST.Expr right;

        BinaryOp(BinaryOpType op, AST.Expr left, AST.Expr right) {
            this.op = op;
            this.left = left;
            this.right = right;
        }

        public String toString() {
            return String.format("[BinaryOp %s (%s %s)]", op.name(), left.line, right.line);
        }
    }

    class UnaryOp extends Expr {
        BinaryOpType op;
        AST.Expr child;

        UnaryOp(BinaryOpType op, AST.Expr child) {
            this.op = op;
            this.child = child;
        }

        public String toString() {
            return String.format("[UnaryOp %s (%s)]", op.name(), child.line);
        }
    }

}
