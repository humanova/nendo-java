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

    static class BuiltinFunction extends Function {
        public BuiltinFunction(String name) {
            super(name, null);
        }
    }

    static abstract class BuiltinOneArgFunction<T> extends BuiltinFunction {
        public BuiltinOneArgFunction(String name) {
            super(name);
        }
        abstract double execute(T arg);
    }

    static abstract class BuiltinTwoArgFunction<T,D> extends BuiltinFunction {
        public BuiltinTwoArgFunction(String name) {
            super(name);
        }
        abstract double execute(T arg1, D arg2);
    }

    static class SqrtFunction extends BuiltinOneArgFunction<Double> {
        public SqrtFunction() { super("sqrt"); }
        double execute(Double arg) {
            return Math.sqrt(arg);
        }
    }

    static class AbsFunction extends BuiltinOneArgFunction<Double> {
        public AbsFunction() { super("abs"); }
        double execute(Double arg) {
            return Math.abs(arg);
        }
    }

    static class CeilFunction extends BuiltinOneArgFunction<Double> {
        public CeilFunction() { super("ceil"); }
        double execute(Double arg) {
            return Math.ceil(arg);
        }
    }

    static class FloorFunction extends BuiltinOneArgFunction<Double> {
        public FloorFunction() { super("floor"); }
        double execute(Double arg) {
            return Math.floor(arg);
        }
    }

    static class LogFunction extends BuiltinOneArgFunction<Double> {
        public LogFunction() { super("log"); }
        double execute(Double arg) {
            return Math.log(arg);
        }
    }

    static class Log10Function extends BuiltinOneArgFunction<Double> {
        public Log10Function() { super("log10"); }
        double execute(Double arg) {
            return Math.log10(arg);
        }
    }

    static class SinFunction extends BuiltinOneArgFunction<Double> {
        public SinFunction() { super("sin"); }
        double execute(Double arg) {
            return Math.sin(arg);
        }
    }

    static class CosFunction extends BuiltinOneArgFunction<Double> {
        public CosFunction() { super("cos"); }
        double execute(Double arg) {
            return Math.cos(arg);
        }
    }

    static class AcosFunction extends BuiltinOneArgFunction<Double> {
        public AcosFunction() { super("acos"); }
        double execute(Double arg) {
            return Math.acos(arg);
        }
    }

    static class AsinFunction extends BuiltinOneArgFunction<Double> {
        public AsinFunction() { super("asin"); }
        double execute(Double arg) {
            return Math.asin(arg);
        }
    }

    static class AtanFunction extends BuiltinOneArgFunction<Double> {
        public AtanFunction() { super("atan"); }
        double execute(Double arg) {
            return Math.atan(arg);
        }
    }

    static class PowFunction extends BuiltinTwoArgFunction<Double, Double> {
        public PowFunction() { super("pow"); }
        double execute(Double arg1, Double arg2) {
            return Math.pow(arg1, arg2);
        }
    }
}
