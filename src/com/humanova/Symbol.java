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

    public static abstract class BuiltinOneArgFunction extends BuiltinFunction {
        public BuiltinOneArgFunction(String name) {
            super(name);
        }
        abstract double execute(double arg);
    }

    public static abstract class BuiltinTwoArgFunction extends BuiltinFunction {
        public BuiltinTwoArgFunction(String name) {
            super(name);
        }
        abstract double execute(double arg1, double arg2);
    }

    public static class SqrtFunction extends BuiltinOneArgFunction {
        public SqrtFunction() { super("sqrt"); }
        double execute(double arg) {
            return Math.sqrt(arg);
        }
    }

    public static class AbsFunction extends BuiltinOneArgFunction {
        public AbsFunction() { super("abs"); }
        double execute(double arg) {
            return Math.abs(arg);
        }
    }

    public static class CeilFunction extends BuiltinOneArgFunction {
        public CeilFunction() { super("ceil"); }
        double execute(double arg) {
            return Math.ceil(arg);
        }
    }

    public static class FloorFunction extends BuiltinOneArgFunction {
        public FloorFunction() { super("floor"); }
        double execute(double arg) {
            return Math.floor(arg);
        }
    }

    public static class LogFunction extends BuiltinOneArgFunction {
        public LogFunction() { super("log"); }
        double execute(double arg) {
            return Math.log(arg);
        }
    }

    public static class Log10Function extends BuiltinOneArgFunction {
        public Log10Function() { super("log10"); }
        double execute(double arg) {
            return Math.log10(arg);
        }
    }

    public static class SinFunction extends BuiltinOneArgFunction {
        public SinFunction() { super("sin"); }
        double execute(double arg) {
            return Math.sin(arg);
        }
    }

    public static class CosFunction extends BuiltinOneArgFunction {
        public CosFunction() { super("cos"); }
        double execute(double arg) {
            return Math.cos(arg);
        }
    }

    public static class AcosFunction extends BuiltinOneArgFunction {
        public AcosFunction() { super("acos"); }
        double execute(double arg) {
            return Math.acos(arg);
        }
    }

    public static class AsinFunction extends BuiltinOneArgFunction {
        public AsinFunction() { super("asin"); }
        double execute(double arg) {
            return Math.asin(arg);
        }
    }

    public static class AtanFunction extends BuiltinOneArgFunction {
        public AtanFunction() { super("atan"); }
        double execute(double arg) {
            return Math.atan(arg);
        }
    }

    public static class PowFunction extends BuiltinTwoArgFunction {
        public PowFunction() { super("pow"); }
        double execute(double arg1, double arg2) {
            return Math.pow(arg1, arg2);
        }
    }
}
