package com.humanova;

import java.util.ArrayList;

public class Symbol {
    static abstract class Symb {
    }

    static class Var extends Symb {
        String name;
        double value; // double cuz i am lazy...
        int scope;

        public Var(String name, double value, int scope) {
            this.name = name;
            this.value = value;
            this.scope = scope;
        }
    }

    static class Function extends Symb {
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

    static abstract class BuiltinFunction<T> extends Function {
        public BuiltinFunction(String name) {
            super(name, null);
        }
        abstract double execute(T arg);
    }

    static class SqrtFunction extends BuiltinFunction<Double> {
        public SqrtFunction() { super("sqrt"); }
        double execute(Double arg) {
            return Math.sqrt(arg);
        }
    }

    static class AbsFunction extends BuiltinFunction<Double> {
        public AbsFunction() { super("abs"); }
        double execute(Double arg) {
            return Math.abs(arg);
        }
    }

    static class CeilFunction extends BuiltinFunction<Double> {
        public CeilFunction() { super("ceil"); }
        double execute(Double arg) {
            return Math.ceil(arg);
        }
    }

    static class FloorFunction extends BuiltinFunction<Double> {
        public FloorFunction() { super("floor"); }
        double execute(Double arg) {
            return Math.floor(arg);
        }
    }

    static class LogFunction extends BuiltinFunction<Double> {
        public LogFunction() { super("log"); }
        double execute(Double arg) {
            return Math.log(arg);
        }
    }

    static class Log10Function extends BuiltinFunction<Double> {
        public Log10Function() { super("log10"); }
        double execute(Double arg) {
            return Math.log10(arg);
        }
    }

    static class SinFunction extends BuiltinFunction<Double> {
        public SinFunction() { super("sin"); }
        double execute(Double arg) {
            return Math.sin(arg);
        }
    }

    static class CosFunction extends BuiltinFunction<Double> {
        public CosFunction() { super("cos"); }
        double execute(Double arg) {
            return Math.cos(arg);
        }
    }

    static class AcosFunction extends BuiltinFunction<Double> {
        public AcosFunction() { super("acos"); }
        double execute(Double arg) {
            return Math.acos(arg);
        }
    }

    static class AsinFunction extends BuiltinFunction<Double> {
        public AsinFunction() { super("asin"); }
        double execute(Double arg) {
            return Math.asin(arg);
        }
    }

    static class AtanFunction extends BuiltinFunction<Double> {
        public AtanFunction() { super("atan"); }
        double execute(Double arg) {
            return Math.atan(arg);
        }
    }
}
