package com.humanova;

import java.util.ArrayList;

public class Symbol {
    public static abstract class Symb {
    }

    public static class Var extends Symb {
        public String name;
        public double value; // double cuz i am lazy...
        public int scope;

        public Var(String name, double value, int scope) {
            this.name = name;
            this.value = value;
            this.scope = scope;
        }
    }

    public static class Function extends Symb {
        public String name;
        public ArrayList<String> params;
        public AST.Expr body;

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

    public static class BuiltinFunction extends Function {
        public BuiltinFunction(String name) {
            super(name, null);
        }
    }

    public static abstract class BuiltinOneArgFunction<T> extends BuiltinFunction {
        public BuiltinOneArgFunction(String name) {
            super(name);
        }
        abstract double execute(T arg);
    }

    public static abstract class BuiltinTwoArgFunction<T,D> extends BuiltinFunction {
        public BuiltinTwoArgFunction(String name) {
            super(name);
        }
        abstract double execute(T arg1, D arg2);
    }

    public static class SqrtFunction extends BuiltinOneArgFunction<Double> {
        public SqrtFunction() { super("sqrt"); }
        double execute(Double arg) {
            return Math.sqrt(arg);
        }
    }

    public static class AbsFunction extends BuiltinOneArgFunction<Double> {
        public AbsFunction() { super("abs"); }
        double execute(Double arg) {
            return Math.abs(arg);
        }
    }

    public static class CeilFunction extends BuiltinOneArgFunction<Double> {
        public CeilFunction() { super("ceil"); }
        double execute(Double arg) {
            return Math.ceil(arg);
        }
    }

    public static class FloorFunction extends BuiltinOneArgFunction<Double> {
        public FloorFunction() { super("floor"); }
        double execute(Double arg) {
            return Math.floor(arg);
        }
    }

    public static class LogFunction extends BuiltinOneArgFunction<Double> {
        public LogFunction() { super("log"); }
        double execute(Double arg) {
            return Math.log(arg);
        }
    }

    public static class Log10Function extends BuiltinOneArgFunction<Double> {
        public Log10Function() { super("log10"); }
        double execute(Double arg) {
            return Math.log10(arg);
        }
    }

    public static class SinFunction extends BuiltinOneArgFunction<Double> {
        public SinFunction() { super("sin"); }
        double execute(Double arg) {
            return Math.sin(arg);
        }
    }

    public static class CosFunction extends BuiltinOneArgFunction<Double> {
        public CosFunction() { super("cos"); }
        double execute(Double arg) {
            return Math.cos(arg);
        }
    }

    public static class AcosFunction extends BuiltinOneArgFunction<Double> {
        public AcosFunction() { super("acos"); }
        double execute(Double arg) {
            return Math.acos(arg);
        }
    }

    public static class AsinFunction extends BuiltinOneArgFunction<Double> {
        public AsinFunction() { super("asin"); }
        double execute(Double arg) {
            return Math.asin(arg);
        }
    }

    public static class AtanFunction extends BuiltinOneArgFunction<Double> {
        public AtanFunction() { super("atan"); }
        double execute(Double arg) {
            return Math.atan(arg);
        }
    }

    public static class PowFunction extends BuiltinTwoArgFunction<Double, Double> {
        public PowFunction() { super("pow"); }
        double execute(Double arg1, Double arg2) {
            return Math.pow(arg1, arg2);
        }
    }
}
