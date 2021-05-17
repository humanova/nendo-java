# nendo

Nendo is a simple math interpreter with programmable features.

## Variables

```
> a = 10
> b = a + 5
> c *= b + a
```

## Loops

```
> a = 0
> loop 10 : a+=2
> a
20

# calculate fibonacci(10)
> n = 0
> i = 1
> loop 10 : temp=n, n+=i, i=temp
> n
55.0
```

## Functions

```
> cube(x) = x*x*x                   
> cube(1.048596)
1.1529874765628132

> circleArea(r) = pi * r * r          # (pi is a built-in constant)
> circleArea(5)
78.53981633974483

> line(m,b,x) = m*x + b               # line function
> line(2, -4, 5)
6.0

> poly2(a,b,c,x) = a*x*x + b*x + c    # 2nd order polynomial
> poly2(1, 2, -1, 3)                  # x^2 + 2x + -1 | x = 3
14.0

> discriminant(a,b,c) = b*b - (4*a*c) # discriminant of 2nd order poly.
> discriminant(1, 2, -1)
8.0
```

## Built-in functions

```
# find the natural logarithm of 2
> log(2)
0.6931471805599453

# find the square root of 2
> sqrt(2)
1.4142135623730951

# find the distance between two points using pythagorean theorem 
> pow2(x) = x*x
> distance2d(x0,y0,x1,y1) = sqrt(pow2(x0-x1) + pow2(y0-y1)) 
> distance2d(0,0,7,24)
25.0
```